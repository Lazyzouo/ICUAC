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

ICUAC applies explicit enforcement rules rather than acting as a general-purpose anti-cheat. Depending on configuration, it can block root and nested `execute run` commands, hide blocked commands from client Tab completion, isolate selected game modes by world, kill players who cross configured void or Nether-roof boundaries, clear death drops in selected worlds, confiscate listed materials, remove invalid enchantments or excessive potion effects, repair oversized item stacks, and rate-limit end-crystal attacks.

The NBT module is a rule-based item validator, not a universal raw-NBT scanner, exploit database, or anti-cheat engine. It inspects only information exposed through Paper/Bukkit APIs, including serialized item size, ItemMeta text, PDC keys and values, attributes, selected unsafe component flags, and nested container items. It cannot guarantee detection of every malformed, custom, or version-specific NBT payload. Official presets enable many destructive checks by default; matched items can be removed permanently, and strict rules can conflict with legitimate custom-item plugins. Administrators must review `config.yml`, back up player data, and test compatibility before production use.

The project provides Simplified Chinese and English default packages, Folia-aware scheduling, official generic presets, and an updater that downloads only language-matched GitHub Release assets after verifying GitHub's SHA-256 digest.

> Current version: **2.0.4**
> Tested server version: **Paper/Folia 1.21.11**
> Required Java version: **21**
> Author: **Lazyz**

## Downloads

Download only from [GitHub Releases](https://github.com/Lazyzouo/ICUAC/releases/latest).

**Official download notice:** Install only an exact `en.us.jar` or `zh.cn.jar` asset published by `Lazyzouo/ICUAC`. GitHub's automatically generated Source code archives and files from third-party mirrors are not installable official plugin builds.

| Asset | Default language | Use case |
| --- | --- | --- |
| `ICUAC-2.0.4-zh.cn.jar` | Simplified Chinese (`zh_CN`) | Chinese server administrators |
| `ICUAC-2.0.4-en.us.jar` | English (`en_US`) | English server administrators |

Both plugin JARs contain the same features. The only difference is the first-run language preset. Existing server configuration is preserved during updates.

No source JAR or standalone checksum file is uploaded as a Release asset. The complete source and comments remain available from the [`main`](https://github.com/Lazyzouo/ICUAC/tree/main) branch.

## Highlights

- Root and nested `/execute ... run ...` command protection.
- Client Tab command hiding with live refresh for bypassed players.
- World-based Creative mode isolation.
- Below-bedrock, void, and Nether-roof enforcement.
- World-specific no-drop death rules.
- Banned item enforcement across inventories and containers.
- Serialized item, metadata, PDC, text, attribute, unsafe-flag, and nested-item NBT checks.
- Illegal enchantment removal and oversized stack repair.
- Potion duration and amplifier limits.
- End-crystal attack interval enforcement for every game mode.
- OP-only runtime crystal cooldown bypass.
- Chinese and English user-facing messages.
- SHA-256-verified update downloads from official GitHub Releases.
- Paper/Folia-compatible schedulers.

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

Build output includes only the Chinese-default and English-default JARs under `build/libs`.

## License

ICUAC is released under the [MIT License](LICENSE).

---

# ICUAC 中文说明

**适用于 Paper 与 Folia 的可配置服务端规则执行插件。**

ICUAC 按配置执行明确的限制与处置规则，并不是通用反作弊。根据配置，它可以拦截根命令及嵌套的 `execute run` 命令、从客户端 Tab 补全中隐藏受限命令、按世界隔离指定游戏模式、处置越过虚空或下界基岩顶阈值的玩家、清空指定世界的死亡掉落、没收列表中的违禁材质、移除非法附魔或超限药水效果、修复超量物品堆叠，以及限制末地水晶连续攻击间隔。

NBT 模块属于基于规则的物品检查器，不是通用原始 NBT 扫描器、漏洞库或反作弊引擎。它只检查 Paper/Bukkit API 能读取的信息，包括物品序列化大小、ItemMeta 文本、PDC 键和值、属性、指定的不安全组件标记及嵌套容器物品；无法保证识别所有畸形、自定义或特定版本的 NBT 数据。官方预设默认启用多项具有删除性质的检查，命中规则的物品可能被永久移除，严格配置也可能与合法的自定义物品插件冲突。正式使用前必须检查 `config.yml`、备份玩家数据并在测试服验证兼容性。

项目提供简体中文与英文默认包、Folia 调度兼容、官方通用参数预设，以及只下载当前语言 GitHub Release 资产并校验 GitHub SHA-256 摘要的自动更新器。

> 当前版本：**2.0.4**
> 已测试服务端版本：**Paper/Folia 1.21.11**
> Java 要求：**21**
> 作者：**Lazyz**

## 下载

请只从 [GitHub Releases](https://github.com/Lazyzouo/ICUAC/releases/latest) 下载：

**官方下载声明：** 请只安装由 `Lazyzouo/ICUAC` 发布且文件名精确包含 `en.us.jar` 或 `zh.cn.jar` 的资源。GitHub 自动生成的 Source code 源码压缩包及第三方镜像提供的文件都不是可安装的官方插件构建。

- `ICUAC-2.0.4-zh.cn.jar`：默认简体中文。
- `ICUAC-2.0.4-en.us.jar`：默认英文。

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

- 危险根命令及嵌套 `execute run` 拦截。
- Tab 命令隐藏与白名单命令树即时刷新。
- 创造模式世界隔离。
- 虚空、基岩层下方及下界基岩顶限制。
- 指定世界死亡无掉落。
- 违禁物品、异常 NBT、PDC、属性及嵌套容器检查。
- 非法附魔清理、非法堆叠修复和异常药水移除。
- 所有游戏模式的末地水晶攻击间隔限制。
- OP 可临时切换个人水晶间隔绕过。
- 简体中文与英文消息。
- GitHub Release 自动检查、下载与 SHA-256 校验。

## 自动更新

插件启动时检查官方 GitHub Release，并按当前语言选择对应 JAR。文件通过 GitHub 提供的 SHA-256 摘要校验后才会下载到服务端更新目录，并在下次服务器重启时安装。失败时后台会输出官方下载地址。更新器不会上传配置或玩家数据。

## 兼容范围

- **已测试：Paper/Folia 1.21.11**
- API：1.21
- Java：21
- 其他 1.21.x 可能可用，但不属于已声明测试基线。
- 不支持缺少 Paper API 的纯 Bukkit/Spigot 环境。

完整配置、限制和逻辑请阅读[管理员指南](docs/ADMIN_GUIDE.md)与[配置参考](docs/CONFIGURATION.md)。
