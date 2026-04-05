package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.model.Role;
import java.util.Optional;

public interface RoleService {
    Optional<Role> getRoleById(Long id);
}
