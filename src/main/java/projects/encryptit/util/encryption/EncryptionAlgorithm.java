package projects.encryptit.util.encryption;

/**
 * Интерфейс для алгоритмов шифрования.
 */
public interface EncryptionAlgorithm {

    /**
     * Шифрует данные.
     * @param data исходные данные
     * @param key ключ шифрования
     * @return зашифрованные данные
     */
    byte[] encrypt(byte[] data, String key);

    /**
     * Дешифрует данные.
     * @param encryptedData зашифрованные данные
     * @param key ключ шифрования
     * @return расшифрованные данные
     */
    byte[] decrypt(byte[] encryptedData, String key);

    /**
     * Возвращает название алгоритма.
     * @return название алгоритма
     */
    String getName();
}