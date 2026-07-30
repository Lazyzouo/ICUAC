# Changelog / 更新日志

All notable changes are documented here. English is listed first, followed by Chinese.

所有重要更新均记录于此；英文在前，中文在后。

## [2.1.2] - 2026-07-30

### English

#### Changed

- Applied a continuous three-stop `#00D2FF` → `#3A7BD5` → `#F2C94C` gradient to every player-visible message line while preserving forced bold text, placeholders, and component interaction styles. Pure decorative divider lines remain unbold.
- Changed every startup console banner content row from centered alignment to a consistent one-space left inset; the fixed width, equal divider halves, console prefix, and semantic status colors are unchanged.
- Added an `ICUAC ADMINISTRATION` version heading, a bilingual server-security-control subtitle, and a star-centered section divider above the startup banner fields.

### 中文

#### 调整

- 所有玩家可见消息行统一应用连续的 `#00D2FF` → `#3A7BD5` → `#F2C94C` 三段渐变，同时保留强制粗体、占位符及组件交互样式；纯装饰分割线继续保持非粗体。
- 启动后台横幅的全部内容行由居中改为距左边框一个空格并统一左对齐；固定宽度、等长分割线、后台前缀及状态色保持不变。
- 启动横幅字段上方新增 `ICUAC ADMINISTRATION` 版本主标题、中英服务器安全管理副标题及以星号为中心的章节分隔线。

## [2.1.1] - 2026-07-29

### English

#### Changed

- Replaced the startup console border with equal-length halves around a centered `✧`, and centered every non-help banner line on that same axis using CJK-aware display widths.
- Kept the help-menu layout unchanged while guaranteeing bold rendering for every player-visible text component; pure decorative divider glyphs remain unbold to preserve alignment.

### 中文

#### 调整

- 将启动后台边框改为以 `✧` 为中心、左右等长的分割线，并按中英文可见宽度将所有非 help 横幅文字统一居中到同一轴线。
- 保持 help 菜单布局不变，同时保证所有玩家可见文字组件均强制粗体；纯装饰分割线继续保持非粗体以确保对齐。

## [2.1.0] - 2026-07-29

### English

#### Added

- Added a bilingual, fixed-width startup console banner with the version, author, tested baseline, active language, repository, privacy statement, and detected Paper/Folia platform.
- Added consistent semantic colors to startup, shutdown, update-check, latest-version, available-version, downloaded-version, manual-download, and update-failure console notices.

#### Changed

- All user-facing and console message text is now rendered in bold while decorative help-menu divider lines remain unbold for alignment.
- Locked official Release asset names to `ICUAC-MAJOR.MINOR.PATCH-en.us.jar` and `ICUAC-MAJOR.MINOR.PATCH-zh.cn.jar`; CI now rejects missing, renamed, or additional JARs before publishing.

### 中文

#### 新增

- 新增固定宽度的双语启动后台横幅，显示版本、作者、测试基线、当前语言、仓库、隐私声明及实际 Paper/Folia 核心。
- 为启动、停用、检查更新、已是最新版、发现新版、下载完成、手动下载及更新失败等后台通知加入对应状态颜色。

#### 调整

- 所有玩家消息与后台文字统一以粗体显示；帮助菜单的纯装饰分割线继续保持非粗体，确保对齐。
- 官方 Release 文件名强制固定为 `ICUAC-MAJOR.MINOR.PATCH-en.us.jar` 与 `ICUAC-MAJOR.MINOR.PATCH-zh.cn.jar`，CI 会在发布前拒绝缺失、改名或多余 JAR。

## [2.0.5] - 2026-07-29

### English

#### Documentation

- Removed the long NBT scope and risk paragraph from the public overview.
- Replaced the short feature list with a per-module table describing each trigger, action, bypass boundary, language package, updater step, and scheduler role.

### 中文

#### 文档

- 从公开概述中移除大段 NBT 范围与风险说明。
- 将简短功能列表改为逐模块说明表，明确各功能的触发条件、处置动作、绕过边界、语言包、更新步骤与调度用途。

## [2.0.4] - 2026-07-29

### English

#### Documentation

- Rewrote the plugin overview to describe each enforcement category and its actual result.
- Clarified that NBT protection is limited to configurable Paper/Bukkit-visible item data, is not a universal raw-NBT or anti-cheat engine, and may permanently remove matched items or conflict with custom-item plugins.

### 中文

#### 文档

- 重写插件概述，明确各类规则的触发范围与实际处置结果。
- 明确 NBT 防护仅检查可配置且 Paper/Bukkit 可见的物品数据，不是通用原始 NBT 或反作弊引擎，并可能永久删除命中物品或与自定义物品插件冲突。

## [2.0.3] - 2026-07-29

### English

#### Added

- Added a bilingual official-release notice that is automatically prepended to every GitHub Release.
- Identified the two installable language JARs and warned against source archives, renamed files, modified builds, third-party mirrors, and reuploads.

### 中文

#### 新增

- 新增自动置于每个 GitHub Release 顶部的中英双语官方发布声明。
- 明确两个可安装语言 JAR，并提示不要误用源码压缩包、重命名文件、修改版、第三方镜像或转载文件。

## [2.0.2] - 2026-07-29

### English

#### Documentation

- Added a prominent bilingual open-source and data-privacy statement.
- Clarified that ICUAC contains no backdoors or telemetry, stores plugin-created data locally, and contacts only GitHub for release checks and downloads.

### 中文

#### 文档

- 新增置顶的中英双语开源与数据隐私声明。
- 明确 ICUAC 不包含后门或遥测，插件创建的数据仅在本地保存，更新检查与下载只访问 GitHub。

## [2.0.1] - 2026-07-29

### English

#### Changed

- GitHub Releases now upload only `ICUAC-2.0.1-zh.cn.jar` and `ICUAC-2.0.1-en.us.jar`.
- Source JARs and standalone checksum files are no longer built or uploaded; source remains available from `main`.
- The updater now selects the release JAR matching the active language and verifies GitHub's SHA-256 asset digest.

### 中文

#### 变更

- GitHub Release 现在只上传 `ICUAC-2.0.1-zh.cn.jar` 与 `ICUAC-2.0.1-en.us.jar`。
- 不再构建或上传源码 JAR 与独立校验文件；源码继续保留在 `main`。
- 自动更新器会按当前语言选择发行 JAR，并校验 GitHub 提供的 SHA-256 资产摘要。

## [2.0.0] - 2026-07-29

### English

#### Added

- Simplified Chinese and English runtime language modes.
- Separate Chinese-default and English-default release JARs.
- SHA-256-verified GitHub Release update checker and automatic downloader.
- Bilingual startup banner with version, author, tested server version, language, and repository.
- Official generic configuration presets isolated from personal runtime configuration.
- Complete open-source documentation, issue templates, CI, CodeQL, Dependabot, and automatic Releases.

#### Changed

- Author metadata is now `Lazyz`.
- Project version advanced to `2.0.0` for the new distribution and update architecture.
- Command errors and CrystalPVP administration messages now use the active language.
- Official compatibility baseline is Paper/Folia `1.21.11` with Java 21.

### 中文

#### 新增

- 简体中文与英文运行语言模式。
- 中文默认及英文默认的独立发行 JAR。
- 使用 SHA-256 校验的 GitHub Release 自动检查与下载。
- 显示版本、作者、测试版本、语言和仓库地址的双语启动横幅。
- 与个人运行配置隔离的官方通用参数预设。
- 完整开源文档、Issue 模板、CI、CodeQL、Dependabot 和自动 Release。

#### 变更

- 作者信息统一为 `Lazyz`。
- 因发行与更新架构升级，版本进位至 `2.0.0`。
- 命令错误和 CrystalPVP 管理消息跟随当前语言。
- 官方兼容测试基线为 Paper/Folia `1.21.11` 与 Java 21。

## [1.1.1] - 2026-07-28

### English

- Fixed `{prefix}` appearing literally in NBT inventory and container cleanup messages.

### 中文

- 修复 NBT 背包和容器清理消息直接显示 `{prefix}` 的问题。

## [1.1.0] - 2026-07-27

### English

- Integrated CrystalPVP end-crystal attack interval protection.
- Added OP-only `/crystallimit bypass` runtime toggle.
- Refreshed client command trees when ICUAC whitelist membership changes.

### 中文

- 融合 CrystalPVP 末地水晶攻击间隔保护。
- 新增仅 OP 使用的 `/crystallimit bypass` 运行期切换。
- ICUAC 白名单变化时即时刷新客户端命令树。

[2.1.2]: https://github.com/Lazyzouo/ICUAC/releases/tag/v2.1.2
[2.1.1]: https://github.com/Lazyzouo/ICUAC/releases/tag/v2.1.1
[2.1.0]: https://github.com/Lazyzouo/ICUAC/releases/tag/v2.1.0
[2.0.5]: https://github.com/Lazyzouo/ICUAC/releases/tag/v2.0.5
[2.0.4]: https://github.com/Lazyzouo/ICUAC/releases/tag/v2.0.4
[2.0.3]: https://github.com/Lazyzouo/ICUAC/releases/tag/v2.0.3
[2.0.2]: https://github.com/Lazyzouo/ICUAC/releases/tag/v2.0.2
[2.0.1]: https://github.com/Lazyzouo/ICUAC/releases/tag/v2.0.1
[2.0.0]: https://github.com/Lazyzouo/ICUAC/releases/tag/v2.0.0
[1.1.1]: https://github.com/Lazyzouo/ICUAC/releases/tag/v1.1.1
[1.1.0]: https://github.com/Lazyzouo/ICUAC/releases/tag/v1.1.0
