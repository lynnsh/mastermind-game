package ashulzhenko.server;

import java.io.IOException;
import java.net.*;

/**
 *
 * @author aline
 */
public class MMServer {
    private int port;
    
    public MMServer(int port) {
        this.port = port;
    }

    public void run() {
        for(;;) {
            try {
                ServerSocket servSocket = new ServerSocket(port);
                try (Socket clntSocket = servSocket.accept()) {
                    MMSession session = new MMSession(clntSocket);
                    session.startNewSession();
                    //session = null;
                }
            }   
            catch (IOException ex) {
                System.out.println("There is an error when communicating with the client.");
                ex.printStackTrace();
            }
        }
    }
    
}
