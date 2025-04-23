package twofish_rsa.twofish.cryptography;

import org.springframework.stereotype.Service;

@Service
public class TwoFishAlgorithm {

    public int[] encrypt(int[] plainText, int[] key) {
        int[][] roundKeys = computeRoundKeys(key);

        int[] whitened = prepareWhiteKey(plainText, roundKeys[0], roundKeys[1]);

        for (int i = 0; i < 16; i++) {
            whitened = encryptionRound(whitened, key, i);
            whitened = new int[]{whitened[2], whitened[3], whitened[0], whitened[1]};
        }

        whitened = new int[]{whitened[2], whitened[3], whitened[0], whitened[1]};
        whitened = prepareWhiteKey(whitened, roundKeys[2], roundKeys[3]);

        return whitened;
    }

    public static int[] encryptionRound(int[] input, int[] key, int round) {
        final int[] s = RoundsKeysGenerator.getS(key);

        int t0 = HFunction.h(input[0], s[1], s[0]);
        int t1 = HFunction.h(Integer.rotateLeft(input[1], 8), s[1], s[0]);

        int[] pPht = RoundsKeysGenerator.pht(t0, t1);

        final int[] roundKeys2r_8_2r_9 = RoundsKeysGenerator.roundKeys(key, round + 4);

        int f0 = pPht[0] + roundKeys2r_8_2r_9[0];
        int f1 = pPht[1] + roundKeys2r_8_2r_9[1];

        int c2 = Integer.rotateRight(f0 ^ input[2], 1);
        int c3 = f1 ^ Integer.rotateLeft(input[3], 1);

        return new int[]{input[0], input[1], c2, c3};
    }

    public int[] decrypt(int[] cypheredText, int[] key) {

        int[][] roundKeys = computeRoundKeys(key);

        int[] whitened = prepareWhiteKey(cypheredText, roundKeys[2], roundKeys[3]);

        whitened = new int[]{whitened[2], whitened[3], whitened[0], whitened[1]};
        for (int i = 15; i >= 0; i--) {
            whitened = decryptionRound(whitened, key, i);
            whitened = new int[]{whitened[2], whitened[3], whitened[0], whitened[1]};
        }

        whitened = prepareWhiteKey(whitened, roundKeys[0], roundKeys[1]);

        return whitened;
    }

    public static int[] decryptionRound(int[] input, int[] key, int round) {
        final int[] s = RoundsKeysGenerator.getS(key);

        int t0 = HFunction.h(input[2], s[1], s[0]);
        int t1 = HFunction.h(Integer.rotateLeft(input[3], 8), s[1], s[0]);

        final int[] pPht = RoundsKeysGenerator.pht(t0, t1);

        final int[] roundKeys = RoundsKeysGenerator.roundKeys(key, round + 4);

        final int f0 = pPht[0] + roundKeys[0];
        final int f1 = pPht[1] + roundKeys[1];

        final int p2 = Integer.rotateLeft(input[0], 1) ^ f0;
        final int p3 = Integer.rotateRight(input[1] ^ f1, 1);

        return new int[]{p2, p3, input[2], input[3]};
    }

    private static int[] whitening(int[] plainText, int k0, int k1, int k2, int k3) {
        return new int[]{
                plainText[0] ^ k0,
                plainText[1] ^ k1,
                plainText[2] ^ k2,
                plainText[3] ^ k3
        };
    }

    private int[][] computeRoundKeys(int[] key) {
        return new int[][]{
                RoundsKeysGenerator.roundKeys(key, 0), // Round Key 01
                RoundsKeysGenerator.roundKeys(key, 1), // Round Key 23
                RoundsKeysGenerator.roundKeys(key, 2), // Round Key 45
                RoundsKeysGenerator.roundKeys(key, 3)  // Round Key 67
        };
    }

    private int[] prepareWhiteKey(int[] input, int[] roundKey1, int[] roundKey2) {
        return whitening(input, roundKey1[0], roundKey1[1], roundKey2[0], roundKey2[1]);
    }
}
