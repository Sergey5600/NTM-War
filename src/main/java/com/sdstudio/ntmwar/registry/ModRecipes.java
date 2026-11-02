package com.sdstudio.ntmwar.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sdstudio.ntmwar.NtmWar;
import com.sdstudio.ntmwar.compat.HbmCompat;
import com.sdstudio.ntmwar.compat.HbmMaterials;
import com.sdstudio.ntmwar.registry.MachineRecipe.Input;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Все рецепты NTM: War.
 * <ul>
 * <li>компоненты, броневая плита и элемент ДЗ — верстак (материалы HBM через OreDictionary);</li>
 * <li>выстрелы — Ammo Press HBM, там же, где HBM собирает свои ракеты для РПГ (регистрация через
 * {@link HbmCompat}); при выключенной настройке или несовместимом HBM — запасной бесформенный рецепт на верстаке
 * из тех же ингредиентов.</li>
 * </ul>
 * Баланс (см. docs/HBM_NOTES.md): кумулятивная ракета HBM в Ammo Press — 1 медная пластина + 1 ТНТ + 1 большая
 * гильза + 3 бездымного пороха на 2 ракеты. Выстрел 125 мм стоит примерно как 2 ракеты: ОФС — самый дешёвый
 * (корпус + ВВ + заряд), кумулятив = ОФС + воронка, БОПС с ОУ дороже вольфрамового (3 слитка U-238 на сердечник
 * против 1,5 слитка вольфрама).
 */
public final class ModRecipes {

    private static final List<MachineRecipe> SHELL_RECIPES = new ArrayList<MachineRecipe>();

    private static int craftingTableRecipes;
    private static boolean shellsInAmmoPress;

    private ModRecipes() {}

    /** Вызывается в init — до postInit HBM, где HBM загружает рецепты своих машин. */
    public static void register(boolean preferAmmoPress) {
        registerComponents();
        registerArmor();
        registerShells(preferAmmoPress);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Компоненты (верстак)
    // ----------------------------------------------------------------------------------------------------------------

    private static void registerComponents() {
        // Сердечник БОПС: длинный стержень — 3 слитка по диагонали.
        addShaped(stack(ModItems.apfsdsRodTungsten, 2),
            "  W",
            " W ",
            "W  ",
            'W', HbmMaterials.INGOT_TUNGSTEN);
        addShaped(stack(ModItems.apfsdsRodDu, 1),
            "  U",
            " U ",
            "U  ",
            'U', HbmMaterials.INGOT_U238);

        // Ведущее устройство: алюминиевые сегменты вокруг сердечника.
        addShaped(stack(ModItems.sabot, 2),
            " A ",
            "A A",
            'A', HbmMaterials.PLATE_ALUMINUM);

        // Метательный заряд: бездымный порох HBM в сгораемой бумажной гильзе, перевязанной нитью.
        addShapeless(stack(ModItems.propellantCharge125, 1),
            HbmMaterials.SMOKELESS_POWDER,
            HbmMaterials.SMOKELESS_POWDER,
            HbmMaterials.SMOKELESS_POWDER,
            Items.paper,
            Items.paper,
            Items.string);

        // Кумулятивная воронка: конус из медных пластин.
        addShaped(stack(ModItems.heatLinerCopper, 3),
            "C C",
            " C ",
            'C', HbmMaterials.PLATE_COPPER);

        // Корпус снаряда: стальной стакан с дном.
        addShaped(stack(ModItems.shellBody125, 2),
            "P P",
            "P P",
            " P ",
            'P', HbmMaterials.PLATE_STEEL);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Броня (верстак) — пока только материалы
    // ----------------------------------------------------------------------------------------------------------------

    private static void registerArmor() {
        // Броневая плита — дорого: 8 стальных пластин + стальной блок (17 слитков стали).
        addShaped(stack(ModItems.armorPlateRha, 1),
            "PPP",
            "PBP",
            "PPP",
            'P', HbmMaterials.PLATE_STEEL,
            'B', HbmMaterials.BLOCK_STEEL);

        // Элемент ДЗ: ВВ между стальными пластинами на броневой плите.
        addShaped(stack(ModItems.eraTile, 4),
            " H ",
            "PAP",
            " H ",
            'H', HbmMaterials.HIGH_EXPLOSIVE,
            'P', HbmMaterials.PLATE_STEEL,
            'A', ModItems.armorPlateRha);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Выстрелы (Ammo Press HBM)
    // ----------------------------------------------------------------------------------------------------------------

    private static void registerShells(boolean preferAmmoPress) {
        Input charge = Input.stack(stack(ModItems.propellantCharge125, 1));
        Input body = Input.stack(stack(ModItems.shellBody125, 1));
        Input sabot = Input.stack(stack(ModItems.sabot, 1));
        Input explosive = Input.ore(HbmMaterials.HIGH_EXPLOSIVE);

        // Раскладка повторяет кумулятивную ракету HBM: боевая часть сверху, корпус в центре, заряд снизу.
        SHELL_RECIPES.add(new MachineRecipe(stack(ModItems.shell125ApfsdsW, 1),
            null, Input.stack(stack(ModItems.apfsdsRodTungsten, 1)), null,
            null, sabot, null,
            null, charge, null));
        SHELL_RECIPES.add(new MachineRecipe(stack(ModItems.shell125ApfsdsDu, 1),
            null, Input.stack(stack(ModItems.apfsdsRodDu, 1)), null,
            null, sabot, null,
            null, charge, null));
        SHELL_RECIPES.add(new MachineRecipe(stack(ModItems.shell125Heat, 1),
            Input.stack(stack(ModItems.heatLinerCopper, 1)), explosive, null,
            null, body, null,
            null, charge, null));
        SHELL_RECIPES.add(new MachineRecipe(stack(ModItems.shell125He, 1),
            null, explosive, null,
            null, body, null,
            null, charge, null));

        shellsInAmmoPress = preferAmmoPress && HbmCompat.registerAmmoPressRecipes(getShellRecipes());
        if (shellsInAmmoPress) return;

        if (preferAmmoPress) {
            NtmWar.LOG.warn("HBM Ammo Press integration is unavailable, shells will be crafted on the crafting table");
        }
        for (MachineRecipe recipe : SHELL_RECIPES) {
            addShapeless(recipe.getOutput(), recipe.flattenIngredients()
                .toArray());
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    /** Рецепты выстрелов в формате машины 3×3 (неизменяемый список). */
    public static List<MachineRecipe> getShellRecipes() {
        return Collections.unmodifiableList(SHELL_RECIPES);
    }

    /** Сколько рецептов зарегистрировано на верстаке. */
    public static int getCraftingTableRecipeCount() {
        return craftingTableRecipes;
    }

    /** true — выстрелы собираются в Ammo Press HBM, false — на верстаке. */
    public static boolean areShellsInAmmoPress() {
        return shellsInAmmoPress;
    }

    private static ItemStack stack(Item item, int count) {
        return new ItemStack(item, count);
    }

    private static void addShaped(ItemStack output, Object... recipe) {
        GameRegistry.addRecipe(new ShapedOreRecipe(output, recipe));
        craftingTableRecipes++;
    }

    private static void addShapeless(ItemStack output, Object... ingredients) {
        GameRegistry.addRecipe(new ShapelessOreRecipe(output, ingredients));
        craftingTableRecipes++;
    }
}
