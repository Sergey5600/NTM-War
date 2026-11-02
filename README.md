# NTM: War

Аддон к [HBM's Nuclear Tech Mod](https://github.com/HbmMods/Hbm-s-Nuclear-Tech-GIT) для Minecraft 1.7.10
от **SD Studio**. Это аддон, а не форк: HBM — внешняя обязательная зависимость.

Планы: танки как сущности, снаряды с моделью пробития (БОПС, кумулятив, ОФС; броня плитами, углы встречи,
рикошеты, заброневое действие), оружие.

**Версия 0.1.0 (тестовая)** содержит только основу:

* компоненты боеприпасов: сердечники БОПС (вольфрам / обеднённый уран), ведущее устройство,
  метательный заряд 125 мм, кумулятивная воронка, корпус снаряда 125 мм;
* выстрелы 125 мм: БОПС (W), БОПС (ОУ), кумулятивный, осколочно-фугасный. Пока это предметы:
  стрельбы и баллистики нет, параметры показаны в тултипе по Shift;
* материалы под будущую броню: броневая плита, элемент динамической защиты;
* рецепты на материалах HBM. Компоненты делаются на верстаке, выстрелы — в **Ammo Press** HBM;
* слабая радиация у сердечника и выстрела с обеднённым ураном (через систему опасностей HBM);
* своя вкладка в креативе «NTM: War».

## Зависимости

| | Версия |
|---|---|
| Minecraft | 1.7.10 |
| Forge | 10.13.4.1614 |
| HBM's Nuclear Tech Mod | 1.0.27_X5863 (обязательно, modid `hbm`) |

Без HBM игра не стартует и показывает экран FML «Missing mods: hbm».

Для разработки HBM подключается из официального Maven HBM (`maven.ntmr.dev`) как деобфусцированный dev-jar.
NotEnoughItems (форк GTNH) добавлен только в dev-клиент для просмотра рецептов и в зависимости мода не входит.
Версии зафиксированы в `gradle.properties` (`hbmVersion`, `neiVersion`).

## Сборка

Проект собран на шаблоне [GTNewHorizons/ExampleMod1.7.10](https://github.com/GTNewHorizons/ExampleMod1.7.10)
(RetroFuturaGradle, Gradle 9).

* **JDK для запуска Gradle:** 17 или новее, рекомендуется 25 (файл `.java-version` шаблона). Мод компилируется
  в байткод Java 8. Нужные JDK, включая Java 8 для `runClient`/`runServer`, Gradle скачает сам
  (toolchains + foojay).
* Нужен доступ в интернет к Maven Forge, Mojang, GTNH (`nexus.gtnewhorizons.com`) и HBM (`maven.ntmr.dev`),
  а также к `files.prismlauncher.org`, `files.minecraftforge.net`, `repo.spongepowered.org`,
  `gregtech.overminddl1.com` (их использует RFG и шаблон GTNH).
* Если Maven Central отвечает `429 Too Many Requests`, просто повторите сборку (скачанное кэшируется), можно с
  `--no-parallel -Dorg.gradle.internal.repository.max.retries=10 -Dorg.gradle.internal.repository.initial.backoff=3000`.
* Нужен git-репозиторий: версия мода берётся из git-тега (у релиза 0.1.0 тег `0.1.0`).
  На коммитах после тега версия будет вида `0.1.0-<ветка>.<N>+<хеш>`.

```bash
./gradlew build
```

Готовый jar появится в `build/libs/` (`ntmwar-<версия>.jar`; `-dev.jar` и `-sources.jar` нужны только для
разработки).

## Запуск в dev-окружении

```bash
./gradlew runClient
```

```bash
./gradlew runServer
```

* Клиент запускается с HBM и NEI. Конфиги, миры и логи лежат в `run/`.
* При старте мод пишет в лог одну итоговую строку. Ожидаемая:
  `NTM: War 0.1.0: registered 12 items, 12 recipes (8 crafting table, 4 HBM Ammo Press), 2 HBM radiation entries`.
* Ник игрока в dev-клиенте задаётся `developmentEnvironmentUserName` в `gradle.properties`.

## Настройки

`config/ntmwar.cfg`:

* `shellRecipesInAmmoPress` (по умолчанию `true`). Если `false`, выстрелы крафтятся на верстаке из тех же
  компонентов (бесформенный рецепт). Пригодится, если сборка использует свой `config/hbmRecipes/ammoPress.json`:
  HBM тогда не вызывает слушатели сторонних модов.

## Структура

```
src/main/java/com/sdstudio/ntmwar/
  NtmWar.java          главный класс (@Mod), только жизненный цикл
  CommonProxy.java     порядок инициализации (общий для клиента и сервера)
  ModConfig.java       настройки
  registry/            ModItems, ModRecipes, MachineRecipe, ModCreativeTab
  item/                ItemBase, ItemComponent (все простые компоненты), ItemShell (все выстрелы)
  ballistics/          ShellType, ShellSpec, ShellSpecs (все параметры выстрелов в одном месте)
  compat/              HbmCompat (единственное место с импортами классов HBM), HbmMaterials (имена OreDictionary)
  client/              ClientProxy, TooltipHelper (только клиентский код)
src/main/resources/assets/ntmwar/
  lang/                en_US.lang, ru_RU.lang
  textures/items/      иконки предметов 16×16
tools/
  gen_placeholder_textures.py   генератор плейсхолдер-иконок (Python + Pillow)
  check_resources.py            проверка текстур и lang-ключей без запуска игры
docs/
  HBM_NOTES.md         заметки по устройству HBM (материалы, API рецептов, радиация, тултипы)
  REPORT_0.1.md        отчёт по версии 0.1: текстуры, рецепты, отступления, чек-лист, планы на 0.2
```

## Иконки

Плейсхолдеры лежат в `src/main/resources/assets/ntmwar/textures/items/<registry name>.png` (16×16, RGBA).
Чтобы поставить свою иконку, замените файл с тем же именем. Генератор не перезаписывает существующие файлы,
пока не передан ключ `--force`:

```bash
python3 tools/gen_placeholder_textures.py
```

Проверить, что у всех предметов есть текстуры и переводы:

```bash
python3 tools/check_resources.py
```
