package projects.encryptit.util.encryption;

import org.springframework.stereotype.Component;

/**
 * Реализация шифра Цезаря.
 */
@Component
public class CaesarCipher implements EncryptionAlgorithm {

    private static final String NAME = "CAESAR";
    private static final int DEFAULT_SHIFT = 3;

    @Override
    public byte[] encrypt(byte[] data, String key) {
        int shift = parseShift(key);
        byte[] result = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            // Правильное шифрование с учетом отрицательных байтов
            int originalByte = data[i] & 0xFF; // Конвертируем в беззнаковый
            int encryptedByte = (originalByte + shift) % 256;
            result[i] = (byte) encryptedByte;
        }

        return result;
    }

    @Override
    public byte[] decrypt(byte[] encryptedData, String key) {
        int shift = parseShift(key);
        byte[] result = new byte[encryptedData.length];

        for (int i = 0; i < encryptedData.length; i++) {
            // Правильное дешифрование с учетом отрицательных байтов
            int encryptedByte = encryptedData[i] & 0xFF; // Конвертируем в беззнаковый
            int decryptedByte = (encryptedByte - shift) % 256;
            if (decryptedByte < 0) {
                decryptedByte += 256;
            }
            result[i] = (byte) decryptedByte;
        }

        return result;
    }

    @Override
    public String getName() {
        return NAME;
    }

    private int parseShift(String key) {
        try {
            return Math.abs(Integer.parseInt(key)) % 256;
        } catch (NumberFormatException e) {
            return DEFAULT_SHIFT;
        }
    }
}