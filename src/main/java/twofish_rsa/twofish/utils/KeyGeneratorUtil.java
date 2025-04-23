package twofish_rsa.twofish.utils;

import java.security.SecureRandom;
import java.util.Arrays;

public final class KeyGeneratorUtil {

    private static final SecureRandom secureRandom = new SecureRandom();

    private KeyGeneratorUtil() {}

    public static byte[] generateSecureKey(int keyLength) {
        validateKeyLength(keyLength);

        byte[] keyBytes = new byte[keyLength / 8];
        secureRandom.nextBytes(keyBytes);

        return keyBytes;
    }

    public static int[] convertKey(byte[] keyBytes) {
        if (keyBytes.length < 16) {
            throw new IllegalArgumentException("Длина ключа должна быть не менее 16 байт");
        }
        int[] keyInts = new int[keyBytes.length / 4];
        for (int i = 0; i < 4; i++) {
            keyInts[i] = ByteConverter.fromBytes(Arrays.copyOfRange(keyBytes, i * 4, (i + 1) * 4));
        }
        return keyInts;
    }

    private static void validateKeyLength(int keyLength) {
        if (keyLength != 128 && keyLength != 192 && keyLength != 256) {
            throw new IllegalArgumentException("Длина ключа должна быть 128/192/256 бит");
            }
    }
}