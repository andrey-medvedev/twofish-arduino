Проект оптической передачи данных с использованием Twofish-шифрования

---

#### Обзор проекта
Этот проект реализует систему безопасной оптической передачи данных между компьютером и микроконтроллером Arduino. Данные шифруются алгоритмом Twofish (256-бит), передаются через лазерные модули и считываются фоторезисторами. Проект включает:
1. Java-приложение для шифрования/дешифрования
2. Прошивку для Arduino
3. Оптическую систему передачи (лазер + фоторезистор)
4. Планируемый веб-интерфейс на Spring MVC

---

#### Аппаратная часть
**Компоненты:**
- Arduino Mega 2560
- 4 лазерных модуля (650нм, 5мВт)
- 4 фоторезистора GL5528
- Резисторы 10 кОм (для делителя напряжения)
- Монтажная плата

**Схема подключения:**
```
Лазер 1  → Цифровой пин 7
Лазер 2  → Цифровой пин 6
Лазер 3  → Цифровой пин 5
Лазер 4  → Цифровой пин 4

Фоторезистор 1 → Аналоговый пин A0
Фоторезистор 2 → Аналоговый пин A1
Фоторезистор 3 → Аналоговый пин A2
Фоторезистор 4 → Аналоговый пин A3
```

**Физическая схема:**

![Безымянный](https://github.com/user-attachments/assets/cd4eb1c8-cb3d-4941-8a6b-d75808b24873)

```
[Лазер 1] ------> [Фоторезистор 1]
[Лазер 2] ------> [Фоторезистор 2]
[Лазер 3] ------> [Фоторезистор 3]
[Лазер 4] ------> [Фоторезистор 4]
```

---

#### Программная часть

**1. Java-приложение (Twofish шифрование):**
```java
// Основной класс приложения
@SpringBootApplication
public class TwofishArduinoApplication {
    public static void main(String[] args) throws IOException {
        // Инициализация Spring
        SpringApplication.run(TwofishArduinoApplication.class, args);
        
        // Генерация 256-битного ключа
        byte[] key = KeyGeneratorUtil.generateSecureKey(256);
        
        // Шифрование файла
        byte[] encrypted = twoFishCryptoService.encryptFile("test.txt", key);
        
        // Передача данных на Arduino
        ArrayList<Byte> received = arduinoTransmittingData(encrypted);
        
        // Дешифрование
        byte[] decrypted = twoFishCryptoService.decryptFile(received, key);
    }
    
    private static ArrayList<Byte> arduinoTransmittingData(byte[] data) {
        // Настройка последовательного порта
        SerialPort port = findArduinoPort("COM6");
        port.setBaudRate(9600);
        port.openPort();
        
        // Обмен данными с Arduino
        for (byte b : data) {
            port.writeBytes(new byte[]{b}, 1);
            byte[] response = new byte[1];
            port.readBytes(response, 1);
            receivedBytes.add(response[0]);
        }
        return receivedBytes;
    }
}
```

**2. Прошивка Arduino (оптическая передача):**
```cpp
const int ledPins[] = {7, 6, 5, 4};
const int ldrPins[] = {A0, A1, A2, A3};
const int thresholds[] = {127, 77, 112, 52};

void setup() {
  Serial.begin(9600);
  for (int i = 0; i < 4; i++) {
    pinMode(ledPins[i], OUTPUT);
  }
  Serial.write(0xAA); // Сигнал готовности
}

void loop() {
  if (Serial.available()) {
    byte input = Serial.read();
    processByte(input);
  }
}

void processByte(byte input) {
  byte result = 0;
  
  // Передача младших 4 бит
  for (int i = 0; i < 4; i++) {
    digitalWrite(ledPins[i], bitRead(input, i));
    delay(75);
    if (analogRead(ldrPins[i]) > thresholds[i]) 
      bitSet(result, i);
    digitalWrite(ledPins[i], LOW);
  }
  
  // Передача старших 4 бит
  for (int i = 0; i < 4; i++) {
    digitalWrite(ledPins[i], bitRead(input, i+4));
    delay(75);
    if (analogRead(ldrPins[i]) > thresholds[i]) 
      bitSet(result, i+4);
    digitalWrite(ledPins[i], LOW);
  }
  
  Serial.write(result); // Отправка результата
}
```

---

#### Принцип работы
1. **Шифрование:**
   - Файл разделяется на 16-байтовые блоки
   - Каждый блок шифруется алгоритмом Twofish
   - Используется 256-битный ключ и режим CBC

2. **Оптическая передача:**
   - Каждый байт разделяется на два 4-битных полубайта
   - Лазеры последовательно передают биты
   - Фоторезисторы считывают оптические сигналы
   - Пороговые значения компенсируют помехи

3. **Дешифрование:**
   - Принятые байты объединяются в блоки
   - Twofish-алгоритм выполняет обратное преобразование
   - Удаляется PKCS7-padding

---

#### Планируемые улучшения
1. **Веб-интерфейс (Spring MVC):**
```java
@Controller
public class CryptoController {
    @PostMapping("/encrypt")
    public String encryptFile(@RequestParam MultipartFile file, Model model) {
        byte[] encrypted = cryptoService.encryptFile(file);
        model.addAttribute("data", Base64.encode(encrypted));
        return "result";
    }
    
    @GetMapping("/transmit")
    public String transmitData(@RequestParam String data) {
        arduinoService.transmit(Base64.decode(data));
        return "transmission-status";
    }
}
```

2. **Функционал веб-интерфейса:**
   - Загрузка/скачивание файлов
   - Визуализация процесса передачи
   - Графики сигналов с датчиков
   - Управление портами Arduino

3. **Улучшения аппаратной части:**
   - Добавление коррекции ошибок (CRC)
   - Реализация дуплексной связи
   - Автокалибровка фоторезисторов
   - Поддержка больших расстояний (линзы)

---

#### Требования
1. **Для Java-приложения:**
   - JDK 17+
   - Maven 3.8+
   - Зависимости: Spring Boot 3.x, jSerialComm

2. **Для Arduino:**
   - Arduino IDE 2.x
   - Библиотеки: отсутствуют (нативный C++)

3. **Аппаратные требования:**
   - Стабильный источник питания 5V
   - Защитные резисторы для лазеров (220 Ом)
   - Темное окружение для минимизации помех

---

#### Сборка и запуск
   - Подключите Arduino к компьютеру
   - Загрузите скетч из раздела "Прошивка"
   - Запишите порт микроконтроллера (например, COM6)
   - склонируйте репозиторий
   - определите пути для чтения и сохранения файлов
   - запустите TwofishArduinoApplication

---

#### Пример работы
```
Шифрование файла "test.txt"...
Отправлено: 0xA3 Получено: 0xA3
Отправлено: 0x1F Получено: 0x1F
...
Передача завершена (128/128 байт)
Дешифрование файла...
Отправлено: 0xA3 Получено: 0xA3
Отправлено: 0x1F Получено: 0x1F
...
```
