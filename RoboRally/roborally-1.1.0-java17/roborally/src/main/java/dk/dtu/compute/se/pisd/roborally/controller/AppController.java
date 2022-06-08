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
import dk.dtu.compute.se.pisd.roborally.model.*;

import dk.dtu.compute.se.pisd.roborally.springRequest.GameClient;
import dk.dtu.compute.se.pisd.roborally.springRequest.JsonConverter;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");
    final private RoboRally roboRally;
    private static final String BOARDSFOLDER = "boards";
    final private List<String> BOARDS = new ArrayList<>();
    private String name;
    private GameController gameController;
    private Optional<Integer> numOfPlayers;
    private Game game;
    private boolean isHost=false;
    private Thread lobbyThread;
    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }
    private int playerNumber;


    // To be deleted
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
            gameController = new GameController(board,0);
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

    // TOOD: Needs to be changed to support the multiplayer . Should upload the current game to the server.
    // Needs to be changed to
    public void saveGame() {
        String fileName=getUserInput("Name of save file:");

        LoadBoard.saveGame(gameController.board,fileName);
    }

    // To be deleted
    public void loadGame() {
        String filename=getUserInput("Name of save file:");
        Board board = LoadBoard.loadGame(filename);
        Player currentPlayer = board.getCurrentPlayer();
        gameController = new GameController(board,0);
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


    // Gets input from user
    // TODO: Should somehow be more of a view thing...
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

    /**
     * This method is called when the user selects host game from the menu. It asks the user to choose amount of players for the game and the board to be played
     * The it loads a board and uploads the game to the server with the correct information.
     * Aftewards it starts a thread which listens to the server to see if other players have joined and updates the view.
     * TODO: The player should also be able to load a previously played game from the server.
     */
    public void hostGame() {
        // A couple of to be stored in the client, when the user hosts.
        isHost=true;
        playerNumber=0; // For now host is always 0.


        // Gives host ability to select amount of players for this game.
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        numOfPlayers = dialog.showAndWait();


        // Gives host ability to choose field.
        BOARDS.clear();
        BOARDS.addAll(List.of(new File("RoboRally/roborally-1.1.0-java17/roborally/src/main/resources/boards").list()));
        ChoiceDialog<String> filename = new ChoiceDialog<>(BOARDS.get(0), BOARDS);
        filename.setTitle("Boards");
        filename.setHeaderText("Select board to play");
        Optional<String> fileNameResult = filename.showAndWait();
        BOARDS.clear();


        // Defensive checks in case something goes with user selection.
        if (numOfPlayers.isPresent()&&fileNameResult.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }
        }



        // Loads the board chosen from the client. In the future this could also be stored on the server.
        Board board = LoadBoard.loadFromBoards(fileNameResult.get());
        for(int i = 5; i >= numOfPlayers.get() ;i--){
            board.getPlayers().remove(i);
        }

        // Inserts own name into the first player object.
        board.getPlayers().get(0).setName(name);


        // Large random number for the serial number. Used to identify games on the server.
        // For the future, there could be a check to see if the number already exists on the server.
        String serialNumber = String.valueOf((int)(Math.random()*1000000));

        this.game = new Game(board,serialNumber,numOfPlayers.get(),true);

        // Converts the game information to json string
        String jsonString = JsonConverter.gameToJson(game);

        // Sends the game information and serial number to the server.
        try {
            GameClient.putGame(serialNumber,jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Creates the view
        String gameName = String.join("",game.getSerialNumber(),".json");
        roboRally.createLobbyView(gameName,game);

        // Stars thread that pulls the game state every 5 seconds and updates the view.
        startLobbyThread(gameName);
    }

    /**
     * This method is called when the user chooses to join a game in the menu bar. It fetches all the games on the server and displays them.
     *
     */
    public void joinGame() {
        List<Board> boards = null;
        String games = null;
        try {
            games = GameClient.getGames();
        } catch (Exception e) {
            e.printStackTrace();
        }




        String[] gamesList = games.split("\n");


        // TODO: Interpret the games from the server and see which ones are available to join.

        // for looop gamesList
        // Game game = GameClient.getGame(gamesList(i))
        // if(game.getReadyToReceivePlayers()){
        //  display...
        // }

        roboRally.createJoinView(gamesList);


    }

    /**
     * This method is called when a user selects a game from the list. It fetches that game from the server and displays the players in that game.
     * Inserts the user into the player list and uploads the game again.
     * It then creates a thread which periodically fetches the game from the server and updates the view
     * @param gameName
     */
    public void startJoinGame(String gameName){
        try {
            String gameJson = GameClient.getGame(gameName);
            game = JsonConverter.jsonToGame(gameJson);

            // Add ourselves to the player list.
            int currentIndex=0;
            try {
                while (game.getBoard().getPlayers().get(currentIndex).getName() != null) {
                    currentIndex++;
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println(e.getMessage());
                return;
            }
            playerNumber=currentIndex;

            game.getBoard().getPlayers().get(currentIndex).setName(name);

            // Upload new game to server

            GameClient.putGame(game.getSerialNumber(),JsonConverter.gameToJson(game));


            // Create lobby view. Lobby view will be attached to the Game instance with Observer pattern, which will be updated frequently.
            // Should therefore display players currently in game in realtime.
            roboRally.createLobbyView(gameName,game);
            startLobbyThread(gameName);
            // Loop hvert 10 sekund.



        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * This method starts a thread which every 2 seconds gets the game from the server and updates the view.
     * If the game is started by host, it will create a gameController object and start the game
     * @param gameName
     */
    private void startLobbyThread(String gameName){
        lobbyThread = new Thread( new Runnable() {
            boolean running;
            public void stopThread(){
                running = false;
                lobbyThread.interrupt();
            }
            public void run() {
                running = true;
                while(running) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException var2) {
                            running = false;
                            lobbyThread.interrupt();
                        }
                        if(running) {
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    updateLobby(gameName);
                                    if (!game.getReadyToReceivePlayers()) {
                                        //start the game and if host->put the game
                                        try{
                                            if(getIsHost()) {
                                                GameClient.putGame(game.getSerialNumber(), JsonConverter.gameToJson(game));
                                            }
                                            game = JsonConverter.jsonToGame(GameClient.getGame(gameName));
                                            game.updated();
                                            gameController = new GameController(game.getBoard(),playerNumber);
                                            gameController.startProgrammingPhase();
                                            roboRally.createBoardView(gameController);
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }

                                        //stop the thread
                                        stopThread();

                                    }
                                }
                            });
                        }
                }
            }
        });
        lobbyThread.start();
    }

    /**
     * This method fetches the game from the server and calls the method to update the view.
     * @param gameName
     */
    public void updateLobby(String gameName){
        try {
            String gameJson = GameClient.getGame(gameName);
            Game newGame = JsonConverter.jsonToGame(gameJson);
            Board newBoard = newGame.getBoard();
            game.getBoard().getPlayers().clear();
            game.getBoard().getPlayers().addAll(newGame.getBoard().getPlayers());
            game.setReadyToReceivePlayers(newGame.getReadyToReceivePlayers());
            game.updated();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * When the host presses play this method is called. It checks if the game is full (player count matches allowed player count)
     * If this check is passed it will change status of the game to not ready to receive players
     */
    public void startHostGame() {
        int amountOfCurrentPlayers=0;
        for(int i=0;i<game.getBoard().getPlayers().size();i++){
            if(game.getBoard().getPlayers().get(i).getName()!=null){
                amountOfCurrentPlayers++;
            }
        }
        System.out.println(amountOfCurrentPlayers);
        if(amountOfCurrentPlayers==game.getMaxAmountOfPlayers()){
            // For now we just start the game as normal...
            game.setReadyToReceivePlayers(false);
        }
        else return;







    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }

    public Game getGame() {
        return game;
    }
    public boolean getIsHost(){
        return isHost;
    }
}
