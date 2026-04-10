# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Foo ERP System** - A modern Enterprise Resource Planning system built with Spring Boot and ShardingSphere, featuring user management, role-based access control, database sharding, and distributed session management.

## Technology Stack

- **Java**: 8 (configured in pom.xml)
- **Spring Boot**: 2.7.0
- **ORM**: MyBatis-Plus 3.5.3.1
- **Database**: MySQL 8.0.28, ShardingSphere 5.1.2 (sharding support)
- **Cache/Session**: Redis (distributed session management)
- **Template Engine**: Thymeleaf
- **Security**: Spring Security with role-based access control
- **Build Tool**: Maven 3.6+

## Key Architecture Components

### Project Structure
```
src/
├── main/
│   ├── java/cn/shinefan/fooerp/
│   │   ├── FooErpApplication.java       # Spring Boot entry point
│   │   ├── config/                     # Configuration classes
│   │   │   ├── SecurityConfiguration   # Spring Security config
│   │   │   ├── SessionConfig           # Redis session config
│   │   │   └── PasswordEncoderConfig   # Password encoding config
│   │   ├── model/                      # Entity models
│   │   │   ├── User.java               # User entity (sharded)
│   │   │   ├── Role.java               # Role entity
│   │   │   ├── Customer.java           # Customer entity
│   │   │   └── Product.java            # Product entity
│   │   ├── repository/                 # Data access layer
│   │   │   └── UserRepository.java     # User repository
│   │   ├── service/                    # Business logic layer
│   │   │   ├── UserService/Impl        # User management
│   │   │   ├── RoleService/Impl        # Role management
│   │   │   ├── CustomerService/Impl    # Customer management
│   │   │   └── ProductService/Impl     # Product management
│   │   ├── mapper/                     # MyBatis mappers
│   │   │   ├── UserMapper.java
│   │   │   ├── RoleMapper.java
│   │   │   ├── CustomerMapper.java
│   │   │   └── ProductMapper.java
│   │   ├── web/                        # Web layer
│   │   │   ├── MainController          # Home page
│   │   │   ├── UserRegistrationController
│   │   │   ├── CustomerController
│   │   │   └── ProductController
│   │   └── util/
│   │       └── SnowflakeIdGenerator    # Distributed ID generator
│   └── resources/
│       ├── application.properties      # Application config
│       ├── schema.sql                  # Database schema
│       ├── data.sql                    # Initial data
│       └── templates/                  # Thymeleaf templates
└── test/                               # Unit tests
    ├── UserServiceTest.java
    ├── RoleServiceTest.java
    ├── CustomerServiceTest.java
    └── ProductServiceTest.java
```

### Database Sharding
The system uses ShardingSphere for database sharding:
- **Sharded Tables**: `users`, `users_roles` (sharded by user ID)
- **Sharding Strategy**: Inline sharding algorithm - `users_$->{id % 2}`, `users_roles_$->{user_id % 2}`
- **Default Data Source**: Other tables (roles, customers, products, etc.) use single data source
- **Configuration**: `src/main/resources/application.properties`

### Security Architecture
- Role-based access control (RBAC)
- Users assigned to roles (many-to-many relationship)
- Password encoding with BCrypt
- Distributed session management using Redis

## Common Development Commands

### Build and Run
```bash
# Build the project (with tests)
mvn clean install

# Build without running tests
mvn clean install -DskipTests

# Run the application
mvn spring-boot:run
```

### Testing
```bash
# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=UserServiceTest

# Run a single test method
mvn test -Dtest=UserServiceTest#testGetUserById
```

### Code Quality
```bash
# Check for compile errors
mvn compile

# Run dependency tree
mvn dependency:tree
```

## Test Structure

All service layer tests follow a consistent pattern using:
- JUnit Jupiter (JUnit 5)
- Mockito for mocking
- @SpringBootTest or @ExtendWith(MockitoExtension.class)
- Test classes located in `src/test/java/cn/shinefan/fooerp/service/`

Example test structure:
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @Test
    void testGetUserById_Success() {
        // Test logic
    }
}
```

## Configuration Files

### Application Properties
Key configuration file: `src/main/resources/application.properties`
- Database connection settings
- ShardingSphere configuration
- Redis configuration (localhost:6379)
- Session timeout: 30 minutes (1800 seconds)
- MyBatis-Plus configuration
- SQL logging configuration

## Key Classes and Files

| File | Purpose |
|------|---------|
| `FooErpApplication.java` | Spring Boot application entry point |
| `SecurityConfiguration.java` | Configures Spring Security and authentication |
| `UserServiceImpl.java` | Implements user management with role handling |
| `SessionConfig.java` | Configures Redis-backed distributed sessions |
| `SnowflakeIdGenerator.java` | Generates unique distributed IDs |