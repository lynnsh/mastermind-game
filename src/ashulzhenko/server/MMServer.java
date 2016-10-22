package ashulzhenko.server;

import java.io.IOException;
import java.net.*;

/**
 * Server class that starts the server loop to cycle through clients.
 * @author Alena Shulzhenko
 * @version 08/09/2016
 * @since 1.8
 */
public class MMServer {
    private int port;
    
    /**
     * Instantiates the object receiving the port number.
     * @param port the port on which the server will listen.
     */
    public MMServer(int port) {
        this.port = port;
    }

    /**
     * Starts the server loop to cycle through clients.
     */
    public void run() {
        try(ServerSocket servSocket = new ServerSocket(port);) {
            for(;;) {
                Socket clntSocket = servSocket.accept();
                //start new session with the detected client
                MMSession session = new MMSession(clntSocket);
                session.startNewSession();
                clntSocket.close();
            }   
            
        }
        catch (IOException ex) {
                System.out.println("There is an error when communicating "
                                 + "with the client.");
            }
    }
    
}
