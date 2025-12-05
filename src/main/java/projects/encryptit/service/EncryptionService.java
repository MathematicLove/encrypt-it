package projects.encryptit.service;

import projects.encryptit.util.encryption.EncryptionAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис для управления шифрованием.
 */
@Service
public class EncryptionService {

    private final Map<String, EncryptionAlgorithm> algorithms = new HashMap<>();

    /**
     * Конструктор с внедрением всех алгоритмов.
     * @param algorithmList список алгоритмов шифрования
     */
    @Autowired
    public EncryptionService(List<EncryptionAlgorithm> algorithmList) {
        for (EncryptionAlgorithm algorithm : algorithmList) {
            algorithms.put(algorithm.getName(), algorithm);
        }
    }

    /**
     * Шифрует данные с использованием указанного алгоритма.
     * @param data данные для шифрования
     * @param algorithmName название алгоритма
     * @param key ключ шифрования
     * @return зашифрованные данные
     * @throws IllegalArgumentException если алгоритм не найден
     */
    public byte[] encrypt(byte[] data, String algorithmName, String key) {
        EncryptionAlgorithm algorithm = algorithms.get(algorithmName.toUpperCase());
        if (algorithm == null) {
            throw new IllegalArgumentException("Алгоритм не найден: " + algorithmName);
        }
        return algorithm.encrypt(data, key);
    }

    /**
     * Дешифрует данные с использованием указанного алгоритма.
     * @param encryptedData зашифрованные данные
     * @param algorithmName название алгоритма
     * @param key ключ шифрования
     * @return расшифрованные данные
     * @throws IllegalArgumentException если алгоритм не найден
     */
    public byte[] decrypt(byte[] encryptedData, String algorithmName, String key) {
        EncryptionAlgorithm algorithm = algorithms.get(algorithmName.toUpperCase());
        if (algorithm == null) {
            throw new IllegalArgumentException("Алгоритм не найден: " + algorithmName);
        }
        return algorithm.decrypt(encryptedData, key);
    }

    /**
     * Возвращает список доступных алгоритмов.
     * @return список названий алгоритмов
     */
    public List<String> getAvailableAlgorithms() {
        return List.copyOf(algorithms.keySet());
    }
}