package twofish_rsa;

import com.fazecast.jSerialComm.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import twofish_rsa.twofish.cryptography.TwoFishAlgorithm;
import twofish_rsa.twofish.file.FileCryptoHelper;
import twofish_rsa.twofish.service.TwoFishCryptoServiceImpl;
import twofish_rsa.twofish.utils.ByteConverter;
import twofish_rsa.twofish.utils.KeyGeneratorUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
public class TwofishArduinoApplication {

    public static void main(String[] args) throws InterruptedException, IOException {
        SpringApplication.run(TwofishArduinoApplication.class, args);

        TwoFishCryptoServiceImpl twoFishCryptoService = new TwoFishCryptoServiceImpl(new TwoFishAlgorithm());
        byte[] key = KeyGeneratorUtil.generateSecureKey(256);

        byte[] encryptedData = twoFishCryptoService.encryptFile("C:\\Users\\andre\\Desktop\\test.txt", key);
        FileCryptoHelper.writeFile("C:\\Users\\andre\\Desktop\\testEncrypted.bin", encryptedData);
        System.out.println("Шифррование прошло упешно");

        byte[] transmittingData = ByteConverter.convertListToArray(arduinoTransmittingData(encryptedData));

        if (Arrays.equals(encryptedData, transmittingData)) {
            System.out.println("Данные совпадают!");
        } else {
            System.out.println("Есть расхождения в данных!");
            System.out.println("Ожидалось: " + Arrays.toString(encryptedData));
            System.out.println("Получено:  " + Arrays.toString(transmittingData));
        }

        System.out.println("Получили зашифрованные данные от микроконтроллера");

        byte[] decryptedData = twoFishCryptoService.decryptFile(transmittingData, key);
        FileCryptoHelper.writeFile("C:\\Users\\andre\\Desktop\\testDecrypted.txt", decryptedData);
        System.out.println("Расшифррование успешно");
    }

    private static ArrayList<Byte> arduinoTransmittingData(byte[] data) {

        // ардуино
        SerialPort arduinoPort = findArduinoPort("COM6");
        if (arduinoPort == null) {
            System.err.println("Arduino not found.");
        }

        arduinoPort.setBaudRate(9600);
        arduinoPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 3000, 0);

        if (!arduinoPort.openPort()) {
            System.err.println("Port opening failed.");
        }

        ArrayList<Byte> transmissionBytes = new ArrayList<>();

        try {
            // Даем Arduino время на перезагрузку (2-3 секунды)
            Thread.sleep(3000);

            // Очищаем буфер порта
            arduinoPort.clearDTR();
            arduinoPort.clearRTS();

            // Ожидание сигнала готовности (0xAA)
            byte[] readySignal = new byte[1];
            arduinoPort.readBytes(readySignal, 1);
            if (readySignal[0] == (byte)0xAA) {
                System.out.println("Arduino ready!");
            }

            byte[] testBytes = data;

            for (byte b : testBytes) {
                arduinoPort.writeBytes(new byte[]{b}, 1);
                System.out.println("Sent: 0x" + String.format("%02X", b));

                // Чтение с повторными попытками
                byte[] response = new byte[1];
                int bytesRead = arduinoPort.readBytes(response, 1);

                transmissionBytes.add(response[0]);

                if (bytesRead == 1) {
                    System.out.println("true");
                    // System.out.println("Received: 0x" + String.format("%02X", response[0]));
                } else {
                    System.out.println("No response.");
                }

                Thread.sleep(500); // Уменьшенная пауза
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            arduinoPort.closePort();
        }
        return transmissionBytes;
    }

    private static SerialPort findArduinoPort(String identifier) {
        for (SerialPort port : SerialPort.getCommPorts()) {
            if (port.getPortDescription().contains(identifier) ||
                    port.getDescriptivePortName().contains(identifier)) {
                return port;
            }
        }
        return null;
    }
}
