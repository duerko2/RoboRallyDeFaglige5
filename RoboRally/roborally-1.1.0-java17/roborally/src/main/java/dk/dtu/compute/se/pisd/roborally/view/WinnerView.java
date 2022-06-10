package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class WinnerView {
    public WinnerView(Player player){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game is over!");
        alert.setContentText("Player "+player.getName()+" has won the game. Press ok to close the game");
        Optional<ButtonType> result = alert.showAndWait();
    }
}
