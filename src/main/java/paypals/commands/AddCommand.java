package paypals.commands;

import paypals.Activity;
import paypals.ActivityManager;
import paypals.Person;
import paypals.exception.ExceptionMessage;
import paypals.exception.PayPalsException;
import paypals.util.Logging;
import paypals.util.UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddCommand extends Command {

    private static final String WRONG_ADD_FORMAT =
            "Format: add d/DESCRIPTION n/PAYER f/FRIEND1 a/AMOUNT_OWED_1 f/FRIEND2 a/AMOUNT_OWED_2...";

    public AddCommand(String command) {
        super(command);
    }

    private String extractValue(String key, ExceptionMessage exceptionMessage) throws PayPalsException {
        String regex = key + "\\s*([^/]+?)(?=\\s+[a-zA-Z]/|$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(command);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            Logging.logWarning("Invalid input format detected");
            System.out.println(WRONG_ADD_FORMAT);
            throw new PayPalsException(exceptionMessage);
        }
    }

    private void validateFriend(String payer, String oweName, HashMap<String, Double> owedMap) throws PayPalsException {
        assert payer != null : "Payer name should not be null";
        assert oweName != null : "OweName should not be null";
        assert owedMap != null : "Owed map should not be null";

        if (payer.equals(oweName)) {
            Logging.logWarning("Payer tried to owe themselves.");
            throw new PayPalsException(ExceptionMessage.PAYER_OWES);
        }
        if (owedMap.containsKey(oweName)) {
            Logging.logWarning("Duplicate friend entry detected: {0} already exists.");
            System.out.println(WRONG_ADD_FORMAT);
            throw new PayPalsException(ExceptionMessage.DUPLICATE_FRIEND);
        }
    }

    public void execute(ActivityManager activityManager, boolean enablePrint) throws PayPalsException {
        assert activityManager != null : "ActivityManager should not be null";

        UI ui = new UI(enablePrint);
        HashMap<String, Double> owed = new HashMap<String, Double>();
        HashMap<String, Double> netOwedMap = activityManager.getNetOwedMap();
        HashMap<String, ArrayList<Activity>> personActivitesMap = activityManager.getPersonActivitiesMap();

        // Step 1: Extract description and payer name
        String description = extractValue("d/", ExceptionMessage.NO_DESCRIPTION);
        String name = extractValue("n/", ExceptionMessage.NO_PAYER);

        assert !description.isEmpty() : "Description should not be null or empty";
        assert !name.isEmpty() : "Payer name should not be null or empty";

        // Step 2: Capture all (f/... a/...) pairs
        double totalOwed = 0;
        String[] pairs = command.split("\\s+f/");
        for (int i = 1; i< pairs.length; i++) {
            String[] parameters = pairs[i].split("\\s+a/");
            if (parameters.length==2) {
                String oweName = parameters[0].trim();
                Double oweAmount;
                try {
                    oweAmount = Double.parseDouble(parameters[1]);
                } catch (Exception e) {
                    Logging.logWarning("Invalid amount entered for friend");
                    throw new PayPalsException(ExceptionMessage.INVALID_AMOUNT);
                }
                validateFriend(name, oweName, owed);
                owed.put(oweName, oweAmount);
                netOwedMap.put(oweName, netOwedMap.getOrDefault(oweName,0.0) - oweAmount);
                totalOwed += oweAmount;

                Logging.logInfo("Friend added successfully");
            } else {
                Logging.logWarning("Incorrect number of parameters detected: {0}");
                throw new PayPalsException(parameters.length < 2
                        ? ExceptionMessage.NO_AMOUNT_ENTERED: ExceptionMessage.MULTIPLE_AMOUNTS_ENTERED);
            }
        }

        netOwedMap.put(name, netOwedMap.getOrDefault(name,0.0) + totalOwed);

        assert totalOwed >= 0 : "Total owed amount should not be negative";

        ui.print("Desc: "+description);
        ui.print("Name of payer: "+name);
        ui.print("Number of friends who owe " + name +": "+owed.size());
        Activity newActivity = new Activity(description, new Person(name, -totalOwed, false), owed);
        activityManager.addActivity(newActivity);

        //Map each friend to the activity
        for (Map.Entry<String, Double> entry : owed.entrySet()){
            ArrayList<Activity> activitiesList =
                    personActivitesMap.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());

            activitiesList.add(newActivity);
        }
        //Map the payer to the activity
        ArrayList<Activity> activitiesList = personActivitesMap.computeIfAbsent(name, k -> new ArrayList<>());
        activitiesList.add(newActivity);

        Logging.logInfo("Activity added successfully");
    }
}
