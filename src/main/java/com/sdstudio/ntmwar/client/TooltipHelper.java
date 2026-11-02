package com.sdstudio.ntmwar.client;

import java.util.List;
import java.util.Locale;

import com.sdstudio.ntmwar.ballistics.ShellSpec;
import com.sdstudio.ntmwar.ballistics.ShellType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

/**
 * Тултипы предметов в стиле HBM: короткая информация всегда, подробности — по зажатому Shift
 * (подсказка "Hold &lt;LSHIFT&gt;" оформлена так же, как у HBM). Весь текст — из lang-файлов.
 * <p>
 * Класс клиентский: вызывается только из методов addInformation, помеченных {@code @SideOnly(Side.CLIENT)}.
 */
@SideOnly(Side.CLIENT)
public final class TooltipHelper {

    /** Разделитель строк внутри одного lang-ключа (как в HBM). */
    private static final String LINE_SEPARATOR = "\\$";

    private TooltipHelper() {}

    /** Добавляет описание из lang-ключа; символ {@code $} в значении разбивает текст на строки. */
    public static void addDescription(String key, List<String> tooltip) {
        for (String line : StatCollector.translateToLocal(key)
            .split(LINE_SEPARATOR)) {
            tooltip.add(line);
        }
    }

    /** Калибр и тип снаряда; по Shift — игровые параметры из {@link ShellSpec}. */
    public static void addShellInfo(ShellSpec spec, List<String> tooltip) {
        tooltip.add(
            EnumChatFormatting.YELLOW + translate("tooltip.ntmwar.shell.caliber", String.valueOf(spec.getCaliberMm())));
        tooltip.add(
            colorOf(spec.getType())
                + translate("tooltip.ntmwar.shell.type", translate(spec.getType()
                    .getTranslationKey())));

        if (!GuiScreen.isShiftKeyDown()) {
            tooltip.add(translate("tooltip.ntmwar.hold_shift"));
            return;
        }

        tooltip.add(EnumChatFormatting.BLUE + translate("tooltip.ntmwar.shell.velocity", format(spec.getMuzzleVelocity())));
        tooltip.add(EnumChatFormatting.BLUE + translate("tooltip.ntmwar.shell.mass", format(spec.getProjectileMass())));
        tooltip.add(
            EnumChatFormatting.BLUE + translate("tooltip.ntmwar.shell.penetration", format(spec.getBasePenetration())));
        if (spec.getExplosiveMass() > 0F) {
            tooltip
                .add(EnumChatFormatting.BLUE + translate("tooltip.ntmwar.shell.explosive", format(spec.getExplosiveMass())));
        }
        tooltip.add(translate("tooltip.ntmwar.shell.placeholder"));
    }

    /** Цвет строки типа снаряда в тултипе. */
    public static EnumChatFormatting colorOf(ShellType type) {
        switch (type) {
            case APFSDS:
                return EnumChatFormatting.AQUA;
            case HEAT:
                return EnumChatFormatting.GOLD;
            case HE:
            default:
                return EnumChatFormatting.RED;
        }
    }

    private static String translate(String key, Object... args) {
        return args.length == 0 ? StatCollector.translateToLocal(key)
            : StatCollector.translateToLocalFormatted(key, args);
    }

    /** 1700 → "1700", 4.85 → "4.9": целые без дробной части, остальное с одним знаком. */
    private static String format(float value) {
        if (value == Math.rint(value)) return String.format(Locale.ROOT, "%.0f", value);
        return String.format(Locale.ROOT, "%.1f", value);
    }
}
