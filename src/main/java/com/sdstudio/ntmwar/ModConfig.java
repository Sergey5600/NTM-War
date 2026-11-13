package com.sdstudio.ntmwar;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/** Настройки мода (config/ntmwar.cfg). */
public final class ModConfig {

    /** true — выстрелы собираются в Ammo Press HBM, false — на верстаке из тех же компонентов. */
    public static boolean shellRecipesInAmmoPress = true;

    private ModConfig() {}

    public static void load(File file) {
        Configuration config = new Configuration(file);
        shellRecipesInAmmoPress = config.getBoolean(
            "shellRecipesInAmmoPress",
            Configuration.CATEGORY_GENERAL,
            true,
            "Craft 125mm shells in the HBM Ammo Press (true) or on the crafting table (false).");
        if (config.hasChanged()) {
            config.save();
        }
    }
}
