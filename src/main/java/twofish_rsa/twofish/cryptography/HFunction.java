package twofish_rsa.twofish.cryptography;

import twofish_rsa.twofish.utils.ByteConverter;
import org.springframework.stereotype.Service;

@Service
public class HFunction {

    private static final byte[][] MDS = new byte[][]{
            new byte[]{(byte) 0x01, (byte) 0xEF,  (byte) 0x5B,  (byte) 0x5B},
            new byte[]{(byte) 0x5B, (byte) 0xEF,  (byte) 0xEF,  (byte) 0x01},
            new byte[]{(byte) 0xEF, (byte) 0x5B,  (byte) 0x01,  (byte) 0xEF},
            new byte[]{(byte) 0xEF, (byte) 0x01,  (byte) 0xEF,  (byte) 0x5B}
    };

    public static int h(int input, int l0, int l1) {
        final byte[] x = ByteConverter.asBytes(input);
        final byte[] y = ByteConverter.asBytes(l1);
        final byte[] z = ByteConverter.asBytes(l0);

        // Первый шаг: Производим первое преобразование каждого байта
        byte transformedX0 = SBoxes.permutationsByte(x[0], true);
        byte transformedX1 = SBoxes.permutationsByte(x[1], false);
        byte transformedX2 = SBoxes.permutationsByte(x[2], true);
        byte transformedX3 = SBoxes.permutationsByte(x[3], false);

        // Второй шаг: Выполняем XOR каждого преобразованного байта с соответствующими байтами y
        byte intermediateResult0 = (byte) (transformedX0 ^ y[0]);
        byte intermediateResult1 = (byte) (transformedX1 ^ y[1]);
        byte intermediateResult2 = (byte) (transformedX2 ^ y[2]);
        byte intermediateResult3 = (byte) (transformedX3 ^ y[3]);

        // Третий шаг: Вторичное преобразование с применением другого флага
        byte secondaryTransformed0 = SBoxes.permutationsByte(intermediateResult0, true);
        byte secondaryTransformed1 = SBoxes.permutationsByte(intermediateResult1, true);
        byte secondaryTransformed2 = SBoxes.permutationsByte(intermediateResult2, false);
        byte secondaryTransformed3 = SBoxes.permutationsByte(intermediateResult3, false);

        // Четвертый шаг: Третье преобразование и финальный XOR с байтами z
        byte finalResult0 = SBoxes.permutationsByte((byte) (secondaryTransformed0 ^ z[0]), false);
        byte finalResult1 = SBoxes.permutationsByte((byte) (secondaryTransformed1 ^ z[1]), true);
        byte finalResult2 = SBoxes.permutationsByte((byte) (secondaryTransformed2 ^ z[2]), false);
        byte finalResult3 = SBoxes.permutationsByte((byte) (secondaryTransformed3 ^ z[3]), true);

        // Завершающая сборка результата
        final byte[] result = new byte[]{
                finalResult0,
                finalResult1,
                finalResult2,
                finalResult3
        };

        return ByteConverter.fromBytes(GaloisField.multiplyMatrix(MDS, result, (byte) 0b10101001));
    }
}
