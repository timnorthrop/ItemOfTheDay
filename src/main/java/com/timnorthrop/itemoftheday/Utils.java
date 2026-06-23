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
                key == ItemTypeKeys.AIR.key() ||
                key == ItemTypeKeys.BARRIER.key() ||
                key == ItemTypeKeys.BEDROCK.key() ||
                key == ItemTypeKeys.BUDDING_AMETHYST.key() ||
                key == ItemTypeKeys.CHORUS_PLANT.key() ||
                key == ItemTypeKeys.DEBUG_STICK.key() ||
                key == ItemTypeKeys.DIRT_PATH.key() ||
                key == ItemTypeKeys.END_PORTAL_FRAME.key() ||
                key == ItemTypeKeys.FARMLAND.key() ||
                key == ItemTypeKeys.FROGSPAWN.key() ||
                key == ItemTypeKeys.JIGSAW.key() ||
                key == ItemTypeKeys.KNOWLEDGE_BOOK.key() ||
                key == ItemTypeKeys.LIGHT.key() ||
                key == ItemTypeKeys.PETRIFIED_OAK_SLAB.key() ||
                key == ItemTypeKeys.PLAYER_HEAD.key() ||
                key == ItemTypeKeys.REINFORCED_DEEPSLATE.key() ||
                key == ItemTypeKeys.SPAWNER.key() ||
                key == ItemTypeKeys.STRUCTURE_BLOCK.key() ||
                key == ItemTypeKeys.STRUCTURE_VOID.key() ||
                key == ItemTypeKeys.TEST_BLOCK.key() ||
                key == ItemTypeKeys.TEST_INSTANCE_BLOCK.key() ||
                key == ItemTypeKeys.TRIAL_SPAWNER.key() ||
                key == ItemTypeKeys.VAULT.key();
    }
}
