package com.sdstudio.ntmwar.ballistics;

import java.util.Locale;

/**
 * Тип снаряда. Определяет, по какой модели в будущем будет считаться пробитие:
 * <ul>
 * <li>{@link #APFSDS} — кинетический (БОПС): пробитие зависит от скорости в момент попадания и угла встречи,
 * возможен рикошет;</li>
 * <li>{@link #HEAT} — кумулятивный: пробитие не зависит от скорости, сильно падает от динамической защиты;</li>
 * <li>{@link #HE} — осколочно-фугасный: слабое пробитие, основной эффект — фугасное и осколочное действие.</li>
 * </ul>
 * Здесь только данные; клиентские вещи (цвет в тултипе и т.п.) живут в пакете client.
 */
public enum ShellType {

    APFSDS(true, false),
    HEAT(false, true),
    HE(false, true);

    private final boolean kinetic;
    private final boolean explosiveFiller;

    ShellType(boolean kinetic, boolean explosiveFiller) {
        this.kinetic = kinetic;
        this.explosiveFiller = explosiveFiller;
    }

    /** Пробитие за счёт кинетической энергии (зависит от скорости снаряда). */
    public boolean isKinetic() {
        return kinetic;
    }

    /** Есть ли разрывной заряд (ВВ). */
    public boolean hasExplosiveFiller() {
        return explosiveFiller;
    }

    /** Ключ локализации названия типа: {@code ntmwar.shell_type.apfsds}. */
    public String getTranslationKey() {
        return "ntmwar.shell_type." + name().toLowerCase(Locale.ROOT);
    }
}
