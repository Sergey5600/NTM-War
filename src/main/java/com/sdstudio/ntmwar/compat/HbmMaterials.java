package com.sdstudio.ntmwar.compat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Материалы HBM, которые использует NTM: War, — только имена в OreDictionary (классы HBM здесь не нужны).
 * Имена сверены с com.hbm.inventory.OreDictManager / MaterialShapes в HBM 1.0.27_X5863, подробности —
 * docs/HBM_NOTES.md. Наличие всех имён проверяется при запуске: {@link HbmCompat#checkMaterials()}.
 */
public final class HbmMaterials {

    /** Слиток вольфрама (HBM: ingot_tungsten). У вольфрама в HBM нет обычной пластины, только литая/сварная. */
    public static final String INGOT_TUNGSTEN = "ingotTungsten";
    /** Слиток урана-238 (обеднённый уран, HBM: ingot_u238). Синоним в HBM — ingotU238. */
    public static final String INGOT_U238 = "ingotUranium238";
    /** Медная пластина (HBM: plate_copper). */
    public static final String PLATE_COPPER = "plateCopper";
    /** Алюминиевая пластина (HBM: plate_aluminium; имя в словаре — американское Aluminum). */
    public static final String PLATE_ALUMINUM = "plateAluminum";
    /** Стальная пластина (HBM: plate_steel). */
    public static final String PLATE_STEEL = "plateSteel";
    /** Стальной блок (HBM: block_steel). */
    public static final String BLOCK_STEEL = "blockSteel";
    /** Любой бездымный порох HBM: кордит и баллистит (OreDictManager.ANY_SMOKELESS). */
    public static final String SMOKELESS_POWDER = "dustAnySmokeless";
    /**
     * Любое бризантное ВВ HBM: ТНТ (ball_tnt) и TATB (OreDictManager.ANY_HIGHEXPLOSIVE).
     * Это же ВВ HBM кладёт в кумулятивную ракету РПГ в Ammo Press.
     */
    public static final String HIGH_EXPLOSIVE = "ingotAnyHighexplosive";

    public static final List<String> ALL = Collections.unmodifiableList(
        Arrays.asList(
            INGOT_TUNGSTEN,
            INGOT_U238,
            PLATE_COPPER,
            PLATE_ALUMINUM,
            PLATE_STEEL,
            BLOCK_STEEL,
            SMOKELESS_POWDER,
            HIGH_EXPLOSIVE));

    private HbmMaterials() {}
}
