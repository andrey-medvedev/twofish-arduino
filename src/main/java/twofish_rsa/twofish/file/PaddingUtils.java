package twofish_rsa.twofish.file;

import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public final class PaddingUtils {

    private PaddingUtils() {}

    public static byte[] addPadding(byte[] data) {
        int paddingLength = 16 - (data.length % 16);
        byte[] padded = new byte[data.length + paddingLength];
        System.arraycopy(data, 0, padded, 0, data.length);

        Arrays.fill(padded, data.length, padded.length, (byte) paddingLength);
        return padded;
    }

    public static byte[] removePadding(byte[] data) {
        int paddingLength = data[data.length - 1];
        if (paddingLength < 1 || paddingLength > 16) {
            throw new IllegalArgumentException("Invalid padding");
        }
        return Arrays.copyOfRange(data, 0, data.length - paddingLength);
    }

}
