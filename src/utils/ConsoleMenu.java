package utils;

import java.util.Scanner;

public final class ConsoleMenu {

    private ConsoleMenu() {
    }

    private static final int HEADER_WIDTH = 46;

    public static void printHeader(String title) {
        int padding = HEADER_WIDTH - title.length();
        int leftPad = padding / 2;
        int rightPad = padding - leftPad;

        System.out.println("╔" + "═".repeat(HEADER_WIDTH) + "╗");
        System.out.println("║" + " ".repeat(leftPad) + title + " ".repeat(rightPad) + "║");
        System.out.println("╚" + "═".repeat(HEADER_WIDTH) + "╝");
    }

    public static void printDivider() {
        System.out.println("───────────────────────────────────────────────────────────────────────");
    }

    public static void pause(Scanner scanner) {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}

