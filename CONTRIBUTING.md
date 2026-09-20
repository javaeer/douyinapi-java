# 贡献指南

感谢你愿意为 `douyinapi-java` 做出贡献！本文档说明参与协作的流程与约定。

## 行为准则

参与本项目即表示你同意遵守 [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)。

## 我能贡献什么

- 报告缺陷或提出功能建议（请使用 [Issue 模板](.github/ISSUE_TEMPLATE)）
- 改进文档、补充示例
- 修复 Bug、新增 API 覆盖
- 完善单元测试

## 开发环境

| 组件 | 版本 |
| --- | --- |
| JDK | 8 及以上（CI 会在 8 / 11 / 17 / 21 上验证） |
| Maven | 3.6.3 及以上，或使用仓库自带 `./mvnw` |

```bash
git clone https://github.com/javaeer/douyinapi-java.git
cd douyinapi-java
./mvnw verify
```

## 分支与提交

- 从 `main` 切出功能分支：`feature/xxx`、`fix/xxx`、`docs/xxx`。
- 提交信息遵循 [Conventional Commits](https://www.conventionalcommits.org/zh-hans/)：

  ```
  <type>(<scope>): <subject>
  ```

  常用 `type`：`feat`、`fix`、`docs`、`test`、`refactor`、`chore`、`build`。

  示例：`feat(pay): 支持分账接口 create_settle`

- 一个 PR 聚焦一件事，避免夹带无关改动。

## 代码规范

- 使用 4 空格缩进，UTF-8 编码，文件以换行结尾（见 [.editorconfig](.editorconfig)）。
- 类与公开方法需有 Javadoc；包、类、方法命名遵循 Java 惯例。
- 提交前请运行：

  ```bash
  ./mvnw -Pquality verify
  ```

  Checkstyle 规则见 [config/checkstyle/checkstyle.xml](config/checkstyle/checkstyle.xml)。

## 测试要求

- 新增或修改功能需附带单元测试，保持 `src/test` 与主代码同构。
- 涉及签名 / 验签的改动，请以官方文档给出的向量为准补充用例。
- 确保本地 `./mvnw verify` 全绿后再提交 PR。

## Pull Request 流程

1. Fork 仓库并从 `main` 创建分支。
2. 完成改动并自测通过。
3. 按 [PR 模板](.github/PULL_REQUEST_TEMPLATE.md) 填写说明，关联相关 Issue。
4. 等待 CI 通过与维护者 Review。

## 发布（维护者）

```bash
./mvnw -Prelease clean deploy
```

发布所需 GPG 密钥与 Central 凭据不随仓库分发，请参考 [SECURITY.md](SECURITY.md) 妥善保管。
