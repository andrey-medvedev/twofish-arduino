package twofish_rsa.twofish.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileCryptoHelper {

    public static byte[] readFile(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    public static void writeFile(String path, byte[] data) throws IOException {
        Files.write(Paths.get(path), data);
    }
}