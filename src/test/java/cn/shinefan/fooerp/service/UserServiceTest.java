package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.mapper.RoleMapper;
import cn.shinefan.fooerp.mapper.UserMapper;
import cn.shinefan.fooerp.model.Role;
import cn.shinefan.fooerp.model.User;
import cn.shinefan.fooerp.repository.UserRepository;
import cn.shinefan.fooerp.web.dto.UserRegistrationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试
 */
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ==================== save() 测试 ====================

    @Test
    void testSave_Success() {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto("John", "Doe", "john@example.com", "password123");
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");

        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(roleMapper.findByName("ROLE_USER")).thenReturn(userRole);
        when(userMapper.insertUserRole(any(), anyLong())).thenReturn(1);
        // 模拟 MyBatis-Plus insert 后设置 ID
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(100L);
            return 1;
        }).when(userMapper).insert(any(User.class));

        // Act
        User result = userService.save(dto);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("$2a$10$encodedPassword", result.getPassword());
        verify(userMapper).insert(any(User.class));
        verify(userMapper).insertUserRole(any(), eq(1L));
    }

    @Test
    void testSave_EncryptsPassword() {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto("Test", "User", "test@example.com", "plainPassword");
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encryptedPassword");
        when(roleMapper.findByName("ROLE_USER")).thenReturn(userRole);

        // Act
        userService.save(dto);

        // Assert
        verify(passwordEncoder).encode("plainPassword");
        verify(userMapper).insert(argThat(user -> "encryptedPassword".equals(user.getPassword())));
    }

    @Test
    void testSave_AssignsDefaultRole() {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto("Alice", "Smith", "alice@example.com", "pass");
        Role userRole = new Role();
        userRole.setId(2L);
        userRole.setName("ROLE_USER");

        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(roleMapper.findByName("ROLE_USER")).thenReturn(userRole);

        // Act
        User result = userService.save(dto);

        // Assert
        assertNotNull(result.getRoles());
        assertEquals(1, result.getRoles().size());
        assertEquals("ROLE_USER", result.getRoles().iterator().next().getName());
    }

    @Test
    void testSave_ThrowsExceptionWhenRoleNotFound() {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto("Test", "User", "test@example.com", "password");

        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(roleMapper.findByName("ROLE_USER")).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.save(dto);
        });
        assertEquals("Default role not found", exception.getMessage());
    }

    // ==================== loadUserByUsername() 测试 ====================

    @Test
    void testLoadUserByUsername_Success() {
        // Arrange
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encodedPassword");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        user.setRoles(java.util.Arrays.asList(role));

        when(userRepository.findByEmail("user@example.com")).thenReturn(user);

        // Act
        UserDetails userDetails = userService.loadUserByUsername("user@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals("user@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_USER".equals(auth.getAuthority())));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistent@example.com");
        });
        assertEquals("Invalid username or password.", exception.getMessage());
    }

    @Test
    void testLoadUserByUsername_MultipleRoles() {
        // Arrange
        User user = new User();
        user.setEmail("admin@example.com");
        user.setPassword("encodedPassword");

        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        user.setRoles(java.util.Arrays.asList(userRole, adminRole));

        when(userRepository.findByEmail("admin@example.com")).thenReturn(user);

        // Act
        UserDetails userDetails = userService.loadUserByUsername("admin@example.com");

        // Assert
        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_USER".equals(auth.getAuthority())));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority())));
    }

    @Test
    void testLoadUserByUsername_EmptyRoles() {
        // Arrange
        User user = new User();
        user.setEmail("norole@example.com");
        user.setPassword("encodedPassword");
        user.setRoles(java.util.Collections.emptyList());

        when(userRepository.findByEmail("norole@example.com")).thenReturn(user);

        // Act
        UserDetails userDetails = userService.loadUserByUsername("norole@example.com");

        // Assert
        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    // ==================== 边界条件测试 ====================

    @Test
    void testSave_WithEmptyFields() {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto("", "", "empty@example.com", "");
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");

        when(passwordEncoder.encode("")).thenReturn("emptyHash");
        when(roleMapper.findByName("ROLE_USER")).thenReturn(userRole);

        // Act
        User result = userService.save(dto);

        // Assert
        assertNotNull(result);
        assertEquals("", result.getFirstName());
        assertEquals("", result.getLastName());
    }

    @Test
    void testLoadUserByUsername_WithNullRoles() {
        // Arrange
        User user = new User();
        user.setEmail("nullrole@example.com");
        user.setPassword("encodedPassword");
        user.setRoles(null);

        when(userRepository.findByEmail("nullrole@example.com")).thenReturn(user);

        // Act & Assert - should handle null roles gracefully or throw NPE
        assertThrows(NullPointerException.class, () -> {
            userService.loadUserByUsername("nullrole@example.com");
        });
    }

    @Test
    void testSave_CallsInsertUserRole() {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto("Test", "User", "test@example.com", "password");
        Role userRole = new Role();
        userRole.setId(5L);
        userRole.setName("ROLE_USER");

        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(roleMapper.findByName("ROLE_USER")).thenReturn(userRole);
        // 模拟 MyBatis-Plus insert 后设置 ID
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(200L);
            return 1;
        }).when(userMapper).insert(any(User.class));

        // Act
        userService.save(dto);

        // Assert
        verify(userMapper).insertUserRole(any(), eq(5L));
    }
}
