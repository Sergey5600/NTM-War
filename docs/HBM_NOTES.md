# Заметки по HBM's Nuclear Tech Mod (разведка для NTM: War 0.1)

Исследованная версия: **1.0.27_X5863**. Это последний релизный тег в
`github.com/HbmMods/Hbm-s-Nuclear-Tech-GIT`, `RefStrings.VERSION = "1.0.27 BETA (5863)"`.
Все API ниже совпадают у тега и у текущего master (проверено `git diff`).
Исходники HBM смотрели в отдельном клоне вне проекта (`../ext/hbm-src`), в репозиторий NTM: War они не попадали.

## 0. Как подключать HBM как зависимость

* Официальный Maven HBM: `https://maven.ntmr.dev/releases` (описан в README HBM, раздел "Using in your project").
* Координаты: `com.hbm:HBM-NTM:1.0.27_X<build>:dev`. Классификатор `dev` означает уже деобфусцированный jar
  с MCP-именами, поэтому `rfg.deobf` не нужен. Есть ещё `:src` с исходниками.
* В манифесте dev-jar'а есть `FMLAT: HBM_at.cfg`. Этот AT открывает `EntityLivingBase *`, поля `KeyBinding`,
  `EntityPlayer.itemInUseCount` и т.д. В dev-окружении его нужно применить к деобфускации Minecraft, иначе код HBM
  получит `IllegalAccessError`. В RFG для этого есть штатная опция
  `minecraft { useDependencyAccessTransformers.set(true) }` (см. `addon.gradle`): RFG сам читает FMLAT из jar'ов
  compileClasspath. Сам файл AT мы не копируем.
* POM HBM тянет старые `codechicken:*` артефакты, поэтому подключаем с `transitive = false`.
* Запасной вариант: CurseForge `hbms-nuclear-tech-mod` (project id 235439) через CurseMaven + `rfg.deobf(...)`.

## 1. modid, регистрация предметов, базовый класс

* **modid: `hbm`** (`com.hbm.lib.RefStrings.MODID`), главный класс `com.hbm.main.MainRegistry`.
  У `@Mod` нет поля `dependencies`.
* Предметы — `public static Item` в `com.hbm.items.ModItems`. Создаются в `ModItems.initializeItem()`,
  регистрируются в `ModItems.registerItem()` вызовом
  `GameRegistry.registerItem(item, item.getUnlocalizedName())`. Отсюда registry name вида **`hbm:item.cordite`**,
  а не `hbm:cordite`. Всё это происходит в preInit HBM (`ModItems.mainRegistry()`).
* **Общего базового класса предметов нет.** Простые предметы создаются как `new Item()`. Есть набор
  специализированных классов: `ItemEnumMulti` (мета-предметы по enum, например `casing`, `ammo_standard`),
  `ItemCustomLore`, `ItemGenericPart`, `ItemAutogen` (автогенерация форм материалов) и т.д.
  NTM: War от них не наследуется: правило «классы HBM только в compat», к тому же они нам не нужны.

## 2. Материалы (OreDictionary)

Имена строятся в `com.hbm.inventory.OreDictManager` (DictFrame) + `inventory/material/MaterialShapes`
(префиксы `ingot`, `plate`, `plateTriple`, `plateSextuple`, `block`, `dust`, …).

| Что нужно | Имя в OreDictionary | Предмет HBM | Примечание |
|---|---|---|---|
| Вольфрам | `ingotTungsten` | `ingot_tungsten` | Обычной пластины у вольфрама нет, только литая `plateTripleTungsten` (литейка) |
| U-238 (обеднённый уран) | `ingotUranium238` (синоним `ingotU238`) | `ingot_u238` | Радиоактивен: 0.25 RAD/с на слиток |
| Медь | `plateCopper` | `plate_copper` | Её же HBM кладёт в кумулятивную ракету |
| Сталь | `plateSteel`, `ingotSteel`, `blockSteel` | `plate_steel`, `ingot_steel`, `block_steel` | Есть `plateTripleSteel`/`plateSextupleSteel` (литая/сварная) |
| Алюминий | `plateAluminum` | `plate_aluminium` | В словаре американское написание Aluminum |
| Бездымный порох | `dustAnySmokeless` | `cordite`, `ballistite` | `ANY_SMOKELESS`. Есть ещё `dustAnyPropellant` (+ чёрный порох) |
| ВВ | `ingotAnyHighexplosive` | `ball_tnt` (ТНТ), `ball_tatb` (TATB) | `ANY_HIGHEXPLOSIVE`. Пластит (семтекс/C4) — `ingotAnyPlasticexplosive`, это более поздняя прогрессия |

**Выбор ВВ:** `ingotAnyHighexplosive` (ТНТ/TATB). Его же HBM использует в кумулятивной ракете РПГ и в 40-мм
гранатах, так что по прогрессии это ровно уровень Ammo Press. Пластит оставлен для будущих мощных боеприпасов.

## 3. Кумулятивная ракета для РПГ

* Предмет: `ammo_standard` с мета `EnumAmmo.ROCKET_HEAT` (`com.hbm.items.weapon.sedna.factory.GunFactory`).
  Название «Rocket, Shaped Charge» / «Ракета (Кумулятивный)».
* Где делается: **Ammo Press** (`tile.machine_ammo_press`, «Пресс для патронов»). Сетка 3×3,
  рецепты позиционные: игрок выбирает рецепт в GUI, и слоты принимают только ингредиенты этого рецепта.
* Рецепт (`com.hbm.inventory.recipes.AmmoPressRecipes.registerDefaults`), на выходе **2 ракеты**:

  ```
  plateCopper            ingotAnyHighexplosive   —
  —                      casing:LARGE            —      (большая гильза из пушечной бронзы)
  —                      3× dustAnySmokeless      —      (или 1× rocket_fuel)
  ```
* API для сторонних модов:
  * `com.hbm.util.CompatExternal.registerRecipeRegisterListener(IRecipeRegisterListener)`. Регистрировать до
    postInit HBM (HBM грузит рецепты машин в своём postInit через `SerializableRecipe.initialize()`).
  * В слушателе `onRecipeLoad(String recipeClassName)` для `"AmmoPressRecipes"` вызывать
    `com.hbm.util.CompatRecipeRegistry.registerAmmoPress(ItemStack output, AStack[9] input)`.
  * Ингредиенты: `RecipesCommon.OreDictStack(name, count)` / `RecipesCommon.ComparableStack(item, count, meta)`.
  * Слушатель обязателен. HBM пересобирает рецепты при старте, по `/ntmreload` и при получении рецептов
    с сервера. Если добавить рецепт напрямую в `AmmoPressRecipes.recipes`, после перезагрузки он пропадёт.
  * **Ограничение:** если в `config/hbmRecipes/` включён пользовательский `ammoPress.json` (без подчёркивания),
    HBM читает рецепты из файла и слушатели **не вызываются**. Тогда наши выстрелы нужно дописать в этот JSON
    вручную или переключить NTM: War на верстак (`shellRecipesInAmmoPress=false`). NTM: War пишет об этом
    предупреждение в лог.
* NEI: у HBM есть `handler/nei/AmmoPressHandler`, он читает `AmmoPressRecipes.recipes`, так что наши рецепты
  попадут туда автоматически.

## 4. Радиоактивность предметов

* Система опасностей: `com.hbm.hazard.HazardSystem`, значения в `com.hbm.hazard.HazardRegistry`.
* Для стороннего предмета:
  `HazardSystem.register(item, new HazardData().addEntry(HazardRegistry.RADIATION, radsPerSecond))`.
  Ключом может быть `Item`, `ItemStack`, `ComparableStack` или имя OreDictionary (`String`).
* HBM сам облучает игрока с таким предметом в инвентаре и сам добавляет строку «[Радиоактивно] … RAD/s»
  в тултип через свой обработчик событий.
* Эталоны: `HazardRegistry.u238 = 0.25`, `ingot = 1.0`, `billet = 0.5`, `nugget = 0.1`.
  Слиток U-238 даёт 0.25 RAD/с.
* В NTM: War: сердечник ОУ `u238 × 0.4 = 0.1` RAD/с, выстрел с ОУ `u238 × 0.3 = 0.075` RAD/с.
* Регистрировать после preInit HBM (свою таблицу HBM заполняет в preInit). У нас это preInit после регистрации
  предметов.

## 5. Тултипы боеприпасов

* Описания HBM лежат в lang-ключах `<unlocalizedName>.desc`, строки разделяются символом `$`
  (`ItemEnumMulti.addInformation`, `ItemCustomLore`).
* Свойства патронов (`ItemAmmo`) раскрашены по категориям: плюсы — `§9` (BLUE), нейтральные — `§e` (YELLOW),
  минусы — `§c` (RED), прочее — `§8` (DARK_GRAY). Альтернативное имя курсивом.
* Подсказка Shift: `desc.misc.lshift=§8Hold <§e§oLSHIFT§8§o> %s` (ru: `§8Удерживайте <§e§oLShift§8§o> %s`).
* Оружие (`ItemGunBaseNT`) показывает характеристики строками «Название: значение».
* NTM: War повторяет этот стиль своими ключами: калибр `§e`, тип снаряда своим цветом, параметры по Shift `§9`,
  пометка о предварительных значениях `§8§o`.

## 6. Готовые компоненты HBM

| Нужный компонент | Что есть в HBM | Решение |
|---|---|---|
| Гильза | `casing` (`SMALL`/`LARGE`/`*_STEEL`/`SHOTSHELL`…): гильзы стрелкового калибра и 40-мм, делаются прессом из пушечной бронзы/оружейной стали | Для 125 мм не подходят по смыслу. Метательный заряд 125 мм — свой предмет (сгораемая гильза, как у реальных 125-мм выстрелов) |
| Пороховой заряд | Нет: HBM кладёт порох (`dustAnySmokeless`) прямо в рецепт пресса | Свой `propellant_charge_125` из пороха HBM |
| Корпус снаряда | `shell` (ItemAutogen, `shellSteel` и т.п.) — конструкционная «обечайка» из 4 слитков для машин и ракет, не корпус снаряда | Свой `shell_body_125` |
| Ведущее устройство | Нет | Свой `sabot` |
| Взрыватель | `fuse` (электрический предохранитель для машин), `safety_fuse` (огнепроводный шнур) — не взрыватели боеприпасов | В 0.1 взрыватель не нужен |
| Броня / ДЗ | `insert_era`, `plate_armor_*` — вставки и пластины для брони игрока | Для техники свои `armor_plate_rha`, `era_tile` |

Итог: для пунктов 3, 4 и 6 ТЗ подходящих аналогов в HBM нет, поэтому сделаны свои предметы.

## 7. Прочее, полезное на будущее (0.2+)

* `CompatExternal`: регистрация целей турелей, кастомные боеголовки ракет (`setWarheadImpact`) — пригодится
  для взаимодействия техники с ПВО HBM.
* Взрывы HBM: `com.hbm.explosion` (`ExplosionNT`, `ExplosionVNT`) — кандидаты для заброневого действия и ОФС.
* Новая оружейная система HBM «sedna» (`items/weapon/sedna`, `BulletConfig`) задаёт урон, пробитие брони
  (`setThresholdNegation`, `setArmorPiercing`) и гильзы. Это референс по балансу урона против брони игрока.
