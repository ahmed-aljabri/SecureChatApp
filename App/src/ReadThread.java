package src;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.net.ssl.SSLSocket;

/**
 * This thread is responsible for reading server's input and printing it to the
 * console. This thread will run in a loop untill the client's connection
 * terminates.
 *
 * 
 */
public class ReadThread extends Thread {
    private BufferedReader reader;
    private SSLSocket socket;
    private ChatClient client;
    private String userName;
    private byte[] generatedHash;

    /**
     * ReadThread Constructor
     * 
     * @param socket
     * @param client
     */
    public ReadThread(SSLSocket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        
        String response;
      
        while (true) {
            try {
                response = reader.readLine();

                if(response.equals("[SERVER]: VALID USERNAME")){
                    System.out.println("\n" + response);
                    userName = reader.readLine();
                    client.setUserName(userName);
                }else{
                   
                    System.out.println("\n" + response);
                    
                }
     
                
                if (client.getUserName() != null) {
                    System.out.print("[" + client.getUserName() + "] : ");
                }
            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }

    public static long getCRC32Checksum(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }

    
}