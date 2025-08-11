package cn.shinefan.fooerp.mapper;

import cn.shinefan.fooerp.model.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    @Select("SELECT id, name FROM role WHERE name = #{name}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name")
    })
    Role findByName(@Param("name") String name);
    
    @Select("SELECT r.id, r.name FROM role r JOIN users_roles ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name")
    })
    List<Role> findRolesByUserId(@Param("userId") Long userId);
}