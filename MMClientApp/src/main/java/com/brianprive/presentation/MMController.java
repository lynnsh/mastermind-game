package com.brianprive.presentation;

import com.brianprive.business.MMClientSession;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The controller class for the Mastermind game.
 * It works with the GUI view and the  business class MMClientSession
 * to provide the game experience for the user.
 * 
 * @author Brian Prive
 * @author Salman Haidar
 * @version 26/10/2016
 * @since 1.8
 */
public class MMController implements Initializable
{
    @FXML
    private Circle boardColor00, boardColor01, boardColor02, boardColor03,
                   boardColor10, boardColor11, boardColor12, boardColor13,
                   boardColor20, boardColor21, boardColor22, boardColor23,
                   boardColor30, boardColor31, boardColor32, boardColor33,
                   boardColor40, boardColor41, boardColor42, boardColor43,
                   boardColor50, boardColor51, boardColor52, boardColor53,
                   boardColor60, boardColor61, boardColor62, boardColor63,
                   boardColor70, boardColor71, boardColor72, boardColor73,
                   boardColor80, boardColor81, boardColor82, boardColor83,
                   boardColor90, boardColor91, boardColor92, boardColor93,
                   boardColor100, boardColor101, boardColor102, boardColor103;
    
    @FXML
    private Circle hint00, hint01, hint02, hint03, 
                   hint10, hint11, hint12, hint13,
                   hint20, hint21, hint22, hint23,
                   hint30, hint31, hint32, hint33,
                   hint40, hint41, hint42, hint43,
                   hint50, hint51, hint52, hint53,
                   hint60, hint61, hint62, hint63,
                   hint70, hint71, hint72, hint73,
                   hint80, hint81, hint82, hint83,
                   hint90, hint91, hint92, hint93;
    
    @FXML
    private Circle color1, color2, color3, color4, 
                   color5, color6, color7, color8;
    
    @FXML
    private Button btn_sendGuess;
    
    @FXML
    private Label lbl_lostWin;
    
    private Circle[][] board;
    private Circle[][] hint;
    private Circle[] colors;
    private Paint color;
    private int round;
    private Circle currentColorSelected;
    private MMClientSession game;
    private String ip;
    private String test;
    private boolean testMode;
    private Stage testStage;
    private Stage gameStage;

    /**
     * Called to initialize a controller after its root element 
     * has been completely processed.
     * @param url the location used to resolve relative paths for 
     *            the root object, or null if the location is not known.
     * @param rb the resources used to localize the root object, 
     *           or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
       board = new Circle[][]{
            {boardColor00, boardColor01, boardColor02, boardColor03},
            {boardColor10, boardColor11, boardColor12, boardColor13},
            {boardColor20, boardColor21, boardColor22, boardColor23},
            {boardColor30, boardColor31, boardColor32, boardColor33},
            {boardColor40, boardColor41, boardColor42, boardColor43},
            {boardColor50, boardColor51, boardColor52, boardColor53},
            {boardColor60, boardColor61, boardColor62, boardColor63},
            {boardColor70, boardColor71, boardColor72, boardColor73},
            {boardColor80, boardColor81, boardColor82, boardColor83},
            {boardColor90, boardColor91, boardColor92, boardColor93},
            {boardColor100, boardColor101, boardColor102, boardColor103}
        };
        
        hint = new Circle[][]{
            {hint00, hint01, hint02, hint03},
            {hint10, hint11, hint12, hint13},
            {hint20, hint21, hint22, hint23},
            {hint30, hint31, hint32, hint33},
            {hint40, hint41, hint42, hint43},
            {hint50, hint51, hint52, hint53},
            {hint60, hint61, hint62, hint63},
            {hint70, hint71, hint72, hint73},
            {hint80, hint81, hint82, hint83},
            {hint90, hint91, hint92, hint93}
        };
        
        colors = new Circle[]{
          color1, color2, color3, color4, color5, color6, color7, color8  
        };
    }
    
    /**
     * Launches the first game.
     */
    public void launch()
    {
        try
        {
            test = "";
            System.out.println("Server IP: " + ip);
            game = new MMClientSession(ip);
            start();
        }
        catch (IOException ioe)
        {
            System.err.println("Error connecting: " + ioe.getMessage());
            System.exit(1);
        }  
    }
    
    /**
     * Exits the Mastermind game.
     */
    public void quit()
    {
        try
        {
            game.quitGame();
            System.exit(0);
        }
        catch(IOException ioe)
        {
            System.exit(1);
        }
    }
    
    /**
     * Sets the IP number of the server.
     * @param ip 
     */
    public void setIp(String ip)
    {
        this.ip = ip;
    }
    
    /**
     * Sets the game stage.
     * @param gameStage 
     */
    public void setStage(Stage gameStage)
    {
        this.gameStage = gameStage;
    }
    
    /**
     * Determines user-selected color and styles the circle appropriately.
     * @param event the event that triggered this method.
     */
    @FXML
    private void getColor(MouseEvent event)
    {
        currentColorSelected.setEffect(new BoxBlur(0.0, 0.0, 0));
        
        Circle circle = (Circle)event.getSource();
        color = circle.getFill();
        circle.setEffect(new BoxBlur(10.0, 10.0, 1));
        
        currentColorSelected = circle;
    }

    
    /**
     * Fills the circle on board with the selected color.
     * If the round is complete (i.e. user filled all 4 circles),
     * the button is enabled.
     * @param event the event that triggered this method.
     */
    @FXML
    private void setColor(MouseEvent event)
    {
        Circle circle = (Circle)event.getSource();        
        circle.setFill(color);       
        boolean roundComplete = true;
        
        for (int i = 0; i < 4; i++)
        {
            Circle currentCircle = board[round][i];           
            if (currentCircle.getFill().equals(Color.WHITE))
                roundComplete = false;
        }
        
        if (roundComplete)
            btn_sendGuess.setDisable(false);     
    }
    
    /**
     * Gets user's guess and sends it to the server.
     * It receives the message from the server and displays
     * the message if the user has won or lost.
     * Otherwise, the clues are displayed.
     * @param event the event that triggered this method.
     */
    @FXML
    private void sendGuess(MouseEvent event)
    {
        btn_sendGuess.setDisable(true);        
        int[] guess = getUserGuesses();       
        
        try
        {
            int[] serverHints = game.playTurn(guess);        
            
            if (game.isOk() && game.isGameWon())
                displayWin();             
            else if (game.isOk() && game.isLostGame())
                displayLose(serverHints);              
            else
            {       
                for (int i = 0; i < 4; i++)
                {
                    displayHint(serverHints[i], i); 
                    
                    if(round < 9)
                    {
                        Circle currentCircle = board[round+1][i];
                        currentCircle.setDisable(false);
                    }
                }
                round++;               
            }
        }
        catch (IOException ioe)
        {
            System.err.println("Connection failed. " + ioe.getMessage());
        }
    }
    
    /**
     * Gets user's guesses from GUI.
     * @return user's guesses as an integer array.
     */
    private int[] getUserGuesses()
    {
        int[] guess = new int[4];
        
        for (int i = 0; i < 4; i++)
        {
            Circle currentCircle = board[round][i];
            currentCircle.setDisable(true);
            guess[i] = getColorId(currentCircle);
        }
        
        return guess;
    }
    
    /**
     * Displays the hint from the serves based on user's previous guess.
     * @param serverHints a hint from the server.
     * @param index the current index in hints array.
     */
    private void displayHint(int currentHint, int index)
    {
        if (currentHint == 12)
        {
            hint[round][index].setFill(Color.WHITE);
            hint[round][index].setVisible(true);
        }
        else if (currentHint == 13)
        {
            hint[round][index].setFill(Color.BLACK);
            hint[round][index].setVisible(true);

        }
    }
    
    /**
     * Displays the appropriate information
     * on the board if the user loses the game.
     * @param serverHints the correct answer set.
     */
    private void displayLose(int[] serverHints)
    {
        for (int i = 0; i < 4; i++)
        {
            displayHint(serverHints[i], i);

            lbl_lostWin.setText("LOST!");
            lbl_lostWin.textFillProperty().set(Color.RED);
            lbl_lostWin.setVisible(true);
            Circle currentCircle = board[10][i];
            currentCircle.setFill(getColorFromId(serverHints[i]));
        }
    }
    
    /**
     * Displays the appropriate information
     * on the board if the user wins the game.
     */
    private void displayWin()
    {
        for (int i = 0; i < 4; i++)
        {
            hint[round][i].setFill(Color.WHITE);
            hint[round][i].setVisible(true);
            lbl_lostWin.setText("WIN!");
            lbl_lostWin.textFillProperty().set(Color.GREEN);
            lbl_lostWin.setVisible(true);

            Circle currentCircle = board[10][i];
            currentCircle.setFill(board[round][i].getFill());
        }
    }   
    
    /**
     * Starts the new game.
     */
    private void start()
    {
        initializeHintsCircles();      
        
        //sets the last row for answer set
        for (int i = 0; i < 4; i++)
        {
            board[10][i].setFill(Color.GRAY);
            board[0][i].setDisable(false);
        }
        
        //sets effects for color circles on the board
        for (int i = 0; i < 8; i++)
            colors[i].setEffect(new BoxBlur(0.0, 0.0, 0));
        
        boardSetUp();
        
        if(testMode)
            showTestDialog();
        
        try
        {
            game.startNewSession(test);
        }
        catch (IOException ioe)
        {
            System.err.println("Error connecting. " + ioe.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Displays the help window.
     * @param event the event that triggered this method.
     */
    @FXML 
    private void getHelp(ActionEvent event)
    {
        Stage helpStage = new Stage();
        
        GridPane grid = setGridPane();       
        Label title = new Label("How to play:");       
        Label helpText = new Label();
        helpText.setText("Try to guess the pattern, in both order and color "
                + "within ten turns. There can be repeats in the pattern.\n"
                + "You play by clicking on a color, then place it on one of "
                + "the first free white circles.\nWhen you submit your guesses,"
                + " the clues will be displayed. You get a black peg if you "
                + "have the right color, but wrong position. You get a white "
                + "peg if you have the right color and right position.\n"
                + "Remember, the clues don't match the position of the guesses "
                + "you submitted.\n\nGood luck!");
        helpText.setWrapText(true);
        
        grid.add(title, 0, 0);
        grid.add(helpText, 0, 1);
        
        Scene stageScene = new Scene(grid, 300, 350);
        helpStage.setTitle("Help");
        helpStage.setScene(stageScene);
        helpStage.setResizable(false);
        helpStage.show();
    }
    
    /**
     * Set the grid pane to add to the stage.
     * @return the grid pane.
     */
    private GridPane setGridPane()
    {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        return grid;
    }
    
    /**
     * Get the test dialog window
     */
    private void showTestDialog()
    {
        testStage = new Stage();
        
        GridPane grid = setGridPane();
        
        Label title = new Label("Enter test colors:");
        TextField testText = new TextField();
        Label testErr = new Label ("*Invalid colors.");
        testErr.setTextFill(Color.RED);
        testErr.setVisible(false);
        
        Button submit = new Button("Submit");      
        submit.setOnAction(event -> setTest(testText, testErr));
        
        grid.add(title, 0, 0);
        grid.add(testText, 0, 1);
        grid.add(submit, 0, 2);
        grid.add(testErr, 0, 3);
        
        Scene stageScene = new Scene(grid, 150, 150);
        testStage.setTitle("Set Colors");
        testStage.setScene(stageScene);
        testStage.setResizable(false);
        testStage.initModality(Modality.APPLICATION_MODAL);
        testStage.initOwner(gameStage);
        testStage.showAndWait();
    }
    
    /**
     * Gets colors to enter as an answer set from the user.
     * @param testText TextField containing user-chosen answer set.
     * @param testErr label displayed if the entered numbers are not valid.
     */
    private void setTest(TextField testText, Label testErr)
    {
        String testInput = testText.getText().trim();
        String testRegex = "^[1-8]{4}$";
        if(!Pattern.matches(testRegex, testInput))
        {
            testErr.setVisible(true);
            test = "";
        }
        else
        {
            testErr.setVisible(false);
            test = testInput;
            testStage.hide();
        }         
    }
    
    /**
     * When user clicks the testMode it sets it on or off.
     * @param event the event that triggered this method.
     */
    @FXML 
    private void testDialog(ActionEvent event)
    {
        MenuItem item = (MenuItem)event.getSource();
        testMode = !testMode;
        if (testMode)
            item.setText("Disable Test Mode");
        else
            item.setText("Enable Test Mode");
    }
   
    /**
     * Initializes hints and circles graphic elements.
     */
    private void initializeHintsCircles()
    {
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                board[i][j].setFill(Color.WHITE);
                board[i][j].setDisable(true);
                hint[i][j].setVisible(false);
            }
        }
    }
     
    /**
     * Starts the new game.
     * If there is a game running, it is closed first.
     * @param event the event that triggered this method.
     */
    @FXML
    private void newGame(ActionEvent event)
    {
        try
        {
            if (!game.isGameWon() && !game.isLostGame())
                game.quitCurrentGame();   
            test = "";
            start(); 
        }
        catch (IOException ioe) {
            System.err.println("Error: " + ioe.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Exits the Mastermind game.
     * @param event the event that triggered this method.
     */
    @FXML
    private void quitGame(ActionEvent event)
    {
        quit();
    }
    
    /**
     * Sets the board with initial values
     */
    private void boardSetUp()
    {
        color = colors[0].getFill();
        colors[0].setEffect(new BoxBlur(10.0, 10.0, 1));
        currentColorSelected = color1;
        btn_sendGuess.setDisable(true);
        lbl_lostWin.setVisible(false);
        round = 0;
    }
    
    /**
     * When given the appropriate circle, return the id of its color.
     * @param circle the circle which color id to find.
     * @return the id of color of the provided circle.
     */
    private int getColorId(Circle circle)
    {   
        int id = 0;
        
        Color clr = (Color)circle.getFill();
        
        if (clr.equals((Color)color1.getFill()))
            id = 1;
        else if (clr.equals((Color)color2.getFill()))
            id = 2;
        else if (clr.equals((Color)color3.getFill()))
            id = 3;
        else if (clr.equals((Color)color4.getFill()))
            id = 4;
        else if (clr.equals((Color)color5.getFill()))
            id = 5;
        else if (clr.equals((Color)color6.getFill()))
            id = 6;
        else if (clr.equals((Color)color7.getFill()))
            id = 7;
        else if (clr.equals((Color)color8.getFill()))
            id = 8;
        
        return id;
    }
    
    /**
     * Returns the color when its id is provided.
     * @param id the id of the color to find.
     * @return the color with the provided id.
     */
    private Color getColorFromId(int id)
    {
        Color idColor = Color.WHITE;
        
        switch (id)
        {
            case 1 : idColor = (Color)color1.getFill();
                    break;
            
            case 2 : idColor = (Color)color2.getFill();
                    break;
            
            case 3 : idColor = (Color)color3.getFill();
                    break;
            
            case 4 : idColor = (Color)color4.getFill();
                    break;
            
            case 5 : idColor = (Color)color5.getFill();
                    break;
            
            case 6 : idColor = (Color)color6.getFill();
                    break;
            
            case 7 : idColor = (Color)color7.getFill();
                    break;
            
            case 8 : idColor = (Color)color8.getFill();
                    break;
        }
        
        return idColor;
    }
    
}
