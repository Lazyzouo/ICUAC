# Security Policy / 安全政策

## English

### Supported Versions

| Version | Supported |
| --- | --- |
| 2.x | Yes |
| 1.x and older | No |

The official tested runtime is Paper/Folia 1.21.11 with Java 21.

### Reporting a Vulnerability

Use GitHub's private vulnerability reporting feature in the repository Security tab. Do not open a public Issue for bypasses, arbitrary file writes, unsafe updater behavior, denial-of-service vectors, or sensitive data exposure.

Include affected versions, server build, reproduction steps, impact, proof of concept, and suggested mitigation when available. Never include real credentials or private player data.

The maintainer will acknowledge a valid report as soon as practical, investigate privately, and publish a release and advisory when remediation is ready.

### Open Source And Data Privacy

ICUAC is fully open source and contains no backdoors or telemetry. It does not collect or upload server configuration, player data, world data, logs, or other plugin-generated information. Files created by the plugin remain on the server's local filesystem. The updater communicates only with GitHub's public Release service to compare versions and download published release assets; ICUAC operates no separate data-collection server.

### Update Trust Model

The built-in updater reads only the official `Lazyzouo/ICUAC` latest public Release. It requires the exact language-specific asset name and a valid SHA-256 digest supplied in GitHub's asset metadata. Administrators can disable checking or automatic download in configuration.

## 中文

### 支持版本

| 版本 | 是否支持 |
| --- | --- |
| 2.x | 是 |
| 1.x 及更早 | 否 |

官方测试环境为 Java 21 与 Paper/Folia 1.21.11。

### 报告漏洞

请使用仓库 Security 页面的私密漏洞报告，不要公开提交绕过、任意文件写入、更新器风险、拒绝服务或敏感数据泄露问题。

请提供受影响版本、服务端构建、复现步骤、影响、概念验证及可行缓解方式。不要附带真实凭据或私人玩家数据。

### 开源与数据隐私

ICUAC 完全开源，不包含后门或遥测，不会收集或上传服务器配置、玩家数据、世界数据、日志及其他插件生成资料。插件创建的文件只保存在服务器本地文件系统。更新器仅与 GitHub 公开 Release 服务通信，用于比较版本并下载已发布的发行文件；ICUAC 不运营独立的数据收集服务器。

### 更新信任模型

内置更新器只读取官方 `Lazyzouo/ICUAC` 最新公开 Release，并要求精确的语言资产文件名以及 GitHub 资产元数据中有效且匹配的 SHA-256 摘要。管理员可以在配置中关闭检查或自动下载。
