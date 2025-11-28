import java.util.Scanner;
public class ProgramaGerado {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double num;
        double a;
        double b;
        double c;
        System.out.println("Quantos números sa série de Fibonacci você quer?");
        num = scanner.nextDouble();
        a = 0;
        b = 1.0;
        while(num >= 0) {
        System.out.println(a);
        c = a + b;
        a = b;
        b = c;
        num = num - 1;
        }
        scanner.close();
    }
}
