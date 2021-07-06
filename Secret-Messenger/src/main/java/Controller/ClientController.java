package Controller;

import Model.Connection;
import Model.Exceptions.ContactNotFoundException;
import Model.Exceptions.CouldNotSendMessageException;
import Model.User;
import View.Menu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class ClientController {

    private static ClientController instance;


    private Socket socket = new Socket();
    private DataOutputStream dataOutputStream;
    private boolean hasSetFocusedUser = false;
    private boolean hasSetFocusedAddress = false;


    public static ClientController getInstance() {
        if (instance == null)
            instance = new ClientController();
        return instance;
    }


    public void setupConnection(String host, int portNumber) throws CouldNotSendMessageException {
        try {
            socket.close();
            socket = new Socket(host, portNumber);
            System.out.println(socket);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            throw new CouldNotSendMessageException();
        }
    }


    public void sendMessageByAddress(String host, int portNumber, String messageText) throws CouldNotSendMessageException {
        setupConnection(host, portNumber);
        try {
            dataOutputStream.writeUTF(Menu.getCurrentUser().getUsername() + "<@@>" + Menu.getCurrentUser().getHost() +
                    "<@@>" + Menu.getCurrentUser().getPortNumber()+ "<@@>" + messageText);
            dataOutputStream.flush();
        } catch (Exception e) {
            throw new CouldNotSendMessageException();
        }

    }



    public void sendMessageByUser(User user, String messageText) throws CouldNotSendMessageException, ContactNotFoundException {
        setupConnection(user.getHost(), user.getPortNumber());
        try {
            dataOutputStream.writeUTF(Menu.getCurrentUser().getUsername() + "<@@>" + Menu.getCurrentUser().getHost() +
                    "<@@>" + Menu.getCurrentUser().getPortNumber() + messageText);
            dataOutputStream.flush();
        } catch (Exception e) {
            throw new ContactNotFoundException();
        }
    }



    public void sendMessageByAddressFocused(String host, int portNumber, String messageText) throws CouldNotSendMessageException {
        if (!hasSetFocusedAddress) {
            setupConnection(host, portNumber);
            hasSetFocusedAddress = true;
        }
        try {
            dataOutputStream.writeUTF(Menu.getCurrentUser().getUsername() + "<@@>" + Menu.getCurrentUser().getHost() +
                    "<@@>" + Menu.getCurrentUser().getPortNumber() + messageText);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new CouldNotSendMessageException();
        }
    }



    public void sendMessageByUserFocused(User user, String messageText) throws CouldNotSendMessageException,
            ContactNotFoundException {
        if (!hasSetFocusedUser) {
            setupConnection(user.getHost(), user.getPortNumber());
            hasSetFocusedUser = true;
        }
        try {
            dataOutputStream.writeUTF(Menu.getCurrentUser().getUsername() + "<@@>" + Menu.getCurrentUser().getHost() +
                    "<@@>" + Menu.getCurrentUser().getPortNumber() + messageText);
            dataOutputStream.flush();
        } catch (Exception e) {
            throw new ContactNotFoundException();
        }
    }

    public void setHasSetFocusedUser(boolean hasSetFocusedUser) {
        this.hasSetFocusedUser = hasSetFocusedUser;
    }

    public void setHasSetFocusedAddress(boolean hasSetFocusedAddress) {
        this.hasSetFocusedAddress = hasSetFocusedAddress;
    }


}
