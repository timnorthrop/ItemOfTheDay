package com.timnorthrop.itemoftheday;

import io.papermc.paper.registry.keys.ItemTypeKeys;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemType;

public class Utils {
    public static boolean isUnobtainable(ItemType m) {
        String name = String.valueOf(m);
        Key key = m.getKey();
        return name.contains("spawn_egg") ||
                name.contains("command_block") ||
                name.contains("infested") ||
                key.compareTo(ItemTypeKeys.AIR.key()) == 0 ||
                key.compareTo(ItemTypeKeys.BARRIER.key()) == 0 ||
                key.compareTo(ItemTypeKeys.BEDROCK.key()) == 0 ||
                key.compareTo(ItemTypeKeys.BUDDING_AMETHYST.key()) == 0 ||
                key.compareTo(ItemTypeKeys.CHORUS_PLANT.key()) == 0 ||
                key.compareTo(ItemTypeKeys.DEBUG_STICK.key()) == 0 ||
                key.compareTo(ItemTypeKeys.DIRT_PATH.key()) == 0 ||
                key.compareTo(ItemTypeKeys.END_PORTAL_FRAME.key()) == 0 ||
                key.compareTo(ItemTypeKeys.FARMLAND.key()) == 0 ||
                key.compareTo(ItemTypeKeys.FROGSPAWN.key()) == 0 ||
                key.compareTo(ItemTypeKeys.JIGSAW.key()) == 0 ||
                key.compareTo(ItemTypeKeys.KNOWLEDGE_BOOK.key()) == 0 ||
                key.compareTo(ItemTypeKeys.LIGHT.key()) == 0 ||
                key.compareTo(ItemTypeKeys.PETRIFIED_OAK_SLAB.key()) == 0 ||
                key.compareTo(ItemTypeKeys.PLAYER_HEAD.key()) == 0 ||
                key.compareTo(ItemTypeKeys.REINFORCED_DEEPSLATE.key()) == 0 ||
                key.compareTo(ItemTypeKeys.SPAWNER.key()) == 0 ||
                key.compareTo(ItemTypeKeys.STRUCTURE_BLOCK.key()) == 0 ||
                key.compareTo(ItemTypeKeys.STRUCTURE_VOID.key()) == 0 ||
                key.compareTo(ItemTypeKeys.TEST_BLOCK.key()) == 0 ||
                key.compareTo(ItemTypeKeys.TEST_INSTANCE_BLOCK.key()) == 0 ||
                key.compareTo(ItemTypeKeys.TRIAL_SPAWNER.key()) == 0 ||
                key.compareTo(ItemTypeKeys.VAULT.key()) == 0;
    }
}
