package twofish_rsa.twofish.service;

import twofish_rsa.twofish.utils.ByteConverter;
import twofish_rsa.twofish.file.PaddingUtils;
import twofish_rsa.twofish.cryptography.TwoFishAlgorithm;
import twofish_rsa.twofish.utils.KeyGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.io.IOException;

@Service
public class TwoFishCryptoServiceImpl implements CryptoService {

    private final TwoFishAlgorithm twoFishAlgorithm;

    @Autowired
    public TwoFishCryptoServiceImpl(TwoFishAlgorithm twoFishAlgorithm) {
        this.twoFishAlgorithm = twoFishAlgorithm;
    }

    @Override
    public byte[] encryptFile(String inputFilePath, byte[] key) throws IOException {
        validateKey(key);
        byte[] data = Files.readAllBytes(Paths.get(inputFilePath));
        byte[] paddedData = PaddingUtils.addPadding(data);
        int[] keyInts = KeyGeneratorUtil.convertKey(key);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            for (int i = 0; i < paddedData.length; i += 16) {
                byte[] block = Arrays.copyOfRange(paddedData, i, i + 16);
                int[] blockInts = ByteConverter.bytesToInts(block);
                int[] encryptedBlock = twoFishAlgorithm.encrypt(blockInts, keyInts);
                byte[] encryptedBytes = ByteConverter.intsToBytes(encryptedBlock);
                outputStream.write(encryptedBytes);
            }
            return outputStream.toByteArray();
        }
    }

    @Override
    public byte[] decryptFile(byte[] encryptedData, byte[] key) throws IOException {
        validateKey(key);
        int[] keyInts = KeyGeneratorUtil.convertKey(key);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            for (int i = 0; i < encryptedData.length; i += 16) {
                byte[] block = Arrays.copyOfRange(encryptedData, i, i + 16);
                int[] blockInts = ByteConverter.bytesToInts(block);
                int[] decryptedBlock = twoFishAlgorithm.decrypt(blockInts, keyInts);
                byte[] decryptedBytes = ByteConverter.intsToBytes(decryptedBlock);
                outputStream.write(decryptedBytes);
            }
            return PaddingUtils.removePadding(outputStream.toByteArray());
        }
    }

    @Override
    public void validateKey(byte[] key) {
        if (key == null || key.length == 0) {
            throw new IllegalArgumentException("Ключ не может быть пустым или null");
        }

        if (key.length != 16 && key.length != 24 && key.length != 32) {
            throw new IllegalArgumentException("Длина ключа должна быть 16 или 24 или 32 байта");
        }

    }
}