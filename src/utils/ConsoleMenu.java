package utils;

import java.util.Scanner;

/** Shared console formatting helpers: boxed headers, dividers, and pause prompts. */
public final class ConsoleMenu {

    private ConsoleMenu() {
    }

    private static final int HEADER_WIDTH = 46;

    /** Prints a boxed header, centering the title regardless of its length. */
    public static void printHeader(String title) {
        int padding = HEADER_WIDTH - title.length();
        int leftPad = padding / 2;
        int rightPad = padding - leftPad;

        System.out.println("╔" + "═".repeat(HEADER_WIDTH) + "╗");
        System.out.println("║" + " ".repeat(leftPad) + title + " ".repeat(rightPad) + "║");
        System.out.println("╚" + "═".repeat(HEADER_WIDTH) + "╝");
    }

    /** Prints a horizontal rule used to separate table rows/sections. */
    public static void printDivider() {
        System.out.println("───────────────────────────────────────────────────────────────────────");
    }

    /** Blocks until the user presses Enter, so a screen doesn't flash by unread. */
    public static void pause(Scanner scanner) {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}

