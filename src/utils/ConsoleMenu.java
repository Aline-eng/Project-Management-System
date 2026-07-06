package utils;

import java.util.Scanner;

public final class ConsoleMenu {

    private ConsoleMenu() {
    }

    public static void printHeader(String title) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║     " + title + "     ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    public static void printDivider() {
        System.out.println("-------------------------------------------");
    }

    public static void pause(Scanner scanner) {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}

