# Changelog / 更新日志

All notable changes are documented here. English is listed first, followed by Chinese.

所有重要更新均记录于此；英文在前，中文在后。

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

[2.0.3]: https://github.com/Lazyzouo/ICUAC/releases/tag/v2.0.3
[2.0.2]: https://github.com/Lazyzouo/ICUAC/releases/tag/v2.0.2
[2.0.1]: https://github.com/Lazyzouo/ICUAC/releases/tag/v2.0.1
[2.0.0]: https://github.com/Lazyzouo/ICUAC/releases/tag/v2.0.0
[1.1.1]: https://github.com/Lazyzouo/ICUAC/releases/tag/v1.1.1
[1.1.0]: https://github.com/Lazyzouo/ICUAC/releases/tag/v1.1.0
