package com.sdstudio.ntmwar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Главный класс NTM: War. Только жизненный цикл мода — вся логика живёт в registry/, compat/ и т.д.
 * <p>
 * HBM's Nuclear Tech Mod — обязательная зависимость: {@code required-after:hbm}. Если HBM не установлен,
 * FML останавливает загрузку до создания экземпляра мода и показывает экран "Missing mods: hbm".
 */
@Mod(
    modid = NtmWar.MODID,
    name = NtmWar.NAME,
    version = NtmWar.VERSION,
    dependencies = NtmWar.DEPENDENCIES,
    acceptedMinecraftVersions = "[1.7.10]")
public class NtmWar {

    public static final String MODID = "ntmwar";
    public static final String NAME = "NTM: War";
    public static final String VERSION = Tags.VERSION;
    /** modid HBM — "hbm" (com.hbm.lib.RefStrings.MODID). */
    public static final String DEPENDENCIES = "required-after:hbm";

    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.Instance(MODID)
    public static NtmWar instance;

    @SidedProxy(
        clientSide = "com.sdstudio.ntmwar.client.ClientProxy",
        serverSide = "com.sdstudio.ntmwar.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
