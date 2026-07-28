# ICUAC

[![Build](https://github.com/Lazyzouo/ICUAC/actions/workflows/build.yml/badge.svg)](https://github.com/Lazyzouo/ICUAC/actions/workflows/build.yml)
[![CodeQL](https://github.com/Lazyzouo/ICUAC/actions/workflows/codeql.yml/badge.svg)](https://github.com/Lazyzouo/ICUAC/actions/workflows/codeql.yml)
[![Release](https://img.shields.io/github/v/release/Lazyzouo/ICUAC)](https://github.com/Lazyzouo/ICUAC/releases/latest)
[![License](https://img.shields.io/github/license/Lazyzouo/ICUAC)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-ED8B00)](https://adoptium.net/)
[![Tested](https://img.shields.io/badge/Tested-1.21.11-2ea44f)](#compatibility)

**A bilingual security and rule-enforcement core for Paper and Folia servers.**

ICUAC protects commands, inventories, NBT data, game modes, dangerous locations, potion effects, item stacks, and end-crystal attack timing. It ships with Simplified Chinese and English editions, official safe presets, Folia-aware scheduling, and a verified GitHub auto-updater.

> Current version: **2.0.0**
> Tested server version: **Paper/Folia 1.21.11**
> Required Java version: **21**
> Author: **Lazyz**

## Downloads

Download only from [GitHub Releases](https://github.com/Lazyzouo/ICUAC/releases/latest).

| Asset | Default language | Use case |
| --- | --- | --- |
| `ICUAC-2.0.0.jar` | Simplified Chinese (`zh_CN`) | Chinese server administrators |
| `ICUAC-2.0.0-en_US.jar` | English (`en_US`) | English server administrators |
| `ICUAC-2.0.0-sources.jar` | Source code | Auditing and development |
| `*.sha256` | N/A | Release integrity verification |

Both plugin JARs contain the same features. The only difference is the first-run language preset. Existing server configuration is preserved during updates.

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

At startup ICUAC checks the latest public GitHub Release. A newer JAR is downloaded only when its matching `.sha256` asset is present and valid. Paper/Bukkit installs it from the update directory on the next server restart. Failures print the official Releases URL for manual recovery.

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

Build output includes the Chinese JAR, English JAR, and source JAR under `build/libs`.

## License

ICUAC is released under the [MIT License](LICENSE).

---

# ICUAC 中文说明

**适用于 Paper 与 Folia 的双语服务器安全与规则控制核心。**

ICUAC 提供命令、背包、NBT、游戏模式、危险坐标、药水、物品堆叠和末地水晶攻击间隔保护，并内置简体中文及英文版本、官方安全预设、Folia 调度兼容和经过 SHA-256 校验的 GitHub 自动更新。

> 当前版本：**2.0.0**
> 已测试服务端版本：**Paper/Folia 1.21.11**
> Java 要求：**21**
> 作者：**Lazyz**

## 下载

请只从 [GitHub Releases](https://github.com/Lazyzouo/ICUAC/releases/latest) 下载：

- `ICUAC-2.0.0.jar`：默认简体中文。
- `ICUAC-2.0.0-en_US.jar`：默认英文。
- `ICUAC-2.0.0-sources.jar`：源码包。
- `*.sha256`：发行文件完整性校验。

两个插件 JAR 功能完全一致，仅首次生成配置时的默认语言不同。自动更新不会覆盖服务器已有配置。

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

插件启动时检查官方 GitHub Release。发现新版本后会校验对应 SHA-256 并下载到服务端更新目录，下次服务器重启时安装。失败时后台会输出官方下载地址。更新器不会上传配置或玩家数据。

## 兼容范围

- **已测试：Paper/Folia 1.21.11**
- API：1.21
- Java：21
- 其他 1.21.x 可能可用，但不属于已声明测试基线。
- 不支持缺少 Paper API 的纯 Bukkit/Spigot 环境。

完整配置、限制和逻辑请阅读[管理员指南](docs/ADMIN_GUIDE.md)与[配置参考](docs/CONFIGURATION.md)。
