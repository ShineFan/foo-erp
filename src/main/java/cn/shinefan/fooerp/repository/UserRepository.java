package cn.shinefan.fooerp.repository;

import cn.shinefan.fooerp.mapper.UserMapper;
import cn.shinefan.fooerp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Repository layer that uses MyBatis mapper
 */
@Repository
public class UserRepository {
    
    @Autowired
    private UserMapper userMapper;
    
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }
    
    public User save(User user) {
        userMapper.insert(user);
        return user;
    }
}
