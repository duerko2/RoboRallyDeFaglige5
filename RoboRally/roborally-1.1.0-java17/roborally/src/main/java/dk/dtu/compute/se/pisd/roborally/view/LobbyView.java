package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class LobbyView extends VBox implements ViewObserver{
    private Pane mainPane;
    private Label label;
    private Label numOfPlayersLabel;
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
        for(int i=0;i<game.getBoard().getPlayers().size();i++){
            this.getChildren().add(new Pane(new Label(game.getBoard().getPlayers().get(i).getName())));
        }

        // Button for refreshing the view
        Button button1 = new Button("Refresh Lobby");
        button1.setOnAction(e->appController.updateLobby(gameName));
        this.getChildren().add(button1);

        // Game information
        numOfPlayersLabel = new Label("Amount of players needed for this game: "+game.getMaxAmountOfPlayers()+". \nBoard: \n"+gameName);
        this.getChildren().add(numOfPlayersLabel);

        // Button only visible to host. Starts the game
        if(appController.getIsHost()){
            Button startButton = new Button("Start Game");
            startButton.setOnAction(e->appController.startHostGame());
            this.getChildren().add(startButton);
        }





    }
}
