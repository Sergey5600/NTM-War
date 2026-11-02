package com.sdstudio.ntmwar;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Общая для клиента и сервера часть инициализации. На выделенном сервере используется как есть,
 * на клиенте — через {@link com.sdstudio.ntmwar.client.ClientProxy}.
 */
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {}

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {}
}
