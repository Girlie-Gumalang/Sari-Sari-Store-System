package util;

public class ConsoleMenu {
    private static final int LINE_LENGTH = 65; 

    public static void printHeader(String title) {
        System.out.println("\n===========================================================");
        
        int spacesNeeded = (LINE_LENGTH - title.length()) / 2;
        
        for (int i = 0; i < spacesNeeded; i++) {
            System.out.print(" ");
        }
        
        System.out.println(title);
        System.out.println("===========================================================");
    }

    public static void printLine() {
        System.out.println("-----------------------------------------");
    }
}