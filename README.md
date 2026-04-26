# AbanGUI (AdvancedBanGUI)

GUI addon for [AdvancedBan](https://www.spigotmc.org/resources/advancedban.8695/) — manage punishments through a chest menu instead of typing commands.

Paper 1.21+ • Java 21

**[English](#english)** | **[Русский](#russian)**

---

<a name="english"></a>
## English

### What it does

`/abangui` → paginated list of player heads → click a player → pick an action → choose a reason → done. The plugin builds and dispatches the AdvancedBan command automatically.

Available actions: **Ban, TempBan, Mute, TempMute, Warn, TempWarn, Unban, Unmute, Unwarn, History**.

Reasons are loaded from `reasons.yml` which has a built-in guide so you can customize everything without reading external docs.

### Requirements

- Paper 1.21+
- Java 21+
- AdvancedBan 2.3.0+

### Setup

1. Grab the jar from releases or [build it yourself](#building)
2. Drop into `plugins/`, make sure AdvancedBan is there too
3. Restart the server
4. Edit configs in `plugins/AbanGUI/` to your liking
5. `/abangui reload` to apply changes on the fly

### Commands

| Command | Description | Permission |
|---------|------------|------------|
| `/abangui` | Opens the player list | `abangui.use` |
| `/abangui reload` | Reloads all configs | `abangui.reload` |
| `/abangui lang <code>` | Switches the active language and saves it to `config.yml` (tab-completes from available `messages_*.yml` files) | `abangui.reload` |
| `/abangui reasons list` | Lists every reason currently loaded into memory (key, command, duration, display) | `abangui.reload` |
| `/abangui reasons reset` | Backs up the current `reasons.yml` and re-extracts a fresh copy from the jar | `abangui.reload` |

Alias: `/agui`

### Permissions

| Node | Grants | Default |
|------|--------|---------|
| `abangui.*` | Full access to everything | op |
| `abangui.use` | Open the menu | op |
| `abangui.ban` | Ban | op |
| `abangui.tempban` | Temp ban | op |
| `abangui.mute` | Mute | op |
| `abangui.tempmute` | Temp mute | op |
| `abangui.warn` | Warn | op |
| `abangui.tempwarn` | Temp warn | op |
| `abangui.unban` | Unban | op |
| `abangui.unmute` | Unmute | op |
| `abangui.unwarn` | Clear warns | op |
| `abangui.history` | View history | op |
| `abangui.reload` | Reload configs | op |

### Config files

| File | What's inside |
|------|--------------|
| `config.yml` | Language, GUI row sizes, players per page |
| `reasons.yml` | Punishment reasons — fully customizable, guide included in the file |
| `messages_en.yml` | English translations |
| `messages_ru.yml` | Russian translations |

Need another language? Copy `messages_en.yml`, rename to `messages_xx.yml`, translate the strings, set `language: "xx"` in `config.yml`. Done.

### How it works

- Player heads use `SkullMeta.setOwningPlayer()` for proper skin rendering
- Item metadata stored via `PersistentDataContainer` — no display name string matching
- Punishments dispatched as console commands following AdvancedBan syntax
- History actions (`/check` + `/history`) run as the player so they see output in chat
- Online players listed first, then offline, both sorted alphabetically

---

<a name="russian"></a>
## Русский

### Что делает

`/abangui` → список игроков с пагинацией → клик по игроку → выбор действия → выбор причины → готово. Плагин сам собирает и выполняет команду AdvancedBan.

Доступные действия: **Ban, TempBan, Mute, TempMute, Warn, TempWarn, Unban, Unmute, Unwarn, History**.

Причины загружаются из `reasons.yml` — внутри файла есть подробный гайд на русском, так что разберётесь без сторонних доков.

### Зависимости

- Paper 1.21+
- Java 21+
- AdvancedBan 2.3.0+

### Установка

1. Скачать jar из релизов или [собрать самому](#сборка)
2. Кинуть в `plugins/`, AdvancedBan тоже должен стоять
3. Рестарт сервера
4. Подкрутить конфиги в `plugins/AbanGUI/`
5. `/abangui reload` чтобы применить изменения без рестарта

### Команды

| Команда | Описание | Право |
|---------|----------|-------|
| `/abangui` | Открыть список игроков | `abangui.use` |
| `/abangui reload` | Перезагрузить конфиги | `abangui.reload` |
| `/abangui lang <код>` | Переключить язык и сохранить выбор в `config.yml` (таб-комплит подсказывает доступные коды по файлам `messages_*.yml`) | `abangui.reload` |
| `/abangui reasons list` | Вывести в чат все причины, которые сейчас загружены в память (ключ, команда, длительность, отображение) | `abangui.reload` |
| `/abangui reasons reset` | Создать бэкап текущего `reasons.yml` и заново выложить свежий из jar | `abangui.reload` |

Алиас: `/agui`

### Права

| Право | Даёт | По умолчанию |
|-------|------|-------------|
| `abangui.*` | Полный доступ | op |
| `abangui.use` | Открыть меню | op |
| `abangui.ban` | Бан | op |
| `abangui.tempban` | Временный бан | op |
| `abangui.mute` | Мьют | op |
| `abangui.tempmute` | Временный мьют | op |
| `abangui.warn` | Варн | op |
| `abangui.tempwarn` | Временный варн | op |
| `abangui.unban` | Разбан | op |
| `abangui.unmute` | Размьют | op |
| `abangui.unwarn` | Снять варны | op |
| `abangui.history` | История наказаний | op |
| `abangui.reload` | Перезагрузка конфигов | op |

### Конфиги

| Файл | Что внутри |
|------|-----------|
| `config.yml` | Язык, размеры менюшек, игроков на страницу |
| `reasons.yml` | Причины наказаний — настраиваются как угодно, гайд внутри файла |
| `messages_en.yml` | Английский перевод |
| `messages_ru.yml` | Русский перевод |

Нужен другой язык? Копируешь `messages_en.yml` → переименовываешь в `messages_xx.yml` → переводишь → ставишь `language: "xx"` в `config.yml`.

---

<a name="building"></a>
<a name="сборка"></a>
## Building / Сборка

```bash
git clone https://github.com/d2glaidee/AbanGUI.git
cd AbanGUI
./gradlew jar
