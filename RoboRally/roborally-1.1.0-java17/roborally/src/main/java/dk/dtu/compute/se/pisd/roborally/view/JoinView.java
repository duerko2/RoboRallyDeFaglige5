package dk.dtu.compute.se.pisd.roborally.view;


import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;

public class JoinView extends VBox implements ViewObserver {

    private Pane mainPane;
    private Label label;
    private Label numOfPlayersLabel;
    private AppController appController;
    public JoinView(AppController appController, String[] games){
        this.appController=appController;
        label = new Label("\nLIST OF GAMES....");
        mainPane = new Pane(label);
        this.getChildren().add(mainPane);

        //Create button or pane for each board in the list.
        for(int i=0;i<games.length;i++){
            Button button = new Button(games[i]);
            button.setOnAction(e->appController.startJoinGame(button.getText()));
            this.getChildren().add(button);
        }



    }

    @Override
    public void updateView(Subject subject) {
    }
}
