package ashulzhenko.server;

import java.net.Socket;
import java.io.*;
import java.util.Arrays;
import java.util.Random;


/**
 * Session class that service a client on a separate thread. 
 * A client can play multiple games.
 *
 * The Runnable interface is used to indicate tha this class is intended 
 * to be executed by a thread.
 * 
 * These are the packet messaging rules:
 * 
 * 0 0 0 0 - client requests new game 
 * (if there is a specific answer set at the beginning of a new game,
 * it is sent instead),
 * 1-8 - client color guesses,
 * 9 0 0 0 - client requests to stop the current game,
 * 10 10 10 10 - server sends OK to start a new game,
 * 12 - server indicates an in-place clue (0 if none found),
 * 13 - server indicates an out-place clue (0 if none found),
 * 14 0 0 0 - client requests to stop session (i.e. do not start a new game),
 * 15 15 15 15 - server indicates that the user lost the game.
 * 
 * @author Alena Shulzhenko
 * @version 06/12/2016
 * @since 1.8
 */
public class MMSession implements Runnable {
    private Socket socket;
    private int[] answerSet;
    private Random random;
    private boolean playNewGame;
    private boolean clientWin;
    private MMPacket util;
    private int round;

    /**
     * Instantiates the object when receiving the socket.
     * @param socket the socket that is created 
     *                when client connects to the server.
     */
    public MMSession(Socket socket) {
        this.socket = socket;
        this.util = new MMPacket(socket);
        this.answerSet = new int[4];
        this.random = new Random();
        this.playNewGame = true;
        this.clientWin = false;
        System.out.println("Running...");
    }

    /**
     * Starts session with the client.
     * It is called when the thread with this client is started.
     * If the client requested to end the current game or to finish all games,
     * the server sends the answer set.
     */
    @Override
    public void run() {  
        try {
            boolean quitCurrent = false;
            //loops through multiple games.
            while(playNewGame && !socket.isClosed())
            {
                startNewGame(); 
                //loop for one game
                while(playNewGame && round < 11 && !socket.isClosed() && 
                      !quitCurrent && !clientWin) {
                    //get client message
                    int[] message = util.receiveMessage();
                    //reply to the message
                    quitCurrent = configureSendReply(message);
                }
                if (!clientWin)
                    util.sendMessage(answerSet);
                quitCurrent = false;
                clientWin = false;
                System.out.println("Out of the game loop");
            }
            System.out.println("Out of the session loop");
        }
        catch(IOException io) {
            System.err.println("There is an error when communicating "
                             + "with the client: " + io.getMessage());
        }
        finally {
            try {
                socket.close();
            }
            catch (IOException e) {
		System.err.println("Exception when closing the socket: " 
                        + e.getMessage());
            }
        }
    }

    /**
     * Initiates a new game and creates a new answer set.
     * @throws IOException If there is a problem when communicating 
     *                     to the client.
     */
    private void startNewGame() throws IOException {
        round = 1;
        int[] message = util.receiveMessage();
        if(message[0] != 14) {
            //reply
            int[] answer = new int[4];
            Arrays.fill(answer, 10);
            util.sendMessage(answer);
            createAnswerSet(message);
        }
        else {
            //stop all games
            playNewGame = false;
        }
    }
    
    /**
     * Sends the appropriate reply according to the client's message.
     * 
     * @param message client's message with the guesses.
     * @return quitCurrent - true if clients wants to quit the current game.
     * @throws IOException If there is a problem when communicating 
     *                     to the client.
     */
    private boolean configureSendReply(int[] message) throws IOException {
        //if user wants to quit all games
        if (message[0] == 14) {
            playNewGame = false;
            return true;
        }
        //if user wants to quit the current game
        else if(message[0] == 9) {
            return true;
        }
        else {
            int[] clues = generateClues(message);
            util.sendMessage(clues);
            round++;
            return false;
        }   
    }
    
    /**
     * Generates clues according to client's guesses.
     * @param clientMessage client's message with the guesses.
     * @return generated clues according to client's guesses.
     */
    private int[] generateClues(int[] clientMessage) {
        int[] clues = new int[4];       
        int[] answer = Arrays.copyOf(answerSet, answerSet.length);
        int[] message = Arrays.copyOf(clientMessage, clientMessage.length);
        
        //the position in clues array
        int clue = findInPlaceClues(message, clues, answer); 
        
        if (clue == 4)
            clientWin = true;
        else {
            if(round == 10)
                //game is lost
                Arrays.fill(clues, 15);
            else
                findOutPlaceClues(message, clues, clue, answer);               
        }
        
        return clues;
    }
    
    /**
     * Finds in-place clues according to the client's guesses.
     * @param message client's message with the guesses.
     * @param clues the array to fill if any in-place clues found.
     * @param answer the copy of the answer set array.
     * @return the current position (index) in clues array.
     */
    private int findInPlaceClues(int[] message, int[] clues, int[] answer) {
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
    
    /**
     * Finds out-place clues according to the client's guesses.
     * @param message client's message with the guesses.
     * @param clues the array to fill if any out-place clues found.
     * @param clue the current position (index) in clues array.
     * @param answer the copy of the answer set array.
     */
    private void findOutPlaceClues(int[] message, int[] clues, 
                                   int clue, int[] answer) {
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

    /**
     * Creates the answer set for the game. 
     * If the client sends some values instead of 0's, 
     * they are chosen instead for the answer set. 
     * If client values are invalid, random answer set is created instead.
     * @param message the client's message.
     */
    private void createAnswerSet(int[] message) {
        boolean valid = true;
        if(message[0] != 0) {
            //check that the supplied answer set is valid
            for(int i : message)
                if (i < 1 || i > 8)
                    valid = false;           
            if(valid)
                answerSet = Arrays.copyOf(message, message.length);
            else
                createRandom();
        }
        else
            createRandom();
    }
    
    /**
     * Creates a random answer set.
     */
    private void createRandom() {
        for(int i = 0; i < answerSet.length; i++)
                answerSet[i] = random.nextInt(8)+1;
    }
    
    /**
     * Searches array to find whether the given key is in the array.
     * @param array the array to search.
     * @param key the key to find in the array.
     * @return the index of the key in the array; -1 if the key is not found.
     */
    private int searchArray(int[] array, int key) {
        for(int i = 0; i < array.length; i++) {
            if(array[i] == key )
                return i;
        }
        return -1;
    }
    
}
