package com.sdstudio.ntmwar.item;

import java.util.List;

import com.sdstudio.ntmwar.ballistics.ShellSpec;
import com.sdstudio.ntmwar.client.TooltipHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Выстрел (унитарный боеприпас) — один класс на все калибры и типы. Все параметры берутся из {@link ShellSpec},
 * registry name выстрела совпадает с {@link ShellSpec#getId()}.
 * <p>
 * В 0.1 это просто предмет; в 0.2 пушка техники будет брать из него ShellSpec и порождать сущность снаряда.
 */
public class ItemShell extends ItemBase {

    public static final int MAX_STACK_SIZE = 8;

    private final ShellSpec spec;

    public ItemShell(ShellSpec spec) {
        super(spec.getId(), MAX_STACK_SIZE);
        this.spec = spec;
    }

    public ShellSpec getSpec() {
        return spec;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        TooltipHelper.addShellInfo(spec, tooltip);
    }
}
