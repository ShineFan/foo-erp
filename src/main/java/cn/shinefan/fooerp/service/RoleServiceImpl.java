package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.mapper.RoleMapper;
import cn.shinefan.fooerp.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Optional<Role> getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        return Optional.ofNullable(role);
    }
}
