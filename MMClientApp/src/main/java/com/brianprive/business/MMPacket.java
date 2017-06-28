package com.brianprive.business;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * A utility class to send and receive messages.
 * @author Alena Shulzhenko
 * @version 08/09/2016
 * @since 1.8
 */
public class MMPacket
{
    private Socket socket;
    
    /**
     * Instantiates the object when receiving the socket.
     * @param socket the Socket object.
     */
    public MMPacket(Socket socket) {
        this.socket = socket;
    }
    
    /**
     * Receives the message through the socket.
     * @return the message that is received
     * @throws IOException If there is a communication problem.
     */
    public int[] receiveMessage() throws IOException {
        byte[] byteBuffer = new byte[4];
        InputStream in = this.socket.getInputStream();
        
        int totalBytesRcvd = 0;	// Total bytes received so far
        int bytesRcvd;		// Bytes received in last read
        while (totalBytesRcvd < byteBuffer.length)
        {
          if ((bytesRcvd = in.read(byteBuffer, totalBytesRcvd,
                            byteBuffer.length - totalBytesRcvd)) == -1)
            throw new SocketException("Connection closed prematurely");
          totalBytesRcvd += bytesRcvd;
        }
        
        int[] array = new int[byteBuffer.length];
        //convert byte[] to int[]
        for(int i = 0; i < array.length; i++)
            array[i] = byteBuffer[i];
        
        return array;
    }
    
    /**
     * Sends the message through the socket..
     * @param message the message to send.
     * @throws IOException If there is a communication problem.
     */
    public void sendMessage(int[] message) throws IOException {
        byte[] byteBuffer = new byte[message.length];
        //convert int[] to byte[]
        for(int i = 0; i < message.length; i++)
            byteBuffer[i] = (byte)message[i];
        
        OutputStream out = this.socket.getOutputStream();
        out.write(byteBuffer, 0, byteBuffer.length);
    }
}