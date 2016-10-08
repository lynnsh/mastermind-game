package ashulzhenko.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * App class that starts the server.
 * @author Alena Shulzhenko
 * @version 08/09/2016
 * @since 1.8
 */
public class MMServerApp {

    /**
     * Main class to start the server.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //port 50,000 for MaterMind
            MMServer server = new MMServer(50000);
            InetAddress serverIp = InetAddress.getLocalHost();
            System.out.println(serverIp.getHostAddress());
            server.run();
        } 
        catch (UnknownHostException ex) {
            System.out.println("Error getting server IP#.");
        }
    }
    
}
