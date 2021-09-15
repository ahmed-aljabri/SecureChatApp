package src;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocket;
import java.util.zip.CRC32;
 
/**
 * DESCRIPTION:
 * This class is initiated by the ChatServer class once a client connection is made.
 * A dedicated thread for that particular client will be created to allow the server
 * to interract with multiple clients.
 * 
 *
 * @author ahmed-aljabri
 */
public class ChatClientHandler extends Thread {
    
    private ChatServer server;
    private PrintWriter writer;
    private BufferedReader reader;
    private SSLSocket sslSocket;

    
    
    
    /**
     * ChatClientHandler Constructor
     * @param sslSocket client's socket connection.
     * @param server    Server object that the client is connected to.
     */
    public ChatClientHandler(SSLSocket sslSocket, ChatServer server) {
        this.sslSocket = sslSocket;
        this.server = server;
    }
 
    public void run() {
        try {

            reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
 
            writer = new PrintWriter(sslSocket.getOutputStream(), true);
            printUsers();
            
            String userName = reader.readLine();

            while (server.getUserNames().contains(userName)){

                writer.println("[SERVER]: This name is already in use, choose another name");

                userName = reader.readLine();
            }

            server.addUserName(userName);
            sendMessage("[SERVER]: VALID USERNAME");
            sendMessage(userName);
            
 
            String serverMessage = "[SERVER]: New user connected: " + userName;
            server.broadcast(serverMessage, this);
 
            String clientMessage;
            String messageChecksum; 
            do {
                //Server recieves the client's message and Checksum.
                clientMessage = reader.readLine();
                String[] split = clientMessage.split(" // ");
                
                //Server checks the recieved checksum value
                byte[] bytes = split[0].getBytes();
                Long recomChecksum = getCRC32Checksum(bytes);
                String serverValidation = "[Recomputed Checksum]: " + recomChecksum;
                //server forwards the message and validates
                serverMessage = "[" + userName + "]: " + clientMessage + serverValidation;
                server.broadcast(serverMessage, this);
                

            } while (!clientMessage.contains("bye"));
 
            server.removeUser(userName, this);
            sslSocket.close();
 
            serverMessage = "[SERVER]: " + userName + " has quitted.";
            server.broadcast(serverMessage, this);

            if(server.hasUsers()){

                System.out.println("Live Threads : " + server.getThreadsList());
            }else {
                System.out.println("[SERVER]: No active clients");
            }
 
        } catch (IOException ex) {
            System.out.println("Error in ChatClientHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
 
    /**
     * This method sends a list of online users to the newly connected user.
     */
    void printUsers() {
        if (server.hasUsers()) {
            writer.println("[SERVER]: Connected users: " + server.getUserNames());
        } else {
            writer.println("[SERVER]: No other users connected");
        }
    }
 
    /**
     * This method sends a message to the client.
     */
    void sendMessage(String message) {
        writer.println(message);
    }

    public static long getCRC32Checksum(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }
}
