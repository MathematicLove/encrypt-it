package projects.encryptit.controller;

import projects.encryptit.service.EncryptionService;
import projects.encryptit.util.FileUtil;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Контроллер для шифрования и дешифрования.
 */
@Controller
public class EncryptionController {

    private final EncryptionService encryptionService;

    public EncryptionController(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    /**
     * Отображает главную страницу после входа.
     * @param model модель для передачи данных в представление
     * @return имя шаблона главной страницы
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("algorithms", encryptionService.getAvailableAlgorithms());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            model.addAttribute("username", auth.getName());
        }
        return "dashboard";
    }

    /**
     * Отображает страницу шифрования.
     * @param model модель для передачи данных в представление
     * @return имя шаблона страницы шифрования
     */
    @GetMapping("/encrypt")
    public String showEncryptPage(Model model) {
        model.addAttribute("algorithms", encryptionService.getAvailableAlgorithms());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            model.addAttribute("username", auth.getName());
        }
        return "encrypt";
    }

    /**
     * Отображает страницу дешифрования.
     * @param model модель для передачи данных в представление
     * @return имя шаблона страницы дешифрования
     */
    @GetMapping("/decrypt")
    public String showDecryptPage(Model model) {
        model.addAttribute("algorithms", encryptionService.getAvailableAlgorithms());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            model.addAttribute("username", auth.getName());
        }
        return "decrypt";
    }

    /**
     * Обрабатывает шифрование текста.
     * @param text текст для шифрования
     * @param algorithm алгоритм шифрования
     * @param key ключ шифрования
     * @return ResponseEntity с зашифрованным файлом
     */
    @PostMapping("/encrypt/text")
    public ResponseEntity<Resource> encryptText(@RequestParam String text,
                                                @RequestParam String algorithm,
                                                @RequestParam String key) {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = encryptionService.encrypt(data, algorithm, key);

        ByteArrayResource resource = new ByteArrayResource(encrypted);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"text_encrypted.txt\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(encrypted.length)
                .body(resource);
    }

    /**
     * Обрабатывает шифрование файла.
     * @param file файл для шифрования
     * @param algorithm алгоритм шифрования
     * @param key ключ шифрования
     * @return ResponseEntity с зашифрованным файлом
     */
    @PostMapping("/encrypt/file")
    public ResponseEntity<Resource> encryptFile(@RequestParam("file") MultipartFile file,
                                                @RequestParam String algorithm,
                                                @RequestParam String key) {
        try {
            byte[] data = file.getBytes();
            byte[] encrypted = encryptionService.encrypt(data, algorithm, key);

            String originalFilename = file.getOriginalFilename();
            String encryptedFilename = originalFilename.substring(0,
                    originalFilename.lastIndexOf('.')) + "_encrypted.txt";

            ByteArrayResource resource = new ByteArrayResource(encrypted);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + encryptedFilename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(encrypted.length)
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла", e);
        }
    }

    /**
     * Обрабатывает дешифрование текста.
     * @param file файл с зашифрованным текстом
     * @param algorithm алгоритм дешифрования
     * @param key ключ дешифрования
     * @return ResponseEntity с расшифрованным текстом
     */
    @PostMapping("/decrypt/text")
    public ResponseEntity<Resource> decryptText(@RequestParam("file") MultipartFile file,
                                                @RequestParam String algorithm,
                                                @RequestParam String key) {
        try {
            byte[] encryptedData = file.getBytes();
            byte[] decrypted = encryptionService.decrypt(encryptedData, algorithm, key);
            String decryptedText = new String(decrypted, StandardCharsets.UTF_8);

            String originalFilename = file.getOriginalFilename();
            String decryptedFilename = originalFilename.substring(0,
                    originalFilename.lastIndexOf('.')) + "_decrypted.txt";

            ByteArrayResource resource = new ByteArrayResource(decryptedText.getBytes(StandardCharsets.UTF_8));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + decryptedFilename + "\"")
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(decryptedText.length())
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла", e);
        }
    }

    /**
     * Обрабатывает дешифрование файла.
     * @param file файл с зашифрованными данными
     * @param algorithm алгоритм дешифрования
     * @param key ключ дешифрования
     * @return ResponseEntity с расшифрованным файлом
     */
    @PostMapping("/decrypt/file")
    public ResponseEntity<Resource> decryptFile(@RequestParam("file") MultipartFile file,
                                                @RequestParam String algorithm,
                                                @RequestParam String key) {
        try {
            byte[] encryptedData = file.getBytes();
            byte[] decrypted = encryptionService.decrypt(encryptedData, algorithm, key);

            String originalFilename = file.getOriginalFilename();

            // Определяем расширение файла на основе сигнатур
            String extension = FileUtil.determineFileExtension(decrypted);
            if (extension.isEmpty()) {
                // Если не удалось определить, используем .txt
                extension = ".txt";
            }

            // Убираем суффиксы из оригинального имени
            String baseName = originalFilename
                    .replace("_encrypted.txt", "")
                    .replace(".txt", "")
                    .replace("_encrypted", "");

            String decryptedFilename = baseName + "_decrypted" + extension;

            // Определяем content-type
            String mediaTypeStr = FileUtil.getMediaType(extension);
            MediaType mediaType = MediaType.parseMediaType(mediaTypeStr);

            ByteArrayResource resource = new ByteArrayResource(decrypted);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + decryptedFilename + "\"")
                    .contentType(mediaType)
                    .contentLength(decrypted.length)
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла", e);
        }
    }
}