# ICUAC

> [!IMPORTANT]
> **Open-Source and Data Privacy Statement**
>
> ICUAC is a fully open-source project. It contains no backdoors, telemetry, or code that collects or uploads server configuration, player data, world data, logs, or other plugin-generated information. All data created by ICUAC remains on your server's local filesystem. Update checks only request public release metadata and software files published in this GitHub repository; ICUAC operates no separate server for collecting server information.
>
> **开源与数据隐私声明**
>
> ICUAC 是一个彻底开源的项目，不包含后门、遥测，也不存在收集或上传服务器配置、玩家数据、世界数据、日志及其他插件生成资料的代码。ICUAC 创建的所有数据均只保存在你的服务器本地文件系统。更新检查仅请求本 GitHub 仓库公开发布的版本元数据与软件文件；ICUAC 不运营任何用于收集服务器资料的独立服务器。

[![Build](https://github.com/Lazyzouo/ICUAC/actions/workflows/build.yml/badge.svg)](https://github.com/Lazyzouo/ICUAC/actions/workflows/build.yml)
[![CodeQL](https://github.com/Lazyzouo/ICUAC/actions/workflows/codeql.yml/badge.svg)](https://github.com/Lazyzouo/ICUAC/actions/workflows/codeql.yml)
[![Release](https://img.shields.io/github/v/release/Lazyzouo/ICUAC)](https://github.com/Lazyzouo/ICUAC/releases/latest)
[![License](https://img.shields.io/github/license/Lazyzouo/ICUAC)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-ED8B00)](https://adoptium.net/)
[![Tested](https://img.shields.io/badge/Tested-1.21.11-2ea44f)](#compatibility)

**A configurable server-side rule-enforcement plugin for Paper and Folia.**

ICUAC applies configurable server-side rules for commands, player state, locations, items, effects, and end-crystal combat. Each module has its own trigger and result, summarized below.

The project provides Simplified Chinese and English default packages, Folia-aware scheduling, official generic presets, and an updater that downloads only language-matched GitHub Release assets after verifying GitHub's SHA-256 digest.

> Current version: **2.1.4**
> Tested server version: **Paper/Folia 1.21.11**
> Required Java version: **21**
> Author: **Lazyz**

## Downloads

Download only from [GitHub Releases](https://github.com/Lazyzouo/ICUAC/releases/latest).

**Official download notice:** Install only an exact `en.us.jar` or `zh.cn.jar` asset published by `Lazyzouo/ICUAC`. GitHub's automatically generated Source code archives and files from third-party mirrors are not installable official plugin builds.

Official Release retention starts at `v2.1.4`. Releases and matching Git tags older than `v2.1.4` are not retained.

| Asset | Default language | Use case |
| --- | --- | --- |
| `ICUAC-2.1.4-zh.cn.jar` | Simplified Chinese (`zh_CN`) | Chinese server administrators |
| `ICUAC-2.1.4-en.us.jar` | English (`en_US`) | English server administrators |

Both plugin JARs contain the same features. The only difference is the first-run language preset. Existing server configuration is preserved during updates.

No source JAR or standalone checksum file is uploaded as a Release asset. The complete source and comments remain available from the [`main`](https://github.com/Lazyzouo/ICUAC/tree/main) branch.

## Highlights

| Function | What ICUAC does |
| --- | --- |
| Command protection | Cancels configured root commands and nested `execute ... run ...` commands up to the configured parsing depth. Command-block and console enforcement are separate options. |
| Tab command hiding | Removes blocked root commands from the client command tree for players without a bypass and refreshes the tree when ICUAC whitelist membership changes. |
| ICUAC whitelist | Bypasses command/Tab, inventory, item-data/NBT, enchantment, effect, stack, game-mode, and coordinate checks. It does not bypass no-drop death worlds or crystal timing. |
| Game-mode isolation | Changes players in configured source game modes to the target mode when they are outside the allowed worlds; configured ignored modes are skipped. |
| Dangerous coordinates | Sets player health to zero when enabled height rules are crossed below bedrock/void thresholds or above the Nether roof in configured worlds. |
| Death-drop control | Clears item and experience drops when a player dies in a configured no-drop world. |
| Banned materials | Removes listed materials from inventories, off-hands, and opened containers, and blocks prohibited transfers into containers. |
| Item data / NBT | Checks configured serialized-size, metadata text, PDC, display text, attribute, unsafe-flag, and nested-container rules; matching items are removed or their transfer is cancelled. |
| Enchantments | Removes enchantments that exceed vanilla validity rules from checked items. |
| Item stacks | Reduces a stack that exceeds its material maximum back to the allowed amount. |
| Potion effects | Removes an entire active effect when its configured duration or amplifier limit is exceeded. |
| End crystals | Cancels crystal damage attempts made sooner than `crystal.hit-interval` in every game mode; an OP can toggle a runtime-only personal bypass. |
| Languages | Provides Chinese-default and English-default JARs with matching messages and complete official presets. |
| Player messages | Removes leading whitespace from every in-game line at send time so help titles, command entries, warnings, and other plugin text start at the left edge. Text remains bold with a continuous `#00D2FF` → `#3A7BD5` → `#F2C94C` gradient; pure decorative divider lines stay unbold. |
| Console presentation | Uses a 76-character aqua frame without the normal message prefix, centers the `ICUAC ADMINISTRATION` version heading and bilingual security-control subtitle, separates the header with a full-width equals line, and aligns all colored field labels and values in fixed columns. |
| Automatic updates | Checks the official GitHub Release at startup, selects the active-language JAR, verifies GitHub's SHA-256 digest, and stages the file for the next restart. |
| Scheduling | Uses Paper/Folia-compatible global, region, entity, and asynchronous scheduling where each task requires it. |

## Quick Start

1. Install Java 21 and Paper/Folia 1.21.11.
2. Download the preferred language JAR from the latest release.
3. Place it in the server `plugins` directory.
4. Start the server once to generate official defaults.
5. Review `plugins/ICUAC/config.yml` before opening the server to players.
6. Use `/icuac reload` after supported configuration changes.

The official preset is intentionally generic. Personal server parameters remain in the runtime `plugins/ICUAC/config.yml` and are not part of this repository.

## Commands

| Command | Access | Description |
| --- | --- | --- |
| `/icuac help` | OP | Display administration help. |
| `/icuac add <player>` | OP | Add a player to the global ICUAC bypass list. |
| `/icuac remove <player>` | OP | Remove a player from the bypass list. |
| `/icuac list` | OP | List bypassed player names. |
| `/icuac reload` | OP | Reload configuration, language, whitelist, and security modules. |
| `/crystalreload` | `crystalpvp.reload` | Reload the end-crystal attack interval. |
| `/crystallimit bypass` | OP | Toggle personal end-crystal cooldown bypass. |

## Languages

Set the runtime language in `plugins/ICUAC/config.yml`:

```yaml
language: en_US # en_US or zh_CN
```

English messages are extracted to `plugins/ICUAC/lang/en_US.yml` and can be customized. Chinese mode preserves the message section in the server configuration for backward compatibility.

## Automatic Updates

Official presets enable update checking and automatic download:

```yaml
updates:
  enabled: true
  auto-download: true
```

At startup ICUAC checks the latest public GitHub Release and selects the JAR matching the active language. It verifies the SHA-256 digest supplied by GitHub before staging the file. Paper/Bukkit installs it from the update directory on the next server restart. Failures print the official Releases URL for manual recovery.

Console notices retain the same blue ICUAC prefix and use aqua, green, yellow, or red text to distinguish checking, success, availability, and failure states. All notice text is bold.

No configuration or player data is uploaded by the updater.

## Compatibility

- **Tested:** Paper and Folia **1.21.11**
- **API declaration:** `1.21`
- **Java:** 21
- Other 1.21.x builds may work, but are not part of the stated tested baseline.
- Bukkit/Spigot without Paper APIs is not supported.

## Documentation

- [Administrator Guide / 管理员指南](docs/ADMIN_GUIDE.md)
- [Configuration Reference / 配置参考](docs/CONFIGURATION.md)
- [Changelog / 更新日志](CHANGELOG.md)
- [Support / 支持](SUPPORT.md)
- [Security Policy / 安全政策](SECURITY.md)
- [Contributing / 贡献指南](CONTRIBUTING.md)

## Building

```bash
./gradlew clean build
```

Build output includes only `ICUAC-<version>-zh.cn.jar` and `ICUAC-<version>-en.us.jar` under `build/libs`. Official Release assets keep those exact names and are never renamed during upload.

## License

ICUAC is released under the [MIT License](LICENSE).

---

# ICUAC 中文说明

**适用于 Paper 与 Folia 的可配置服务端规则执行插件。**

ICUAC 针对命令、玩家状态、坐标、物品、药水效果和末地水晶战斗执行可配置的服务端规则。每个模块的触发条件与处置结果如下。

项目提供简体中文与英文默认包、Folia 调度兼容、官方通用参数预设，以及只下载当前语言 GitHub Release 资产并校验 GitHub SHA-256 摘要的自动更新器。

> 当前版本：**2.1.4**
> 已测试服务端版本：**Paper/Folia 1.21.11**
> Java 要求：**21**
> 作者：**Lazyz**

## 下载

请只从 [GitHub Releases](https://github.com/Lazyzouo/ICUAC/releases/latest) 下载：

**官方下载声明：** 请只安装由 `Lazyzouo/ICUAC` 发布且文件名精确包含 `en.us.jar` 或 `zh.cn.jar` 的资源。GitHub 自动生成的 Source code 源码压缩包及第三方镜像提供的文件都不是可安装的官方插件构建。

官方 Release 从 `v2.1.4` 起保留；早于 `v2.1.4` 的 Release 及对应 Git 标签不再保留。

- `ICUAC-2.1.4-zh.cn.jar`：默认简体中文。
- `ICUAC-2.1.4-en.us.jar`：默认英文。

两个插件 JAR 功能完全一致，仅首次生成配置时的默认语言不同。自动更新不会覆盖服务器已有配置。

Release 不再额外上传源码 JAR 或独立校验文件。完整源码与注释继续保留在 [`main`](https://github.com/Lazyzouo/ICUAC/tree/main) 分支。

## 快速安装

1. 安装 Java 21 和 Paper/Folia 1.21.11。
2. 从最新 Release 下载所需语言版本。
3. 将 JAR 放入服务器 `plugins` 目录。
4. 启动一次服务器生成官方默认配置。
5. 开服前检查 `plugins/ICUAC/config.yml`。
6. 修改支持热重载的配置后执行 `/icuac reload`。

仓库只维护官方参数预设。服务器的个人世界名、阈值和消息配置位于运行目录，不会由 GitHub 仓库同步。

## 主要功能

| 功能 | ICUAC 的实际处理 |
| --- | --- |
| 命令保护 | 取消配置中的根命令及指定解析深度内嵌套的 `execute ... run ...` 命令；命令方块与控制台是否受限可分别配置。 |
| Tab 命令隐藏 | 对没有绕过资格的玩家从客户端命令树移除受限根命令；ICUAC 白名单变化时立即刷新命令树。 |
| ICUAC 白名单 | 绕过命令/Tab、背包、物品数据/NBT、附魔、药水、堆叠、游戏模式和坐标检查；不会绕过指定世界死亡无掉落及水晶攻击间隔。 |
| 游戏模式隔离 | 玩家以配置的来源模式进入非允许世界时切换为目标模式；处于忽略模式时不处理。 |
| 危险坐标 | 玩家越过已启用的基岩层下方、虚空或配置世界下界基岩顶高度时，将其生命值设为零。 |
| 死亡掉落 | 玩家在配置的无掉落世界死亡时，清空物品与经验掉落。 |
| 违禁材质 | 从背包、副手和已打开容器中移除列表内材质，并阻止把违禁物品转移进容器。 |
| 物品数据 / NBT | 按配置检查序列化大小、元数据文本、PDC、显示文本、属性、不安全标记和嵌套容器；命中时删除物品或取消转移。 |
| 附魔 | 从被检查物品上移除超出原版有效规则的附魔。 |
| 物品堆叠 | 将超过材质最大堆叠上限的数量修正到允许值。 |
| 药水效果 | 生效时间或效果等级超过配置上限时，移除整个对应效果。 |
| 末地水晶 | 所有游戏模式中，两次攻击短于 `crystal.hit-interval` 时取消水晶伤害；OP 可临时切换仅对自己的运行期绕过。 |
| 语言 | 提供中文默认和英文默认 JAR，两者带有对应消息与完整官方预设。 |
| 游戏内消息 | 每一行玩家可见消息在发送时移除行首空白，使帮助标题、命令项、警告及其他插件文字均从左边缘开始。文字继续强制粗体并应用连续的 `#00D2FF` → `#3A7BD5` → `#F2C94C` 渐变，纯装饰分割线保持非粗体。 |
| 后台显示 | 启动横幅使用内部宽度 76 的青色边框且不附加普通消息前缀；`ICUAC ADMINISTRATION` 版本主标题与中英安全管理副标题居中，完整等号线分隔标题区，彩色字段标签和值按固定列左对齐。 |
| 自动更新 | 启动时检查官方 GitHub Release，选择当前语言 JAR，校验 GitHub SHA-256 摘要并放入下次重启使用的更新目录。 |
| 调度兼容 | 按任务用途使用兼容 Paper/Folia 的全局、区域、实体与异步调度器。 |

## 自动更新

插件启动时检查官方 GitHub Release，并按当前语言选择对应 JAR。文件通过 GitHub 提供的 SHA-256 摘要校验后才会下载到服务端更新目录，并在下次服务器重启时安装。失败时后台会输出官方下载地址。更新器不会上传配置或玩家数据。

后台通知始终使用同一套蓝色 ICUAC 前缀，并以青、绿、黄、红区分检查、成功、发现新版及失败状态；所有通知文字均以粗体显示。

## 兼容范围

- **已测试：Paper/Folia 1.21.11**
- API：1.21
- Java：21
- 其他 1.21.x 可能可用，但不属于已声明测试基线。
- 不支持缺少 Paper API 的纯 Bukkit/Spigot 环境。

完整配置、限制和逻辑请阅读[管理员指南](docs/ADMIN_GUIDE.md)与[配置参考](docs/CONFIGURATION.md)。
