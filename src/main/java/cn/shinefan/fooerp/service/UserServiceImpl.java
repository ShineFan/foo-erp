package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.model.Role;
import cn.shinefan.fooerp.model.User;
import cn.shinefan.fooerp.mapper.RoleMapper;
import cn.shinefan.fooerp.mapper.UserMapper;
import cn.shinefan.fooerp.repository.UserRepository;
import cn.shinefan.fooerp.web.dto.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User save(UserRegistrationDto registrationDto) {
        // Create user object
        User user = new User(registrationDto.getFirstName(),
                registrationDto.getLastName(), registrationDto.getEmail(),
                passwordEncoder.encode(registrationDto.getPassword()), null);

        // Insert user
        userMapper.insert(user);
        
        // Get the ROLE_USER role from database
        Role userRole = roleMapper.findByName("ROLE_USER");
        if (userRole == null) {
            throw new RuntimeException("Default role not found");
        }
        
        // Link user and role
        userMapper.insertUserRole(user.getId(), userRole.getId());
        
        // Set roles collection
        user.setRoles(Arrays.asList(userRole));
        
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
