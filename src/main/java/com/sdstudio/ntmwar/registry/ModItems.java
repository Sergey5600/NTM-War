package com.sdstudio.ntmwar.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sdstudio.ntmwar.ballistics.ShellSpec;
import com.sdstudio.ntmwar.ballistics.ShellSpecs;
import com.sdstudio.ntmwar.item.ItemBase;
import com.sdstudio.ntmwar.item.ItemComponent;
import com.sdstudio.ntmwar.item.ItemShell;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

/**
 * Регистрация всех предметов NTM: War. Порядок регистрации = порядок во вкладке креатива.
 */
public final class ModItems {

    // Компоненты боеприпасов
    public static Item apfsdsRodTungsten;
    public static Item apfsdsRodDu;
    public static Item sabot;
    public static Item propellantCharge125;
    public static Item heatLinerCopper;
    public static Item shellBody125;

    // Выстрелы
    public static ItemShell shell125ApfsdsW;
    public static ItemShell shell125ApfsdsDu;
    public static ItemShell shell125Heat;
    public static ItemShell shell125He;

    // Задел под броню (пока только материалы)
    public static Item armorPlateRha;
    public static Item eraTile;

    private static final List<ItemBase> REGISTERED = new ArrayList<ItemBase>();

    private ModItems() {}

    /** Вызывается в preInit. */
    public static void register() {
        apfsdsRodTungsten = register(new ItemComponent("apfsds_rod_tungsten", 64));
        apfsdsRodDu = register(new ItemComponent("apfsds_rod_du", 64));
        sabot = register(new ItemComponent("sabot", 64));
        propellantCharge125 = register(new ItemComponent("propellant_charge_125", 16));
        heatLinerCopper = register(new ItemComponent("heat_liner_copper", 64));
        shellBody125 = register(new ItemComponent("shell_body_125", 16));

        shell125ApfsdsW = registerShell(ShellSpecs.APFSDS_125_W);
        shell125ApfsdsDu = registerShell(ShellSpecs.APFSDS_125_DU);
        shell125Heat = registerShell(ShellSpecs.HEAT_125);
        shell125He = registerShell(ShellSpecs.HE_125);

        armorPlateRha = register(new ItemComponent("armor_plate_rha", 64));
        eraTile = register(new ItemComponent("era_tile", 16));
    }

    private static ItemShell registerShell(ShellSpec spec) {
        return register(new ItemShell(spec));
    }

    private static <T extends ItemBase> T register(T item) {
        GameRegistry.registerItem(item, item.getRegistryName());
        REGISTERED.add(item);
        return item;
    }

    /** Все зарегистрированные предметы мода (для лога, проверок и будущих интеграций). */
    public static List<ItemBase> getAll() {
        return Collections.unmodifiableList(REGISTERED);
    }
}
