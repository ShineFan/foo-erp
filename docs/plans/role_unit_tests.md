# Role Module Unit Tests Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Implement comprehensive unit tests for the Role entity and its business logic to ensure correct behavior for role management operations.

**Architecture:** We will follow a Test-Driven Development (TDD) approach. We will target the `RoleService` (business logic layer), mocking the `RoleMapper` (data access layer) to isolate the service logic. We will start with a simple, failing test for a core function (e.g., fetching a role by ID) and iterate until the test passes.

**Tech Stack:** Java 8, Spring Boot 2.7.0, JUnit 5, Mockito, MyBatis-Plus.

---
### Task 1: Test Role Service - Fetch Role by ID

**Files:**
- Modify: `src/main/java/cn/shinefan/fooerp/service/RoleService.java` (If the service doesn't exist, create it)
- Test: `src/test/java/cn/shinefan/fooerp/service/RoleServiceTest.java`

**Step 1: Write the failing test**

In `RoleServiceTest.java`, write a test method `testFetchRoleById_Success` that attempts to call `roleService.getRoleById(id)` and asserts the result is not null, assuming the mock repository returns a role.

```java
@Test
void testFetchRoleById_Success() {
    // Given
    Long roleId = 1L;
    Role mockRole = new Role(); // Assume Role entity is available
    when(roleMapper.selectById(roleId)).thenReturn(Optional.of(mockRole));

    // When
    Optional<Role> result = roleService.getRoleById(roleId);

    // Then
    assertTrue(result.isPresent());
    assertEquals(mockRole.getId(), result.get().getId());
}
```
*Expected: FAIL, likely due to missing implementation in RoleService.*

**Step 2: Run test to verify it fails**

Run: `mvn test -Dtest=RoleServiceTest`
Expected: FAIL, specifically targeting the logic flow in `RoleService`.

**Step 3: Write minimal implementation**

Modify `RoleService.java` to implement `getRoleById(Long id)` by calling `roleMapper.selectById(id)` and wrapping the result in an `Optional`.

```java
// Inside RoleService.java
@Override
public Optional<Role> getRoleById(Long id) {
    return roleMapper.selectById(id);
}
```

**Step 4: Run test to verify it passes**

Run: `mvn test -Dtest=RoleServiceTest`
Expected: PASS

**Step 5: Commit**

```bash
mvn clean package # Ensure build succeeds
git add src/main/java/cn/shinefan/fooerp/service/RoleService.java src/test/java/cn/shinefan/fooerp/service/RoleServiceTest.java
git commit -m "feat: add unit tests for RoleService getRoleById"
```

### Task 2: Test Role Service - Handle Role Not Found

**Files:**
- Modify: `src/main/java/cn/shinefan/fooerp/service/RoleService.java`
- Test: `src/test/java/cn/shinefan/fooerp/service/RoleServiceTest.java`

**Step 1: Write the failing test**

Write a test method `testFetchRoleById_NotFound` that calls `roleService.getRoleById(nonExistentId)` and asserts the result is empty (`Optional.empty()`).

**Step 2: Run test to verify it fails**

Run: `mvn test -Dtest=RoleServiceTest`
Expected: FAIL (if implementation is missing or incorrect).

**Step 3: Write minimal implementation**

Ensure the implementation in `RoleService.java` correctly handles the case where `roleMapper.selectById(id)` returns an empty result, maintaining the `Optional` contract.

**Step 4: Run test to verify it passes**

Run: `mvn test -Dtest=RoleServiceTest`
Expected: PASS

**Step 5: Commit**

```bash
mvn clean package
git add src/main/java/cn/shinefan/fooerp/service/RoleService.java src/test/java/cn/shinefan/fooerp/service/RoleServiceTest.java
git commit -m "fix: refine RoleService to handle not found roles"
```