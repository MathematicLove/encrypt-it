package projects.encryptit.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Утилита для работы с файлами.
 */
public class FileUtil {

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    /**
     * Сохраняет временный файл.
     * @param file загруженный файл
     * @return путь к сохраненному файлу
     * @throws IOException если произошла ошибка при сохранении
     */
    public static Path saveTempFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String tempFilename = System.currentTimeMillis() + "_" + originalFilename;
        Path tempPath = Paths.get(TEMP_DIR, tempFilename);

        Files.write(tempPath, file.getBytes());
        return tempPath;
    }

    /**
     * Удаляет временный файл.
     * @param path путь к файлу
     */
    public static void deleteTempFile(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Игнорируем ошибку удаления временного файла
        }
    }

    /**
     * Получает расширение файла.
     * @param filename имя файла
     * @return расширение файла
     */
    public static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Проверяет, является ли файл изображением.
     * @param filename имя файла
     * @return true если файл является изображением
     */
    public static boolean isImageFile(String filename) {
        String extension = getFileExtension(filename);
        return extension.matches("(jpg|jpeg|png|gif|bmp)");
    }

    /**
     * Проверяет, является ли файл текстовым.
     * @param filename имя файла
     * @return true если файл является текстовым
     */
    public static boolean isTextFile(String filename) {
        String extension = getFileExtension(filename);
        return extension.matches("(txt|yml|yaml|xml|json|properties|cfg|conf|ini)");
    }

    /**
     * Определяет тип файла по сигнатурам (магическим числам).
     * @param data данные файла
     * @return тип файла ("image", "text", "binary")
     */
    public static String determineFileType(byte[] data) {
        if (data.length < 4) return "text";

        // Проверяем JPEG
        if (data[0] == (byte)0xFF && data[1] == (byte)0xD8 && data[2] == (byte)0xFF) {
            return "image";
        }

        // Проверяем PNG
        if (data[0] == (byte)0x89 && data[1] == (byte)0x50 &&
            data[2] == (byte)0x4E && data[3] == (byte)0x47) {
            return "image";
        }

        // Проверяем GIF
        if (data[0] == 'G' && data[1] == 'I' && data[2] == 'F') {
            return "image";
        }

        // Проверяем BMP
        if (data[0] == 'B' && data[1] == 'M') {
            return "image";
        }

        // Проверяем, является ли текст UTF-8
        if (isTextContent(data)) {
            return "text";
        }

        return "binary";
    }

    /**
     * Определяет расширение файла по сигнатурам.
     * @param data данные файла
     * @return расширение файла (например, ".jpg", ".png", ".txt")
     */
    public static String determineFileExtension(byte[] data) {
        String fileType = determineFileType(data);

        if ("image".equals(fileType)) {
            // Определяем конкретный тип изображения
            if (data.length >= 4) {
                // JPEG
                if (data[0] == (byte)0xFF && data[1] == (byte)0xD8 && data[2] == (byte)0xFF) {
                    return ".jpg";
                }
                // PNG
                if (data[0] == (byte)0x89 && data[1] == (byte)0x50 &&
                    data[2] == (byte)0x4E && data[3] == (byte)0x47) {
                    return ".png";
                }
                // GIF
                if (data[0] == 'G' && data[1] == 'I' && data[2] == 'F') {
                    return ".gif";
                }
                // BMP
                if (data[0] == 'B' && data[1] == 'M') {
                    return ".bmp";
                }
            }
            return ".jpg"; // по умолчанию для изображений
        }

        if ("text".equals(fileType)) {
            return ".txt";
        }

        return ".bin"; // для бинарных файлов
    }

    /**
     * Проверяет, являются ли данные текстовыми.
     * @param data данные
     * @return true если данные являются текстом
     */
    public static boolean isTextContent(byte[] data) {
        if (data.length == 0) return true;

        // Проверяем первые 1000 байт на наличие не-текстовых символов
        int checkLength = Math.min(data.length, 1000);
        int textCharCount = 0;

        for (int i = 0; i < checkLength; i++) {
            byte b = data[i];
            // ASCII печатные символы (32-126), табуляция, перевод строки, возврат каретки
            if ((b >= 32 && b <= 126) || b == 9 || b == 10 || b == 13) {
                textCharCount++;
            }
        }

        // Если более 95% символов - текст, считаем файл текстовым
        return (double)textCharCount / checkLength > 0.95;
    }

    /**
     * Проверяет, являются ли данные изображением.
     * @param data данные
     * @return true если данные являются изображением
     */
    public static boolean isImageData(byte[] data) {
        return "image".equals(determineFileType(data));
    }

    /**
     * Определяет MediaType по расширению файла.
     * @param extension расширение файла
     * @return соответствующий MediaType
     */
    public static String getMediaType(String extension) {
        switch (extension.toLowerCase()) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".bmp":
                return "image/bmp";
            case ".txt":
                return "text/plain";
            case ".pdf":
                return "application/pdf";
            default:
                return "application/octet-stream";
        }
    }
}