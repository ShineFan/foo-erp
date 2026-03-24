# PROJECT KNOWLEDGE BASE

**Generated:** 2026-03-24
**Commit:** 9834a1a
**Branch:** main

## OVERVIEW

Java Spring Boot ERP system with MyBatis-Plus ORM, Spring Security auth, ShardingSphere DB sharding, Redis session. Small project (14 Java files, ~536 LOC).

## STRUCTURE
```
foo-erp/
├── pom.xml                         # Maven build (Spring Boot 2.7.0, Java 8)
├── src/main/java/cn/shinefan/fooerp/
│   ├── FooErpApplication.java     # Main entry
│   ├── config/                    # Security, Session, PasswordEncoder
│   ├── mapper/                    # MyBatis interfaces (UserMapper, RoleMapper)
│   ├── model/                     # Entities (User, Role)
│   ├── repository/                # Spring Data style (UNUSED - mixed pattern)
│   ├── service/                  # Business layer
│   └── web/                       # Controllers + DTOs
└── src/main/resources/
    ├── application.properties     # DB sharding config, Redis session
    ├── schema.sql / data.sql      # SQL init scripts
    └── templates/                  # Thymeleaf HTML
```

## WHERE TO LOOK

| Task | Location | Notes |
|------|----------|-------|
| Add new entity | `model/` + `mapper/` + `service/` | Follow existing 3-layer pattern |
| Add new API | `web/` | Controllers go here (non-standard) |
| Config | `config/` or `application.properties` | Security, session, sharding |
| DB schema | `schema.sql` | Table definitions |

## CODE MAP

| Symbol | Type | Location | Role |
|--------|------|----------|------|
| FooErpApplication | class | FooErpApplication.java | Main entry |
| User | entity | model/User.java | User entity |
| Role | entity | model/Role.java | Role entity |
| UserService | interface | service/UserService.java | User business logic |
| UserMapper | interface | mapper/UserMapper.java | MyBatis mapper |
| SecurityConfiguration | class | config/SecurityConfiguration.java | Spring Security |

## CONVENTIONS (THIS PROJECT)

- **Package structure**: Standard Spring Boot (`config/`, `mapper/`, `model/`, `service/`, `web/`)
- **DAO pattern**: MyBatis mappers in `mapper/` - BUT `repository/` also exists (dead code?)
- **Controller location**: `web/` not `controller/` or `api/` (non-standard)
- **DTO location**: Nested under `web/dto/` not root-level `dto/`
- **Lombok**: Heavily used (`@Data`, `@Mapper`, `@MapperScan`)

## ANTI-PATTERNS (THIS PROJECT)

- **No anti-pattern comments found** - codebase is clean
- **TODO**: Add test directory (`src/test/`) - none exists

## UNIQUE STYLES

- **DB Sharding**: Uses ShardingSphere with 2 shards, Snowflake key generator
- **Session**: Redis-backed with 30-min timeout
- **Mixed patterns**: MyBatis + Spring Data Repository coexist (remove one)

## COMMANDS
```bash
mvn clean package          # Build
mvn spring-boot:run       # Run dev
mvn test                  # Run tests (none exist yet)
```

## NOTES

- README.md says Java 17 / Spring Boot 3.x but pom.xml has Java 8 / Spring Boot 2.7.0 — **mismatch**
- No tests exist yet (src/test/ missing)
- No CI/CD pipeline (no GitHub Actions, no Makefile)
