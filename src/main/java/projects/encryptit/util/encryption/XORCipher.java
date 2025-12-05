package projects.encryptit.util.encryption;

import org.springframework.stereotype.Component;

/**
 * Реализация XOR шифрования.
 */
@Component
public class XORCipher implements EncryptionAlgorithm {

    private static final String NAME = "XOR";

    @Override
    public byte[] encrypt(byte[] data, String key) {
        byte[] keyBytes = key.getBytes();
        byte[] result = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ keyBytes[i % keyBytes.length]);
        }

        return result;
    }

    @Override
    public byte[] decrypt(byte[] encryptedData, String key) {
        // XOR обратим - шифрование и дешифрование одинаковы
        return encrypt(encryptedData, key);
    }

    @Override
    public String getName() {
        return NAME;
    }
}