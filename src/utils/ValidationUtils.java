package utils;

import enums.TaskStatus;

import java.util.Scanner;

/**
 * Console input readers that loop until a valid value is entered, rather
 * than crashing or returning a bad value on the first invalid attempt.
 */
public final class ValidationUtils {
    private ValidationUtils() {

    }

    /** Re-prompts until the user enters a parseable whole number. */
    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Please enter a valid whole number.");
            }
        }
    }
    /** Re-prompts until the user enters a whole number greater than 0. */
    public static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            int value = readInt(scanner, prompt);
            if (value <= 0) {
                System.out.println("❌ Error: Value must be greater than 0.");
                continue;
            }
            return value;
        }
    }
    /** Re-prompts until the user enters a whole number between min and max (inclusive). */
    public static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            int value = readInt(scanner, prompt);
            if (value < min || value > max) {
                System.out.println("❌ Error: Please enter a number between " + min + " and " + max + ".");
                continue;
            }
            return value;
        }
    }
    /** Re-prompts until the user enters a number greater than 0. */
    public static double readPositiveDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value <= 0) {
                    System.out.println("❌ Error: Value must be greater than 0.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Please enter a valid number.");
            }
        }
    }
    /** Re-prompts until the user enters a number that is not negative. */
    public static double readNonNegativeDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value < 0) {
                    System.out.println("❌ Error: Value cannot be negative.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Please enter a valid number.");
            }
        }
    }
    /** Re-prompts until the user enters a non-blank value. */
    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("❌ Error: This field cannot be empty.");
                continue;
            }
            return input;
        }
    }
    /**
     * Re-prompts until the user enters text containing at least one letter -
     * rejects blank input and input that is only digits/symbols (e.g. "12345"
     * is not a reasonable project or task name). Digits are still allowed
     * as part of the text (e.g. "Alpha Tracker 2024" is fine).
     */
    public static String readValidText(Scanner scanner, String prompt) {
        while (true) {
            String input = readNonEmptyString(scanner, prompt);
            if (!input.matches(".*[a-zA-Z].*")) {
                System.out.println("❌ Error: Must contain at least one letter, not just numbers/symbols.");
                continue;
            }
            return input;
        }
    }
    /** Re-prompts until the user enters text matching a valid TaskStatus label. */
    public static TaskStatus readValidStatus(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            TaskStatus status = TaskStatus.fromLabel(input);
            if (status == null) {
                System.out.println("❌ Error: Invalid status. Please choose from [Pending, In Progress, Completed].");
                continue;
            }
            return status;
        }
    }
}
