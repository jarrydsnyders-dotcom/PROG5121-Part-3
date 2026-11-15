import java.util.Scanner;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.ArrayList;

class QuickChatApp {

    public static class Login {
        private String firstName;
        private String lastName;

        private boolean isRegistered = false;
        private String registeredUsername;
        private String registeredPassword;
        private String registeredCell;

        public Login() {}

        public Login(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public boolean checkUserName(String username) {
            if (username == null) return false;
            boolean hasUnderscore = username.contains("_");
            boolean correctLength = username.length() <= 5;
            return hasUnderscore && correctLength;
        }

        public boolean checkPasswordComplexity(String password) {
            if (password == null) return false;
            boolean minLength = password.length() >= 8;
            boolean hasCapital = !password.equals(password.toLowerCase());
            boolean hasNumber = Pattern.compile("[0-9]").matcher(password).find();
            boolean hasSpecial = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
            return minLength && hasCapital && hasNumber && hasSpecial;
        }

        public boolean checkCellPhoneNumber(String cellPhoneNumber) {
            if (cellPhoneNumber == null) return false;
            String regex = "^\\+\\d{1,3}\\d{1,10}$";
            return Pattern.matches(regex, cellPhoneNumber);
        }

        public String registerUser(String username, String password, String cellPhoneNumber, String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;

            if (!checkUserName(username)) {
                return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
            }

            if (!checkPasswordComplexity(password)) {
                return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
            }

            if (!checkCellPhoneNumber(cellPhoneNumber)) {
                return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
            }

            this.registeredUsername = username;
            this.registeredPassword = password;
            this.registeredCell = cellPhoneNumber;
            this.isRegistered = true;

            return "Username successfully captured.\nPassword successfully captured.\nCell number successfully captured.";
        }

        public boolean loginUser(String enteredUsername, String enteredPassword) {
            if (!isRegistered) return false;
            return enteredUsername.equals(registeredUsername) && enteredPassword.equals(registeredPassword);
        }

        public String returnLoginStatus(boolean loginSuccess) {
            if (loginSuccess) {
                return "Welcome " + firstName + " " + lastName + ", it is great to see you again.";
            } else {
                return "Username or password incorrect, please try again.";
            }
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }

    public static class Message {
        private String messageID;
        private int numSentMessages;
        private String sender;
        private String recipient;
        private String messageText;
        private String messageHash;
        private String status; // "Sent", "Stored", "Disregarded"

        public Message() {
            this.messageID = generateMessageID();
        }

        public Message(String sender, String recipient, String messageText, int sentCount, String status) {
            this.messageID = generateMessageID();
            this.sender = sender;
            this.recipient = recipient;
            this.messageText = messageText;
            this.numSentMessages = sentCount;
            this.status = status;
            this.messageHash = createMessageHash();
        }

        private String generateMessageID() {
            Random rand = new Random();
            long id = 1000000000L + (long)(rand.nextDouble() * 9000000000L);
            return String.valueOf(id);
        }

        public boolean checkMessageID() {
            return messageID != null && messageID.length() <= 10;
        }

        public boolean checkRecipientCell(String recipientCell) {
            if (recipientCell == null) return false;
            String regex = "^\\+\\d{1,3}\\d{1,10}$";
            return Pattern.matches(regex, recipientCell);
        }

        public String createMessageHash() {
            if (messageID == null || messageID.length() < 2 || messageText == null || messageText.trim().isEmpty()) {
                return "";
            }
            String firstTwoId = messageID.substring(0, 2);
            String cleanedMsg = messageText.replaceAll("[^a-zA-Z ]", "").trim();
            String[] words = cleanedMsg.split("\\s+");

            String firstWord = words.length > 0 ? words[0] : "";
            String lastWord = words.length > 0 ? words[words.length - 1] : "";
            String combinedWords = (firstWord + lastWord).toUpperCase();

            return firstTwoId + ":" + numSentMessages + ":" + combinedWords;
        }

        public boolean checkMessageLength() {
            return messageText != null && messageText.length() <= 250;
        }

        public String getMessageID() { return messageID; }
        public String getSender() { return sender; }
        public String getRecipient() { return recipient; }
        public String getMessageText() { return messageText; }
        public String getMessageHash() { return messageHash; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String sentMessage(int choice) {
            switch (choice) {
                case 1:
                    this.status = "Sent";
                    return "Message successfully sent.";
                case 2:
                    this.status = "Disregarded";
                    return "Press 0 to delete the message.";
                case 3:
                    this.status = "Stored";
                    return "Message successfully stored.";
                default:
                    return "Invalid option selected.";
            }
        }

        public String printMessages() {
            return "Message ID: " + messageID + "\n" +
                    "Message Hash: " + messageHash + "\n" +
                    "Sender: " + sender + "\n" +
                    "Recipient: " + recipient + "\n" +
                    "Message: " + messageText + "\n" +
                    "Status: " + status;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("========================================");
        System.out.println(" WELCOME TO QUICKCHAT APP ");
        System.out.println("========================================");

        System.out.print("Enter First Name: ");
        String fName = scanner.nextLine();
        System.out.print("Enter Last Name: ");
        String lName = scanner.nextLine();

        Login loginSystem = new Login(fName, lName);

        boolean registered = false;
        while (!registered) {
            System.out.println("\n--- Registration ---");
            System.out.print("Enter Username (must contain '_' and be <= 5 chars, e.g., kyl_1): ");
            String uname = scanner.nextLine();
            System.out.print("Enter Password (>= 8 chars, 1 capital, 1 number, 1 special): ");
            String pwd = scanner.nextLine();
            System.out.print("Enter Cell Phone Number (with international code, e.g., +27838968976): ");
            String cell = scanner.nextLine();

            String regResult = loginSystem.registerUser(uname, pwd, cell, fName, lName);
            System.out.println(regResult);

            if (regResult.contains(" successfully captured.")) {
                registered = true;
            }
        }

        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.println("\n--- Login ---");
            System.out.print("Enter Username: ");
            String loginUser = scanner.nextLine();
            System.out.print("Enter Password: ");
            String loginPwd = scanner.nextLine();

            loggedIn = loginSystem.loginUser(loginUser, loginPwd);
            String statusMsg = loginSystem.returnLoginStatus(loggedIn);
            System.out.println(statusMsg);
        }

        System.out.println("\nWelcome to QuickChat.");

        ArrayList<Message> sentMessagesList = new ArrayList<>();
        ArrayList<Message> disregardedMessagesList = new ArrayList<>();
        ArrayList<Message> storedMessagesList = new ArrayList<>();
        ArrayList<String> messageHashList = new ArrayList<>();
        ArrayList<String> messageIdList = new ArrayList<>();

        int choice = 0;
        int sentCount = 0;

        while (choice != 4) {
            System.out.println("\n----------------- MENU -----------------");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages (Coming Soon)");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.print("Enter your choice: ");
            choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    System.out.print("How many messages do you wish to enter? ");
                    int numMessages = Integer.parseInt(scanner.nextLine());

                    for (int i = 0; i < numMessages; i++) {
                        System.out.println("\n--- Processing Message " + (i + 1) + " of " + numMessages + " ---");

                        System.out.print("Enter Recipient Cell Number (e.g., +27718693002): ");
                        String recipientCell = scanner.nextLine();

                        Message tempValidator = new Message();
                        while (!tempValidator.checkRecipientCell(recipientCell)) {
                            System.out.println("Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.");
                            System.out.print("Re-enter Recipient Cell Number: ");
                            recipientCell = scanner.nextLine();
                        }

                        System.out.print("Enter Message text (max 250 characters): ");
                        String text = scanner.nextLine();

                        sentCount++;
                        Message msg = new Message(fName + " " + lName, recipientCell, text, sentCount, "Sent");

                        while (!msg.checkMessageLength()) {
                            System.out.println("Please enter a message of less than 250 characters.");
                            System.out.print("Re-enter Message text: ");
                            text = scanner.nextLine();
                            msg = new Message(fName + " " + lName, recipientCell, text, sentCount, "Sent");
                        }

                        System.out.println("Message ready to send.");
                        System.out.println("Choose an action:");
                        System.out.println("1) Send Message");
                        System.out.println("2) Disregard Message (Delete)");
                        System.out.println("3) Store Message to send later");
                        System.out.print("Enter choice (1-3): ");
                        int actionChoice = Integer.parseInt(scanner.nextLine());

                        String actionResponse = msg.sentMessage(actionChoice);
                        System.out.println(actionResponse);

                        messageIdList.add(msg.getMessageID());
                        messageHashList.add(msg.getMessageHash());

                        if (actionChoice == 1) {
                            sentMessagesList.add(msg);
                            System.out.println("\n--- Message Details ---");
                            System.out.println(msg.printMessages());
                        } else if (actionChoice == 2) {
                            disregardedMessagesList.add(msg);
                            System.out.println("Press 0 to delete the message.");
                        } else if (actionChoice == 3) {
                            storedMessagesList.add(msg);
                            System.out.println("\n--- Message Details ---");
                            System.out.println(msg.printMessages());
                        }
                    }
                    System.out.println("\nTotal messages successfully processed: " + sentCount);
                    break;

                case 2:
                    System.out.println("Coming Soon.");
                    break;

                case 3:
                    System.out.println("Exiting QuickChat. Goodbye!");
                    break;

                case 4:

                    boolean backToMain = false;
                    while (!backToMain) {
                        System.out.println("\n--- Stored Messages Menu ---");
                        System.out.println("a) Display the sender and recipient of all stored messages");
                        System.out.println("b) Display the longest stored message");
                        System.out.println("c) Search for a message ID and display corresponding recipient and message");
                        System.out.println("d) Search for all messages stored for a particular recipient");
                        System.out.println("e) Delete a message using the message hash");
                        System.out.println("f) Display a report listing full details of all stored messages");
                        System.out.println("g) Return to Main Menu");
                        System.out.print("Enter your choice (a-g): ");
                        String subChoice = scanner.nextLine().trim().toLowerCase();

                        switch (subChoice) {
                            case "a":
                                System.out.println("\n--- Sender and Recipient of All Stored Messages ---");
                                if (storedMessagesList.isEmpty()) {
                                    System.out.println("No stored messages found.");
                                } else {
                                    for (Message m : storedMessagesList) {
                                        System.out.println("Sender: " + m.getSender() + " | Recipient: " + m.getRecipient());
                                    }
                                }
                                break;

                            case "b":
                                System.out.println("\n--- Longest Stored Message ---");
                                if (storedMessagesList.isEmpty()) {
                                    System.out.println("No stored messages found.");
                                } else {
                                    Message longest = storedMessagesList.get(0);
                                    for (Message m : storedMessagesList) {
                                        if (m.getMessageText().length() > longest.getMessageText().length()) {
                                            longest = m;
                                        }
                                    }
                                    System.out.println("Message: \"" + longest.getMessageText() + "\" (Length: " + longest.getMessageText().length() + ")");
                                }
                                break;

                            case "c":
                                System.out.println("\n--- Search Message by ID ---");
                                System.out.print("Enter Message ID to search: ");
                                String searchId = scanner.nextLine().trim();
                                boolean foundId = false;
                                for (Message m : storedMessagesList) {
                                    if (m.getMessageID().equals(searchId)) {
                                        System.out.println("Recipient: " + m.getRecipient() + " | Message: " + m.getMessageText());
                                        foundId = true;
                                        break;
                                    }
                                }
                                if (!foundId) {
                                    System.out.println("No stored message found with ID: " + searchId);
                                }
                                break;

                            case "d":
                                System.out.println("\n--- Search Messages by Recipient ---");
                                System.out.print("Enter Recipient Cell Number: ");
                                String searchRecipient = scanner.nextLine().trim();
                                boolean foundRecip = false;
                                for (Message m : storedMessagesList) {
                                    if (m.getRecipient().equals(searchRecipient)) {
                                        System.out.println("Message: " + m.getMessageText());
                                        foundRecip = true;
                                    }
                                }
                                if (!foundRecip) {
                                    System.out.println("No stored messages found for recipient: " + searchRecipient);
                                }
                                break;

                            case "e":
                                System.out.println("\n--- Delete Message by Hash ---");
                                System.out.print("Enter Message Hash to delete: ");
                                String searchHash = scanner.nextLine().trim();
                                boolean removed = false;
                                for (int i = 0; i < storedMessagesList.size(); i++) {
                                    Message m = storedMessagesList.get(i);
                                    if (m.getMessageHash().equalsIgnoreCase(searchHash)) {
                                        System.out.println("Message: \"" + m.getMessageText() + "\" successfully deleted.");
                                        storedMessagesList.remove(i);
                                        removed = true;
                                        break;
                                    }
                                }
                                if (!removed) {
                                    System.out.println("No stored message found matching hash: " + searchHash);
                                }
                                break;

                            case "f":
                                System.out.println("\n--- Report: All Stored Messages Details ---");
                                if (storedMessagesList.isEmpty()) {
                                    System.out.println("No stored messages found.");
                                } else {
                                    for (Message m : storedMessagesList) {
                                        System.out.println("----------------------------------------");
                                        System.out.println(m.printMessages());
                                    }
                                    System.out.println("----------------------------------------");
                                }
                                break;

                            case "g":
                                backToMain = true;
                                break;

                            default:
                                System.out.println("Invalid option. Please choose between a and g.");
                        }
                    }
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }
}