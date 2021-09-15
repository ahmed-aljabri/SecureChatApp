package src;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.net.ssl.*;

/**
 * This class is responsible for reading in user's input and sending it back to the server
 * This thread will run in a loop untill the client's connection terminates.
 *
 * 
 */
public class WriteThread extends Thread {
    private PrintWriter writer;
    private SSLSocket socket;
    private ChatClient client;
    String userName;
 /**
  * WriteThread Constructor
  * @param socket
  * @param client
  */
    public WriteThread(SSLSocket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;
 
        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
 
    public void run() {
        
        Console console = System.console();
  

       while(client.getUserName() == null){ 
        userName = console.readLine("\nEnter your name: ");
        writer.println(userName);
       }

        String text = "";

        do {
            text = console.readLine("[" + client.getUserName() + "]: ");
            byte[] dataBytes = text.getBytes();
            long checkSum = getCRC32Checksum(dataBytes);
            text = text + " // " + checkSum;
            writer.println(text);
            
        } while (!text.contains("bye"));
 
        try {
            socket.close();
        } catch (IOException ex) {
 
            System.out.println("Error writing to server: " + ex.getMessage());
        }
    }

    public void setUsername(String name){
        this.userName = name;
    }

    public static long getCRC32Checksum(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }
}
