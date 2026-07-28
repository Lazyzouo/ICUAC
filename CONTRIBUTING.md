# Contributing / 贡献指南

## English

Contributions are welcome for reproducible bug fixes, Paper/Folia compatibility, translations, tests, documentation, and security hardening.

### Requirements

- Java 21
- Paper/Folia 1.21.11 as the tested baseline
- No secrets, server-specific world names, player data, or private endpoints
- English first and Chinese second in public-facing repository documentation
- Semantic version updates for every behavior/configuration change
- Matching bilingual entries in `CHANGELOG.md`

### Workflow

1. Create a focused branch from `main`.
2. Keep official defaults generic; do not commit `src/main/resources/config.yml`.
3. Update both official presets when parameters change.
4. Update administrator documentation when behavior, limits, permissions, or migration steps change.
5. Run `./gradlew clean build`.
6. Open a Pull Request using the provided template.

Pull Requests should be small enough to review, explain security implications, and avoid unrelated formatting churn.

## 中文

欢迎提交可复现错误修复、Paper/Folia 兼容、翻译、测试、文档和安全加固。

要求：

- 使用 Java 21，并以 Paper/Folia 1.21.11 为测试基线。
- 不提交密钥、服务器专用世界名、玩家数据或私人地址。
- 仓库公开说明遵循英文在前、中文在后。
- 行为或配置变化必须按语义化版本更新版本号，并同步双语 CHANGELOG。
- 参数变化需同时更新两个官方预设；限制、权限、逻辑或迁移变化需更新管理员文档。
- 提交 PR 前运行 `./gradlew clean build`。
