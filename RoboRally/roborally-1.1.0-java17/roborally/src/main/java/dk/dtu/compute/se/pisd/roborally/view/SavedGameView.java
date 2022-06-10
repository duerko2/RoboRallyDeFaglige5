package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import javax.swing.*;
import java.awt.*;

public class SavedGameView extends VBox implements ViewObserver {
    private Pane mainPane;
    private Label label;
    private Label numOfPlayerslabel;
    private AppController appController;

public SavedGameView(AppController appController, String[] games ){
    this.appController=appController;
    label = new Label("\nLIST OF NOT PLAYED GAMES");
    mainPane = new Pane(label);
    this.getChildren().add(mainPane);

    for (int i=0; i<games.length;i++){
        if(games[i].contains("save_")) {
            Button button = new Button(games[i]);
            button.setOnAction(e -> appController.LoadHostGame(button.getText()));
            this.getChildren().add(button);
        }
    }
}
    @Override
    public void updateView(Subject subject) {

    }


}
