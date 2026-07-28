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

### Update Trust Model

The built-in updater reads only the official `Lazyzouo/ICUAC` latest public Release. It requires an exact release asset name and matching SHA-256 file. Administrators can disable checking or automatic download in configuration.

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

### 更新信任模型

内置更新器只读取官方 `Lazyzouo/ICUAC` 最新公开 Release，并要求精确文件名和匹配的 SHA-256。管理员可以在配置中关闭检查或自动下载。
