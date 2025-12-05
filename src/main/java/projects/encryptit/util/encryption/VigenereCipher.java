package projects.encryptit.util.encryption;

import org.springframework.stereotype.Component;

/**
 * Реализация шифра Виженера для байтов.
 */
@Component
public class VigenereCipher implements EncryptionAlgorithm {

    private static final String NAME = "VIGENERE";

    @Override
    public byte[] encrypt(byte[] data, String key) {
        byte[] keyBytes = key.getBytes();
        byte[] result = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            int dataByte = data[i] & 0xFF;
            int keyByte = keyBytes[i % keyBytes.length] & 0xFF;
            result[i] = (byte) ((dataByte + keyByte) % 256);
        }

        return result;
    }

    @Override
    public byte[] decrypt(byte[] encryptedData, String key) {
        byte[] keyBytes = key.getBytes();
        byte[] result = new byte[encryptedData.length];

        for (int i = 0; i < encryptedData.length; i++) {
            int encryptedByte = encryptedData[i] & 0xFF;
            int keyByte = keyBytes[i % keyBytes.length] & 0xFF;
            result[i] = (byte) ((encryptedByte - keyByte + 256) % 256);
        }

        return result;
    }

    @Override
    public String getName() {
        return NAME;
    }
}