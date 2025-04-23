package twofish_rsa.twofish.service;

import java.io.IOException;

public interface CryptoService {

    void encryptFile(String inputFilePath,
                     String outputFilePath, byte[] key) throws IOException;

    void decryptFile(String inputFilePath,
                     String outputFilePath, byte[] key) throws IOException;

    void validateKey(byte[] key);
}
