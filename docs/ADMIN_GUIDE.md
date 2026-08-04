# ICUAC Administrator Guide / ICUAC 管理员指南

> Version: 2.2.0
> Tested: Paper/Folia 1.21.11
> Java: 21

## English

### Open Source And Data Privacy

ICUAC is fully open source and contains no backdoors or telemetry. Server configuration, player data, world data, logs, and other plugin-generated information are never collected or uploaded. All ICUAC-created data remains on the server's local filesystem. The updater only requests public metadata and release files from GitHub and does not communicate with an ICUAC-operated data-collection server.

### Operational Model

ICUAC combines preventive checks with destructive remediation. Depending on the module, it may cancel an event, remove an item, strip an enchantment, clamp a stack size, remove a potion effect, switch game mode, or kill a player who crosses a configured world boundary. Back up player data before enabling strict production settings.

### Runtime Files

- `plugins/ICUAC/config.yml`: personal runtime parameters and Chinese-compatible messages.
- `plugins/ICUAC/whitelist.yml`: ICUAC global bypass identities.
- `plugins/ICUAC/lang/en_US.yml`: editable English messages, generated when English mode is used.
- Server update directory: receives a verified new JAR when auto-download succeeds.

Repository presets are official generic defaults. They are not intended to contain a specific server's world names or private settings.

### Configuration Migration

Never delete `plugins/ICUAC/config.yml` when updating ICUAC. Replace the JAR and restart the server; startup and `/icuac reload` both run the same schema-aware migration.

- `config-version` is managed by ICUAC and records the public configuration schema.
- Existing values, lists, messages, explicit empty strings, comments, and custom keys are preserved. Only missing official defaults and missing official comments are added.
- Migration selects the bundled Chinese or English template from the existing `language` value, regardless of which language JAR was installed for the update.
- A legacy boolean at a path whose new official section contains `enabled` is moved to that `enabled` child without changing its value.
- Before any write, the exact original file is copied to `plugins/ICUAC/backups/config-before-v<schema>-<timestamp>-<id>.yml`.
- The updated YAML is written to a temporary file, parsed again for validation, and atomically replaces `config.yml` where the filesystem supports it.
- Invalid YAML or a write failure leaves the original file in place and prevents startup/reload from continuing with a partially migrated configuration.
- An unknown scalar/section conflict is preserved and reported; ICUAC does not guess or overwrite it. Obsolete custom or former keys are not deleted automatically.
- A configuration with a higher `config-version` than the running plugin is preserved without downgrade writes.

### Release Authenticity

Use only the exact `ICUAC-<version>-en.us.jar` or `ICUAC-<version>-zh.cn.jar` asset from the official `Lazyzouo/ICUAC` GitHub Release. Source code archives are not installable plugin JARs. Renamed files, modified builds, third-party mirrors, and reuploads are outside the official distribution and cannot be authenticated by ICUAC's release process. Official automation validates these two exact build names and uploads them without renaming; missing, renamed, additional, source, alias, or checksum assets cause the Release build to fail.

### Whitelist Scope

The ICUAC whitelist bypasses command blocking/Tab hiding, inventory security checks, NBT, enchantment, effect and stack checks, game-mode isolation, and dangerous-coordinate checks. It does **not** bypass no-drop death worlds or end-crystal attack timing.

Adding or removing an online player refreshes the client's command tree immediately.

### Enforcement Summary

| Module | Trigger | Result |
| --- | --- | --- |
| Command protection | Blocked root or nested execute command | Cancel command and notify sender |
| Creative isolation | Watched mode outside allowed worlds | Switch to configured target mode |
| Below-bedrock / Nether roof | Move or teleport beyond threshold | Set player health to zero |
| Death drops | Death in configured world | Clear item and XP drops |
| Banned items / illegal NBT | Inventory and interaction checks | Remove item or cancel transfer |
| Illegal enchantments | Enchantment exceeds vanilla validity | Remove invalid enchantments |
| Illegal stacks | Amount exceeds material maximum | Clamp to material maximum |
| Illegal effects | Duration or amplifier exceeds limit | Remove entire effect |
| Crystal interval | Crystal hit occurs too soon | Cancel damage event |

### End-Crystal Logic

`crystal.hit-interval` is measured in milliseconds and applies to all game modes. `/crystallimit bypass` toggles a runtime-only OP bypass for the command sender. The bypass is cleared when the plugin stops and is ineffective while that player is not OP.

### Reload Boundaries

Use `/icuac reload` for normal configuration changes. It first performs the same safe configuration migration used at startup, then reloads language, whitelist, game-mode settings, item/NBT/effect/stack settings and crystal timing. The global inventory scheduler period is created only during plugin startup; changing `global-settings.inventory-check-interval-ticks` requires a restart.

`/crystalreload` reloads the YAML file and crystal interval, but does not explicitly rebuild every cached module. Use `/icuac reload` for changes outside the crystal section.

### Automatic Updates

At startup the updater calls the official `Lazyzouo/ICUAC` latest-release endpoint. It selects `ICUAC-<version>-en.us.jar` for English mode or `ICUAC-<version>-zh.cn.jar` for Chinese mode, and requires GitHub's asset metadata to contain a valid matching SHA-256 digest. The file is staged for the next restart. Network, API, asset, write, or checksum failures are logged with the official Releases URL.

### Console Presentation

Startup prints a bilingual banner with an internal width of 76 characters. The aqua top, middle, and bottom borders are full-width equals lines. The `ICUAC ADMINISTRATION` version heading and `SERVER SECURITY CONTROL / 服务器安全管理` subtitle are centered using CJK-aware display widths; version, author, tested baseline, language, GitHub, and privacy rows are left-aligned, their labels are padded to a fixed 14-character display width, and their colons share one column. Banner rows are sent without the normal `[ICUAC] »` prefix so the frame remains aligned; startup status and updater notices retain that prefix and their semantic colors. Every player-visible line has leading whitespace removed at send time, so help titles, command entries, warnings, and other plugin messages begin at the left edge even when an existing configuration contains indentation. Player text remains bold with the continuous `#00D2FF` → `#3A7BD5` → `#F2C94C` gradient, while pure decorative dividers remain unbold.

### Deployment Checklist

1. Back up configuration, whitelist, worlds, and player data.
2. Confirm Java 21 and Paper/Folia 1.21.11.
3. Review official world placeholders such as `creative_world`.
4. Test normal players, OPs, and ICUAC-whitelisted players separately.
5. Verify other plugins that use custom NBT, PDC, models, attributes, or item rarity.
6. Review logs after startup and after `/icuac reload`.
7. Keep only trusted identities in the ICUAC whitelist.

---

## 中文

### 开源与数据隐私

ICUAC 完全开源，不包含后门或遥测。服务器配置、玩家数据、世界数据、日志及其他插件生成资料均不会被收集或上传；ICUAC 创建的所有数据只保存在服务器本地文件系统。更新器只向 GitHub 请求公开版本元数据与发行文件，不会连接任何由 ICUAC 运营的数据收集服务器。

### 运行逻辑

ICUAC 同时包含预防拦截与破坏性处置。不同模块可能取消事件、删除物品、移除附魔、修正堆叠、清除药水、切换游戏模式，或直接将越界玩家生命值设为零。正式启用严格配置前请备份玩家数据。

### 运行文件

- `plugins/ICUAC/config.yml`：服务器个人参数与兼容中文消息。
- `plugins/ICUAC/whitelist.yml`：ICUAC 全局豁免名单。
- `plugins/ICUAC/lang/en_US.yml`：英文模式生成的可编辑消息。
- 服务端更新目录：自动更新校验成功后存放待安装 JAR。

仓库中的配置是官方通用预设，不应保存具体服务器的世界名或私人参数。

### 配置自动迁移

更新 ICUAC 时不要删除 `plugins/ICUAC/config.yml`。只需替换 JAR 并重启服务器；插件启动和 `/icuac reload` 都会执行同一套按结构迁移。

- `config-version` 由 ICUAC 自动维护，用于标记公开配置结构版本。
- 现有参数、列表、消息、主动设置的空字符串、注释和自定义键全部保留，只补入缺失的官方默认项与注释。
- 迁移模板按现有 `language` 选择中英文，不受本次安装的语言 JAR 影响。
- 旧配置若在新官方结构中对应含 `enabled` 的分节，原布尔值会移入 `enabled`，值本身不改变。
- 每次实际写入前，原文件会完整备份到 `plugins/ICUAC/backups/config-before-v<结构版本>-<时间>-<编号>.yml`。
- 新 YAML 先写入临时文件并重新解析验证；文件系统支持时再以原子方式替换 `config.yml`。
- YAML 无效或写入失败时不会覆盖原文件，启动或重载也不会带着半迁移配置继续执行。
- 无法安全推断的标量/分节类型冲突保持原样并在后台提示；旧自定义键或已废弃键不会被自动删除。
- 若配置的 `config-version` 高于当前插件，插件不会执行降级写入。

### 发行文件真伪

请只使用官方 `Lazyzouo/ICUAC` GitHub Release 中名称精确为 `ICUAC-<版本>-en.us.jar` 或 `ICUAC-<版本>-zh.cn.jar` 的资源。Source code 源码压缩包不是可安装插件；重命名文件、修改版、第三方镜像及转载文件不属于官方发行范围，也无法由 ICUAC 官方发布流程验证。官方自动化会校验这两个构建文件的精确名称并保持原名上传；缺失、改名、多余、源码、别名或校验文件都会让 Release 构建失败。

### 白名单范围

ICUAC 白名单会绕过命令拦截/Tab 隐藏、背包安全、NBT、附魔、药水、堆叠、创造模式隔离及危险坐标检查，但**不会**绕过指定世界死亡无掉落，也不会绕过末地水晶攻击间隔。

在线玩家加入或移出白名单时，客户端命令树会立即刷新。

### 处置概要

| 模块 | 触发条件 | 结果 |
| --- | --- | --- |
| 命令防护 | 命中根命令或嵌套 execute | 取消命令并提示 |
| 创造隔离 | 受监控模式位于非许可世界 | 切换为目标模式 |
| 基岩下方/下界顶 | 移动或传送越过阈值 | 玩家生命值归零 |
| 死亡掉落 | 在配置世界死亡 | 清空物品与经验掉落 |
| 违禁品/异常 NBT | 背包及交互检查命中 | 删除物品或取消转移 |
| 非法附魔 | 超出原版有效范围 | 移除非法附魔 |
| 非法堆叠 | 数量超过材质上限 | 修正到材质上限 |
| 异常药水 | 时间或等级超限 | 移除整个效果 |
| 水晶间隔 | 过快攻击末地水晶 | 取消伤害事件 |

### 水晶逻辑

`crystal.hit-interval` 单位为毫秒，对所有游戏模式生效。`/crystallimit bypass` 仅切换发送命令 OP 自己的运行期绕过；插件停用后清除，玩家失去 OP 时不生效。

### 重载边界

常规配置修改使用 `/icuac reload`。该命令会先执行与启动时相同的安全迁移，再重载语言、白名单与各安全模块。全局背包扫描周期只在插件启动时创建，因此修改 `global-settings.inventory-check-interval-ticks` 后必须重启。

`/crystalreload` 主要更新水晶间隔。其他模块修改仍应使用 `/icuac reload`。

### 自动更新

插件启动时检查 `Lazyzouo/ICUAC` 的最新 Release。英文模式选择 `ICUAC-<版本>-en.us.jar`，中文模式选择 `ICUAC-<版本>-zh.cn.jar`；只有 GitHub 资产元数据提供有效且匹配的 SHA-256 摘要时，文件才会进入服务端更新目录，并在下次重启安装。任何失败都会在后台输出原因及官方下载地址。

### 后台显示

启动时会输出内部宽度为 76 个字符的双语横幅，顶部、中部与底部均使用完整等号组成的青色边框。`ICUAC ADMINISTRATION` 版本主标题和 `SERVER SECURITY CONTROL / 服务器安全管理` 副标题按中英文显示宽度居中；版本、作者、测试基线、语言、GitHub 与隐私声明行左对齐，字段标签补齐到 14 个显示宽度，所有冒号位于同一列。横幅行不附加普通 `[ICUAC] »` 前缀以保持边框对齐，启动状态与更新器通知则继续保留该前缀及状态颜色。所有玩家可见消息在发送时统一移除行首空白，因此帮助标题、命令项、警告及其他插件消息均从左边缘开始，服务器现有配置中的缩进也会自动移除。玩家文字继续使用粗体与连续的 `#00D2FF` → `#3A7BD5` → `#F2C94C` 渐变，纯装饰分割线保持非粗体。

### 部署检查

1. 备份配置、白名单、世界和玩家数据。
2. 确认 Java 21 与 Paper/Folia 1.21.11。
3. 修改 `creative_world` 等官方占位世界名。
4. 分别测试普通玩家、OP 和 ICUAC 白名单玩家。
5. 验证使用 NBT、PDC、模型、属性或稀有度的其他插件。
6. 检查启动及重载后的后台日志。
7. 只对白名单加入完全可信的账号。
