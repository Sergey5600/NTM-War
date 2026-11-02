package com.sdstudio.ntmwar.item;

import com.sdstudio.ntmwar.NtmWar;
import com.sdstudio.ntmwar.registry.ModCreativeTab;

import net.minecraft.item.Item;

/**
 * Общая настройка предметов NTM: War: registry name, ключ локализации, текстура и вкладка креатива.
 * <ul>
 * <li>registry name: {@code ntmwar:<name>}</li>
 * <li>ключ названия: {@code item.ntmwar.<name>.name}</li>
 * <li>текстура: {@code assets/ntmwar/textures/items/<name>.png}</li>
 * </ul>
 */
public abstract class ItemBase extends Item {

    private final String registryName;

    protected ItemBase(String registryName, int maxStackSize) {
        this.registryName = registryName;
        setUnlocalizedName(NtmWar.MODID + "." + registryName);
        setTextureName(NtmWar.MODID + ":" + registryName);
        setMaxStackSize(maxStackSize);
        setCreativeTab(ModCreativeTab.INSTANCE);
    }

    /** Имя без modid, под которым предмет регистрируется в GameRegistry. */
    public String getRegistryName() {
        return registryName;
    }
}
