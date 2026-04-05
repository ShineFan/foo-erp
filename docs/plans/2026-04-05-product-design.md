# Product API Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Deliver a backend-only REST API for Product at /api/products with full CRUD and pagination, using MyBatis-Plus, Snowflake IDs, and a Snowflake-based sharding strategy across ds_0 and ds_1. No Lombok. Constructor-injected dependencies. Snake_case DB columns. All code follows the 3-layer pattern: Product (entity), ProductDto, ProductMapper, ProductService, ProductServiceImpl, and ProductController.

**Architecture:** The system is a clean 3-layer Spring Boot app: REST controller -> service layer (constructor-injected) -> MyBatis-Plus mapper -> database. Data is persisted to snake_case columns. A Snowflake ID generator provides IDs; routing to ds_0 or ds_1 is determined by mod(id, 2). Pagination is supported on list endpoints via MyBatis-Plus Page. No Thymeleaf views—API returns JSON only.

**Tech Stack:** Java 8, Spring Boot 2.x, MyBatis-Plus, Snowflake ID generator, ShardingSphere (or simple DataSource router), Jackson, JUnit 5. No Lombok; explicit JavaBean-style POJOs. 

---

## Task Breakdown

Each task below is atomic and independent. Use constructor injection for all dependencies. Create or modify only the files listed. Do not introduce Lombok or new frameworks beyond what's specified.

## Task 1: Domain primitives – Product entity and DTO

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/product/model/Product.java`
- Create: `src/main/java/cn/shinefan/fooerp/product/dto/ProductDto.java`

**GOAL:** Define the Product domain with explicit fields and plain getters/setters (no Lombok).

**Context:** This establishes the data model and the JSON payload contract for API interactions.

**Step 1: Create skeletons**
**Step 2: Ensure fields are serializable and use snake_case column names via explicit @TableField mappings.**

**EXPECTED OUTCOME:** Both classes compile with no Lombok and reflect the core fields: id, name, description, price, stock, createdAt, updatedAt.

**REQUIRED TOOLS:** Java 8, Maven, IDE, compilation in project build.
**MUST DO:** Implement explicit getters/setters. Do not annotate with Lombok. Use `java.math.BigDecimal` for price and `java.time.LocalDateTime` for timestamps.
**MUST NOT DO:** Use Lombok annotations or any implicit code generation.

---

## Task 2: Product converter – DTO <-> Entity mapping

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/product/mapper/ProductConverter.java`

**GOAL:** Provide explicit, single-responsibility mapping between `Product` and `ProductDto` without Lombok.

**CONTEXT:** Keeps a clean separation between persistence model and API contract.

**EXPECTED OUTCOME:** A small utility with two methods: `toEntity(ProductDto dto)` and `toDto(Product entity)`.

**REQUIRED TOOLS:** Basic Java, no external dependencies.
**MUST DO:** Implement as a final class with static methods or as a minimal spring component if preferred (but no injection required).
**MUST NOT DO:** Introduce circular dependencies or heavy frameworks.

---

## Task 3: Snowflake ID generator

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/product/util/SnowflakeIdGenerator.java`

**GOAL:** Provide a reliable, thread-safe Snowflake ID generator for primary keys.

**CONTEXT:** IDs must be unique globally and suitable for sharding routing (id % 2).

**EXPECTED OUTCOME:** A class with a method `public synchronized long nextId()` that returns unique IDs. Support 41-bit timestamp, 10-bit worker, 12-bit sequence as a typical Snowflake variant.

**REQUIRED TOOLS:** Java 8, no external libs.
**MUST DO:** Implement minimal but correct Snowflake-like generator.
**MUST NOT DO:** Rely on Lombok or external libraries.

---

## Task 4: ProductMapper – MyBatis-Plus mapper

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/product/mapper/ProductMapper.java`

**GOAL:** Define the MyBatis-Plus mapper for Product with basic CRUD inherited methods.

**CONTEXT:** This bridges service layer with the database.

**EXPECTED OUTCOME:** `ProductMapper` extends `BaseMapper<Product>`. Annotate with appropriate package and imports.

**REQUIRED TOOLS:** MyBatis-Plus in project dependencies.
**MUST DO:** Use explicit imports; no Lombok.
**MUST NOT DO:** Add custom SQL files unless necessary for tests.

---

## Task 5: ProductService interface

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/product/service/ProductService.java`

**GOAL:** Define the contract for product operations: create, read, update, delete, and list with pagination.

**CONTEXT:** Provides abstraction for business logic and testability.

**EXPECTED OUTCOME:** Interface with methods like: `ProductDto create(ProductDto dto)`, `ProductDto getById(Long id)`, `ProductDto update(Long id, ProductDto dto)`, `void delete(Long id)`, `Page<ProductDto> list(int page, int size)`.

**REQUIRED TOOLS:** Java 8; MyBatis-Plus Page type.
**MUST DO:** Use constructor injection in implementation; no Lombok.
**MUST NOT DO:** Tie to web layer in service layer.

---

## Task 6: ProductServiceImpl – implementation with constructor injection

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/product/service/ProductServiceImpl.java`

**GOAL:** Implement the service using constructor-based injection for mapper and utilities.

**CONTEXT:** Encapsulates business rules and coordinates mapping between entity and DTO.

**EXPECTED OUTCOME:** Fully functional `create`, `getById`, `update`, `delete`, and `list` methods using `ProductMapper`, `ProductConverter`, and `SnowflakeIdGenerator`.

**REQUIRED TOOLS:** Spring context; MyBatis-Plus; Java 8.
**MUST DO:** Use explicit mapping via `ProductConverter`; generate IDs with `SnowflakeIdGenerator` when creating new records.
**MUST NOT DO:** Bypass constructor injection or rely on field injection.

---

## Task 7: REST Controller – API surface at /api/products

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/product/controller/ProductController.java`

**GOAL:** Expose full CRUD via REST endpoints with JSON payloads and pagination support.

**CONTEXT:** Implements the API surface described in the design: POST /api/products, GET /api/products/{id}, PUT /api/products/{id}, DELETE /api/products/{id}, GET /api/products?page=&size=.

**EXPECTED OUTCOME:** Endpoints wired to `ProductService`; proper HTTP status codes; input validation; pagination parameters accepted; responses serialized as `ProductDto`.

**REQUIRED TOOLS:** Spring Web, Jackson, Java 8.
**MUST DO:** Use constructor injection for `ProductService`. Use `@RequestBody` for create/update, `@PathVariable` for IDs.
**MUST NOT DO:** Return HTML or view templates; no Lombok usage.

---

## Task 8: Sharding router and data sources

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/config/ShardRoutingConfig.java`

**GOAL:** Route reads/writes to ds_0 or ds_1 based on the product id using `mod(id, 2)` as the shard key.

**CONTEXT:** Enables horizontal partitioning across two data sources.

**EXPECTED OUTCOME:** A simple routing data source or a Spring configuration that selects the correct `DataSource` instance based on the ID supplied to the DAO layer.

**REQUIRED TOOLS:** Spring Boot, JDBC, DataSource configuration.
**MUST DO:** Implement deterministic routing when creating or accessing by id.
**MUST NOT DO:** Introduce a heavy custom ORM; keep it simple and testable.

---

## Task 9: Database schema – product table

**Files:**
- Update: `src/main/resources/schema.sql`

**GOAL:** Define the product table with snake_case columns.

**Context:** The persistence schema must align with the entity field mappings.

**EXPECTED OUTCOME:** SQL statements to create table `product` with columns: `id BIGINT`, `name VARCHAR`, `description TEXT`, `price DECIMAL(19,2)`, `stock INT`, `created_at TIMESTAMP`, `updated_at TIMESTAMP`.

**REQUIRED TOOLS:** Database container or dev DB; migration script loaded on startup.
**MUST DO:** Use snake_case for all columns; allow indexing on `name` and `created_at` if needed.
**MUST NOT DO:** Use non-descriptive column names.

---

## Task 10: MyBatis-Plus configuration for snake_case mapping

**Files:**
- Update: `src/main/resources/application.yml` (or properties)

**GOAL:** Ensure MyBatis-Plus respects snake_case columns without Lombok.

**EXPECTED OUTCOME:** Configuration section enabling underscore-to-camel-case mapping if desired, and explicit mapping hints on entity fields if needed.

**REQUIRED TOOLS:** Spring Boot configuration.
**MUST DO:** Do not enable Lombok; rely on explicit getters/setters.
**MUST NOT DO:** Introduce extra transformers beyond scope.

---

## Task 11: Validation and error handling basics

**Files:**
- Update: `src/main/java/cn/shinefan/fooerp/product/controller/ProductController.java`
- Create: `src/main/java/cn/shinefan/fooerp/product/exception/ApiException.java`
- Create: `src/main/java/cn/shinefan/fooerp/product/exception/ApiErrorResponse.java`

**GOAL:** Provide simple validation and standardized error responses.

**EXPECTED OUTCOME:** Controller validates input;, when invalid, returns 400 with a clear error payload.

**REQUIRED TOOLS:** Java 8, Spring Validation.
**MUST DO:** Include at least basic error handling for not found and bad requests.
**MUST NOT DO:** Add heavy exception hierarchies beyond scope.

---

## Task 12: Tests – skeletons for service and controller

**Files:**
- Create: `src/test/java/cn/shinefan/fooerp/product/service/ProductServiceTest.java`
- Create: `src/test/java/cn/shinefan/fooerp/product/controller/ProductControllerTest.java`

**GOAL:** Provide unit-test scaffolding to drive development via TDD.

**EXPECTED OUTCOME:** Test skeletons with basic tests for create/get/list; mocks for mapper and id generator.

**REQUIRED TOOLS:** JUnit 5, Mockito.
**MUST DO:** Outline tests that will fail first, then implement minimal code to satisfy.
**MUST NOT DO:** Bloat tests with integration concerns yet.

---

## Task 13: Build and verification plan

**Files:**
- No new files; update the existing build config if needed (pom.xml)

**GOAL:** Confirm compile passes, tests (where present) pass, and the package builds into a jar.

**EXPECTED OUTCOME:** `mvn -q clean package` completes with exit code 0; `mvn test` runs without failures (or provides a plan for added tests).

**REQUIRED TOOLS:** Maven, JDK 8.
**MUST DO:** Run build locally as part of every change.
**MUST NOT DO:** Skip verification steps.

---

## Task 14: Documentation and handoff

**Files:**
- Update: `docs/plans/2026-04-05-product-design.md` (plan content itself)

**GOAL:** Provide a complete, actionable plan for developers with clear success criteria.

**CONTEXT:** This file now contains the plan to implement the feature end-to-end.

**EXPECTED OUTCOME:** A living plan that can be consumed by the subagent-driven-development workflow.

**REQUIRED TOOLS:** Markdown; Git for commits.
**MUST DO:** Ensure plan is up-to-date and accurate to the architecture.
**MUST NOT DO:** Leave gaps in instructions or assumptions.

---

## Task 15: Final verification and handover

**Files:**
- No new files; run verification commands.

**GOAL:** Validate the plan and prepare for execution by the team.

**EXPECTED OUTCOME:** Plan approved for execution; readiness to start with Task 1.

**REQUIRED TOOLS:** Git; Maven; Java.
**MUST DO:** Provide a quick execution guide for the first task; confirm success criteria for the plan as a whole.
**MUST NOT DO:** Skip documentation.

---

## Next actions

- Plan complete and saved to docs/plans/2026-04-05-product-design.md. Two execution options: Subagent-Driven in this session or Parallel Session in a fresh worktree. Which approach would you like? If you choose Subagent-Driven, I will start with Task 1 and spin up a fresh subagent per task with code reviews between steps.
