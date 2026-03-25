# Foo ERP 系统技术详细设计文档

## 1. 项目概述

### 1.1 项目简介

Foo ERP 是一个基于 Spring Boot 的企业资源规划（ERP）系统，提供用户管理、客户管理等核心功能。系统采用现代化的技术栈，支持数据库分片和分布式会话管理。

### 1.2 技术栈

| 层级 | 技术选型 | 版本 |
|------|----------|------|
| 后端框架 | Spring Boot | 2.7.0 |
| Java 版本 | Java | 8 |
| ORM 框架 | MyBatis-Plus | 3.5.3.1 |
| 数据库 | MySQL | 8.0+ |
| 缓存/会话 | Redis | 6.0+ |
| 数据库中间件 | ShardingSphere | 5.1.2 |
| 模板引擎 | Thymeleaf | Spring Boot 内置 |
| 安全框架 | Spring Security | Spring Boot 内置 |
| 构建工具 | Maven | 3.6+ |
| 代码生成 | Lombok | 1.18.30 |

---

## 2. 系统架构

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                      Presentation Layer                       │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────────┐│
│  │MainController│ │CustomerController│ │UserRegistrationController││
│  └─────────────┘ └─────────────┘ └─────────────────────────┘│
│                    Thymeleaf Templates                       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      Service Layer                           │
│  ┌─────────────┐ ┌───────────────────┐                     │
│  │UserService  │ │CustomerService     │                     │
│  └─────────────┘ └───────────────────┘                     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      Mapper Layer                            │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐   │
│  │UserMapper   │ │RoleMapper   │ │CustomerMapper       │   │
│  └─────────────┘ └─────────────┘ └─────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    ShardingSphere                            │
│         (Database Sharding Middleware)                       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      Data Source                             │
│  ┌─────────────────────┐  ┌─────────────────────┐           │
│  │   ds0 (users_0)     │  │   ds0 (users_1)     │           │
│  │   ds0 (users_roles_0)│  │  ds0 (users_roles_1)│          │
│  │   ds0 (customer_0) │  │   ds0 (customer_1)  │           │
│  └─────────────────────┘  └─────────────────────┘           │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 分层架构说明

#### 表现层（Web Layer）
- **位置**: `cn.shinefan.fooerp.web`
- **职责**: 处理 HTTP 请求，返回视图或 JSON 响应
- **组件**:
  - `MainController`: 首页和登录页面路由
  - `UserRegistrationController`: 用户注册功能
  - `CustomerController`: 客户管理 CRUD 操作

#### 服务层（Service Layer）
- **位置**: `cn.shinefan.fooerp.service`
- **职责**: 业务逻辑处理，事务管理
- **组件**:
  - `UserService`: 用户服务，集成 Spring Security UserDetailsService
  - `CustomerService`: 客户管理服务

#### 数据访问层（Mapper Layer）
- **位置**: `cn.shinefan.fooerp.mapper`
- **职责**: 数据库操作，SQL 执行
- **技术**: MyBatis-Plus BaseMapper + 自定义注解

#### 实体层（Model Layer）
- **位置**: `cn.shinefan.fooerp.model`
- **职责**: 数据模型定义
- **组件**: User, Role, Customer

---

## 3. 数据库设计

### 3.1 数据表结构

#### 3.1.1 用户表（分片）
```sql
CREATE TABLE users_0 (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE users_1 (
  -- 结构同上
);
```

**分片策略**:
- 分片键: `id`
- 算法: `users_${id % 2}`
- 分布式 ID: Snowflake 算法

#### 3.1.2 角色表（不分片）
```sql
CREATE TABLE role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 3.1.3 用户角色关联表（分片）
```sql
CREATE TABLE users_roles_0 (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE users_roles_1 (
  -- 结构同上
);
```

**分片策略**:
- 分片键: `user_id`
- 算法: `users_roles_${user_id % 2}`

#### 3.1.4 客户表（分片）
```sql
CREATE TABLE customer_0 (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  phone VARCHAR(255),
  address VARCHAR(255),
  company VARCHAR(255),
  status VARCHAR(50) DEFAULT 'ACTIVE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE customer_1 (
  -- 结构同上
);
```

### 3.2 分片配置详情

```properties
# 数据源配置
spring.shardingsphere.datasource.names=ds0
spring.shardingsphere.datasource.ds0.url=jdbc:mysql://localhost:3306/foo_erp

# 用户表分片规则
spring.shardingsphere.rules.sharding.tables.users.actual-data-nodes=ds0.users_$->{0..1}
spring.shardingsphere.rules.sharding.tables.users.table-strategy.standard.sharding-column=id
spring.shardingsphere.rules.sharding.tables.users.key-generate-strategy.key-generator-name=snowflake

# 用户角色关联表分片规则
spring.shardingsphere.rules.sharding.tables.users_roles.actual-data-nodes=ds0.users_roles_$->{0..1}
spring.shardingsphere.rules.sharding.tables.users_roles.table-strategy.standard.sharding-column=user_id

# 绑定表（避免笛卡尔积）
spring.shardingsphere.rules.sharding.binding-tables[0]=users,users_roles
```

---

## 4. 安全架构

### 4.1 认证机制

#### 4.1.1 技术选型
- **认证方式**: 表单登录（Form Login）
- **密码加密**: BCrypt
- **会话管理**: 分布式 Redis 会话

#### 4.1.2 认证流程
```
用户请求 ──▶ 登录页面 ──▶ 提交凭证
    │
    ▼
Spring Security ──▶ DaoAuthenticationProvider
    │
    ▼
UserService.loadUserByUsername() ──▶ 查询用户信息
    │
    ▼
BCryptPasswordEncoder 验证密码
    │
    ▼
生成 Security Context ──▶ 存入 Redis Session
```

#### 4.1.3 安全配置要点

```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    // 1. 自定义 UserDetailsService
    // 2. BCrypt 密码编码器
    // 3. 登录页面 /login（公开）
    // 4. 其他请求需要认证
    // 5. 记住我功能（1天有效期）
    // 6. 单用户登录（maximumSessions=1）
}
```

### 4.2 授权配置

| 路径 | 权限 | 说明 |
|------|------|------|
| `/login` | 公开 | 登录页面 |
| `/registration**` | 公开 | 注册页面 |
| `/js/**`, `/css/**`, `/img/**` | 公开 | 静态资源 |
| `/**` | 已认证 | 其他请求需要登录 |

### 4.3 密码安全

- **加密算法**: BCrypt（自动加盐）
- **强度**: 默认强度 10
- **存储**: 完整 bcrypt 哈希值存储

---

## 5. 会话管理

### 5.1 Redis 会话存储

```java
@Configuration
@EnableRedisHttpSession
public class SessionConfig {
    // Redis 连接工厂
    @Bean
    public LettuceConnectionFactory connectionFactory();
    
    // Cookie 配置
    @Bean
    public CookieSerializer cookieSerializer();
}
```

### 5.2 配置参数

| 配置项 | 值 | 说明 |
|--------|-----|------|
| `spring.session.store-type` | redis | 会话存储类型 |
| `spring.session.redis.flush-mode` | on-save | 刷新模式 |
| `spring.session.redis.namespace` | spring:session | Redis 键命名空间 |
| `server.servlet.session.timeout` | 1800s | 会话超时（30分钟） |

### 5.3 分布式会话优势

1. **多节点部署**: 支持水平扩展，多个应用实例共享会话
2. **Session 持久化**: 重启应用不会丢失会话
3. **跨域支持**: 支持子域名共享会话

---

## 6. API 设计

### 6.1 用户注册接口

```
POST /registration
Content-Type: application/x-www-form-urlencoded

参数:
- firstName: String (必填)
- lastName: String (必填)
- email: String (必填，唯一)
- password: String (必填)

响应: 
- 成功: redirect:/registration?success
- 失败: 返回注册页面并显示错误
```

### 6.2 客户管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/customers` | 客户列表 |
| GET | `/customers/new` | 新建客户表单 |
| POST | `/customers` | 创建客户 |
| GET | `customers/{id}/edit` | 编辑客户表单 |
| POST | `customers/{id}` | 更新客户 |
| GET | `customers/{id}/delete` | 删除客户 |

### 6.3 页面路由

| 方法 | 路径 | 视图 | 说明 |
|------|------|------|------|
| GET | `/` | index | 首页 |
| GET | `/login` | login | 登录页 |
| GET | `/registration` | registration | 注册页 |
| GET | `/customers` | customers | 客户列表页 |
| GET | `/customers/new` | customer-form | 客户表单页 |
| GET | `customers/{id}/edit` | customer-form | 客户编辑页 |

---

## 7. 核心模块设计

### 7.1 用户模块

#### 7.1.1 数据模型

```java
@TableName("users")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("first_name")
    private String firstName;
    
    @TableField("last_name")
    private String lastName;
    
    @TableField("email")
    private String email;
    
    @TableField("password")
    private String password;
    
    @TableField(exist = false)
    private Collection<Role> roles;
}
```

#### 7.1.2 服务实现

```java
@Service
public class UserServiceImpl implements UserService {
    // 1. 注册用户
    //    - 创建 User 实体
    //    - 密码 BCrypt 加密
    //    - 插入数据库
    //    - 分配 ROLE_USER 角色
    
    // 2. 加载用户（Spring Security）
    //    - 根据 email 查询
    //    - 转换为 Spring Security UserDetails
    //    - 映射角色为 GrantedAuthority
}
```

### 7.2 客户模块

#### 7.2.1 数据模型

```java
@TableName("customer")
public class Customer {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String company;
    private String status;  // ACTIVE, INACTIVE
}
```

#### 7.2.2 服务接口

```java
public interface CustomerService {
    Customer save(Customer customer);      // 创建
    Customer update(Customer customer);    // 更新
    void delete(Long id);                  // 删除
    Customer findById(Long id);            // 按ID查询
    Customer findByEmail(String email);    // 按邮箱查询
    List<Customer> findAll();               // 全部查询
    List<Customer> findByStatus(String status);  // 按状态查询
}
```

---

## 8. 配置说明

### 8.1 应用配置（application.properties）

```properties
# 服务器
server.port=8080
server.servlet.session.timeout=1800

# 数据库（通过 ShardingSphere）
spring.shardingsphere.datasource.ds0.url=jdbc:mysql://localhost:3306/foo_erp

# MyBatis-Plus
mybatis-plus.type-aliases-package=cn.shinefan.fooerp.model
mybatis-plus.configuration.map-underscore-to-camel-case=true

# Redis
spring.redis.host=localhost
spring.redis.port=6379

# 会话
spring.session.store-type=redis
spring.session.redis.flush-mode=on-save

# Thymeleaf（开发模式）
spring.thymeleaf.cache=false

# 日志
logging.level.cn.shinefan.fooerp.mapper=DEBUG
```

### 8.2 ShardingSphere 分片算法

| 表名 | 分片键 | 算法 | 说明 |
|------|--------|------|------|
| users | id | `users_${id % 2}` | 按 ID 奇偶分片 |
| users_roles | user_id | `users_roles_${user_id % 2}` | 按 user_id 关联分片 |
| customer | 未配置 | - | 实际有分片，但配置缺失 |

---

## 9. 项目目录结构

```
foo-erp/
├── pom.xml                              # Maven 配置
├── src/main/
│   ├── java/cn/shinefan/fooerp/
│   │   ├── FooErpApplication.java      # 启动类
│   │   ├── config/                     # 配置类
│   │   │   ├── SecurityConfiguration.java
│   │   │   ├── SessionConfig.java
│   │   │   └── PasswordEncoderConfig.java
│   │   ├── model/                      # 实体类
│   │   │   ├── User.java
│   │   │   ├── Role.java
│   │   │   └── Customer.java
│   │   ├── mapper/                     # MyBatis Mapper
│   │   │   ├── UserMapper.java
│   │   │   ├── RoleMapper.java
│   │   │   └── CustomerMapper.java
│   │   ├── service/                    # 服务层
│   │   │   ├── UserService.java
│   │   │   ├── UserServiceImpl.java
│   │   │   ├── CustomerService.java
│   │   │   └── CustomerServiceImpl.java
│   │   ├── repository/                  # 数据访问仓库（混合模式）
│   │   │   └── UserRepository.java
│   │   └── web/                         # 控制器层
│   │       ├── MainController.java
│   │       ├── UserRegistrationController.java
│   │       ├── CustomerController.java
│   │       └── dto/                     # 数据传输对象
│   │           ├── UserRegistrationDto.java
│   │           └── CustomerDto.java
│   └── resources/
│       ├── application.properties       # 应用配置
│       ├── schema.sql                    # 数据库 Schema
│       ├── data.sql                      # 初始数据
│       └── templates/                   # Thymeleaf 模板
│           ├── login.html
│           ├── registration.html
│           ├── index.html
│           ├── customers.html
│           └── customer-form.html
└── docs/                                 # 文档
    └── technical-design.md              # 本文档
```

---

## 10. 已知问题与改进建议

### 10.1 文档与实现不一致

| 问题 | 现状 | 文档说明 |
|------|------|----------|
| Java 版本 | pom.xml: Java 8 | README: Java 17 |
| Spring Boot | 2.7.0 | README: 3.x |

**建议**: 统一文档与实际实现

### 10.2 架构模式混用

- 同时存在 `mapper/` 和 `repository/` 目录
- `UserServiceImpl` 同时依赖两者

**建议**: 统一使用 MyBatis-Plus 的 Mapper 模式，移除 repository 封装

### 10.3 缺失功能

1. **单元测试**: 无 `src/test/` 目录
2. **错误处理**: 缺少全局异常处理器
3. **分片配置不完整**: customer 表在 schema.sql 有分片，但 application.properties 未配置
4. **静态资源**: 缺少 `js/`, `css/`, `img/` 目录

### 10.4 安全建议

1. 密码重置功能缺失
2. 邮箱验证功能缺失
3. 登录尝试限制缺失（防暴力破解）
4. CSRF 保护需验证

### 10.5 可扩展性建议

1. 考虑使用 Spring Data JPA 替代 MyBatis-Plus（如果需要更复杂的查询）
2. 添加 API 文档（Swagger/OpenAPI）
3. 添加请求日志/审计功能
4. 考虑使用 Spring Cache 优化查询性能

---

## 11. 附录

### 11.1 启动命令

```bash
# 构建
mvn clean package

# 运行
mvn spring-boot:run

# 测试
mvn test
```

### 11.2 默认数据

| 角色 | 说明 |
|------|------|
| ROLE_USER | 普通用户 |
| ROLE_ADMIN | 管理员 |

---

*文档版本: 1.0*
*生成日期: 2026-03-24*
