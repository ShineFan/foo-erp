package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.mapper.RoleMapper;
import cn.shinefan.fooerp.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RoleServiceTest {

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Ensure the service is instantiated with the mocked mapper
        roleService = new RoleServiceImpl();
        // Manually inject mocks since we are not using Spring context
        try {
            java.lang.reflect.Field field = RoleServiceImpl.class.getDeclaredField("roleMapper");
            field.setAccessible(true);
            field.set(roleService, roleMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testFetchRoleById_Success() {
        Long id = 1L;
        Role mockRole = new Role();
        mockRole.setId(id);
        when(roleMapper.selectById(id)).thenReturn(mockRole);

        Optional<Role> result = roleService.getRoleById(id);

        assertTrue(result.isPresent(), "Expected a role to be returned");
        assertEquals(id, result.get().getId(), "Returned role should have the requested id");
    }

    @Test
    void testFetchRoleById_NotFound() {
        Long id = 999L;
        when(roleMapper.selectById(id)).thenReturn(null);

        Optional<Role> result = roleService.getRoleById(id);

        assertFalse(result.isPresent(), "Expected no role when not found");
    }
}
