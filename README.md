# douyinapi-java

> 抖音开放平台（账号 / 授权）与抖音担保支付（Douyin Pay）的 Java SDK。

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](#环境要求)
[![Maven](https://img.shields.io/badge/Maven-3.6.3%2B-blue.svg)](#环境要求)
[![CI](https://github.com/javaeer/douyinapi-java/actions/workflows/ci.yml/badge.svg)](https://github.com/javaeer/douyinapi-java/actions/workflows/ci.yml)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

`douyinapi-java` 用一套统一、面向对象的 API 封装了抖音开放平台的服务端调用，覆盖 **OAuth 授权登录**、**获取用户信息**、**应用级 client_token**，以及 **担保支付的预下单 / 查单 / 退款 / 退款查询 / 异步回调验签** 等能力。

SDK 只依赖少数成熟的基础库（OkHttp、Jackson、Commons Codec），对业务代码零侵入，适用于任何 Java 8+ 服务端应用。

## 目录

- [特性](#特性)
- [环境要求](#环境要求)
- [安装](#安装)
- [快速开始](#快速开始)
- [模块与 API 一览](#模块与-api-一览)
- [设计说明](#设计说明)
- [项目结构](#项目结构)
- [构建与测试](#构建与测试)
- [常见问题](#常见问题)
- [参与贡献](#参与贡献)
- [许可证](#许可证)

## 特性

- **统一入口**：`DouyinClient` 聚合配置、HTTP 执行器与各业务服务，一次构建、处处可用。
- **OAuth 授权登录**：拼接授权页地址、用 `code` 换取 `access_token`、刷新 / 续期 `refresh_token`、获取用户信息、解析回调参数。
- **担保支付**：预下单、查询订单、发起退款、查询退款，自动完成身份字段注入与 MD5 签名。
- **回调验签**：内置 `SignUtils`，支持担保支付的 **MD5** 签名与通用交易系统的 **RSA-SHA256** 签名 / 验签。
- **可测试设计**：通过 `DouyinRequestExecutor` 接口解耦底层 HTTP 客户端，单元测试可注入 mock，无需真实网络。
- **安全的 HTTP 层**：基于 OkHttp，连接池 / 调度器进程内共享；对非幂等写入关闭连接层自动重试，避免重复下单 / 退款。
- **零强制日志依赖**：SDK 不绑定任何日志实现，由宿主应用自由选择。

## 环境要求

| 组件 | 版本 |
| --- | --- |
| JDK | 8 及以上 |
| Maven | 3.6.3 及以上（或使用仓库自带的 `./mvnw`） |
| 抖音开放平台应用 | 需具备开放平台 / 担保支付的相应权限 |

## 安装

### Maven

```xml
<dependency>
    <groupId>cn.net.yunlou</groupId>
    <artifactId>douyinapi-java</artifactId>
    <version>1.0.0-alpha</version>
</dependency>
```

### Gradle

```groovy
implementation 'cn.net.yunlou:douyinapi-java:1.0.0-alpha'
```

> 尚未发布到中央仓库时，可先 `git clone` 本项目并执行 `./mvnw install`，在本地仓库中安装后再引用。

## 快速开始

### 1. 构建客户端

```java
import cn.net.yunlou.douyin.DouyinClient;
import cn.net.yunlou.douyin.DouyinClientImpl;

DouyinClient client = DouyinClientImpl.builder()
        .clientKey("你的 client_key")
        .clientSecret("你的 client_secret")
        .merchantId("你的 merchant_id")   // 担保支付需要
        .salt("你的支付 salt")            // 担保支付签名需要
        .build();
```

### 2. OAuth 授权登录

```java
// 拼接授权页地址，引导用户跳转
String authorizeUrl = client.getOAuthService()
        .buildAuthorizeUrl("https://your.site/callback", "user_info");

// 用户授权后回调带回 code，用它换取 access_token
AccessTokenResult token = client.getOAuthService().getAccessToken(code);
String accessToken = token.getAccessToken();

// 获取用户信息
UserInfoResult userInfo = client.getOAuthService().getUserInfo(accessToken, token.getOpenId());
System.out.println(userInfo.getNickname());
```

### 3. 担保支付下单

```java
TradeCreateRequest req = new TradeCreateRequest();
req.setOutOrderNo("ORDER_20260101_0001");  // 开发者侧订单号，需唯一
req.setTotalAmount(100);                   // 单位：分
req.setSubject("测试商品");
req.setValidTime(3600);
req.setNotifyUrl("https://your.site/pay/notify");

TradeCreateResult result = client.getPayService().createOrder(req);
// 将 result.getOrderId() / result.getOrderToken() 返回前端，由 tt.pay 唤起收银台
```

### 4. 回调验签

```java
// notifyBody 为平台推送到 notify_url 的原始报文
PayNotifyResult notify = client.getPayService().parseNotify(notifyBody);
if (notify.isSignatureValid()) {
    // 验签通过，再处理业务
    OrderTrade trade = notify.parseMsg(OrderTrade.class);
    // ... 更新订单状态 ...
}
```

## 模块与 API 一览

| 服务 | 接口 | 主要方法 |
| --- | --- | --- |
| 统一入口 | `DouyinClient` | `getConfigStorage()` / `getRequestExecutor()` / `getOAuthService()` / `getPayService()` |
| 账号与授权 | `DouyinOAuthService` | `buildAuthorizeUrl` / `getAccessToken` / `refreshToken` / `getClientToken` / `renewRefreshToken` / `getUserInfo` / `parseCallback` |
| 担保支付 | `DouyinPayService` | `createOrder` / `queryOrder` / `createRefund` / `queryRefund` / `parseNotify` / `verifyNotify` |
| 配置 | `DouyinConfigStorage` | 提供 `clientKey` / `clientSecret` / `merchantId` / `salt` / 超时 / 域名等 |
| HTTP 执行器 | `DouyinRequestExecutor` | `postForm` / `postJson` / `get` |
| 签名工具 | `cn.net.yunlou.douyin.util.SignUtils` | `createSign` / `notifySign` / `verifyNotify` / `signRsa` / `verifyRsa` |

## 设计说明

SDK 采用与 [WxJava](https://github.com/Wechat-Group/WxJava) 类似的分层结构，各组件职责单一、可替换：

```
DouyinClient
 ├── DouyinConfigStorage      配置存储（凭证、超时、域名）
 ├── DouyinRequestExecutor    HTTP 执行层（默认 OkHttp 实现）
 ├── DouyinOAuthService       账号 / 授权业务
 └── DouyinPayService         担保支付业务
```

- **配置与实现分离**：`DouyinConfigStorage` 为接口，默认实现 `DefaultDouyinConfigImpl` 基于不可变 Builder 构造。
- **HTTP 可插拔**：默认 `OkHttpDouyinRequestExecutor`，也支持注入自定义 `OkHttpClient` 以复用宿主连接池 / 拦截器。
- **异常统一**：所有失败（HTTP 非 2xx、平台业务错误码、签名失败）统一抛出 `DouyinErrorException`，并携带 `errorCode` / `errorDescription`。
- **响应统一**：`BaseResponse` 同时兼容开放平台的 `error_code` / `description` 与支付接口的 `err_no` / `err_tips`。

> 安全提示：`client_secret`、`salt`、RSA 私钥等敏感信息请存放于服务端配置中心或密钥系统，切勿硬编码或下发到客户端。

## 项目结构

```
douyinapi-java
├── pom.xml
├── mvnw / mvnw.cmd / .mvn          # Maven Wrapper
├── config/checkstyle/              # 代码风格规则
├── .github/                        # Issue / PR 模板、CI、Dependabot
└── src
    ├── main/java/cn/net/yunlou/douyin
    │   ├── DouyinClient.java            # 统一入口
    │   ├── DouyinClientImpl.java
    │   ├── DouyinConfigStorage.java     # 配置存储接口
    │   ├── DouyinConstants.java         # 端点与常量
    │   ├── DouyinRequestExecutor.java   # HTTP 执行器接口
    │   ├── bean/                        # 请求 / 响应模型
    │   ├── oauth/                       # 账号与授权服务
    │   ├── pay/                         # 担保支付服务
    │   ├── impl/                        # 默认实现
    │   ├── error/                       # 异常
    │   └── util/                        # 工具类
    └── test/java/cn/net/yunlou/douyin    # 单元测试
```

## 构建与测试

```bash
# 编译并运行全部单元测试
./mvnw test

# 完整构建（编译 + 测试 + 打 jar + 生成覆盖率报告）
./mvnw verify

# 附带 Checkstyle 代码风格检查
./mvnw -Pquality verify

# 发布到 Maven 中央仓库（需要 GPG 密钥与 central 凭据）
./mvnw -Prelease deploy
```

覆盖率报告生成于 `target/site/jacoco/index.html`。

## 常见问题

<details>
<summary>为什么 SDK 不直接依赖某个日志实现？</summary>

库应当只声明对日志门面（facade）的需求，而不强制替换宿主应用的日志实现。当前版本未在内部打日志；若后续引入，将仅依赖 `slf4j-api`，把实现的选择权交给使用方。

</details>

<details>
<summary>支付签名用 MD5 还是 RSA？</summary>

担保支付（旧版）使用 `salt` 参与的 **MD5** 签名；通用交易系统（新版）使用 **RSA-SHA256**。两者均由 `SignUtils` 提供，见 [签名工具](#模块与-api-一览)。

</details>

<details>
<summary>如何拿到最新的源码？</summary>

```bash
git clone https://github.com/javaeer/douyinapi-java.git
cd douyinapi-java
```

</details>

## 参与贡献

欢迎提交 Issue 与 Pull Request。开始之前请阅读 [CONTRIBUTING.md](CONTRIBUTING.md) 与 [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)。安全相关问题请按 [SECURITY.md](SECURITY.md) 私下反馈。

## 许可证

本项目基于 [Apache License 2.0](LICENSE) 开源。

```
Copyright 2026 javaeer (Yunlou Network)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
