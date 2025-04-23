package twofish_rsa.twofish.cryptography;

public class GaloisField {

    public static byte[] multiplyMatrix(byte[][] matrix, byte[] vector, byte polynomial) {
        byte[] resultVector = new byte[matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            final byte[] currentRow = matrix[i];
            resultVector[i] = multiply(currentRow[0], vector[0], polynomial);
            for (int j = 1; j < currentRow.length; j++) {
                resultVector[i] ^= multiply(currentRow[j], vector[j], polynomial); // XOR - операция сложения в GF(2^n)
            }
        }

        return resultVector;
    }

    private static byte multiply(byte a, byte b, byte polynomial) {
        byte product = 0;
        for (int i = 0; i < 8; i++) {
            if ((b & 1) != 0) {
                product ^= a;
            }
            a = (byte)((a << 1) ^ ((a & 0x80) != 0 ? polynomial : 0)); // Применение примитивного многочлена
            b = (byte)(b >> 1);
        }
        return product;
    }
}