# 更新日志

本文件记录 `douyinapi-java` 的重要变更。

格式参考 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)，
版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [Unreleased]

### Added

- 开源工程化配套：CI（多 JDK 矩阵）、Dependabot、Issue / PR 模板、Checkstyle 规则、JaCoCo 覆盖率。

### Changed

- 构建配置：GPG 签名 / 源码与 Javadoc 产物 / Central 发布收敛至 `release` profile，普通 `mvn verify` 不再需要密钥。
- 依赖治理：移除未使用的 guava、commons-text、commons-lang3、logback、slf4j；Lombok 调整为 `provided`。

### Fixed

- 清理 `bean/pay` 下多个请求类的未使用 import。
- 修正 `DouyinClientImpl` 中与实现不符的 Javadoc（Apache HttpClient → OkHttp）。

## [1.0.0-alpha] - 2026-07-27

### Added

- 首个公开版本。
- OAuth 授权登录：授权页拼接、换取 / 刷新 / 续期 token、获取用户信息、解析回调。
- 担保支付：预下单、查询订单、发起退款、查询退款。
- 签名工具：担保支付 MD5 签名 / 验签、通用交易系统 RSA-SHA256 签名 / 验签。
- 统一异常 `DouyinErrorException` 与响应基类 `BaseResponse`。

[Unreleased]: https://github.com/javaeer/douyinapi-java/compare/v1.0.0-alpha...HEAD
[1.0.0-alpha]: https://github.com/javaeer/douyinapi-java/releases/tag/v1.0.0-alpha
