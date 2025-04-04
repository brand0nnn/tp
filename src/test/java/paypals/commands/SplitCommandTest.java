package paypals.commands;

import org.junit.jupiter.api.Test;
import paypals.PayPalsTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SplitCommandTest extends PayPalsTest {

    @Test
    public void testSplitCommand_oneActivityOneFriend_twoSystemPrintLines() {
        callCommand(new AddCommand("d/lunch n/John f/Jane a/28"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        callCommand(new SplitCommand(""));

        System.setOut(System.out);

        String expectedOutput = "Best way to settle debts:\n" +
                "Jane pays John $28.00";
        assertEquals(expectedOutput.trim().replace("\r\n", "\n"), outputStream.toString().trim().replace("\r\n", "\n"));
    }

    @Test
    public void testSplitCommand_oneActivityTwoFriends_threeSystemPrintLines() {
        callCommand(new AddCommand("d/lunch n/John f/Jane a/28 f/Jeremy a/10"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        callCommand(new SplitCommand(""));

        System.setOut(System.out);

        String expectedOutput = "Best way to settle debts:\n" +
                "Jane pays John $28.00\n" +
                "Jeremy pays John $10.00\n";
        assertEquals(expectedOutput.trim().replace("\r\n", "\n"), outputStream.toString().trim().replace("\r\n", "\n"));
    }

    @Test
    public void testSplitCommand_twoActivities_threeSystemPrintLines() {
        callCommand(new AddCommand("d/lunch n/John f/Jane a/28 f/Jeremy a/10"));
        callCommand(new AddCommand("d/lunch n/John f/Jane a/18"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        callCommand(new SplitCommand(""));

        System.setOut(System.out);

        String expectedOutput = "Best way to settle debts:\n" +
                "Jane pays John $46.00\n" +
                "Jeremy pays John $10.00\n";
        assertEquals(expectedOutput.trim().replace("\r\n", "\n"), outputStream.toString().trim().replace("\r\n", "\n"));
    }

    @Test
    public void isExit_someInput_expectFalse() {
        SplitCommand command = new SplitCommand("");

        assertFalse(command.isExit(), "isExit() should return false for a SplitCommand");
    }
}
