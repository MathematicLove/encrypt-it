package projects.encryptit.util.encryption;

import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Реализация AES шифрования.
 */
@Component
public class AESEncryption implements EncryptionAlgorithm {

    private static final String NAME = "AES";
    private static final String ALGORITHM = "AES";
    private static final int KEY_LENGTH = 16;

    @Override
    public byte[] encrypt(byte[] data, String key) {
        try {
            byte[] keyBytes = normalizeKey(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка AES шифрования", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] encryptedData, String key) {
        try {
            byte[] keyBytes = normalizeKey(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка AES дешифрования", e);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    private byte[] normalizeKey(String key) {
        byte[] keyBytes = key.getBytes();
        if (keyBytes.length == KEY_LENGTH) {
            return keyBytes;
        }

        byte[] normalized = new byte[KEY_LENGTH];
        for (int i = 0; i < KEY_LENGTH; i++) {
            normalized[i] = keyBytes[i % keyBytes.length];
        }
        return normalized;
    }
}