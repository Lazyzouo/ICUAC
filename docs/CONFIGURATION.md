# Configuration Reference / 配置参考

> ICUAC 2.0.1 · Paper/Folia 1.21.11 · Java 21

## English

### Core and Updates

| Path | Official default | Meaning |
| --- | --- | --- |
| `language` | `zh_CN` or `en_US` by artifact | UI language. Supported values: `zh_CN`, `en_US`. |
| `updates.enabled` | `true` | Check GitHub Releases during startup. |
| `updates.auto-download` | `true` | Verify and stage newer releases automatically. |
| `global-settings.inventory-check-interval-ticks` | `1` | Online inventory scan period. Restart required after changing. |
| `crystal.hit-interval` | `200` | Minimum end-crystal attack interval in milliseconds. |

### Command Protection

| Path | Default | Meaning |
| --- | --- | --- |
| `command-blocking-enabled` | `true` | Master command-blocking switch. |
| `command-blocking-max-depth` | `4` | Nested `/execute ... run` parse depth. |
| `blocked-commands` | `kill`, `effect` | Blocked root commands. Namespaces are normalized. |
| `hide-blocked-commands-from-tab` | `true` | Remove blocked roots from non-bypassed client command trees. |
| `permission-bypass-enabled` | `false` | Enable the `icuac.bypass` permission. |
| `block-command-blocks` | `true` | Apply blocking to command blocks. |
| `block-console-commands` | `false` | Apply blocking to console commands. |

### Game Mode and Worlds

`creative-world-whitelist` controls watched source modes, ignored modes, allowed world names, target mode, check frequency, and logging. World names are exact strings. The official placeholder `creative_world` must be replaced when appropriate.

### Environment

- `features.prevent-below-bedrock`: normal-world and Nether/End Y thresholds.
- `features.prevent-above-nether-bedrock`: Nether-only roof threshold and exact world list; an empty list means every Nether world.
- `features.death-drop-control`: exact worlds where item and XP drops are cleared. ICUAC whitelist does not bypass this rule.

### Items, Effects, and Stacks

- `banned-items` uses Bukkit `Material` names. Invalid values are logged and ignored.
- Effect amplifier limits are zero-based: configured `3` permits displayed level IV.
- An illegal stack is clamped to the material's vanilla maximum.
- Illegal enchantments are removed when above the Bukkit maximum or invalid for the item.

### NBT

NBT checks cover serialized size, metadata string size/keywords, PDC key count/bytes, display text, attribute modifiers, unsafe flags, BlockState containers, bundles, banned nested items, nested enchantments, and nested stacks.

Important constraints:

- `unsafe-flags.block-attribute-modifiers: true` rejects every custom attribute modifier before numeric limits are evaluated.
- Broad `banned-meta-keywords` can reject legitimate plugin items.
- Custom model data, PDC, rarity, fire resistance, attributes, and hidden tooltips are commonly used by legitimate plugins; test compatibility first.
- Illegal NBT and banned items are normally destroyed without recovery.
- `max-nested-depth: 2` rejects content beyond the configured depth.

### Messages

Chinese mode reads `messages` from `config.yml`, preserving legacy customization. English mode reads `plugins/ICUAC/lang/en_US.yml` and falls back to the config message when a key is missing. Legacy `&` colors and `&#RRGGBB` colors are supported.

---

## 中文

### 核心与更新

| 路径 | 官方默认值 | 说明 |
| --- | --- | --- |
| `language` | 依发行物为 `zh_CN` 或 `en_US` | 界面语言，仅支持这两个值。 |
| `updates.enabled` | `true` | 启动时检查 GitHub Release。 |
| `updates.auto-download` | `true` | 自动校验并暂存新版本。 |
| `global-settings.inventory-check-interval-ticks` | `1` | 在线背包扫描周期；修改后需重启。 |
| `crystal.hit-interval` | `200` | 末地水晶最短攻击间隔，单位毫秒。 |

### 命令防护

| 路径 | 默认值 | 说明 |
| --- | --- | --- |
| `command-blocking-enabled` | `true` | 命令拦截总开关。 |
| `command-blocking-max-depth` | `4` | `/execute ... run` 嵌套解析深度。 |
| `blocked-commands` | `kill`、`effect` | 受限根命令，会标准化命名空间。 |
| `hide-blocked-commands-from-tab` | `true` | 对未豁免玩家隐藏受限根命令。 |
| `permission-bypass-enabled` | `false` | 是否启用 `icuac.bypass`。 |
| `block-command-blocks` | `true` | 是否拦截命令方块。 |
| `block-console-commands` | `false` | 是否拦截控制台。 |

### 游戏模式与世界

`creative-world-whitelist` 配置来源模式、忽略模式、许可世界、目标模式、检查周期和日志。世界名为精确匹配；官方占位值 `creative_world` 应按服务器情况修改。

### 环境

- `features.prevent-below-bedrock`：普通世界以及下界/末地的 Y 阈值。
- `features.prevent-above-nether-bedrock`：仅下界生效；世界列表为空表示所有下界。
- `features.death-drop-control`：指定世界清空死亡物品与经验；ICUAC 白名单不绕过此规则。

### 物品、药水与堆叠

- `banned-items` 使用 Bukkit `Material` 名称，无效值记录警告后忽略。
- 药水 amplifier 从 0 开始，配置 `3` 允许游戏显示 IV 级。
- 非法堆叠会修正到材质原版上限。
- 超等级或不适用于该物品的附魔会被移除。

### NBT

检查范围包括序列化大小、元数据字符串/关键字、PDC、显示文本、属性、不安全标记、方块状态容器、收纳袋及嵌套物品。

重要限制：

- `block-attribute-modifiers: true` 会在数值上限判定前拒绝所有自定义属性。
- 过宽的 NBT 关键词可能误判合法插件物品。
- 模型数据、PDC、稀有度、防火、属性和隐藏提示常被其他插件使用，上线前必须测试。
- 异常 NBT 与违禁物品通常直接销毁，没有回收机制。

### 消息

中文模式读取 `config.yml` 的 `messages`，保持旧版自定义兼容；英文模式读取 `plugins/ICUAC/lang/en_US.yml`，缺少键时回退到配置消息。支持 `&` 与 `&#RRGGBB` 颜色。
