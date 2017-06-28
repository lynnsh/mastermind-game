package com.brianprive.business;

import java.net.*;  // for Socket
import java.io.*;   // for IOException and Input/OutputStream

/**
 * This class is responsible for maintaining the session with the server.
 * It communicates with the client by receiving and 
 * sending the appropriate messages.
 * 
 * @author Brian Prive
 * @author Salman Haidar
 * @version 25/10/2016
 * @since 1.8
 */
public class MMClientSession
{
    private Socket socket;
    private InputStream in;
    private MMPacket packet;
    private final static int SERV_PORT = 50000;
    
    private boolean lostGame;
    private boolean gameWon;
    private boolean isOk;
    private int[] receivedMessage;
    
    /**
     * Instantiates the object receiving the IP number of the server.
     * 
     * @param server the IP number of the server.
     * @throws IOException If there is a problem when communicating 
     *                     with the server.
     */
    public MMClientSession(String server) throws IOException
    {
        socket = new Socket();
        //creates the socket with the specified timeout
        socket.connect(new InetSocketAddress(server, SERV_PORT), 1000);
        packet = new MMPacket(socket);
        lostGame = false;
        gameWon = false;
    }
    
    /**
     * Starts new session with the server.
     * 
     * @param test
     * @throws IOException If there is a problem when communicating 
     *                     with the server.
     */
    public void startNewSession(String test) throws IOException
    {
        lostGame = false;
        gameWon = false;
        isOk = true;
        
        //Send start new game
        if(test.isEmpty()) 
            packet.sendMessage(new int[]{0,0,0,0});
        else
            packet.sendMessage(getIntArray(test));
        
        //Get OK
        receivedMessage = packet.receiveMessage();
        
        //Check for okay
        if (receivedMessage[0] != 10)
            isOk = false;
    }
    
    /**
     * Sends user input for one turn to the server and receives
     * the server's reply.
     * @param userIntput user's choice for the current turn.
     * @return server's reply message.
     * @throws IOException If there is a problem when communicating 
     *                     with the server.
     */
    public int[] playTurn(int[] userIntput) throws IOException
    {
        //check if user wants to quit the game
        for (int num : userIntput)
            if (num == 9)
                lostGame = true;

        packet.sendMessage(userIntput);
        
        //receive message if user does not quit the game
        if (!lostGame)
            receivedMessage = getServerMessage();
            
        return receivedMessage;
    }

    /**
     * Sends the server the quit message from the user.
     * 
     * @throws IOException If there is a problem when communicating 
     *                     with the server.
     */
    public void quitGame() throws IOException
    {
        packet.sendMessage(new int[]{14,14,14,14});
    }
    
    /**
     * Quits current game with the server.
     * 
     * @throws IOException If there is a problem when communicating 
     *                     with the server.
     */
    public void quitCurrentGame() throws IOException
    {
        packet.sendMessage(new int[]{9,9,9,9});  
        receivedMessage = packet.receiveMessage();
    }
    
    /**
     * Returns true if game is lost; false otherwise.
     * 
     * @return true if game is lost; false otherwise.
     */
    public boolean isLostGame() {
        return lostGame;
    }

    /**
     * Returns true if game is won; false otherwise.
     * 
     * @return true if game is won; false otherwise.
     */
    public boolean isGameWon() {
        return gameWon;
    }

    /**
     * Returns true if the server accepted the connection; false otherwise.
     * 
     * @return true if the server accepted the connection; false otherwise.
     */
    public boolean isOk() {
        return isOk;
    }

    /**
     * Receives server's message with hints. If the game is ended, 
     * it also receives the correct answer set.
     * 
     * @return server's message.
     * @throws IOException 
     */
    private int[] getServerMessage() throws IOException {
        receivedMessage = packet.receiveMessage();
        
        for (int num : receivedMessage) {
            //the game is lost
            if (num == 15)
                lostGame = true;
            //the game is won
            else if (num == 12)
                gameWon = true;
            else {
                lostGame = false;
                gameWon = false;
            }
        }
        
        //receive the answer set
        if(receivedMessage[0] == 15)
            receivedMessage = packet.receiveMessage();

        return receivedMessage;
    }
    
    /**
     * Receives a String and return the numeric representation
     * 
     * @param str
     * @return 
     */
    private int[] getIntArray(String str)
    {
        int[] array = new int[str.length()];
        
        for(int i = 0; i < str.length(); i++)
            array[i] = Character.getNumericValue(str.charAt(i));
        
        return array;
    }
}