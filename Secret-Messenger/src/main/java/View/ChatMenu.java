package View;

import Controller.ChatController;
import Model.Exceptions.*;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatMenu extends Menu {

    public ChatMenu() {
        this.commandPatterns = this.setCommands();
    }


    @Override
    public Pattern[] setCommands() {
        String[] commands = {"^portconfig (?=[\\w -]*--\\blisten\\b)(?=[\\w -]*--\\bport (?<portNumber>\\d+)?\\b).+$",
                "^portconfig (?=[\\w -]*--\\blisten\\b)(?=[\\w -]*--\\bport (?<portNumber>\\d+)?\\b)" +
                        "(?=[\\w -]*--\\brebind\\b).+", "^send (?=[\\w -]*--\\bmessage \"(?<message>\\d+)\"?\\b)" +
                "(?=[\\w -]*--\\bport (?<portNumber>\\d+)?\\b)(?=[\\w -]*--\\bhost (?<host>\\d+)?\\b).+",
                "^send (?=[\\w -]*--\\bmessage \"(?<message>\\d+)\"?\\b)(?=[\\w -]*--\\busername " +
                        "(?<username>\\d+)?\\b).+", "^focus (?=[\\w -]*--\\bstart\\b)(?=[\\w -]*--\\bhost " +
                "(?<host>\\d+)?\\b).+", "^send (?=[\\w -]*--\\bmessage \"(?<message>\\d+)\"?\\b)" +
                "(?=[\\w -]*--\\bport (?<portNumber>\\d+)?\\b).+", "^focus (?=[\\w -]*--\\bport (?<port>\\d+)?\\b).+",
                "^focus (?=[\\w -]*--\\bstart\\b)(?=[\\w -]*--\\bhost (?<host>\\d+)?\\b)(?=[\\w -]*--\\bport " +
                        "(?<portNumber>\\d+)?\\b).+", "^focus (?=[\\w -]*--\\bstart\\b)(?=[\\w -]*--\\busername " +
                "(?<username>\\d+)?\\b).+", "^send (?=[\\w -]*--\\bmessage \"(?<message>\\d+)\"?\\b).+"};

        Pattern[] patterns = new Pattern[commands.length];
        for (int i = 0; i < commands.length; i++)
            patterns[i] = Pattern.compile(commands[i]);
        return patterns;
    }


    private void setPort(String command) throws PortIsAlreadySetException {
        Matcher matcher = getMatcher(command, commandPatterns[0].pattern());
        matcher.find();
        try {
            int portNumber = Integer.parseInt(matcher.group("portNumber"));
            ChatController.getInstance().setPort(portNumber);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number!");
        }
    }


    private void rebindPort(String command) throws PortIsNotOpenException {
        Matcher matcher = getMatcher(command, commandPatterns[1].pattern());
        matcher.find();
        try {
            int portNumber = Integer.parseInt(matcher.group("portNumber"));
            ChatController.getInstance().rebindPort(portNumber);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number!");
        }
    }


    private void closePort(String command) throws PortIsNotOpenException {
        Matcher matcher = getMatcher(command, commandPatterns[2].pattern());
        matcher.find();
        try {
            int portNumber = Integer.parseInt(matcher.group("portNumber"));
            ChatController.getInstance().closePort(portNumber);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number!");
        }
    }


    private void sendMessageByAddress(String command) throws CouldNotSendMessageException {
        Matcher matcher = getMatcher(command, commandPatterns[3].pattern());
        matcher.find();

        try {
            String message = matcher.group("message");
            String host = matcher.group("host");
            int portNumber = Integer.parseInt(matcher.group("portNumber"));
            ChatController.getInstance().sendMessageByAddress(message, host, portNumber);
        } catch (NumberFormatException e) {
            throw new CouldNotSendMessageException();
        }
    }

    private void sendMessageByUsername(String command) throws CouldNotSendMessageException, ContactNotFoundException {
        Matcher matcher = getMatcher(command, commandPatterns[4].pattern());
        matcher.find();
        String message = matcher.group("message");
        String username = matcher.group("username");
        ChatController.getInstance().sendMessageByUsername(username, message);

    }


    private void focusONHost(String command) {
        ChatController.getInstance().stopFocus();
        Matcher matcher = getMatcher(command, commandPatterns[5].pattern());
        matcher.find();
        String host = matcher.group("host");
        ChatController.getInstance().focusOnHost(host);
    }


    private void sendMessageByAddressFocusedOnHost(String command) throws CouldNotSendMessageException,
            NotFocusedOnHostException {
        Matcher matcher = getMatcher(command, commandPatterns[6].pattern());
        matcher.find();
        try {
            String message = matcher.group("message");
            int portNumber = Integer.parseInt(matcher.group("portNumber"));
            ChatController.getInstance().sendMessageByAddressFocusedOnHost(message, portNumber);
        } catch (NumberFormatException e) {
            throw new CouldNotSendMessageException();
        }
    }


    private void focusOnPort(String command) throws Exception {
        Matcher matcher = getMatcher(command, commandPatterns[7].pattern());
        matcher.find();
        try {
            int portNumber = Integer.parseInt(matcher.group("portNumber"));
            ChatController.getInstance().focusOnPort(portNumber);
        } catch (NumberFormatException e) {
            throw new Exception("Invalid port number!");
        }
    }

    private void focusOnHostAndPort(String command) throws Exception {
        Matcher matcher = getMatcher(command, commandPatterns[8].pattern());
        matcher.find();
        try {
            String host = matcher.group("host");
            int portNumber = Integer.parseInt(matcher.group("portNumber"));
            ChatController.getInstance().focusOnHostAndPort(host, portNumber);
        } catch (NumberFormatException e) {
            throw new Exception("Invalid port number!");
        }
    }

    private void focusOnUser(String command) throws ContactNotFoundException {
        ChatController.getInstance().stopFocus();
        Matcher matcher = getMatcher(command, commandPatterns[9].pattern());
        matcher.find();
        String username = matcher.group("username");
        ChatController.getInstance().focusOnUser(username);
    }


    private void sendMessageFullFocused(String command) throws NotFullFoucsedException {
        Matcher matcher = getMatcher(command, commandPatterns[10].pattern());
        matcher.find();
        String message = matcher.group("message");
        ChatController.getInstance().sendMessageFullFocused(message);
    }


    private void stopFocus() {
        ChatController.getInstance().stopFocus();
    }


    private void showContacts() throws NoItemAvailableException {
        ChatController.getInstance().showContacts();
    }


    public void run(Scanner input) {
        String command = input.nextLine();
        try {
            if (this.commandPatterns[0].matcher(command).find() && Menu.getCurrentMenu() instanceof ChatMenu)
                this.setPort(command);
            else if (this.commandPatterns[1].matcher(command).find() && Menu.getCurrentMenu() instanceof ChatMenu)
                this.rebindPort(command);
            else if (this.commandPatterns[2].matcher(command).find() && Menu.getCurrentMenu() instanceof ChatMenu)
                this.closePort(command);
            else if (this.commandPatterns[3].matcher(command).find() && Menu.getCurrentMenu() instanceof ChatMenu)
                this.sendMessageByAddress(command);
            else if (this.commandPatterns[4].matcher(command).find() && Menu.getCurrentMenu() instanceof ChatMenu)
                this.sendMessageByUsername(command);
            else if (this.commandPatterns[5].matcher(command).find() && Menu.getCurrentMenu() instanceof ChatMenu)
                this.focusONHost(command);
            else if (this.commandPatterns[6].matcher(command).find() && Menu.getCurrentMenu() instanceof ChatMenu)
                this.sendMessageByAddressFocusedOnHost(command);
            else if (this.commandPatterns[7].matcher(command).find() && Menu.getCurrentMenu() instanceof ChatMenu)
                this.focusOnPort(command);
            else if (this.commandPatterns[8].matcher(command).find() && Menu.getCurrentMenu() instanceof ChatMenu)
                this.focusOnHostAndPort(command);
            else if (this.commandPatterns[9].matcher(command).find() && Menu.getCurrentMenu() instanceof ChatMenu)
                this.focusOnUser(command);
            else if (this.commandPatterns[10].matcher(command).find() && Menu.getCurrentMenu() instanceof ChatMenu)
                this.sendMessageFullFocused(command);
            else if (this.commandPatterns[11].matcher(command).find() && Menu.getCurrentMenu() instanceof ChatMenu)
                this.stopFocus();
            else
                throw new InvalidCommandException();
            System.out.println("success");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    //TODO saving user contact info

}


