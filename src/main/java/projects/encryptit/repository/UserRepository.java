package projects.encryptit.repository;


import projects.encryptit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Поиск пользователя по имени.
     * @param username имя пользователя
     * @return Optional с пользователем
     */
    Optional<User> findByUsername(String username);

    /**
     * Поиск пользователя по email.
     * @param email электронная почта
     * @return Optional с пользователем
     */
    Optional<User> findByEmail(String email);

    /**
     * Проверка существования пользователя по имени.
     * @param username имя пользователя
     * @return true если пользователь существует
     */
    boolean existsByUsername(String username);

    /**
     * Проверка существования пользователя по email.
     * @param email электронная почта
     * @return true если пользователь существует
     */
    boolean existsByEmail(String email);
}