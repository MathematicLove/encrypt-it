package projects.encryptit.controller;

import projects.encryptit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Контроллер для аутентификации и регистрации.
 */
@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Отображает страницу входа.
     * @return имя шаблона страницы входа
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    /**
     * Отображает страницу регистрации.
     * @return имя шаблона страницы регистрации
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    /**
     * Обрабатывает регистрацию нового пользователя.
     * @param username имя пользователя
     * @param email электронная почта
     * @param password пароль
     * @param model модель для передачи данных в представление
     * @return перенаправление на страницу входа или регистрации с ошибкой
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model) {
        try {
            userService.registerUser(username, email, password);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}