package projects.encryptit.security;

import projects.encryptit.model.User;
import projects.encryptit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис для загрузки пользователей в Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь не найден: " + username);
        }

        return new CustomUserDetails(userOpt.get());
    }
}