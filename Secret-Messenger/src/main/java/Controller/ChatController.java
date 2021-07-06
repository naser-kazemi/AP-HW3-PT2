package Controller;

import Model.Connection;
import Model.Exceptions.*;
import Model.Message;
import Model.User;
import View.ChatMenu;
import View.Menu;

import java.util.ArrayList;
import java.util.Comparator;

public class ChatController {

    private final ArrayList<Integer> openPorts = new ArrayList<>();
    private boolean isPortSet = false;
    private boolean isFocusOnHostMode = false;

    public void setPortSet(boolean portSet) {
        isPortSet = portSet;
    }

    private boolean isFocusOnPortMode = false;
    private String focusedHost = null;
    private int focusedPort = -1;
    private boolean isFocusOnUserMode = false;
    private User focusedUser = null;
    private int currentPort = -1;


    private static ChatController instance;

    private ChatController() {
    }


    public static ChatController getInstance() {
        if (instance == null) {
            instance = new ChatController();
        }

        return instance;
    }


    //TODO setPort
    public void setPort(int portNumber) throws Exception {
        if (isPortSet)
            throw new PortIsAlreadySetException();
        ServerController.getInstance().setPort(portNumber);
        openPorts.add(portNumber);
        isPortSet = true;
        currentPort = portNumber;
    }


    //TODO rebindPort
    public void rebindPort(int portNumber) throws PortIsAlreadySetException {
        if (openPorts.contains(portNumber))
            throw new PortIsAlreadySetException();
        ServerController.getInstance().rebindPort(portNumber);
        currentPort = portNumber;
    }


    //TODO closePort
    public void closePort(int portNumber) throws PortIsNotOpenException {
        ServerController.getInstance().closePort(portNumber);
    }


    //TODO
    public void sendMessageByAddress(String messageText, String host, int portNumber)
            throws CouldNotSendMessageException {
        Message message = new Message(Menu.getCurrentUser(), messageText);
        Menu.getCurrentUser().addSentMessage(message);
        ClientController.getInstance().sendMessageByAddress(host, portNumber, message.getText());
    }


    //TODO
    public void sendMessageByUsername(String username, String messageText)
            throws CouldNotSendMessageException, ContactNotFoundException {
        User user = Menu.getCurrentUser().getContactByUsername(username);
        if (user == null)
            throw new ContactNotFoundException();
        if (Connection.getConnectionByHostAndPort(user.getHost(), user.getPortNumber()) == null)
            ClientController.getInstance().setupConnection(user.getHost(), user.getPortNumber());
        Message message = new Message(Menu.getCurrentUser(), messageText);
        Menu.getCurrentUser().addSentMessage(message);
        ClientController.getInstance().sendMessageByUser(user, message.getText());
    }

    //TODO
    public void sendMessageByAddressFocusedOnHost(String messageText, int portNumber)
            throws CouldNotSendMessageException, NotFocusedOnHostException {
        if (!isFocusOnHostMode)
            throw new NotFocusedOnHostException();
        Message message = new Message(Menu.getCurrentUser(), messageText);
        Menu.getCurrentUser().addSentMessage(message);
        ClientController.getInstance().sendMessageByAddress(focusedHost, portNumber, message.getText());
    }

    public void focusOnHost(String host) {
        isFocusOnHostMode = true;
        focusedHost = host;
    }


    public void focusOnPort(int portNumber) throws NotFocusedOnHostException {
        if (!isFocusOnHostMode)
            throw new NotFocusedOnHostException();
        isFocusOnPortMode = true;
        focusedPort = portNumber;
    }

    public void focusOnHostAndPort(String host, int portNumber) {
        isFocusOnHostMode = true;
        focusedHost = host;
        isFocusOnPortMode = true;
        focusedPort = portNumber;
    }

    public void focusOnUser(String username) throws ContactNotFoundException {
        if (Menu.getCurrentUser().getContactByUsername(username) == null)
            throw new ContactNotFoundException();
        isFocusOnUserMode = true;
        focusedUser = Menu.getCurrentUser().getContactByUsername(username);
    }

    public void sendMessageFullFocused(String messageText) throws NotFullFoucsedException,
            CouldNotSendMessageException, ContactNotFoundException {
        if (!(isFocusOnUserMode || isFocusOnPortMode))
            throw new NotFullFoucsedException();
        Message message = new Message(Menu.getCurrentUser(), messageText);
        Menu.getCurrentUser().addSentMessage(message);
        if (isFocusOnUserMode)
            ClientController.getInstance().sendMessageByUserFocused(focusedUser, message.getText());
        else
            ClientController.getInstance().sendMessageByAddressFocused(focusedHost, focusedPort, message.getText());
    }


    public void stopFocus() {
        isFocusOnHostMode = false;
        focusedHost = null;
        isFocusOnPortMode = false;
        focusedPort = -1;
        isFocusOnUserMode = false;
        focusedUser = null;
        ClientController.getInstance().setHasSetFocusedUser(false);
        ClientController.getInstance().setHasSetFocusedAddress(false);
    }

    public String showContacts() throws NoItemAvailableException {
        if (Menu.getCurrentUser().getContacts().isEmpty())
            throw new NoItemAvailableException();
        StringBuilder result = new StringBuilder();
        for (User contactedUser : Menu.getCurrentUser().getContacts())
            result.append(contactedUser.getUsername()).append(" -> ").append(contactedUser.getHost())
                    .append(":").append(contactedUser.getPortNumber()).append(System.lineSeparator());
        return result.toString();
    }

    public String showContactByUsername(String username) throws ContactNotFoundException {
        User contact = Menu.getCurrentUser().getContactByUsername(username);
        if (contact == null)
            throw new ContactNotFoundException();
        return contact.getHost() + ":" + contact.getPortNumber();
    }

    public String showSenders() throws NoItemAvailableException {
        if (Menu.getCurrentUser().getSenders().isEmpty())
            throw new NoItemAvailableException();
        StringBuilder result = new StringBuilder();
        for (User sender : Menu.getCurrentUser().getSenders())
            result.append(sender.getUsername()).append(System.lineSeparator());
        return result.toString();
    }

    public String showMessages() throws NoItemAvailableException {
        if (Menu.getCurrentUser().getReceivedMessages().isEmpty())
            throw new NoItemAvailableException();
        Menu.getCurrentUser().sortReceivedMessages();
        StringBuilder result = new StringBuilder();
        for (Message receivedMessage : Menu.getCurrentUser().getReceivedMessages())
            result.append(receivedMessage.getSender().getUsername()).append(" -> ").append(receivedMessage.getText())
                    .append(System.lineSeparator());
        return result.toString();
    }

    public int sendersCount() {
        return Menu.getCurrentUser().getSenders().size();
    }

    public int messagesCount() {
        return Menu.getCurrentUser().getMessageCount();
    }


    public String messagesFromUser(String username) throws NoItemAvailableException {
        User sender = Menu.getCurrentUser().getSenderByUsername(username);
        if (sender == null)
            throw new NoItemAvailableException();
        ArrayList<Message> messages = new ArrayList<>();
        for (Message receivedMessage : Menu.getCurrentUser().getReceivedMessages())
            if (receivedMessage.getSender().getUsername().equals(sender.getUsername()))
                messages.add(receivedMessage);
        messages.sort(new Comparator<Message>() {

            @Override
            public int compare(Message o1, Message o2) {
                return o1.getReceivingTime().compareTo(o2.getReceivingTime());
            }
        });
        StringBuilder result = new StringBuilder();
        for (Message message : messages)
            result.append(message.getText())
                    .append(System.lineSeparator());
        return result.toString();
    }

    public int messagesCountFromUser(String username) {
        User sender = Menu.getCurrentUser().getSenderByUsername(username);
        if (sender == null)
            return 0;
        ArrayList<Message> messages = new ArrayList<>();
        for (Message receivedMessage : Menu.getCurrentUser().getReceivedMessages())
            if (receivedMessage.getSender().getUsername().equals(sender.getUsername()))
                messages.add(receivedMessage);
        return messages.size();
    }

    public void receiveMessage(String message) {
        new ChatMenu().showMessage(message);
    }

    public void saveContact(String username, String host, String port) throws Exception {
        try {
            int portNumber = Integer.parseInt(port);
            if (Menu.getCurrentUser().getContactByUsername(username) != null)
                Menu.getCurrentUser().getContacts().remove(Menu.getCurrentUser().getContactByUsername(username));
            User user = new User(username, "");
            user.setHost(host);
            user.setPortNumber(portNumber);
            Menu.getCurrentUser().getContacts().add(user);
        } catch (NumberFormatException e) {
            throw new Exception("Invalid port number!");
        }



    }

}
