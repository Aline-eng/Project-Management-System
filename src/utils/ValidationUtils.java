package utils;

import models.TaskStatus;

import java.util.Scanner;

public final class ValidationUtils {
    private ValidationUtils() {

    }
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
