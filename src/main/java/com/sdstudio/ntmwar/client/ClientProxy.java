package com.sdstudio.ntmwar.client;

import com.sdstudio.ntmwar.CommonProxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Клиентская часть инициализации. Всё, что трогает клиентские классы Minecraft (рендер, модели, GUI),
 * регистрируется отсюда, чтобы выделенный сервер их не загружал.
 * <p>
 * В 0.1 иконки предметов регистрируются ванильным механизмом (setTextureName), поэтому здесь пока пусто;
 * в 0.2 сюда лягут рендереры сущностей снарядов и техники.
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}
