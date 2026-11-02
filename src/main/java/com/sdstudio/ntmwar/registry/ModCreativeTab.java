package com.sdstudio.ntmwar.registry;

import com.sdstudio.ntmwar.NtmWar;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/** Вкладка креатива "NTM: War" (ключ {@code itemGroup.ntmwar}), иконка — кумулятивный выстрел. */
public final class ModCreativeTab extends CreativeTabs {

    public static final ModCreativeTab INSTANCE = new ModCreativeTab();

    private ModCreativeTab() {
        super(NtmWar.MODID);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return ModItems.shell125Heat;
    }
}
