package twofish_rsa.twofish.service;

import java.io.IOException;

public interface CryptoService {

    byte[] encryptFile(String inputFilePath, byte[] key) throws IOException;

    byte[] decryptFile(byte[] encryptedData, byte[] key) throws IOException;

    void validateKey(byte[] key);
}
