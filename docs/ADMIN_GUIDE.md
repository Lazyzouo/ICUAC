# ICUAC Administrator Guide / ICUAC 管理员指南

> Version: 2.0.0
> Tested: Paper/Folia 1.21.11
> Java: 21

## English

### Operational Model

ICUAC combines preventive checks with destructive remediation. Depending on the module, it may cancel an event, remove an item, strip an enchantment, clamp a stack size, remove a potion effect, switch game mode, or kill a player who crosses a configured world boundary. Back up player data before enabling strict production settings.

### Runtime Files

- `plugins/ICUAC/config.yml`: personal runtime parameters and Chinese-compatible messages.
- `plugins/ICUAC/whitelist.yml`: ICUAC global bypass identities.
- `plugins/ICUAC/lang/en_US.yml`: editable English messages, generated when English mode is used.
- Server update directory: receives a verified new JAR when auto-download succeeds.

Repository presets are official generic defaults. They are not intended to contain a specific server's world names or private settings.

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

Use `/icuac reload` for normal configuration changes. It reloads language, whitelist, game-mode settings, item/NBT/effect/stack settings and crystal timing. The global inventory scheduler period is created only during plugin startup; changing `global-settings.inventory-check-interval-ticks` requires a restart.

`/crystalreload` reloads the YAML file and crystal interval, but does not explicitly rebuild every cached module. Use `/icuac reload` for changes outside the crystal section.

### Automatic Updates

At startup the updater calls the official `Lazyzouo/ICUAC` latest-release endpoint. A release is installed only when the exact `ICUAC-<version>.jar` and matching `.sha256` assets exist and the digest matches. The file is staged for the next restart. Network, API, asset, write, or checksum failures are logged with the official Releases URL.

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

### 运行逻辑

ICUAC 同时包含预防拦截与破坏性处置。不同模块可能取消事件、删除物品、移除附魔、修正堆叠、清除药水、切换游戏模式，或直接将越界玩家生命值设为零。正式启用严格配置前请备份玩家数据。

### 运行文件

- `plugins/ICUAC/config.yml`：服务器个人参数与兼容中文消息。
- `plugins/ICUAC/whitelist.yml`：ICUAC 全局豁免名单。
- `plugins/ICUAC/lang/en_US.yml`：英文模式生成的可编辑消息。
- 服务端更新目录：自动更新校验成功后存放待安装 JAR。

仓库中的配置是官方通用预设，不应保存具体服务器的世界名或私人参数。

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

常规配置修改使用 `/icuac reload`。全局背包扫描周期只在插件启动时创建，因此修改 `global-settings.inventory-check-interval-ticks` 后必须重启。

`/crystalreload` 主要更新水晶间隔。其他模块修改仍应使用 `/icuac reload`。

### 自动更新

插件启动时检查 `Lazyzouo/ICUAC` 的最新 Release。只有目标 JAR 与同名 `.sha256` 同时存在且校验一致时才会进入服务端更新目录，并在下次重启安装。任何失败都会在后台输出原因及官方下载地址。

### 部署检查

1. 备份配置、白名单、世界和玩家数据。
2. 确认 Java 21 与 Paper/Folia 1.21.11。
3. 修改 `creative_world` 等官方占位世界名。
4. 分别测试普通玩家、OP 和 ICUAC 白名单玩家。
5. 验证使用 NBT、PDC、模型、属性或稀有度的其他插件。
6. 检查启动及重载后的后台日志。
7. 只对白名单加入完全可信的账号。
