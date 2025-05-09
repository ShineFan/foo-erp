package cn.shinefan.fooerp.mapper;

import cn.shinefan.fooerp.model.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

@Mapper
public interface RoleMapper {
    Role findById(@Param("id") Long id);
    Role findByName(@Param("name") String name);
    Long insertRole(Role role);
    Collection<Role> findRolesByUserId(@Param("userId") Long userId);
}
