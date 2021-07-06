package View;

import Controller.ChatController;
import Controller.UserConfigController;
import Model.Exceptions.*;
import org.apache.commons.cli.*;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatMenu extends Menu {

    public ChatMenu() {
        this.commandPatterns = this.setCommands();
    }


    @Override
    public Pattern[] setCommands() {
        String[] commands = {"^portconfig (?=[\\w -]*--\\blisten\\b)(?=[\\w -]*--\\bport (?<portNumber>[\\d.]+)?\\b).+$",
                "^portconfig (?=[\\w -]*--\\blisten\\b)(?=[\\w -]*--\\bport (?<portNumber>[\\d.]+)?\\b)" +
                "(?=[\\w -]*--\\brebind\\b).+$", "^portconfig (?=[\\w -]*--\\bclose\\b)(?=[\\w -]*--\\bport (?<portNumber>[\\d.]+)?\\b).+"
                , "^send (?=[\\w -]*--\\bmessage \"(?<message>.*)\"?\\b)(?=[\\w -]*--\\bport (?<portNumber>[\\d.]+)?\\b)" +
                "(?=[\\w -]*--\\bhost (?<host>\\S+)?\\b).+$", "^send (?=[\\w -]*--\\bmessage \"(?<message>\\d+)\"?\\b)" +
                "(?=[\\w -]*--\\busername (?<username>\\S+)?\\b).+$", "^focus (?=[\\w -]*--\\bstart\\b)(?=[\\w -]*--\\bhost " +
                "(?<host>\\S+)?\\b).+$", "^send (?=[\\w -]*--\\bmessage \"(?<message>.+)\"?\\b)(?=[\\w -]*--\\bport " +
                "(?<portNumber>[\\d.]+)?\\b).+$", "^focus (?=[\\w -]*--\\bport (?<portNumber>[\\d.]+)?\\b).+$",
                "^focus (?=[\\w -]*--\\bstart\\b)(?=[\\w -]*--\\bhost (?<host>\\S+)?\\b)(?=[\\w -]*--\\bport (?<portNumber>[\\d.]+)" +
                "?\\b).+", "^focus (?=[\\w -]*--\\bstart\\b)(?=[\\w -]*--\\busername (?<username>\\S+)?\\b).+$",
                "^send (?=[\\w -]*--\\bmessage \"(?<message>.+)\"?\\b).+$", "^focus (?=[\\w -]*--\\bstop\\b).+$",
                "show (?=[\\w -]*--\\bcontacts\\b).+$", "show (?=[\\w -]*--\\bcontact (?<username>\\S+)?\\b).+$", "show " +
                "(?=[\\w -]*--\\bsenders\\b).+$", "show (?=[\\w -]*--\\bmessages\\b).+$", "show (?=[\\w -]*--\\bcount\\b)" +
                "(?=[\\w -]*--\\bsenders\\b).+$", "show (?=[\\w -]*--\\bcount\\b)(?=[\\w -]*--\\bmessages\\b).+$",
                "show (?=[\\w -]*--\\bmessages\\b)(?=[\\w -]*--\\bfrom (?<username>\\S+)?\\b).+$", "show (?=[\\w -]*--\\bcount\\b)" +
                "(?=[\\w -]*--\\bmessages\\b)(?=[\\w -]*--\\bfrom (?<username>\\S+)?\\b).+$", "^userconfig --logout$"};

        Pattern[] patterns = new Pattern[commands.length];
        for (int i = 0; i < commands.length; i++)
            patterns[i] = Pattern.compile(commands[i]);
        return patterns;
    }


    private void setPort(String command) throws Exception {
        Matcher matcher = getMatcher(command, commandPatterns[0].pattern());
        matcher.find();
        try {
            int portNumber = Integer.parseInt(matcher.group("portNumber"));
            ChatController.getInstance().setPort(portNumber);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number!");
        }
    }


    private void rebindPort(String command) throws PortIsAlreadySetException {
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


    private void sendMessageByAddress(String message, String host, String port) throws CouldNotSendMessageException {
        try {
            int portNumber = Integer.parseInt(port);
            ChatController.getInstance().sendMessageByAddress(message, host, portNumber);
        } catch (NumberFormatException e) {
            throw new CouldNotSendMessageException();
        }
    }

    private void sendMessageByUsername(String username, String message) throws CouldNotSendMessageException, ContactNotFoundException {
        ChatController.getInstance().sendMessageByUsername(username, message);
    }


    private void focusONHost(String command) {
        ChatController.getInstance().stopFocus();
        Matcher matcher = getMatcher(command, commandPatterns[5].pattern());
        matcher.find();
        String host = matcher.group("host");
        ChatController.getInstance().focusOnHost(host);
    }


    private void sendMessageByAddressFocusedOnHost(String message, String port) throws CouldNotSendMessageException,
            NotFocusedOnHostException {
        try {
            int portNumber = Integer.parseInt(port);
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


    private void sendMessageFullFocused(String message) throws NotFullFoucsedException, CouldNotSendMessageException,
            ContactNotFoundException {
        ChatController.getInstance().sendMessageFullFocused(message);
    }


    private void stopFocus() {
        ChatController.getInstance().stopFocus();
    }


    private void showContacts() throws NoItemAvailableException {
        System.out.println(ChatController.getInstance().showContacts());
    }

    private void showContactByUsername(String command) throws ContactNotFoundException {
        Matcher matcher = getMatcher(command, commandPatterns[10].pattern());
        matcher.find();
        String username = matcher.group("username");
        System.out.println(ChatController.getInstance().showContactByUsername(username));
    }

    private void showSenders() throws NoItemAvailableException {
        System.out.println(ChatController.getInstance().showSenders());
    }


    private void showMessages() throws NoItemAvailableException {
        System.out.print(ChatController.getInstance().showMessages());
    }


    private void showSendersCount() {
        System.out.println(ChatController.getInstance().sendersCount());
    }

    private void showMessagesCount() {
        System.out.println(ChatController.getInstance().messagesCount());
    }


    private void showMessagesFromUser(String command) throws NoItemAvailableException {
        Matcher matcher = getMatcher(command, commandPatterns[10].pattern());
        matcher.find();
        String username = matcher.group("username");
        System.out.println(ChatController.getInstance().messagesFromUser(username));
    }

    private void showMessagesCountFromUser(String command) {
        Matcher matcher = getMatcher(command, commandPatterns[10].pattern());
        matcher.find();
        String username = matcher.group("username");
        System.out.println(ChatController.getInstance().messagesCountFromUser(username));
    }


    private void logout() throws UserNotLoggedInException {
        if (Menu.getCurrentMenu() instanceof UserConfigMenu)
            throw new UserNotLoggedInException();
        Menu.setLoggedIn(false);
        Menu.setCurrentMenu(new UserConfigMenu());
    }


    private boolean isInCorrectMenu() {
        return Menu.getCurrentMenu() instanceof ChatMenu;
    }


    private void parseAndSendMessage(String command) throws NotFullFoucsedException, NotFocusedOnHostException,
            CouldNotSendMessageException, ContactNotFoundException {
        CommandLineParser parser = new BasicParser();
        Options options = new Options();
        options.addOption("message", true, "first param");
        options.addOption("port", true, "second param");
        options.addOption("host", true, "third param");
        options.addOption("username", true, "forth param");
        String[] parts = command.split(" ");
        int firstIndex = 0;
        int lastIndex = 0;
        for (int i = 0; i < parts.length; i++)
            if (parts[i].startsWith("\"")) {
                firstIndex = i;
                break;
            }
        for (int i = firstIndex + 1; i < parts.length; i++)
            if (parts[i].endsWith("\"")) {
                lastIndex = i;
                break;
            }
        StringBuilder message = new StringBuilder();
        for (int i = firstIndex; i < lastIndex; i++) message.append(parts[i]).append(" ");
        message.append(parts[lastIndex]);
        String[] parts2 = new String[parts.length - (lastIndex - firstIndex)];
        System.arraycopy(parts, 0, parts2, 0, firstIndex);
        parts2[firstIndex] = message.substring(1, message.length() - 1);
        System.arraycopy(parts, 0, parts2, 0, firstIndex);
        if (parts.length - (lastIndex + 1) >= 0)
            System.arraycopy(parts, lastIndex + 1, parts2, lastIndex + 1 - lastIndex + firstIndex,
                    parts.length - (lastIndex + 1));
        for (int i = 0; i < parts2.length; i++) if (parts2[i].startsWith("-")) parts2[i] = parts2[i].substring(1);
        try {
            CommandLine commandLine = parser.parse(options, parts2);
            sendMessage(commandLine.getOptionValue("message"), commandLine.getOptionValue("host"),
                    commandLine.getOptionValue("port"), commandLine.getOptionValue("username"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void parseAndConfigurePort(String command) throws Exception {
        if (command.contains("--close"))
            this.closePort(command);
        else if (command.contains("--rebind"))
            this.rebindPort(command);
        else
            this.setPort(command);
    }

    private void parseAndFocus(String command) throws Exception {
        if (command.contains("--stop"))
            this.stopFocus();
        else if (command.contains("--start") && command.contains("--host") && command.contains("port"))
            this.focusOnHostAndPort(command);
        else if (command.contains("--start") && command.contains("--username"))
            this.focusOnUser(command);
        else if (command.contains("--start"))
            this.focusONHost(command);
        else
            this.focusOnPort(command);


    }


    private void showItem(String command) throws NoItemAvailableException, ContactNotFoundException {
        if (this.commandPatterns[12].matcher(command).find() && isInCorrectMenu())
            this.showContacts();
        else if (this.commandPatterns[13].matcher(command).find() && isInCorrectMenu())
            this.showContactByUsername(command);
        else if (this.commandPatterns[14].matcher(command).find() && isInCorrectMenu() && !command.contains("--count"))
            this.showSenders();
        else if (this.commandPatterns[15].matcher(command).find() && isInCorrectMenu() && !command.contains("--count"))
            this.showMessages();
        else if (this.commandPatterns[16].matcher(command).find() && isInCorrectMenu())
            this.showSendersCount();
        else if (this.commandPatterns[17].matcher(command).find() && isInCorrectMenu())
            this.showMessagesCount();
        else if (this.commandPatterns[18].matcher(command).find() && isInCorrectMenu())
            this.showMessagesFromUser(command);
        else if (this.commandPatterns[19].matcher(command).find() && isInCorrectMenu())
            this.showMessagesCountFromUser(command);
    }


    private void sendMessage(String message, String host, String port, String username) throws NotFullFoucsedException,
            CouldNotSendMessageException, NotFocusedOnHostException, ContactNotFoundException {
        if (username != null)
            this.sendMessageByUsername(username, message);
        else if (host == null && port == null && username == null)
            this.sendMessageFullFocused(message);
        else if (host == null)
            this.sendMessageByAddressFocusedOnHost(message, port);
        else
            sendMessageByAddress(message, host, port);
    }



    private void saveContact(String command) throws Exception {
        CommandLineParser parser = new BasicParser();
        Options options = new Options();
        options.addOption("link", false, "first param");
        options.addOption("username", true, "second param");
        options.addOption("host", true, "third param");
        options.addOption("port", true, "forth param");
        String[] parts = command.split(" ");
        for (int i = 0; i < parts.length; i++) if (parts[i].startsWith("-")) parts[i] = parts[i].substring(1);
        try {
            CommandLine commandLine = parser.parse(options, parts);
            ChatController.getInstance().saveContact(commandLine.getOptionValue("username"), commandLine.getOptionValue("host"),
                    commandLine.getOptionValue("port"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }





    public void run(Scanner input) {
        String command = input.nextLine();
        try {
            if (!UserConfigController.isLoggedIn()) throw new UserHasNoAccessException();
            else if (command.startsWith("portconfig") && isInCorrectMenu())
                this.parseAndConfigurePort(command);
            else if (command.startsWith("send") && isInCorrectMenu())
                this.parseAndSendMessage(command);
            else if (command.startsWith("focus") && isInCorrectMenu())
                this.parseAndFocus(command);
            else if (command.startsWith("show") && isInCorrectMenu())
                this.showItem(command);
            else if (this.commandPatterns[20].matcher(command).find() && isInCorrectMenu())
                this.logout();
            else if (command.startsWith("contactconfig") && isInCorrectMenu())
                this.saveContact(command);
            else
                throw new InvalidCommandException();
            if (!command.startsWith("show"))
                System.out.println("success");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public static void main(String[] args) {
        int counter = 0;
        for (Pattern commandPattern : new ChatMenu().commandPatterns) {
            System.out.println(commandPattern);
            counter++;
        }
        System.out.println(counter);
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public boolean matchesAPattern(String command) {
        for (Pattern commandPattern : this.commandPatterns)
            if (commandPattern.matcher(command).matches())
                return true;
        return false;
    }


    //TODO saving user contact info

}
