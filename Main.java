import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);//объявляем и инициализируем сканер для ввода с клавиатуры
        startCalc();//вызываем метод startCalc (подобное в дальнейшем не будет комментироваться)

        while (true) {//бесконечный цикл (подобное в дальнейшем не будет комментироваться)
            System.out.println("Ввод: ");//выводим текст в консоль (подобное в дальнейшем не будет комментироваться)
            scanner.hasNext();
            String line = scanner.nextLine();//считываем введ1нную строку

            if (line.equals("exit")) {//если введённая строка - exit, то (подобное в дальнейшем не будет комментироваться)
                exitCalc();
                break;//прерываем цикл (подобное в дальнейшем не будет комментироваться)
            }

            String output = calc(line);//присваиваем строке output значение, которое вернёт calc (подобное в дальнейшем не будет комментироваться)
            if(output.equals("")){
                break;
            }else {
                System.out.println(output);
            }
        }
        scanner.close();

    }

    private static void startCalc() {//выводим приветственное сообщение
        System.out.println("Добро пожаловать в Калькулятор, он работает только с арабскими и римскими цифрами от 1 до 10");
        System.out.println("Сложение(+), Вычитание(-), Умножение(*), Деление(/)");
        System.out.println("Если Вы хотите покинуть программу, введите 'exit'");
    }

    private static void exitCalc() {//выводим прощальное сообщение
        System.out.print("До скорых встреч!");
    }

    public static String calc(String input){//проводим вычисления, в качестве параметра принимает введённую строку
        try {//ловим исключение (подобное в дальнейшем не будет комментироваться)
            String[] symbols = input.split(" ");// разбиваем строку по пробелам
            if (symbols.length != 3)// если массив слов, полученный выше, имеет длинну отличную от трёх
                throw new Exception("Что-то пошло не так, попробуйте еще раз");// бросаем исключение (подобное в дальнейшем не будет комментироваться)

            Number firstNumber = NumberService.parseAndValidate(symbols[0]);// определяем(опознаём) первое число
            Number secondNumber = NumberService.parseAndValidate(symbols[2], firstNumber.getType());// определяем(опознаём) второе число, учитывая тип первого
            String result = ActionService.calculate(firstNumber, secondNumber, symbols[1]);// получаем результат вычислений
            return "Ответ: " + result;// возвращаем результат выполнения функции

        } catch (Exception e) {
            System.out.println(e.getMessage());// ловим, брошенное нами, искключение и выводим сообщение
            exitCalc();
            return "";
        }
    }
}

class ActionService {

    public static String calculate(Number first, Number second, String action) throws Exception {// метод, отвечающий за выполнение операции между числами
        // в данном методе мы отказываемся от обработки исключения, "выталкивая" его на уровунь выше
        int result;

        switch (action) {// сравниваем символ action с символами операций
            case "+":
                result = first.getValue() + second.getValue();
                break;
            case "-":
                result = first.getValue() - second.getValue();
                break;
            case "*":
                result = first.getValue() * second.getValue();
                break;
            case "/":
                result = first.getValue() / second.getValue();
                break;
            default:// в случае если символ action не совпадает ни с одним символом операции, то выбрасываем исключение
                throw new Exception("Не правильно введен символ операции, используйте только +, -, *, /");
        }

        if (first.getType() == NumberType.ROMAN) {// если тип числа равен константе NumberType.ROMAN, то
            return NumberService.toRomanNumber(result);// преобразуем в римскую запись, иначе
        } else return String.valueOf(result);// возвращаем значение переменной result в виде строки
    }
}

class NumberService {

    private static TreeMap<Integer, String> romanString = new TreeMap<>();//создаём и инициализируем переменную, хранящую в себе пары
    // "ключ-значение"

    static {// добавляем значения в переменную romanString
        romanString.put(1, "I");
        romanString.put(4, "IV");
        romanString.put(5, "V");
        romanString.put(9, "IX");
        romanString.put(10, "X");
        romanString.put(40, "XL");
        romanString.put(50, "L");
        romanString.put(90, "XC");
        romanString.put(100, "C");
    }

    static Number parseAndValidate(String symbol) throws Exception {// метод, предназначенный для "опознания" числа

        int value; // объявляем переменную типа int (подобное в дальнейшем не будет комментироваться)
        NumberType type;

        try {
            value = Integer.parseInt(symbol);// с помощью метода parseInt пытаемся преобразовать строку в число
            type = NumberType.ARABIC;// если мы смогли выполнить преобразование, то это арабское число
        } catch (NumberFormatException e) {
            value = toArabicNumber(symbol);// если неполучилось преобразовать к арабскому числу, то возможно, что это римское число
            type = NumberType.ROMAN;
        }

        if (value < 1 || value > 10) {// выполнится если value < 1 или value > 10
            throw new Exception("Неподходящее значение числа(ел), используйте числа от 1 до 10 включительно");
        }

        return new Number(value, type);
    }

    static Number parseAndValidate(String symbol, NumberType type) throws Exception {// метод, предназначенный для "опознания" числа,
        //а также и для проверки совпадения типов с первым введённым числом

        Number number = parseAndValidate(symbol);
        if (number.getType() != type) {// если типы чисел не совпадают
            throw new Exception("Числа разных типов, используйте один тип вводных значений");
        }

        return number;
    }

    private static int letterToNumber(char letter) {// метод, необходимый для преобразования римского символов в число

        int result = -1;

        for (Map.Entry<Integer, String> entry : romanString.entrySet()) {// цикл, проходящий по всем парам из romanString
            if (entry.getValue().equals(String.valueOf(letter)))// если символ letter совпадает со значением переменной entry, то
                result = entry.getKey();// result равен ключу entry
        }
        return result;
    }

    static String toRomanNumber(Integer number) throws Exception {// метод, необходимый для преобразования в римское число

        Integer i = romanString.floorKey(number);// присваиваем i наибольшее число из ключей romanString, которое меньше number
        if (i == null) {// если ключне найден, то выбрасываем исключение
            throw new Exception("Римское число меньше или равно нулю");
        }

        if (number == i) {
            return romanString.get(number);
        }
        return romanString.get(i) + toRomanNumber(number - i);
    }

    static int toArabicNumber(String roman) throws Exception {//метод для преобразования римского числа в арабское
        int result = 0;

        int i = 0;
        while (i < roman.length()) {
            char letter = roman.charAt(i);// получаем i-ый символ из строки
            int num = letterToNumber(letter);// преобразуем полученный символ в число

            if (num < 0)
                throw new Exception("Неверный римский символ");

            i++;// проверяем следующий символ
            if (i == roman.length()) {
                result += num;
            } else {
                int nextNum = letterToNumber(roman.charAt(i));
                if (nextNum > num) {// если число полученное из i+1 символа больше чем из i-го символа, то
                    result += (nextNum - num);// находим разность между большим и меньшим, иначе
                    i++;
                } else result += num;// складываем
            }
        }
        return result;
    }
}

class Number {

    private int value;// поле, хранящее в себе значение
    private NumberType type;// поле, хранящее в себе тип

    Number(int value, NumberType type) {// конструктор класса с параметрами типа int и NumberType
        this.value = value;
        this.type = type;
    }

    int getValue() {// метод, возвращающий значение поля value
        return value;
    }

    NumberType getType() {// метод, возвращающий значение поля type
        return type;
    }
}

enum NumberType { // Перечисление, представляет собой набор логически связанных констант (фактически представляет новый тип)
    ARABIC,
    ROMAN
}