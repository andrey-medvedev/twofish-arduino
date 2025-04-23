package twofish_rsa.twofish.utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public final class ByteConverter {

    private ByteConverter() {}

    public static int[] bytesToInts(byte[] bytes) {
        int[] ints = new int[4];
        for (int i = 0; i < 4; i++) {
            ints[i] = fromBytes(Arrays.copyOfRange(bytes, i * 4, (i + 1) * 4));
        }
        return ints;
    }

    public static byte[] intsToBytes(int[] ints) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int value : ints) {
            baos.write(asBytes(value), 0, 4);
        }
        return baos.toByteArray();
    }

    public static byte[] asBytes(int intValue) {
        return new byte[]{
                (byte) (intValue & 0xFF),
                (byte) ((intValue >>> 8) & 0xFF),
                (byte) ((intValue >>> 16) & 0xFF),
                (byte) ((intValue >>> 24) & 0xFF)
        };
    }

    public static int fromBytes(byte[] bytes) {
        int S0 = 0;
        for (int i = 0; i < 4; i++) {
            S0 |= ((0xFF & bytes[i]) << (i * 8));
        }
        return S0;
    }

    public static byte[] convertListToArray(ArrayList<Byte> list) {
        byte[] array = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

}
