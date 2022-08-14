import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        startCalc();

        while (true) {
            System.out.println("Ввод: ");
            scanner.hasNext();
            String line = scanner.nextLine();

            if (line.equals("exit")) {
                exitCalc();
                break;
            }

            String output = calc(line);
            if(output.equals("")){
                break;
            }else {
                System.out.println(output);
            }
        }
        scanner.close();

    }

    private static void startCalc() {
        System.out.println("Добро пожаловать в Калькулятор, он работает только с арабскими и римскими цифрами от 1 до 10");
        System.out.println("Сложение(+), Вычитание(-), Умножение(*), Деление(/)");
        System.out.println("Если Вы хотите покинуть программу, введите 'exit'");
    }

    private static void exitCalc() {
        System.out.print("До скорых встреч!");
    }

    public static String calc(String input){
        try {
            String[] symbols = input.split(" ");
            if (symbols.length != 3)
                throw new Exception("Что-то пошло не так, попробуйте еще раз");

            Number firstNumber = NumberService.parseAndValidate(symbols[0]);
            Number secondNumber = NumberService.parseAndValidate(symbols[2], firstNumber.getType());
            String result = ActionService.calculate(firstNumber, secondNumber, symbols[1]);
            return "Ответ: " + result;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            exitCalc();
            return "";
        }
    }
}

class ActionService {

    public static String calculate(Number first, Number second, String action) throws Exception {
        
        int result;

        switch (action) {
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
            default:
                throw new Exception("Не правильно введен символ операции, используйте только +, -, *, /");
        }

        if (first.getType() == NumberType.ROMAN) {
            return NumberService.toRomanNumber(result);
        } else return String.valueOf(result);
    }
}

class NumberService {

    private static TreeMap<Integer, String> romanString = new TreeMap<>();
   

    static {
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

    static Number parseAndValidate(String symbol) throws Exception {

        int value; 
        NumberType type;

        try {
            value = Integer.parseInt(symbol);
            type = NumberType.ARABIC;
        } catch (NumberFormatException e) {
            value = toArabicNumber(symbol);
            type = NumberType.ROMAN;
        }

        if (value < 1 || value > 10) {
            throw new Exception("Неподходящее значение числа(ел), используйте числа от 1 до 10 включительно");
        }

        return new Number(value, type);
    }

    static Number parseAndValidate(String symbol, NumberType type) throws Exception {
        

        Number number = parseAndValidate(symbol);
        if (number.getType() != type) {
            throw new Exception("Числа разных типов, используйте один тип вводных значений");
        }

        return number;
    }

    private static int letterToNumber(char letter) {

        int result = -1;

        for (Map.Entry<Integer, String> entry : romanString.entrySet()) {
            if (entry.getValue().equals(String.valueOf(letter)))
                result = entry.getKey();
        }
        return result;
    }

    static String toRomanNumber(Integer number) throws Exception {

        Integer i = romanString.floorKey(number);
        if (i == null) {
            throw new Exception("Римское число меньше или равно нулю");
        }

        if (number == i) {
            return romanString.get(number);
        }
        return romanString.get(i) + toRomanNumber(number - i);
    }

    static int toArabicNumber(String roman) throws Exception {
        int result = 0;

        int i = 0;
        while (i < roman.length()) {
            char letter = roman.charAt(i);
            int num = letterToNumber(letter);

            if (num < 0)
                throw new Exception("Неверный римский символ");

            i++;
            if (i == roman.length()) {
                result += num;
            } else {
                int nextNum = letterToNumber(roman.charAt(i));
                if (nextNum > num) {
                    result += (nextNum - num);
                    i++;
                } else result += num;
            }
        }
        return result;
    }
}

class Number {

    private int value;
    private NumberType type;

    Number(int value, NumberType type) {
        this.value = value;
        this.type = type;
    }

    int getValue() {
        return value;
    }

    NumberType getType() {
        return type;
    }
}

enum NumberType { 
    ARABIC,
    ROMAN
}
