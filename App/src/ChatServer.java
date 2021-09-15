package src;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
 
/**
 * DESCRIPTION:
 * This class is responsible for listening to client connections on a given port.
 * As soon as a client connects the connection will be passed to the ChatClientHandler class.
 * 
 * USAGE:
 * Syntaxt from the command Line: java ChatServer <port number>
 * To terminate the server: Ctrl + C
 *
 * @author ahmed-aljabri 
 */
public class ChatServer {
    
    private Set<String> userNames = new HashSet<>();
    private Set<ChatClientHandler> userThreads = new HashSet<>();

    public static final boolean DEBUG = false;
    public static int DEFAULT_PORT = 8282;
    public static int SERVER_PORT;
    public static final String KEYSTORE_LOCATION = "C:/Keys2/ServerKeyStore.jks"; //Replace with your own server key path
    public static final String KEYSTORE_PASSWORD = "a1s2d3f4g5h6"; //Replace with your set password

    public ChatServer(int portNumber) {
        this.SERVER_PORT= portNumber;
    }

    public static void main(String[] args) {

        System.setProperty("javax.net.ssl.keyStore", KEYSTORE_LOCATION);
        System.setProperty("javax.net.ssl.keyStorePassword", KEYSTORE_PASSWORD);

        if (DEBUG)
            System.setProperty("javax.net.debug", "all");

        if (args.length < 1) {
            System.out.println("[SERVER]: Syntax: java ChatServer <port-number>");
            System.exit(0);
        }

        SERVER_PORT = Integer.parseInt(args[0]);

        ChatServer server = new ChatServer(SERVER_PORT);
        server.execute();
    }
 
    public void execute() {
        try {
            
            ServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(SERVER_PORT);

            serverSocket.setEnabledProtocols(new String[]{"SSLv3", "TLSv1"});
            
            System.out.println("[SERVER]:Chat Server is listening on port " + SERVER_PORT);
            
 
            while (true) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                System.out.println("[SERVER]: New user connected");
 
                ChatClientHandler newUser = new ChatClientHandler(clientSocket, this);
                newUser.start();
                userThreads.add(newUser);

                System.out.println("Live Threads: " + getThreadsList());
                
 
            }
 
        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
 
  
 
    /**
     * This method delivers a message to all other users connected. 
     */
    void broadcast(String message, ChatClientHandler excludeUser) {
        for (ChatClientHandler aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            
            }
        }
    }
 
    /**
     * This method keeps a record of connected clients.
     */
    void addUserName(String userName) {
        userNames.add(userName);
    }
 
    /**
     * This method removes a user once their connection is terminated.
     */
    void removeUser(String userName, ChatClientHandler aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("[SERVER]: The user " + userName + " quitted");
        }
    }
    
    /**
     * Getter method for the list of clients.
     * @return
     */
    Set<String> getUserNames() {
        return this.userNames;
    }

    public Set<ChatClientHandler> getThreadsList(){

        return userThreads;
    }
 
    /**
     * This methhod checks if there are other users connected (Does not count the currently connected user)
     * @return "true" for yes, "false" for no.
     */
    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }

}