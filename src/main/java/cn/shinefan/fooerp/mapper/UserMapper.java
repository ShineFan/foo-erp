package cn.shinefan.fooerp.mapper;

import cn.shinefan.fooerp.model.Role;
import cn.shinefan.fooerp.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM users WHERE email = #{email}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "firstName", column = "first_name"),
        @Result(property = "lastName", column = "last_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "password", column = "password")
    })
    User findByEmail(String email);
    
    @Insert("INSERT INTO users_roles (user_id, role_id) VALUES (#{userId}, #{roleId})")
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    @Select("SELECT r.id, r.name FROM role r JOIN users_roles ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name")
    })
    List<Role> findRolesByUserId(@Param("userId") Long userId);
}