package projects.encryptit.service;

import projects.encryptit.model.User;
import projects.encryptit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Сервис для работы с пользователями.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Регистрирует нового пользователя.
     * @param username имя пользователя
     * @param email электронная почта
     * @param password пароль
     * @return созданный пользователь
     * @throws IllegalArgumentException если пользователь уже существует
     */
    public User registerUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        User user = new User(username, email, passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    /**
     * Аутентифицирует пользователя.
     * @param username имя пользователя
     * @param password пароль
     * @return Optional с пользователем, если аутентификация успешна
     */
    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword()) && user.isEnabled()) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Находит пользователя по имени.
     * @param username имя пользователя
     * @return Optional с пользователем
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Проверяет существование пользователя.
     * @param username имя пользователя
     * @return true если пользователь существует
     */
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
