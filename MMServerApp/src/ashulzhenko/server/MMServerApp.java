package ashulzhenko.server;

import java.net.InetAddress;

/**
 * Application class that starts the server.
 * @author Alena Shulzhenko
 * @version 25/10/2016
 * @since 1.8
 */
public class MMServerApp {

    /**
     * Main class to start the server.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //port 50,000 for MasterMind
            MMServer server = new MMServer(50000);
            InetAddress serverIp = InetAddress.getLocalHost();
            System.out.println("Server IP: "+serverIp.getHostAddress());
            server.run();
        } 
        catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }
    
}
