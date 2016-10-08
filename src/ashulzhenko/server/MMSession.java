package ashulzhenko.server;

import java.net.Socket;
import java.net.SocketException;
import java.io.*;
import java.util.Arrays;
import java.util.Random;


/**
 * Session class that service a client. A client can play multiple games.
 * 
 * These are the packet messaging rules:
 * 
 * 0 0 0 0 - client requests new game (or a specific answer set at the beginning of a new game),
 * 1-8 - client color guesses,
 * 9 0 0 0 - client requests to stop the current game,
 * 10 10 10 10 - server sends OK to start a new game,
 * 12 - server indicates an in-place clue (0 if none found),
 * 13 - server indicates an out-place clue (0 if none found),
 * 14 0 0 0 - client requests to stop session (i.e. do not start a new game),
 * 15 15 15 15 - server indicates that the user lost the game.
 * 
 * @author Alena Shulzhenko
 * @version 08/09/2016
 * @since 1.8
 */
public class MMSession {
    private Socket socket;
    private int[] answerSet = new int[4];
    private Random random = new Random();
    private int round;
    private boolean playNewGame = true;
    private MMPacket util;

    /**
     * Instantiates the object when receiving the socket.
     * @param socket the socket that is created when client connects to the server.
     */
    public MMSession(Socket socket) {
        this.socket = socket;
        MMPacket util = new MMPacket();
    }

    /**
     * Starts session with the client.
     * @throws IOException If there is a problem when communicating to the client.
     */
    public void startNewSession() throws IOException {       
        //loops through multiple games.
        while(playNewGame && !socket.isClosed()) {
            boolean userQuit = false;
            startNewGame(); 
            System.out.println(Arrays.toString(answerSet));
            
            //loop for one game
            while(round < 11 && !socket.isClosed() && !userQuit) {
                //get client message
                int[] message = util.receiveMessage(socket);
                //check if user wants to quit this game
                if(message[0]!= 9) {
                    int[] clues = generateClues(message);
                    util.sendMessage(socket, clues);
                    round++;
                }
                else
                    userQuit = true;
                System.out.println("message: "+Arrays.toString(message) +" " + round);
            }
        }
    }

    /**
     * Initiates a new game and creates a new answer set.
     * @throws IOException If there is a problem when communicating to the client.
     */
    private void startNewGame() throws IOException {
        round = 1;
        int[] message = util.receiveMessage(socket);
        if(message[0] != 14) {
            //reply
            int[] answer = new int[4];
            Arrays.fill(answer, 10);
            util.sendMessage(socket, answer);
            createAnswerSet(message);
        }
        else 
            //stop all games
            playNewGame = false;
    }
    
    
    
    /**
     * Generates clues according to client's guesses.
     * @param message client's message with the guesses.
     * @return generated clues according to client's guesses.
     */
    private int[] generateClues(int[] message) {
        int[] clues = new int[4];       
        int[] answer = Arrays.copyOf(answerSet, answerSet.length);
        
        int clue = findInPlaceClues(message, clues, answer); //the position in clues array       
        
        if(clue != 4) {
            if(round == 10)
                //game is lost
                Arrays.fill(clues, 0xf);
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
    private void findOutPlaceClues(int[] message, int[] clues, int clue, int[] answer) {
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
     * @param message the client's message.
     */
    private void createAnswerSet(int[] message) {
        if(message[0] != 0) {
            answerSet = Arrays.copyOf(message, message.length);
        }
        else {
            for(int i = 0; i < answerSet.length; i++)
                answerSet[i] = random.nextInt(8)+1;
        }
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
