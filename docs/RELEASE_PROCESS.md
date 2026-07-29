# Release Process / 发布流程

## English

ICUAC releases are automated from `main`.

1. Update `build.gradle` using semantic versioning.
2. Update both official presets and bilingual documentation when relevant.
3. Add an English-first, Chinese-second section to `CHANGELOG.md` named `## [VERSION]`.
4. Run `./gradlew clean build` and inspect both language JARs.
5. Commit to `main`; the configured local post-commit hook pushes when authentication is available.
6. GitHub Actions builds from a clean checkout, extracts the matching changelog section, creates tag `vVERSION`, and uploads only the `zh.cn` and `en.us` JARs.
7. Verify CI, CodeQL, the two release assets, GitHub-provided asset digests, and updater compatibility.

A version already present in GitHub Releases is not republished. Every functional update must therefore have a new version.

## 中文

ICUAC 从 `main` 自动发布：

1. 按语义化版本更新 `build.gradle`。
2. 需要时同步两个官方预设及双语文档。
3. 在 `CHANGELOG.md` 新增英文在前、中文在后的 `## [版本]` 区块。
4. 执行 `./gradlew clean build` 并检查两个语言 JAR。
5. 提交到 `main`；GitHub 认证可用时，本地 post-commit Hook 自动推送。
6. GitHub Actions 在干净环境构建、提取更新日志、创建 `v版本` 标签，并只上传 `zh.cn` 与 `en.us` 两个 JAR。
7. 检查 CI、CodeQL、两个发行文件、GitHub 提供的资产摘要及更新器兼容性。

已有 GitHub Release 的版本不会重复发布，因此每次功能更新都必须使用新版本号。
