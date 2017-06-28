package com.brianprive.presentation;

import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Control;
import static javafx.application.Application.launch;

/**
 * The class that starts the application.
 * 
 * @author Brian Prive
 * @author Salman Haidar
 * @version 25/10/2016
 * @since 1.8
 */
public class MMApp extends Application {

    private final FXMLLoader loader = new FXMLLoader(getClass().getResource
        ("/fxml/Scene.fxml"));
    
    private Label error;
    private TextField[] ips;
    private Stage ipStage;
    private Stage gameStage; 
    private String ip; 
    
    /**
     * Stops the game.
     */
    @Override
    public void stop()
    {
        MMController controller = loader.getController();
        
        //Might not have started
        if(controller != null)
            controller.quit();
    }
    
    /**
     * Prepares graphic elements for user to display.
     * 
     * @param stage the stage where the GUI is placed.
     */
    @Override
    public void start(Stage stage)
    {       
        try
        {
            gameStage = stage;
            ipStage = new Stage();

            ips = new TextField[]{new TextField(""), new TextField(""), 
                                  new TextField(""), new TextField("")};
            
            Button btn = new Button("Enter");
            btn.setOnAction(event -> getServerIp());

            HBox hbTitle = setHBox(new Label("Enter Server IP"));
            HBox hbErr = setHBox(setErrorLabel());
            HBox hbBtn = setHBox(btn);

            GridPane grid = setUpGrid(hbTitle, ips, hbErr, hbBtn);

            prepareIpStage(grid);
            
        }
        catch(Exception e)
        {
            System.err.println("Error displaying layout: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * The starting point of the application.
     * 
     * @param args cli arguments if they are provided.
     */
    public static void main(String[] args)
    {
        launch(args);
    }
    
    /**
     * Retrieves server's IP address from the user input.
     * Launches a new game if IP number was valid.
     * 
     * @return true if the provided IP was valid; false otherwise.
     */
    private boolean getServerIp()
    {
        //user input
        String input = ips[0].getText().trim() + "." + ips[1].getText().trim()
                        + "." + ips[2].getText().trim() + "."
                        + ips[3].getText().trim();
       
        
        String ipRegex = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                         "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                         "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                         "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        
        
        if(!Pattern.matches(ipRegex, input)) 
        {
            error.setVisible(true);
            return false;
        }
        else 
        {
            ip = input;
            error.setVisible(false);
            ipStage.hide();                
            launchGame();
            return true;
        }
    }

    /**
     * Launches a new game.
     * Prepares graphic elements for the Mastermind game.
     */
    private void launchGame()
    {
        try 
        {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/Styles.css");
            
            MMController controller = loader.getController();
            controller.setIp(ip);
            controller.setStage(gameStage);
            controller.launch();
            
            gameStage.setTitle("Mastermind");
            gameStage.setScene(scene);
            gameStage.setResizable(false);
            gameStage.show();
        }
        catch (Exception e)
        {
            System.exit(1);
        }
    }    
    
    /**
     * Prepares the main stage for GUI for the IP address from.
     * @param grid the root element where the rest of graphic elements
     * are placed.
     */
    private void prepareIpStage(GridPane grid)
    {
        Scene stageScene = new Scene(grid, 300, 200);
        ipStage.setTitle("Enter Server IP");
        ipStage.setScene(stageScene);
        ipStage.setResizable(false);
        ipStage.show();
    }

    /**
     * Sets error label that will indicate if user-provided IP is invalid.
     * 
     * @return the created error label.
     */
    private Label setErrorLabel()
    {
        error = new Label("*Not a valid IP");
        error.setVisible(false);
        error.setTextFill(Color.RED);
        return error;
    }

    /**
     * Sets horizontal box where the provided element
     * will be placed with the centered alignment.
     * 
     * @param control the control to place in the box.
     * @return the horizontal box with the provided control in place.
     */
    private HBox setHBox(Control control)
    {
        HBox hb = new HBox(11);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().add(control);
        return hb;
    }

    /**
     * Sets up the root grid element for the IP form.
     * 
     * @param hbTitle the horizontal box containing title of the window.
     * @param ips text fields where the user will enter the IP.
     * @param hbErr the horizontal box containing error label.
     * @param hbBtn the horizontal box containing button.
     * @return the grid with all provided elements placed.
     */
    private GridPane setUpGrid(HBox hbTitle, TextField[] ips, 
                               HBox hbErr, HBox hbBtn)
    {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        
        grid.add(hbTitle, 0, 1, 11, 1);
        grid.add(ips[0], 2, 2);
        grid.add(new Label("."), 3, 2);
        grid.add(ips[1], 4, 2);
        grid.add(new Label("."), 5, 2);
        grid.add(ips[2], 6, 2);
        grid.add(new Label("."), 7, 2);
        grid.add(ips[3], 8, 2);
        grid.add(hbErr, 0, 3, 11, 1);
        grid.add(hbBtn, 0, 4, 11, 1);
        
        return grid;
    }
}