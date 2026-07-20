import enums.TaskStatus;
import org.junit.jupiter.api.Test;
import utils.ValidationUtils;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for ValidationUtils' retry-until-valid input readers. Each simulated
 * input is one invalid attempt followed by a valid one, proving the retry
 * loop actually re-prompts instead of just failing on the first bad entry.
 *
 * This is only testable at all because ValidationUtils takes the Scanner as
 * a parameter instead of reading System.in directly - a Scanner can wrap any
 * text source, including a plain String, so no real keyboard input is needed.
 */
class ValidationTests {

    @Test
    void readNonEmptyStringSkipsBlankLinesAndReturnsFirstRealValue() {
        Scanner scanner = new Scanner("\nHello\n");
        String result = ValidationUtils.readNonEmptyString(scanner, "prompt: ");
        assertEquals("Hello", result);
    }

    @Test
    void readValidTextRejectsPureNumbersThenAcceptsRealText() {
        Scanner scanner = new Scanner("12345\nAlpha Tracker\n");
        String result = ValidationUtils.readValidText(scanner, "prompt: ");
        assertEquals("Alpha Tracker", result);
    }

    @Test
    void readPositiveIntRejectsNonPositiveValuesThenAcceptsAValidOne() {
        Scanner scanner = new Scanner("-5\n0\n10\n");
        int result = ValidationUtils.readPositiveInt(scanner, "prompt: ");
        assertEquals(10, result);
    }

    @Test
    void readIntInRangeRejectsOutOfRangeThenAcceptsAValidChoice() {
        Scanner scanner = new Scanner("9\n2\n");
        int result = ValidationUtils.readIntInRange(scanner, "prompt: ", 1, 2);
        assertEquals(2, result);
    }

    @Test
    void readPositiveDoubleRejectsUnparsableTextThenAcceptsANumber() {
        Scanner scanner = new Scanner("abc\n100.5\n");
        double result = ValidationUtils.readPositiveDouble(scanner, "prompt: ");
        assertEquals(100.5, result);
    }

    @Test
    void readNonNegativeDoubleAcceptsZero() {
        Scanner scanner = new Scanner("-20\n0\n");
        double result = ValidationUtils.readNonNegativeDouble(scanner, "prompt: ");
        assertEquals(0.0, result);
    }

    @Test
    void readValidStatusRejectsAnUnknownLabelThenAcceptsAValidOne() {
        Scanner scanner = new Scanner("Done\nCompleted\n");
        TaskStatus result = ValidationUtils.readValidStatus(scanner, "prompt: ");
        assertEquals(TaskStatus.COMPLETED, result);
    }
}
