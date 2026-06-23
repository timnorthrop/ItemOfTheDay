package com.timnorthrop.itemoftheday;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Random;
import java.util.ArrayList;

public class ItemOfTheDayPlugin extends JavaPlugin {
    private ItemType iotd;
    private ItemStack reward;
    private Set<String> blacklistedItems = new HashSet<>();

    private final Registry<ItemType> itemReg = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);

    @Override
    public void onEnable() {
        saveDefaultConfig();

        DateWatcher dw = new DateWatcher(this);
        dw.start();

        if (!getConfig().contains("blacklisted-items")) {
            List<String> items = itemReg.stream()
                    .filter(Utils::isUnobtainable)
                    .map(m -> m.getKey().asString())
                    .sorted()
                    .toList();

            getConfig().set("blacklisted-items", items);
            saveConfig();
        }
        blacklistedItems = new HashSet<>(getConfig().getStringList("blacklisted-items"));

        long lastRefreshEpochDay = getConfig().getLong("last-refresh-epoch-date");
        if (lastRefreshEpochDay == 0) {
            shuffleIotd();
        } else {
            LocalDate lastRefresh = LocalDate.ofEpochDay(lastRefreshEpochDay);
            if (lastRefresh.isEqual(LocalDate.now())) {
                try {
                    setIotd(itemReg.getOrThrow(Key.key(Objects.requireNonNull(getConfig().getString("iotd")))));
                } catch (RuntimeException e) {
                    shuffleIotd();
                }

                ItemStack currentReward = getConfig().getItemStack("current-reward");
                setRewardOrDefault(currentReward);
            } else {
                shuffleIotd();
            }
        }

        registerCommands();

        getLogger().info("ItemOfTheDay enabled!");
    }

    protected void shuffleIotd() {
        List<ItemType> items = itemReg.stream()
                .filter(m -> !blacklistedItems.contains(m.getKey().asString()))
                .toList();

        Random rand = new Random();
        setIotd(items.get(rand.nextInt(items.size())));
        setRewardOrDefault(null);
    }

    private void setIotd(ItemType iotd) {
        if (!blacklistedItems.contains(iotd.getKey().asString())) {
            this.iotd = iotd;
            getConfig().set("iotd", iotd.getKey().asString());
            if (getConfig().getLong("last-refresh-epoch-date") != LocalDate.now().toEpochDay()) {
                getConfig().set("last-refresh-epoch-date", LocalDate.now().toEpochDay());
                getConfig().set("claimed", new ArrayList<String>());
            }
            saveConfig();
        } else {
            throw new IllegalArgumentException();
        }
    }

    protected void setRewardOrDefault(ItemStack stack) {
        if (stack == null) {
            ItemStack defaultReward = getConfig().getItemStack("default-reward");
            if (defaultReward == null) {
                ItemStack newDefault = new ItemStack(Material.DIAMOND, 1);
                getConfig().set("default-reward", newDefault);
                saveConfig();

                stack = newDefault;
            } else {
                stack = defaultReward;
            }
        }
        this.reward = stack;
        getConfig().set("current-reward", stack);
        saveConfig();
    }

    private void registerCommands() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("iotd");

        root.then(Commands.literal("info").executes(ctx -> {
            ctx.getSource().getSender().sendRichMessage("Today's <light_purple>Item of the Day</light_purple> is: " +
                    "<gold>" + iotd.getKey().getKey() + "</gold><newline>Go find one, hold it in your main hand, " +
                    "and run '/iotd exchange' to receive <green>" + reward.getAmount() + "x " +
                    reward.getType().getKey().getKey() + "</green>!");
            return Command.SINGLE_SUCCESS;
        }));

        root.then(Commands.literal("exchange").executes(ctx -> {
            if (!(ctx.getSource().getSender() instanceof Player player)) {
                ctx.getSource().getSender().sendMessage("Only players can exchange for the Item of the Day.");
                return Command.SINGLE_SUCCESS;
            }
            final List<String> claimed = getConfig().getStringList("claimed");
            if (claimed.contains(player.getUniqueId().toString())) {
                player.sendRichMessage("<red>You've already claimed the <light_purple>Item of the Day</light_purple> " +
                        "today!");
                return Command.SINGLE_SUCCESS;
            }
            final ItemStack held = player.getInventory().getItemInMainHand();
            if (held.getType().asItemType() != iotd) {
                player.sendRichMessage("<red>Incorrect item.</red>");
                return Command.SINGLE_SUCCESS;
            }
            if (held.getAmount() != 1) {
                player.sendRichMessage("<red>Hold only one of the item to exchange.</red>");
                return Command.SINGLE_SUCCESS;
            }

            player.getInventory().setItemInMainHand(reward);
            claimed.add(player.getUniqueId().toString());
            getConfig().set("claimed", claimed);
            saveConfig();
            player.sendRichMessage("<aqua><bold>Congrats!</bold></aqua> You exchanged the <light_purple>Item " +
                    "of the Day</light_purple> (<gold>" + iotd.getKey().getKey() + "</gold>) for <green>" +
                    reward.getAmount() + "x " + reward.getType().getKey().getKey() + "</green>!");
            getLogger().info(player.getName() + " has exchanged the Item of the Day for a reward.");
            return Command.SINGLE_SUCCESS;
        }));

        root.then(Commands.literal("shuffle").requires(sender -> sender.getSender().isOp())
                .executes(ctx -> {
                    shuffleIotd();
                    ctx.getSource().getSender().sendRichMessage("The <light_purple>Item of the Day</light_purple> is " +
                            "now <gold>" + iotd.getKey().getKey() + "</gold>, and the reward has been reset to the " +
                            "default reward (<green>" + reward.getAmount() + "x " + reward.getType().getKey().getKey() +
                            "</green>).");
                    return Command.SINGLE_SUCCESS;
                }));

        root.then(Commands.literal("set").requires(sender -> sender.getSender().isOp())
                .then(Commands.argument("item", ArgumentTypes.resource(RegistryKey.ITEM))
                        .executes(ctx -> {
                            final ItemType material = ctx.getArgument("item", ItemType.class);
                            try {
                                setIotd(Objects.requireNonNull(material));
                                ctx.getSource().getSender().sendRichMessage("Set <light_purple>Item of the " +
                                        "Day</light_purple> to <gold>" + iotd.getKey().getKey() + "</gold>.");
                            } catch (RuntimeException e) {
                                ctx.getSource().getSender().sendRichMessage("<red>Material is invalid or " +
                                        "is blacklisted by config.</red>");
                            }
                            return Command.SINGLE_SUCCESS;
                        })));

        root.then(Commands.literal("set-reward").requires(ctx -> ctx.getSender().isOp())
                .then(Commands.argument("reward", ArgumentTypes.itemStack())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(ctx -> {
                                    final ItemStack itemStack = ctx.getArgument("reward", ItemStack.class);
                                    final int amount = ctx.getArgument("amount", Integer.class);
                                    final CommandSender sender = ctx.getSource().getSender();
                                    try {
                                        validateStack(itemStack, amount, sender);
                                    } catch (IllegalArgumentException e) {
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    setRewardOrDefault(itemStack);
                                    sender.sendRichMessage("Set reward to <green>" + amount + "x " +
                                            itemStack.getType().getKey().getKey() + "</green>.");
                                    return Command.SINGLE_SUCCESS;
                                }))));

        root.then(Commands.literal("set-default-reward").requires(ctx -> ctx.getSender().isOp())
                .then(Commands.argument("default-reward", ArgumentTypes.itemStack())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(ctx -> {
                                    final ItemStack itemStack = ctx.getArgument("default-reward", ItemStack.class);
                                    final int amount = ctx.getArgument("amount", Integer.class);
                                    final CommandSender sender = ctx.getSource().getSender();
                                    try {
                                        validateStack(itemStack, amount, sender);
                                    } catch (IllegalArgumentException e) {
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    getConfig().set("default-reward", itemStack);
                                    saveConfig();
                                    sender.sendRichMessage("Set default reward to <green>" + amount + "x " +
                                            itemStack.getType().getKey().getKey() + "</green>.");
                                    return Command.SINGLE_SUCCESS;
                                }))));

        root.then(Commands.literal("reset-claimed").requires(ctx -> ctx.getSender().isOp())
                .executes(ctx -> {
                    getConfig().set("claimed", new ArrayList<String>());
                    ctx.getSource().getSender().sendRichMessage("<green>Reset list of players who've claimed the " +
                            "<light_purple>Item of the Day</light_purple>.");
                    return Command.SINGLE_SUCCESS;
        }));

        LiteralCommandNode<CommandSourceStack> builtRoot = root.build();
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                commands -> commands.registrar().register(builtRoot));
    }

    private static void validateStack(ItemStack stack, int amount, CommandSender sender)
            throws IllegalArgumentException {
        if (stack == null) {
            sender.sendMessage("<red>Invalid reward.</red>");
            throw new IllegalArgumentException();
        }

        if (amount < 1 || amount > stack.getMaxStackSize()) {
            sender.sendMessage("<red>Invalid stack size.</red>");
            throw new IllegalArgumentException();
        }
        stack.setAmount(amount);
    }

    @Override
    public void onDisable() {
        getLogger().info("ItemOfTheDay disabled!");
    }
}
