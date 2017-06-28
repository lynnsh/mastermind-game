package ashulzhenko.server;
import java.io.IOException;
import java.net.*;

/**
 * Server class that starts the server loop to cycle through clients.
 * @author Alena Shulzhenko
 * @version 06/12/2016
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
     * @throws IOException if there is an error creating server socket.
     */
    public void run() throws IOException {
        ServerSocket servSocket = new ServerSocket(port);
        for(;;) {
            try {
                Socket clntSocket = servSocket.accept();
                System.out.println("Client connected at: " + 
                        clntSocket.getInetAddress().getHostAddress());
                //create new session with the detected client
                MMSession session = new MMSession(clntSocket);
                //start a new thread for this client
                Thread thread = new Thread(session);
                thread.start();
                System.out.println("Created and started Thread = " 
                        + thread.getName());
                
            }
            catch(IOException ex) {
                System.err.println("There is an error when communicating "
                             + "with the client: " + ex.getMessage());
            }
        }             
    }   
}
