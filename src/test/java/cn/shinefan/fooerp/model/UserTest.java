package cn.shinefan.fooerp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * User 实体单元测试
 */
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testDefaultConstructor() {
        User newUser = new User();
        assertNull(newUser.getId());
        assertNull(newUser.getFirstName());
        assertNull(newUser.getLastName());
        assertNull(newUser.getEmail());
        assertNull(newUser.getPassword());
        assertNull(newUser.getRoles());
    }

    @Test
    void testParameterizedConstructor() {
        Role role1 = new Role();
        role1.setName("USER");
        Role role2 = new Role();
        role2.setName("ADMIN");
        Collection<Role> roles = Arrays.asList(role1, role2);

        User newUser = new User("John", "Doe", "john@example.com", "password123", roles);

        assertNull(newUser.getId()); // ID is auto-generated
        assertEquals("John", newUser.getFirstName());
        assertEquals("Doe", newUser.getLastName());
        assertEquals("john@example.com", newUser.getEmail());
        assertEquals("password123", newUser.getPassword());
        assertEquals(2, newUser.getRoles().size());
    }

    @Test
    void testSetAndGetId() {
        Long id = 1L;
        user.setId(id);
        assertEquals(id, user.getId());
    }

    @Test
    void testSetAndGetFirstName() {
        String firstName = "Alice";
        user.setFirstName(firstName);
        assertEquals(firstName, user.getFirstName());
    }

    @Test
    void testSetAndGetLastName() {
        String lastName = "Smith";
        user.setLastName(lastName);
        assertEquals(lastName, user.getLastName());
    }

    @Test
    void testSetAndGetEmail() {
        String email = "alice@example.com";
        user.setEmail(email);
        assertEquals(email, user.getEmail());
    }

    @Test
    void testSetAndGetPassword() {
        String password = "securePassword";
        user.setPassword(password);
        assertEquals(password, user.getPassword());
    }

    @Test
    void testSetAndGetRoles() {
        Role role = new Role();
        role.setName("ADMIN");
        Collection<Role> roles = new ArrayList<>();
        roles.add(role);

        user.setRoles(roles);

        assertNotNull(user.getRoles());
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void testSetEmptyRoles() {
        Collection<Role> roles = new ArrayList<>();
        user.setRoles(roles);

        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void testSetNullRoles() {
        user.setRoles(null);
        assertNull(user.getRoles());
    }

    @Test
    void testToString() {
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setPassword("pass");

        String str = user.toString();

        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("firstName='Test'"));
        assertTrue(str.contains("lastName='User'"));
        assertTrue(str.contains("email='test@example.com'"));
    }

    @Test
    void testToStringWithRoles() {
        Role role = new Role();
        role.setName("USER");
        user.setId(1L);
        user.setFirstName("Test");
        user.setRoles(Arrays.asList(role));

        String str = user.toString();

        assertTrue(str.contains("roles="));
    }

    @Test
    void testMultipleUsersIndependence() {
        User user1 = new User();
        User user2 = new User();

        user1.setFirstName("User1");
        user2.setFirstName("User2");

        assertEquals("User1", user1.getFirstName());
        assertEquals("User2", user2.getFirstName());
        assertNotEquals(user1.getFirstName(), user2.getFirstName());
    }

    @Test
    void testUserWithEmailContainingAtSymbol() {
        user.setEmail("valid.email@test.com");
        assertTrue(user.getEmail().contains("@"));
    }

    @Test
    void testPasswordNotNull() {
        user.setPassword("nonNullPassword");
        assertNotNull(user.getPassword());
    }
}
