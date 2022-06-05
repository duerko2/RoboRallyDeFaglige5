/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;

import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.springRequest.GameClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class AppController extends FieldAction implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    final private RoboRally roboRally;
    private static final String BOARDSFOLDER = "boards";
    final private List<String> BOARDS = new ArrayList<>();
    private String name;

    private GameController gameController;

    private Optional<Integer> numOfPlayers;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();


        BOARDS.clear();
        BOARDS.addAll(List.of(new File("RoboRally/roborally-1.1.0-java17/roborally/src/main/resources/boards").list()));
        ChoiceDialog<String> filename = new ChoiceDialog<>(BOARDS.get(0), BOARDS);
        filename.setTitle("Boards");
        filename.setHeaderText("Select board to play");
        Optional<String> fileNameResult = filename.showAndWait();
        BOARDS.clear();

        if (result.isPresent()&&fileNameResult.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }

            // XXX the board should eventually be created programmatically or loaded from a file
            //     here we just create an empty board with the required number of players.
            /*
            Board board = new Board(8,8);
            gameController = new GameController(board);
            int no = result.get();
            for (int i = 0; i < no; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1),0);
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
            }

            // XXX: V2
            // board.setCurrentPlayer(board.getPlayer(0));
            gameController.startProgrammingPhase();

             */
            // Real implementation of loading a board

            Board board = LoadBoard.loadBoard(fileNameResult.get());
            gameController = new GameController(board);
            int no = result.get();
            for (int i = 0; i < no; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1),0);
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
            }

            // XXX: V2
            // board.setCurrentPlayer(board.getPlayer(0));
            gameController.startProgrammingPhase();

            roboRally.createBoardView(gameController);

        }
    }

    public void saveGame() {
        String fileName=getUserInput("Name of save file:");

        LoadBoard.saveGame(gameController.board,fileName);
    }

    public void loadGame() {
        String filename=getUserInput("Name of save file:");
        Board board = LoadBoard.loadGame(filename);
        Player currentPlayer = board.getCurrentPlayer();
        gameController = new GameController(board);
        // XXX: V2
        // board.setCurrentPlayer(board.getPlayer(0));
        gameController.startProgrammingPhase(true,currentPlayer);


        roboRally.createBoardView(gameController);
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        return false;
    }

    public String getUserInput(String ask){
        Stage dialogue = new Stage();
        dialogue.setTitle(ask);

        final TextField textField = new TextField();
        final Button submitButton = new Button("Submit");
        submitButton.setDefaultButton(true);
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t) {
                dialogue.close();
            }
        });

        final VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER_RIGHT);
        layout.setStyle("-fx-background-color: azure; -fx-padding: 10;");
        layout.getChildren().setAll(
                textField,
                submitButton
        );

        dialogue.setScene(new Scene(layout));

        textField.setMinHeight(TextField.USE_PREF_SIZE);

        dialogue.showAndWait();
        return textField.getText();
    }

    public void hostGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        numOfPlayers = dialog.showAndWait();

        BOARDS.clear();
        BOARDS.addAll(List.of(new File("RoboRally/roborally-1.1.0-java17/roborally/src/main/resources/boards").list()));
        ChoiceDialog<String> filename = new ChoiceDialog<>(BOARDS.get(0), BOARDS);
        filename.setTitle("Boards");
        filename.setHeaderText("Select board to play");
        Optional<String> fileNameResult = filename.showAndWait();
        BOARDS.clear();

        if (numOfPlayers.isPresent()&&fileNameResult.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }
        }
        Board board = LoadBoard.loadBoard(fileNameResult.get());
        gameController = new GameController(board);


        //Here we create new player objects for the board. In the future the board should already have these objects and we just impose our name on the right one.
        Player player = new Player(board, PLAYER_COLORS.get(0), name,0);
        board.addPlayer(player);
        player.setSpace(board.getSpace(0 % board.width, 0));



        roboRally.createHostView(numOfPlayers.get(),fileNameResult.get());


        // TODO: Upload the game to the server

        //TODO: Pull the game for the server in a loop until it's in progress.





    }

    public void joinGame() {
        List<Board> boards=null;
        // TODO: Loop  every 5 or 10 seconds seconds to get games from server and call createJoinView with that list.
        try {
            String games = GameClient.getGames();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: Interpret the games from the server and see which ones are available to join.
        // For now hard coded with a default game
        List<String> games= List.of(new String[]{"Marcus's Game", "Beier's Game"});


        roboRally.createJoinView(games);


    }

    public void startJoinGame(String gameName){

    }

    public void startHostGame() {
        // TODO: Include check to see if lobby is full.


        // For now we just start the game as normal...
        gameController.startProgrammingPhase();

        roboRally.createBoardView(gameController);


    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
}
