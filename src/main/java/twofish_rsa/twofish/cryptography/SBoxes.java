package twofish_rsa.twofish.cryptography;

public class SBoxes {

    private static final byte[][] q0 = new byte[][]{
            new byte[]{(byte) 0x8, (byte) 0x1, (byte) 0x7, (byte) 0xD, (byte) 0x6, (byte) 0xF, (byte) 0x3, (byte) 0x2,
                    (byte) 0x0, (byte) 0xB, (byte) 0x5, (byte) 0x9, (byte) 0xE, (byte) 0xC, (byte) 0xA, (byte) 0x4},

            new byte[]{(byte) 0xE, (byte) 0xC, (byte) 0xB, (byte) 0x8, (byte) 0x1, (byte) 0x2, (byte) 0x3, (byte) 0x5,
                    (byte) 0xF, (byte) 0x4, (byte) 0xA, (byte) 0x6, (byte) 0x7, (byte) 0x0, (byte) 0x9, (byte) 0xD},

            new byte[]{(byte) 0xB, (byte) 0xA, (byte) 0x5, (byte) 0xE, (byte) 0x6, (byte) 0xD, (byte) 0x9, (byte) 0x0,
                    (byte) 0xC, (byte) 0x8, (byte) 0xF, (byte) 0x3, (byte) 0x2, (byte) 0x4, (byte) 0x7, (byte) 0x1},

            new byte[]{(byte) 0xD, (byte) 0x7, (byte) 0xF, (byte) 0x4, (byte) 0x1, (byte) 0x2, (byte) 0x6, (byte) 0xE,
                    (byte) 0x9, (byte) 0xB, (byte) 0x3, (byte) 0x0, (byte) 0x8, (byte) 0x5, (byte) 0xC, (byte) 0xA}
    };

    private static final byte[][] q1 = new byte[][]{
            new byte[]{(byte) 0x2, (byte) 0x8, (byte) 0xB, (byte) 0xD, (byte) 0xF, (byte) 0x7, (byte) 0x6, (byte) 0xE,
                    (byte) 0x3, (byte) 0x1, (byte) 0x9, (byte) 0x4, (byte) 0x0, (byte) 0xA, (byte) 0xC, (byte) 0x5},

            new byte[]{(byte) 0x1, (byte) 0xE, (byte) 0x2, (byte) 0xB, (byte) 0x4, (byte) 0xC, (byte) 0x3, (byte) 0x7,
                    (byte) 0x6, (byte) 0xD, (byte) 0xA, (byte) 0x5, (byte) 0xF, (byte) 0x9, (byte) 0x0, (byte) 0x8},

            new byte[]{(byte) 0x4, (byte) 0xC, (byte) 0x7, (byte) 0x5, (byte) 0x1, (byte) 0x6, (byte) 0x9, (byte) 0xA,
                    (byte) 0x0, (byte) 0xE, (byte) 0xD, (byte) 0x8, (byte) 0x2, (byte) 0xB, (byte) 0x3, (byte) 0xF},

            new byte[]{(byte) 0xB, (byte) 0x9, (byte) 0x5, (byte) 0x1, (byte) 0xC, (byte) 0x3, (byte) 0xD, (byte) 0xE,
                    (byte) 0x6, (byte) 0x4, (byte) 0x7, (byte) 0xF, (byte) 0x2, (byte) 0x0, (byte) 0x8, (byte) 0xA}
    };

    public static byte permutationsByte(byte input, boolean useQ0) {

        byte[][] substitutionTable = useQ0 ? q0 : q1;

        byte upperHalf = (byte) ((input >> 4) & 0xF);
        byte lowerHalf = (byte) (input & 0xF);

        byte mixedUpperLower = (byte) (upperHalf ^ lowerHalf);

        byte complexTransformation = (byte) (upperHalf ^ ((lowerHalf & 1) << 3 | lowerHalf >> 1) ^ ((8 * upperHalf) & 0xF));

        byte afterFirstSubstitution = substitutionTable[0][mixedUpperLower];
        byte afterSecondSubstitution = substitutionTable[1][complexTransformation];

        byte combinedResults = (byte) (afterFirstSubstitution ^ afterSecondSubstitution);

        byte secondComplexTransformation = (byte) (afterFirstSubstitution ^ ((afterSecondSubstitution & 1) << 3 | afterSecondSubstitution >> 1) ^ ((8 * afterFirstSubstitution) & 0xF));

        byte thirdSubstitution = substitutionTable[2][combinedResults];
        byte fourthSubstitution = substitutionTable[3][secondComplexTransformation];

        return (byte) ((fourthSubstitution << 4) | thirdSubstitution);

    }

}
