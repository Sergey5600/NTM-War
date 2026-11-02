#!/usr/bin/env python3
"""Проверка ресурсов NTM: War без запуска игры.

Ищет в исходниках все предметы и ключи локализации и проверяет, что:
  * у каждого предмета есть текстура 16x16 PNG с альфа-каналом;
  * у каждого предмета есть название (.name) в en_US.lang и ru_RU.lang, у компонентов — описание (.desc);
  * все ключи, упомянутые в коде (tooltip.*, ntmwar.shell_type.*, itemGroup.*), есть в обоих lang-файлах;
  * наборы ключей en_US и ru_RU совпадают, а число %s в переводах одинаковое.

Использование (из корня проекта):  python3 tools/check_resources.py
Код возврата 0 — всё в порядке, 1 — есть ошибки.
"""

import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
JAVA = os.path.join(ROOT, "src", "main", "java", "com", "sdstudio", "ntmwar")
ASSETS = os.path.join(ROOT, "src", "main", "resources", "assets", "ntmwar")
LANGS = ("en_US", "ru_RU")
MODID = "ntmwar"


def read(path):
    with open(path, encoding="utf-8") as f:
        return f.read()


def java_sources():
    for base, _, files in os.walk(JAVA):
        for name in files:
            if name.endswith(".java"):
                yield os.path.join(base, name), read(os.path.join(base, name))


def load_lang(name):
    entries = {}
    for line in read(os.path.join(ASSETS, "lang", name + ".lang")).splitlines():
        if not line.strip() or line.lstrip().startswith("#"):
            continue
        key, sep, value = line.partition("=")
        if not sep:
            raise ValueError("%s.lang: bad line %r" % (name, line))
        if key in entries:
            raise ValueError("%s.lang: duplicate key %s" % (name, key))
        entries[key] = value
    return entries


def main():
    errors = []
    sources = dict(java_sources())
    all_src = "\n".join(sources.values())

    components = re.findall(r'new ItemComponent\("([a-z0-9_]+)"', all_src)
    shells = re.findall(r'new ShellSpec\(\s*"([a-z0-9_]+)"', all_src)
    items = components + shells
    shell_types = [t.lower() for t in re.findall(r"^\s*([A-Z]+)\((?:true|false)", read(
        os.path.join(JAVA, "ballistics", "ShellType.java")), re.M)]

    required = {"itemGroup." + MODID}
    for name in items:
        required.add("item.%s.%s.name" % (MODID, name))
    for name in components:
        required.add("item.%s.%s.desc" % (MODID, name))
    for t in shell_types:
        required.add("%s.shell_type.%s" % (MODID, t))
    required.update(re.findall(r'"(tooltip\.%s\.[a-z0-9_.]+)"' % MODID, all_src))

    langs = {name: load_lang(name) for name in LANGS}
    for name, entries in langs.items():
        for key in sorted(required - set(entries)):
            errors.append("%s.lang: missing key %s" % (name, key))
        for key in sorted(set(entries) - required):
            errors.append("%s.lang: unused key %s" % (name, key))
    en, ru = langs["en_US"], langs["ru_RU"]
    for key in sorted(set(en) & set(ru)):
        if en[key].count("%s") != ru[key].count("%s"):
            errors.append("format arguments differ between languages: " + key)

    try:
        from PIL import Image
    except ImportError:
        Image = None
        print("Pillow not installed: texture size/format check skipped")
    for name in items:
        path = os.path.join(ASSETS, "textures", "items", name + ".png")
        if not os.path.isfile(path):
            errors.append("missing texture " + os.path.relpath(path, ROOT))
        elif Image is not None:
            with Image.open(path) as img:
                if img.size != (16, 16) or img.mode not in ("RGBA", "LA", "P"):
                    errors.append("texture %s: expected 16x16 with alpha, got %s %s" % (name, img.size, img.mode))

    print("items: %d (%d components/armor, %d shells), lang keys required: %d"
          % (len(items), len(components), len(shells), len(required)))
    for e in errors:
        print("ERROR: " + e)
    print("OK" if not errors else "FAILED: %d error(s)" % len(errors))
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())
