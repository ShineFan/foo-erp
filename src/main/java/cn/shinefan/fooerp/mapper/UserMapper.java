package cn.shinefan.fooerp.mapper;

import cn.shinefan.fooerp.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findById(Long id);
    
    User findByEmail(String email);
    
    int insert(User user);
    
    int update(User user);
    
    int deleteById(Long id);
    
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
