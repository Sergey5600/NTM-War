package com.sdstudio.ntmwar.ballistics;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Все выстрелы мода и их параметры — единственное место, где крутится баланс.
 * <p>
 * ВНИМАНИЕ: значения — игровые плейсхолдеры, ориентированные на порядок величин реальных 125-мм выстрелов,
 * а не на конкретные изделия. Менять свободно.
 */
public final class ShellSpecs {

    /** Калибр гладкоствольной танковой пушки 125 мм. */
    public static final int CALIBER_125 = 125;

    // id, калибр, тип, скорость (м/с), масса (кг), пробитие (мм RHA), ВВ (кг ТНТ)

    public static final ShellSpec APFSDS_125_W = new ShellSpec(
        "shell_125_apfsds_w", CALIBER_125, ShellType.APFSDS, 1700F, 4.8F, 500F, 0F);

    public static final ShellSpec APFSDS_125_DU = new ShellSpec(
        "shell_125_apfsds_du", CALIBER_125, ShellType.APFSDS, 1700F, 4.9F, 560F, 0F);

    public static final ShellSpec HEAT_125 = new ShellSpec(
        "shell_125_heat", CALIBER_125, ShellType.HEAT, 900F, 19.0F, 450F, 1.7F);

    public static final ShellSpec HE_125 = new ShellSpec(
        "shell_125_he", CALIBER_125, ShellType.HE, 850F, 23.0F, 40F, 3.4F);

    /** Все выстрелы в порядке появления во вкладке креатива. */
    public static final List<ShellSpec> ALL = Collections
        .unmodifiableList(Arrays.asList(APFSDS_125_W, APFSDS_125_DU, HEAT_125, HE_125));

    private ShellSpecs() {}

    /** @return параметры выстрела по id или {@code null}, если такого нет */
    public static ShellSpec byId(String id) {
        for (ShellSpec spec : ALL) {
            if (spec.getId()
                .equals(id)) return spec;
        }
        return null;
    }
}
