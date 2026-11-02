#!/usr/bin/env python3
"""Генератор плейсхолдер-иконок 16x16 для NTM: War.

У каждой группы предметов свой цвет и силуэт, поверх — 1–2 буквы, чтобы предметы различались в игре:
    БОПС (APFSDS) — бирюзовый, кумулятив (HEAT) — оранжевый, ОФС (HE) — красный,
    компоненты — латунно-серый, броня — оливковый.

По умолчанию существующие PNG НЕ перезаписываются, чтобы не затереть нарисованные иконки.
Перегенерировать всё: --force.

Использование (из корня проекта):
    python3 tools/gen_placeholder_textures.py [--force] [--out ПАПКА]

Зависимости: Python 3.8+, Pillow (pip install Pillow).
"""

import argparse
import os
import sys

from PIL import Image

SIZE = 16
DEFAULT_OUT = os.path.join(
    os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
    "src", "main", "resources", "assets", "ntmwar", "textures", "items",
)

# Цвета групп: (основной, контур). Буквы — белые с тёмной тенью.
GROUPS = {
    "apfsds": ((64, 190, 200), (16, 70, 80)),
    "heat": ((235, 140, 40), (95, 45, 5)),
    "he": ((200, 50, 45), (80, 15, 15)),
    "component": ((170, 160, 125), (65, 60, 45)),
    "armor": ((110, 125, 70), (40, 48, 22)),
}

# registry name -> (группа, силуэт, аббревиатура)
ITEMS = {
    "apfsds_rod_tungsten": ("component", "rod", "W"),
    "apfsds_rod_du": ("component", "rod", "DU"),
    "sabot": ("component", "disc", "SB"),
    "propellant_charge_125": ("component", "bag", "PC"),
    "heat_liner_copper": ("component", "cone", "CL"),
    "shell_body_125": ("component", "body", "SH"),
    "shell_125_apfsds_w": ("apfsds", "shell", "AW"),
    "shell_125_apfsds_du": ("apfsds", "shell", "AU"),
    "shell_125_heat": ("heat", "shell", "HT"),
    "shell_125_he": ("he", "shell", "HE"),
    "armor_plate_rha": ("armor", "plate", "RA"),
    "era_tile": ("armor", "tile", "ER"),
}

# Пиксельный шрифт 3x5 (только нужные буквы).
FONT = {
    "A": ["010", "101", "111", "101", "101"],
    "C": ["011", "100", "100", "100", "011"],
    "D": ["110", "101", "101", "101", "110"],
    "E": ["111", "100", "110", "100", "111"],
    "H": ["101", "101", "111", "101", "101"],
    "L": ["100", "100", "100", "100", "111"],
    "P": ["110", "101", "110", "100", "100"],
    "R": ["110", "101", "110", "101", "101"],
    "S": ["011", "100", "010", "001", "110"],
    "T": ["111", "010", "010", "010", "010"],
    "U": ["101", "101", "101", "101", "111"],
    "B": ["110", "101", "110", "101", "110"],
    "W": ["101", "101", "101", "111", "101"],
}


def silhouette(shape):
    """Возвращает набор (x, y) пикселей силуэта 16x16."""
    pts = set()

    def rect(x0, y0, x1, y1):
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                pts.add((x, y))

    if shape == "shell":  # унитарный выстрел: снаряд + гильза
        rect(3, 4, 12, 14)
        rect(4, 2, 11, 3)
        rect(6, 1, 9, 1)
        rect(7, 0, 8, 0)
        rect(2, 15, 13, 15)
    elif shape == "rod":  # длинный стержень по диагонали
        for i in range(2, 14):
            rect(i - 1, 15 - i - 1, i + 1, 15 - i + 1)
    elif shape == "disc":  # сегменты ведущего устройства
        for y in range(SIZE):
            for x in range(SIZE):
                if (x - 7.5) ** 2 + (y - 7.5) ** 2 <= 7.2 ** 2:
                    pts.add((x, y))
    elif shape == "bag":  # картуз заряда
        rect(3, 2, 12, 14)
        rect(5, 1, 10, 1)
    elif shape == "cone":  # воронка
        for y in range(1, 15):
            half = 1 + (y * 6) // 14
            rect(8 - half, y, 7 + half, y)
    elif shape == "body":  # корпус снаряда
        rect(4, 2, 11, 15)
        rect(5, 1, 10, 1)
    elif shape == "plate":  # броневая плита
        rect(1, 2, 14, 13)
    elif shape == "tile":  # элемент ДЗ (коробка)
        rect(2, 3, 13, 12)
    else:
        raise ValueError("unknown shape: " + shape)
    return pts


def draw_icon(group, shape, letters):
    fill, outline = GROUPS[group]
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    px = img.load()
    pts = silhouette(shape)

    for (x, y) in pts:
        edge = any((x + dx, y + dy) not in pts for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)))
        if edge:
            px[x, y] = outline + (255,)
        else:
            # лёгкий вертикальный градиент, чтобы иконка не была плоской
            k = 1.15 - 0.3 * (y / (SIZE - 1))
            px[x, y] = tuple(max(0, min(255, int(c * k))) for c in fill) + (255,)

    # Буквы 3x5 с промежутком 1px, по центру
    width = len(letters) * 4 - 1
    x0 = (SIZE - width) // 2
    y0 = (SIZE - 5) // 2 + 1
    text = set()
    for i, ch in enumerate(letters):
        for gy, row in enumerate(FONT[ch]):
            for gx, bit in enumerate(row):
                if bit == "1":
                    text.add((x0 + i * 4 + gx, y0 + gy))
    for (x, y) in text:  # тень на 1px вправо-вниз для читаемости на любом фоне
        if (x + 1, y + 1) not in text and x + 1 < SIZE and y + 1 < SIZE:
            px[x + 1, y + 1] = (20, 20, 20, 255)
    for (x, y) in text:
        px[x, y] = (255, 255, 255, 255)
    return img


def main(argv):
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--force", action="store_true", help="перезаписать существующие PNG")
    parser.add_argument("--out", default=DEFAULT_OUT, help="папка для PNG (по умолчанию textures/items мода)")
    args = parser.parse_args(argv)

    os.makedirs(args.out, exist_ok=True)
    written = skipped = 0
    for name, (group, shape, letters) in ITEMS.items():
        path = os.path.join(args.out, name + ".png")
        if os.path.exists(path) and not args.force:
            skipped += 1
            continue
        draw_icon(group, shape, letters).save(path, optimize=True)
        written += 1
    print("written: %d, skipped (already exist): %d, out: %s" % (written, skipped, args.out))
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
