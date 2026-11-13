package com.sdstudio.ntmwar.compat;

import java.util.ArrayList;
import java.util.List;

import com.hbm.hazard.HazardData;
import com.hbm.hazard.HazardRegistry;
import com.hbm.hazard.HazardSystem;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.recipes.AmmoPressRecipes;
import com.hbm.util.CompatExternal;
import com.hbm.util.CompatRecipeRegistry;
import com.sdstudio.ntmwar.NtmWar;
import com.sdstudio.ntmwar.registry.MachineRecipe;
import com.sdstudio.ntmwar.registry.ModItems;

import api.hbm.recipe.IRecipeRegisterListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * ЕДИНСТВЕННОЕ место в NTM: War, где импортируются классы HBM. При обновлении HBM чинить нужно только здесь.
 * <p>
 * Используется только публичный API HBM для сторонних модов (HBM 1.0.27_X5863):
 * <ul>
 * <li>{@link CompatExternal#registerRecipeRegisterListener} + {@link CompatRecipeRegistry#registerAmmoPress} —
 * рецепты Ammo Press. Слушатель обязателен: HBM пересобирает рецепты машин при старте, командой /ntmreload и при
 * синхронизации с сервером, и рецепты, добавленные напрямую, после этого пропали бы;</li>
 * <li>{@link HazardSystem#register} — радиоактивность предметов (тултип и облучение игрока делает сам HBM).</li>
 * </ul>
 * Каждый вызов обёрнут в перехват {@link LinkageError}: если API HBM изменится, игра не упадёт, а в логе будет
 * понятная ошибка.
 */
public final class HbmCompat {

    /** modid HBM (com.hbm.lib.RefStrings.MODID). */
    public static final String MODID = "hbm";

    /** Радиация сердечника ОУ, RAD/с: 40% слитка U-238 (у слитка HazardRegistry.u238 = 0.25). */
    public static final float RADIATION_DU_ROD = HazardRegistry.u238 * 0.4F;
    /** Радиация выстрела с сердечником ОУ, RAD/с: сердечник частично закрыт ведущим устройством. */
    public static final float RADIATION_DU_SHELL = HazardRegistry.u238 * 0.3F;

    private static int ammoPressRecipeCount;
    private static int hazardCount;

    private HbmCompat() {}

    // ----------------------------------------------------------------------------------------------------------------
    // Ammo Press
    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Регистрирует рецепты выстрелов в Ammo Press HBM. Вызывать до postInit HBM (у нас — в init).
     *
     * @return false, если API HBM недоступен (рецепты нужно делать на верстаке)
     */
    public static boolean registerAmmoPressRecipes(List<MachineRecipe> recipes) {
        try {
            CompatExternal.registerRecipeRegisterListener(new AmmoPressListener(new ArrayList<MachineRecipe>(recipes)));
            return true;
        } catch (LinkageError e) {
            NtmWar.LOG.error("Incompatible HBM version: cannot register Ammo Press recipe listener", e);
            return false;
        }
    }

    /** Сколько рецептов NTM: War добавлено в Ammo Press при последней загрузке рецептов HBM. */
    public static int getAmmoPressRecipeCount() {
        return ammoPressRecipeCount;
    }

    private static final class AmmoPressListener implements IRecipeRegisterListener {

        /** HBM передаёт простое имя класса SerializableRecipe, рецепты которого сейчас загружаются. */
        private static final String RECIPE_TYPE = AmmoPressRecipes.class.getSimpleName();

        private final List<MachineRecipe> recipes;

        AmmoPressListener(List<MachineRecipe> recipes) {
            this.recipes = recipes;
        }

        @Override
        public void onRecipeLoad(String recipeClassName) {
            if (!RECIPE_TYPE.equals(recipeClassName)) return;
            int added = 0;
            try {
                for (MachineRecipe recipe : recipes) {
                    CompatRecipeRegistry.registerAmmoPress(recipe.getOutput(), toHbmGrid(recipe.getGrid()));
                    added++;
                }
            } catch (RuntimeException | LinkageError e) {
                NtmWar.LOG.error("Failed to add NTM: War recipes to the HBM Ammo Press", e);
            }
            ammoPressRecipeCount = added;
        }
    }

    private static AStack[] toHbmGrid(MachineRecipe.Input[] grid) {
        AStack[] result = new AStack[grid.length];
        for (int i = 0; i < grid.length; i++) {
            MachineRecipe.Input input = grid[i];
            if (input == null) continue;
            if (input.isOre()) {
                result[i] = new OreDictStack((String) input.getIngredient(), input.getCount());
            } else {
                ItemStack stack = (ItemStack) input.getIngredient();
                result[i] = new ComparableStack(stack.getItem(), input.getCount(), stack.getItemDamage());
            }
        }
        return result;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Радиация
    // ----------------------------------------------------------------------------------------------------------------

    /** Вешает слабую радиацию на сердечник ОУ и выстрел с ОУ. Вызывать после регистрации предметов. */
    public static void registerHazards() {
        try {
            registerRadiation(ModItems.apfsdsRodDu, RADIATION_DU_ROD);
            registerRadiation(ModItems.shell125ApfsdsDu, RADIATION_DU_SHELL);
        } catch (LinkageError e) {
            NtmWar.LOG.error("Incompatible HBM version: cannot register item radiation", e);
        }
    }

    /** Сколько предметов NTM: War зарегистрировано в системе опасностей HBM. */
    public static int getHazardCount() {
        return hazardCount;
    }

    private static void registerRadiation(Item item, float radsPerSecond) {
        HazardSystem.register(item, new HazardData().addEntry(HazardRegistry.RADIATION, radsPerSecond));
        hazardCount++;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Материалы
    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Проверяет, что все материалы HBM из {@link HbmMaterials} есть в OreDictionary. Вызывать в postInit.
     *
     * @return количество отсутствующих материалов (0 — всё в порядке)
     */
    public static int checkMaterials() {
        int missing = 0;
        for (String oreName : HbmMaterials.ALL) {
            if (OreDictionary.getOres(oreName)
                .isEmpty()) {
                NtmWar.LOG.warn("OreDictionary has no items for '{}': recipes using it cannot be crafted", oreName);
                missing++;
            }
        }
        return missing;
    }
}
