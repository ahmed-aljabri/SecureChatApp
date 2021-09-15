package src;
import java.net.*;
import java.io.*;
import javax.net.ssl.*;
import java.util.zip.*;
/**
 * DESCRIPTION:
 * This class is run by each client to initiate a connection to the running server.
 * 
 * USAGE:
 * Syntaxt from the command Line: java ChatClient <hostname> <port number>
 * To leave the chat type: bye
 *
 * @author ahmed-aljabri
 */
public class ChatClient {

    private String hostname;
    private String userName;
    private int port;

    public static final boolean DEBUG = false;
    public static final String TRUSTSTORE_LOCATION = "C:/CA2/ClientKeyStore.jks";
    public static final String TRUSTSTORE_PASSWORD = "a1s2d3f4g5h6";

    /**
     * ChatClient Constructor
     * 
     * @param hostname server's hostname
     * @param port     server's port
     */
    public ChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public static void main(String[] args) {

        System.setProperty("javax.net.ssl.trustStore", TRUSTSTORE_LOCATION);
        System.setProperty("javax.net.ssl.trustStorePassword", TRUSTSTORE_PASSWORD);

        if (DEBUG)
            System.setProperty("javax.net.debug", "all");

        if (args.length < 2)
            return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        ChatClient client = new ChatClient(hostname, port);
        client.execute();
    }

 
    public void execute() {
        
        SSLSocketFactory f = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try {


            SSLSocket c = (SSLSocket) f.createSocket(hostname, port);

            c.startHandshake();
 
            System.out.println("Connected to the chat server");
 
            new ReadThread(c, this).start();
            new WriteThread(c, this).start();
 
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
 
    }
    /**
     * Setter method to set the clients username
     */
    void setUserName(String userName) {
        this.userName = userName;
    }
    /**
     * Getter method for the users name.
     * @return username
     */
    String getUserName() {
        return this.userName;
    }

    public static long getCRC32Checksum(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }


}
