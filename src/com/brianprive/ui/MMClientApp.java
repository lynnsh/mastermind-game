package com.brianprive.ui;

/**
 * 
 * Main app that starts the run processes
 * 
 * @author Brian Prive
 * @since October 1st, 2016
 */
public class MMClientApp
{
    public static void main(String[] args)
    {
        MMClient client = new MMClient();
        
        //Method that start the game session
        client.run();
    }
}
