package ashulzhenko.server;

import java.net.Socket;
import java.net.SocketException;
import java.io.*;
import java.util.Arrays;
import java.util.Random;

/*
packets messaging:
0s - client : new game
1-8 - client : colors
9 - client : stop current game
10 - return ok
12 - in place clue
13 - out place clue
14 - client : do start new game
ffff - user lost the game
*/

/**
 * represents >=1 mastermind games
 * @author aline
 */
public class MMSession {
    private Socket socket;
    private byte[] answerSet = new byte[4];
    private Random random = new Random();
    private int round;
    private boolean playAgain = true;

    public MMSession(Socket socket) {
        this.socket = socket;
    }

    public void startNewSession() throws IOException {       
        //games
        while(playAgain && !socket.isClosed()) {
            boolean gameOver = true;
            startNewGame(); 
            System.out.println(Arrays.toString(answerSet));
            
            //game
            while(round < 11 && !socket.isClosed() && gameOver) {
                //get client message
                byte[] message = receiveMessage();
                if(message[0]!= 9){
                    byte[] clues = generateClues(message);
                    sendMessage(clues);
                    round++;
                }
                else
                    gameOver = false;
                System.out.println("message: "+Arrays.toString(message) +" " + round);
            }
            //playAgain = true;
            //playAgain = receiveMessage()[0] == 14;
        }
    }

    private void startNewGame() throws IOException {
        round = 1;
        //get client message
        //check contents?
        byte[] message = receiveMessage();
        if(message[0] != 9) {
            //reply
            byte[] answer = new byte[4];
            answer[0] = 10;
            sendMessage(answer);
            createAnswerSet(message);
        }
        else playAgain = false;
    }
    
    private byte[] receiveMessage() throws IOException {
        byte[] byteBuffer = new byte[4];
        InputStream in = socket.getInputStream();
        
        int totalBytesRcvd = 0;	// Total bytes received so far
        int bytesRcvd;		// Bytes received in last read
        while (totalBytesRcvd < byteBuffer.length)
        {
          if ((bytesRcvd = in.read(byteBuffer, totalBytesRcvd,
                            byteBuffer.length - totalBytesRcvd)) == -1)
              //where to catch it?
            throw new SocketException("Connection closed prematurely");
          totalBytesRcvd += bytesRcvd;
        }
        
        return byteBuffer;
    }
    
    

    private void sendMessage(byte[] message) throws IOException {
        OutputStream out = socket.getOutputStream();
        out.write(message, 0, message.length);
    }
    
    
    private byte[] generateClues(byte[] message) {
        byte[] clues = new byte[4];       
        byte[] answer = Arrays.copyOf(answerSet, answerSet.length);
        
        int clue = findInPlaceClues(message, clues, answer);       
        
        if(clue != 4) {
            if(round == 10)
                Arrays.fill(clues, (byte)0xf);
            else
                findOutPlaceClues(message, clues, clue, answer);               
        }
        
        return clues;
    }
    
    private int findInPlaceClues(byte[] message, byte[] clues, byte[] answer) {
        int clue = 0;
        
        for(int i = 0; i < message.length; i++) {
            if(message[i] == answer[i]) {
                clues[clue] = 12;
                clue++;
                message[i] = 0;
                answer[i] = 0;
            }
        }
        
        return clue;
    }
    
    private void findOutPlaceClues(byte[] message, byte[] clues, int clue, byte[] answer) {
        for (int i = 0; i < message.length; i++) {
            if(message[i] != 0) {
                int index = searchArray(answer, message[i]);
                if(index != -1) {
                    clues[clue] = 13;
                    clue++;
                    answer[index] = 0;
                }
            }
        }
    }

    private void createAnswerSet(byte[] message) {
        if(message[0] != 0) {
            answerSet = Arrays.copyOf(message, message.length);
        }
        else {
            for(int i = 0; i < answerSet.length; i++)
                answerSet[i] = (byte)(random.nextInt(8)+1);
        }
    }
    
    
    
    private int searchArray(byte[] array, int key) {
        for(int i = 0; i < array.length; i++) {
            if(array[i] == key )
                return i;
        }
        return -1;
    }
    
}
