package com.sdstudio.ntmwar.item;

import java.util.List;

import com.sdstudio.ntmwar.client.TooltipHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Простой компонент (сердечник, ведущее устройство, заряд, броневая плита и т.п.) — один класс на все.
 * Описание в тултипе берётся из ключа {@code item.ntmwar.<name>.desc}.
 */
public class ItemComponent extends ItemBase {

    public ItemComponent(String registryName, int maxStackSize) {
        super(registryName, maxStackSize);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        TooltipHelper.addDescription(getUnlocalizedName() + ".desc", tooltip);
    }
}
