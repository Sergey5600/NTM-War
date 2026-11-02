package com.sdstudio.ntmwar;

import com.sdstudio.ntmwar.compat.HbmCompat;
import com.sdstudio.ntmwar.registry.ModItems;
import com.sdstudio.ntmwar.registry.ModRecipes;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Общая для клиента и сервера часть инициализации. На выделенном сервере используется как есть,
 * на клиенте — через {@link com.sdstudio.ntmwar.client.ClientProxy}.
 * <p>
 * Порядок относительно HBM: из-за {@code required-after:hbm} каждая фаза HBM выполняется раньше нашей.
 * Рецепты машин HBM загружает в своём postInit, поэтому слушатель Ammo Press регистрируется у нас в init.
 */
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        ModConfig.load(event.getSuggestedConfigurationFile());
        ModItems.register();
        HbmCompat.registerHazards();
    }

    public void init(FMLInitializationEvent event) {
        ModRecipes.register(ModConfig.shellRecipesInAmmoPress);
    }

    public void postInit(FMLPostInitializationEvent event) {
        HbmCompat.checkMaterials();

        int craftingTable = ModRecipes.getCraftingTableRecipeCount();
        int ammoPress = HbmCompat.getAmmoPressRecipeCount();
        if (ModRecipes.areShellsInAmmoPress() && ammoPress != ModRecipes.getShellRecipes()
            .size()) {
            NtmWar.LOG.warn(
                "Expected {} HBM Ammo Press recipes, but HBM loaded {} (custom hbmRecipes/ammoPress.json?)",
                ModRecipes.getShellRecipes()
                    .size(),
                ammoPress);
        }

        NtmWar.LOG.info(
            "{} {}: registered {} items, {} recipes ({} crafting table, {} HBM Ammo Press), {} HBM radiation entries",
            NtmWar.NAME,
            NtmWar.VERSION,
            ModItems.getAll()
                .size(),
            craftingTable + ammoPress,
            craftingTable,
            ammoPress,
            HbmCompat.getHazardCount());
    }
}
