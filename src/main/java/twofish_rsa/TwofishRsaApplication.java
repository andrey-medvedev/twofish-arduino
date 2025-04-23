package twofish_rsa;

import com.fazecast.jSerialComm.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TwofishRsaApplication {
    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(TwofishRsaApplication.class, args);

        // TwoFishCryptoServiceImpl twoFishCryptoService = new TwoFishCryptoServiceImpl(new TwoFishAlgorithm());
        // byte[] key = KeyGeneratorUtil.generateSecureKey(256);

        SerialPort arduinoPort = findArduinoPort("COM6");
        if (arduinoPort == null) {
            System.err.println("Arduino not found.");
            return;
        }

        arduinoPort.setBaudRate(9600);
        arduinoPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 3000, 0);

        if (!arduinoPort.openPort()) {
            System.err.println("Port opening failed.");
            return;
        }

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

            byte[] testBytes = new byte[]{(byte)0x3C, (byte)0x5A, (byte)0xFF, (byte)0x3C, (byte)0x5A, (byte)0xFF};

            for (byte b : testBytes) {
                arduinoPort.writeBytes(new byte[]{b}, 1);
                System.out.println("Sent: 0x" + String.format("%02X", b));

                // Увеличиваем таймаут чтения
                byte[] response = new byte[1];
                int bytesRead = arduinoPort.readBytes(response, 1);

                if (bytesRead == 1) {
                    System.out.println("Received: 0x" + String.format("%02X", response[0]));
                } else {
                    System.out.println("No response.");
                }

                Thread.sleep(500); // Уменьшенная пауза
            }
        } finally {
            arduinoPort.closePort();
        }
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
