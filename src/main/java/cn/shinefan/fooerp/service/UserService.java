package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.model.User;
import cn.shinefan.fooerp.web.dto.UserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User save(UserRegistrationDto registrationDto);
}
