package paypals.exception;

public enum ExceptionMessage {
    INVALID_COMMAND("INPUT ERROR: Invalid command entered" +
            "\nTry these commands: add | addequal | delete | " +
            "edit | list | split | paid | unpaid | change | exit | help"),
    NO_DESCRIPTION("INPUT ERROR: No activity description, d/DESCRIPTION"),
    NO_IDENTIFIER("INPUT ERROR: No identifier entered, i/IDENTIFIER"),
    INVALID_FORMAT("INPUT ERROR: Correct format should be: "),
    INVALID_IDENTIFIER("INPUT ERROR: Invalid identifier entered, should be an integer: "),
    LIST_BALANCE_FORMAT("INPUT ERROR: Correct format should be: list balance n/NAME"),
    OUTOFBOUNDS_IDENTIFIER("INPUT ERROR: Identifier entered is out of bounds: "),
    OUTOFBOUNDS_UNSETTLED_IDENTIFIER("INPUT ERROR: Identifier entered is out of bounds in unsettled activities " +
                                    "(to clarify use 'list n/NAME' command): "),
    OUTOFBOUNDS_SETTLED_IDENTIFIER("INPUT ERROR: Identifier entered is out of bounds in settled activities " +
                                    "(to clarify use 'list n/NAME' command): "),
    NO_PAYER("INPUT ERROR: No name of payer"),
    PAYER_OWES("LOGIC ERROR: Payer owes himself or herself: "),
    DUPLICATE_FRIEND("LOGIC ERROR: Friend is mentioned more than once in an activity: "),
    INVALID_FRIEND("INPUT ERROR: Invalid friend entered: "),
    INVALID_AMOUNT("INPUT ERROR: Amount owed is not a number"),
    AMOUNT_OUT_OF_BOUNDS("INPUT ERROR: Amount entered should be more than $0"),
    NO_AMOUNT_ENTERED("INPUT ERROR: Amount owed is not entered for 1 or more friends"),
    MULTIPLE_AMOUNTS_ENTERED("INPUT ERROR: Multiple amounts entered for 1 or more friends"),
    ALREADY_PAID("LOGIC ERROR: Friend has already paid for this activity"),
    ALREADY_UNPAID("LOGIC ERROR: Friend has not paid for this activity"),
    STORAGE_DIR_NOT_CREATED("ERROR: An error has occurred, storage directory not created"),
    STORAGE_FILE_NOT_CREATED("ERROR: An error has occurred, storage file not created"),
    STORAGE_FILE_NOT_FOUND("ERROR: An error has occurred, storage file not found"),
    SAVE_NOT_WRITTEN("ERROR: An error has occurred, storage file not written"),
    LOAD_ERROR("An activity has been detected to be modified and corrupted, skipping corrupted activity..."),
    EDIT_AMOUNT_WHEN_PAID("INPUT ERROR: Unable to edit amount owed if it has been paid"),
    EDIT_FORMAT_ERROR("INPUT ERROR: Invalid edit format. The format should be one of the following:" +
            "\n Edit description: edit i/ID d/DESCRIPTION" +
            "\n Edit payer name: edit i/ID n/NEWNAME" +
            "\n Edit friend name: edit i/ID f/NEWNAME o/OLDNAME" +
            "\n Edit friend amount owed: edit i/ID a/NEWAMOUNT o/NAME"),
    NEGATIVE_AMOUNT("INPUT ERROR: Negative amount entered"),
    LARGE_AMOUNT("INPUT ERROR: Too large amount entered exceeding limit"),
    NOT_MONEY_FORMAT("INPUT ERROR: Please enter amounts up to 2 decimal places."),
    NO_FRIENDS("INPUT ERROR: No friends were entered"),
    INVALID_GROUP_NUMBER("INPUT ERROR: Group number does not exist or is invalid, please enter a valid group number"),
    EMPTY_FILENAME("INPUT ERROR: Input should not be empty. Please try again."),
    INVALID_FILENAME("INPUT ERROR: Filename specified must be a valid filename. Please try again."),
    FILENAME_DOES_NOT_EXIST("INPUT ERROR: Filename does not exist. Please try again."),
    NO_GROUP("There are no groups to delete."),
    FRIEND_DOES_NOT_EXIST("INPUT ERROR: The friend you have specified does not exist."),
    NUMBERS_IN_NAME("INPUT ERROR: Names cannot contain numbers."),
    SLASH_IN_NAME("INPUT ERROR: Names cannot contain slash character, /."),
    FRIEND_NAME_SAME_AS_PAYER("INPUT ERROR: New name cannot be the same as the payer."),
    FRIEND_NAME_SAME_AS_ANOTHER_FRIEND("INPUT ERROR: New friend name cannot be the same as another friend."),
    PAYER_NAME_SAME_AS_FRIEND("INPUT ERROR: New payer name cannot be the same a friend already in the activity"),
    MORE_THAN_1000_ACTIVITIES("You have hit 1000 or more activities for this group, " +
            "you are unable to add more activities"),
    LARGE_NUMBER_OF_ACTIVITIES("You have somehow entered a large number of activities (probably through modifying " +
            "save files), please ensure that there are less than 1000 activities for any given group" +
            "\n Exiting the program now..."),
    MULTIPLE_AMOUNTS_FOR_ADDEQUAL("INPUT ERROR: Only one amount can be provided."),
    PAYER_NAME_DOES_NOT_EXIST("INPUT ERROR: Payer name entered does not exist."),
    EXTRA_PARAMETERS("INPUT ERROR: Extra parameters detected, please follow the correct format."),
    MULTIPLE_IDENTIFIER("INPUT ERROR: Multiple Identifiers entered"),
    INVALID_DELETE_FORMAT("INPUT ERROR: Incorrect delete command format. The format is: delete i/ID");
    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
