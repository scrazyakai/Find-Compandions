FindCompandions - 寻伴
 FindCompandions是一个高效、便捷的伙伴匹配平台，旨在帮助用户快速找到志同道合的伙伴，支持用户注册、登录、组队、实时聊天等核心功能。后端基于 Spring Boot 构建，集成 MyBatis-Plus、Redis、WebSocket 等技术，适用于学习、社交和实际应用场景。

📁 项目结构
Find-Companions/
├── .gitignore
├── pom.xml
├── sql/
│   └── create_table.sql             # 数据库建表脚本
├── src/
│   └── main/
│       ├── java/com/akai/findCompanions/
│       │   ├── common/              # 通用工具类和常量
│       │   ├── config/              # 配置类（如 WebSocket、Swagger）
│       │   ├── controller/          # 控制层（接口入口）
│       │   ├── enums/               # 枚举定义
│       │   ├── exception/           # 全局异常处理
│       │   ├── model/               # 实体层，包括 VO/DTO/DO
│       │   ├── service/             # 业务逻辑接口与实现
│       │   └── FindCompanionsApplication.java
│       └── resources/
│           ├── mapper/              # MyBatis 映射文件
│           ├── application.yml      # 默认配置
│           └── application-prod.yml # 生产环境配置
└── README.md


🛠 技术栈

后端框架：Spring Boot 2.6.4  
数据库：MySQL 8.0.33  
ORM 框架：MyBatis-Plus 3.5.1  
缓存中间件：Redis  
接口文档：Swagger + Knife4j  
实时通信：WebSocket  
JSON 序列化：FastJSON 2.0.49


⚙️ 环境配置
数据库配置（application.yml）
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/yupao?useSSL=false&serverTimezone=UTC
    username: root
    password: 123456

Redis 配置
spring:
  redis:
    host: localhost
    port: 6379
    database: 0

阿里云 OSS 配置
aliyun:
  oss:
    endpoint: oss-cn-beijing.aliyuncs.com
    accessKeyId: ${OSS_ACCESS_KEY_ID}
    accessKeySecret: ${OSS_ACCESS_KEY_SECRET}
    bucketName: akainews

⚠️ 安全提示：建议通过环境变量或配置中心管理密钥信息，避免硬编码敏感数据。

🚀 核心功能
👤 用户模块

注册/登录：支持账号密码验证，安全高效。
个人信息管理：支持修改用户名、头像、个人标签等。
智能推荐：基于标签和匹配算法，推荐潜在伙伴。

👥 队伍模块

创建/解散队伍：快速组建或解散队伍。
加入/退出队伍：支持关键词搜索，灵活加入感兴趣的队伍。
队伍管理：支持多条件筛选队伍列表，管理员可更新队伍信息。

💬 聊天模块

实时私聊：基于 WebSocket 的低延迟通信。
聊天记录：支持持久化存储，随时查看历史消息。


🧪 快速启动

确保 MySQL 和 Redis 已安装并运行。
执行 sql/create_table.sql 初始化数据库表结构。
在 IDE 中运行主类 FindCompanionsApplication。
访问接口文档：http://localhost:8080/api/doc.html（Knife4j）。


🔐 注意事项

根据部署环境选择配置文件（开发：application.yml；生产：application-prod.yml）。
阿里云 OSS 配置建议使用环境变量，避免明文存储密钥。
确保 MySQL 8 配置 serverTimezone=UTC，以避免时间戳偏差。


🙋‍♂️ 项目贡献者

[scrazy_akai]

欢迎提交 Pull Request 或 Issue，共同优化项目！

📬 联系我们
如有建议或问题，请联系：[你的邮箱地址]。