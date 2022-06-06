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
    public LobbyView(int numOfPlayers, String boardName, Game game,AppController appController){
        this.appController=appController;



        label = new Label(appController.getName()+"'s game\nLIST OF PLAYERS....");
        mainPane = new Pane(label);
        numOfPlayersLabel = new Label("Amount of players needed for this game: "+numOfPlayers+". \nBoard: "+boardName);
        Button button1 = new Button("Start Game");
        button1.setOnAction(e->appController.startHostGame());

        this.getChildren().add(mainPane);
        this.getChildren().add(button1);
        this.getChildren().add(numOfPlayersLabel);

        game.attach(this);
        updateView(game);

    }

    @Override
    public void updateView(Subject subject) {
    }
}
