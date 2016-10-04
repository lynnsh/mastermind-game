package ashulzhenko.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author aline
 */
public class MMServerApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            MMServer server = new MMServer(50000);
            InetAddress address = InetAddress.getLocalHost(); //UnknownHostException
            System.out.println(address.getHostAddress());
            server.run();
        } 
        catch (UnknownHostException ex) {
            System.out.println("Error getting host IP#.");
        }
    }
    
}
