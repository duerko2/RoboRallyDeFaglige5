package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.awt.*;

public class LobbyView extends VBox implements ViewObserver{
    private Label label;
    private Label numOfPlayersLabel;
    private VBox buttonPanel;
    private Button startButton;
    private AppController appController;
    private Game game;
    private String gameName;
    public LobbyView(int numOfPlayers, String gameName,AppController appController){
        this.appController=appController;
        this.game=appController.getGame();
        this.gameName=gameName;

        game.attach(this);
        update(game);
    }

    @Override
    public void updateView(Subject subject) {

        this.getChildren().clear();

        label = new Label(game.getBoard().getPlayers().get(0).getName()+"'s game\nLIST OF PLAYERS....\n");
        this.getChildren().add(new Pane(label));
        for(int i=0;i<game.getBoard().getPlayers().size();i++){
            this.getChildren().add(new Pane(new Label("Player "+(i+1)+": "+game.getBoard().getPlayers().get(i).getName())));
        }
        /*
        // Button for refreshing the view

        Button button1 = new Button("Refresh Lobby");
        button1.setOnAction();
        this.getChildren().add(button1);
        */
        // Game information
        numOfPlayersLabel = new Label("Amount of players needed for this game: "+game.getMaxAmountOfPlayers()+". \nBoard: \n"+"Serial number: "+gameName);
        this.getChildren().add(numOfPlayersLabel);
        // Button only visible to host. Starts the game
        if(appController.getIsHost()){
            startButton = new Button("Start Game");
            startButton.setOnAction(e->appController.startHostGame());
            buttonPanel = new VBox();
            buttonPanel.setAlignment(Pos.CENTER);
            buttonPanel.getChildren().add(startButton);
            this.getChildren().add(buttonPanel);
        }





    }
}
