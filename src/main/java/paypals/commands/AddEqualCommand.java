package paypals.commands;

import paypals.Activity;
import paypals.ActivityManager;
import paypals.Person;
import paypals.exception.ExceptionMessage;
import paypals.exception.PayPalsException;
import paypals.util.Logging;
import paypals.util.UI;

import java.util.Arrays;
import java.util.HashMap;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the addequal command in the PayPals application.
 * This command allows a user to add a new expense activity where the total amount
 * is split equally among all participants including the payer.
 */
public class AddEqualCommand extends AddCommand {

    private static final String WRONG_ADDEQUAL_FORMAT =
            "addequal d/DESCRIPTION n/PAYER f/FRIEND1 f/FRIEND2 ... a/AMOUNT_OWED";
    private static final double LARGE_AMOUNT_LIMIT = 10000.0;
    private static final String MONEY_FORMAT = "^-?\\d+(\\.\\d{1,2})?$";

    /**
     * Constructs an AddEqualCommand with the raw user input command.
     *
     * @param command the full input string entered by the user
     */
    public AddEqualCommand(String command) {
        super(command);
    }

    /**
     * Executes the addequal command to record an activity with equal expense sharing.
     * The total amount is split among the payer and all listed friends.
     *
     * @param activityManager the manager that handles all activities
     * @param enablePrint     flag to control whether output should be shown
     * @throws PayPalsException if the command format or amount is invalid
     */
    public void execute(ActivityManager activityManager, boolean enablePrint) throws PayPalsException {
        assert activityManager != null : "ActivityManager should not be null";

        UI ui = new UI(enablePrint);
        HashMap<String, String> names = new HashMap<>();
        HashMap<String, Double> owed = new HashMap<>();
        if (activityManager.getSize() >= 1000) {
            throw new PayPalsException(ExceptionMessage.MORE_THAN_1000_ACTIVITIES);
        }
        validatePrefixOrder();
        String description = extractValue("d/", ExceptionMessage.NO_DESCRIPTION);
        String name = extractValue("n/", ExceptionMessage.NO_PAYER);
        if (name.matches(".*\\d.*")) {
            throw new PayPalsException(ExceptionMessage.NUMBERS_IN_NAME);
        }
        if (name.contains("/")) {
            throw new PayPalsException(ExceptionMessage.SLASH_IN_NAME);
        }

        assert !description.isEmpty() : "Description should not be null or empty";
        assert !name.isEmpty() : "Payer name should not be null or empty";

        double totalAmount = getTotalAmount();
        assert totalAmount >= 0 : "Total amount should not be less than 0";

        String friendsPart = command.split("(?i)a/")[0];
        String[] friends = friendsPart.split("\\s+(?i)f/");
        if (friends.length <= 1) {
            throw new PayPalsException(ExceptionMessage.NO_FRIENDS);
        }
        friends = Arrays.copyOfRange(friends, 1, friends.length);  // remove command part

        double amount = totalAmount/ (friends.length + 1);
        BigDecimal bdAmount = new BigDecimal(Double.toString(amount));
        bdAmount = bdAmount.setScale(2, RoundingMode.HALF_EVEN);
        double roundedAmount = bdAmount.doubleValue();

        for (String friend : friends) {
            String friendName = friend.trim();

            // Add explicit validation for empty friend names.
            if (friendName.isEmpty()) {
                throw new PayPalsException(ExceptionMessage.INVALID_FRIEND);
            }
            if (friendName.matches(".*\\d.*")) {
                throw new PayPalsException(ExceptionMessage.NUMBERS_IN_NAME);
            }
            if (friendName.contains("/")) {
                throw new PayPalsException(ExceptionMessage.SLASH_IN_NAME);
            }

            validateFriend(name, friendName, names);
            names.put(friendName.toLowerCase(), friendName);
            owed.put(friendName, roundedAmount);
            Logging.logInfo("Friend added successfully");
        }

        ui.print("Desc: " + description);
        ui.print("Name of payer: " + name);
        ui.print("Number of friends who owe " + name + ": " + owed.size());

        Activity newActivity = new Activity(description, new Person(name, -(totalAmount - roundedAmount), false), owed);
        activityManager.addActivity(newActivity);

        Logging.logInfo("Activity added successfully");
    }

    /**
     * Extracts and validates the total amount from the user input.
     *
     * @return the parsed amount as a double
     * @throws PayPalsException if the amount is not a number, negative, or improperly formatted
     */
    private double getTotalAmount() throws PayPalsException {
        int count = command.split("(?i)a/").length - 1; // Count occurrences of "a/"
        if (count > 1) {
            Logging.logWarning("Multiple 'a/' prefixes found in command");
            throw new PayPalsException(ExceptionMessage.MULTIPLE_AMOUNTS_FOR_ADDEQUAL);
        }

        String amountEntered = extractValue("a/", ExceptionMessage.NO_AMOUNT_ENTERED);
        double totalAmount;
        try {
            totalAmount = Double.parseDouble(amountEntered);
        } catch (NumberFormatException e) {
            Logging.logWarning("Invalid amount entered: " + amountEntered);
            throw new PayPalsException(ExceptionMessage.INVALID_AMOUNT);
        }
        if (!isValidAmount(amountEntered)) {
            throw new PayPalsException(ExceptionMessage.NOT_MONEY_FORMAT);
        }
        if (totalAmount > LARGE_AMOUNT_LIMIT) {
            Logging.logWarning("Amount entered exceeds upper limit");
            throw new PayPalsException(ExceptionMessage.LARGE_AMOUNT);
        }
        if (totalAmount <= 0) {
            throw new PayPalsException(ExceptionMessage.NEGATIVE_AMOUNT);
        }
        return totalAmount;
    }

    /**
     * Validates whether a given string represents a valid monetary amount.
     *
     * @param amountStr the string to validate
     * @return true if the format is valid; false otherwise
     */
    public boolean isValidAmount(String amountStr) {
        return amountStr.matches(MONEY_FORMAT);
    }

    @Override
    public void validatePrefixOrder() throws PayPalsException {
        // Get positions of d/ flag
        int dIndex = command.indexOf("d/");
        if (dIndex == -1) {
            dIndex = command.indexOf("D/");
        }
        // Get positions of n/ flag
        int nIndex = command.indexOf("n/");
        if (nIndex == -1) {
            nIndex = command.indexOf("N/");
        }
        // Get positions of f/ flag
        int fIndex = command.indexOf("f/");
        if (fIndex == -1) {
            fIndex = command.indexOf("F/");
        }
        // Get positions of a/ flag
        int aIndex = command.indexOf("a/");
        if (aIndex == -1) {
            aIndex = command.indexOf("A/");
        }

        // Check for incorrect flags, with 2 or more characters before the '/' character
        String regex = "(?<=\\S[a-zA-Z]\\/)([^\\/]+?)(?=\\s+[a-zA-Z]+\\/|$)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        if (matcher.find()) {
            throw new PayPalsException(ExceptionMessage.INVALID_FORMAT, WRONG_ADDEQUAL_FORMAT);
        }

        if (dIndex == -1){
            throw new PayPalsException(ExceptionMessage.NO_DESCRIPTION);
        }

        if (nIndex == -1){
            throw new PayPalsException(ExceptionMessage.NO_PAYER);
        }

        if (fIndex == -1){
            throw new PayPalsException(ExceptionMessage.NO_FRIENDS);
        }

        if (aIndex == -1){
            throw new PayPalsException(ExceptionMessage.NO_AMOUNT_ENTERED);
        }

        if (!(dIndex < nIndex && nIndex < fIndex && fIndex < aIndex)) {
            throw new PayPalsException(ExceptionMessage.INVALID_FORMAT, WRONG_ADDEQUAL_FORMAT);
        }
        if (command.indexOf("d/",dIndex+1) != -1 || command.indexOf("n/",nIndex+1) != -1){
            throw new PayPalsException(ExceptionMessage.INVALID_FORMAT, WRONG_ADDEQUAL_FORMAT);
        }
    }
}
