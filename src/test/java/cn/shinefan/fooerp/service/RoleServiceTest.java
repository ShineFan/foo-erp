package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.mapper.RoleMapper;
import cn.shinefan.fooerp.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role mockRole;

    @BeforeEach
    void setUp() {
        mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setName("ROLE_USER");
    }

    @Test
    void testFetchRoleById_Success() {
        Long id = 1L;
        when(roleMapper.selectById(id)).thenReturn(mockRole);

        Optional<Role> result = roleService.getRoleById(id);

        assertTrue(result.isPresent(), "Expected role to be present");
        assertEquals(id, result.get().getId());
    }

    @Test
    void testFetchRoleById_NotFound() {
        Long id = 999L;
        when(roleMapper.selectById(id)).thenReturn(null);

        Optional<Role> result = roleService.getRoleById(id);

        assertFalse(result.isPresent(), "Expected role to be absent");
    }
}
