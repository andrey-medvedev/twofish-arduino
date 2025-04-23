package twofish_rsa.twofish.cryptography;

import twofish_rsa.twofish.utils.ByteConverter;

public class RoundsKeysGenerator {

    private static final byte[][] RS = new byte[][]{
            new byte[]{(byte) 0x01, (byte) 0xA4, (byte) 0x55, (byte) 0x87,
                    (byte) 0x5A, (byte) 0x58, (byte) 0xDB, (byte) 0x9E},

            new byte[]{(byte) 0xA4, (byte) 0x56, (byte) 0x82, (byte) 0xF3,
                    (byte) 0x1E, (byte) 0xC6, (byte) 0x68, (byte) 0xE5},

            new byte[]{(byte) 0x02, (byte) 0xA1, (byte) 0xFC, (byte) 0xC1,
                    (byte) 0x47, (byte) 0xAE, (byte) 0x3D, (byte) 0x19},

            new byte[]{(byte) 0xA4, (byte) 0x55, (byte) 0x87, (byte) 0x5A,
                    (byte) 0x58, (byte) 0xDB, (byte) 0x9E, (byte) 0x03}
    };

    public static int[] roundKeys(int[] key, int round) {
        int[] evenParts = new int[] { key[0], key[2] };
        int[] oddParts = new int[] { key[1], key[3] };

        int rho = (1 << 24) | (1 << 16) | (1 << 8) | 1;

        int firstPart = HFunction.h(2 * round * rho, evenParts[0], evenParts[1]);

        int secondPart = Integer.rotateLeft(HFunction.h((2 * round + 1) * rho, oddParts[0], oddParts[1]), 8);

        int[] intermediateValues = pht(firstPart, secondPart);

        int finalKeyPart1 = intermediateValues[0];
        int finalKeyPart2 = Integer.rotateLeft(intermediateValues[1], 9);

        return new int[] { finalKeyPart1, finalKeyPart2 };
    }

    public static int[] pht(int a, int b) {
        int firstSum = a + b;
        int secondSum = a + 2 * b;
        return new int[] {firstSum, secondSum};
    }

    public static int[] getS(int[] key) {
        int m0 = key[0];
        int m1 = key[1];
        int m2 = key[2];
        int m3 = key[3];
        int S0 = RS(m0, m1);
        int S1 = RS(m2, m3);
        return new int[] { S0, S1 };
    }

    public static int RS(int X, int Y) {
        byte[] xBytes = ByteConverter.asBytes(X);
        byte[] yBytes = ByteConverter.asBytes(Y);
        byte[] xyCombined = new byte[8];
        System.arraycopy(xBytes, 0, xyCombined, 0, 4);
        System.arraycopy(yBytes, 0, xyCombined, 4, 4);
        byte[] result = GaloisField.multiplyMatrix(RS, xyCombined, (byte) 0b101001101);
        return ByteConverter.fromBytes(result);
    }
}
