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

    // TOOD: Needs to be changed to support the multiplayer . Should upload the current game to the server.
    // Needs to be changed to
    public void saveGame() {

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
            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }


    public void exit() {
        if(getIsHost()){
            // If you are the host and you leave, the game should not allow players to join anymore.
            game.setReadyToReceivePlayers(false);
            try {
                GameClient.putGame(game.getSerialNumber(),JsonConverter.gameToJson(game));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            System.exit(0);
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
        boolean f = true;
        isHost = true;
        playerNumber = 0; // For now host is always 0.
        if (f == true) {
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

            roboRally.createLoadServerView(gamesList);

            // indsæt martins fx hvor man kun kan se spil der kører
            //loadSaveGame();
        } else {


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
            if (numOfPlayers.isPresent() && fileNameResult.isPresent()) {
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
            for (int i = 5; i >= numOfPlayers.get(); i--) {
                board.getPlayers().remove(i);
            }

            // Inserts own name into the first player object.
            board.getPlayers().get(0).setName(name);


            // Large random number for the serial number. Used to identify games on the server.
            // For the future, there could be a check to see if the number already exists on the server.
            String serialNumber = String.valueOf((int) (Math.random() * 1000000));

            this.game = new Game(board, serialNumber, numOfPlayers.get(), true);

            // Converts the game information to json string
            String jsonString = JsonConverter.gameToJson(game);

            // Sends the game information and serial number to the server.
            try {
                GameClient.putGame(serialNumber, jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Creates the view

            roboRally.createLobbyView(serialNumber, game);

            // Stars thread that pulls the game state every 5 seconds and updates the view.
            startLobbyThread(serialNumber);
        }
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
        List<String>joinableGamesList= new ArrayList<>();

        // TODO: Interpret the games from the server and see which ones are available to join.
        for(int i=0; i<gamesList.length;i++){
            Game tempGame=null;
            try {
                tempGame = JsonConverter.jsonToGame(GameClient.getGame(gamesList[i]));
            } catch (Exception e) {
            }
            try {
                if (tempGame.getReadyToReceivePlayers()){
                    joinableGamesList.add(gamesList[i]);
                }
            } catch(NullPointerException e){
            }
        }

        // for looop gamesList
        // Game game = GameClient.getGame(gamesList(i))
        // if(game.getReadyToReceivePlayers()){
        //  display...
        // }



        roboRally.createJoinView(joinableGamesList);


    }

    /**
     * This method is called when a user selects a game from the list. It fetches that game from the server and displays the players in that game.
     * Inserts the user into the player list and uploads the game again.
     * It then creates a thread which periodically fetches the game from the server and updates the view
     * @param serialNumber
     */
    public void startJoinGame(String serialNumber){

        try {
            String gameJson = GameClient.getGame(serialNumber);
            game = JsonConverter.jsonToGame(gameJson);

            ArrayList<Integer> PLAYER_SELECT_NUMBER = new ArrayList<Integer>();

            for (int i = 1 ; i < game.getMaxAmountOfPlayers() ; i++){
                PLAYER_SELECT_NUMBER.add(i+1);
            }

            // Add ourselves to the player list.

            for (int i = 1 ; i < game.getMaxAmountOfPlayers() ; i++){
                try {
                    if ((game.getBoard().getPlayers().get(i).getName() != null && PLAYER_SELECT_NUMBER.contains(i + 1))) {
                            PLAYER_SELECT_NUMBER.remove(PLAYER_SELECT_NUMBER.indexOf(i + 1));
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }

            /*

            Game tempGame = JsonConverter.jsonToGame(GameClient.getGame(game.getSerialNumber()));
            int rat = tempGame.getMaxAmountOfPlayers();
            for(int i = 0; i < tempGame.getBoard().getPlayers().size(); i++){
                if(tempGame.getBoard().getPlayers().get(i).get!=null){

                }
            }


             */
            int result=0;
            try {
                ChoiceDialog<Integer> dialogchoice = new ChoiceDialog<>(PLAYER_SELECT_NUMBER.get(0), PLAYER_SELECT_NUMBER);
                dialogchoice.setTitle("Player number");
                dialogchoice.setHeaderText("Select your player number");
                 result= dialogchoice.showAndWait().get();
            } catch (IndexOutOfBoundsException e){
                // What happens when the user can't join the game because it's full should go here. Right now nothing happens.
                return;
            }

            playerNumber=result-1;

            game.getBoard().getPlayers().get(result-1).setName(name);

            // Upload new game to server

            GameClient.putGame(game.getSerialNumber(),JsonConverter.gameToJson(game));


            // Create lobby view. Lobby view will be attached to the Game instance with Observer pattern, which will be updated frequently.
            // Should therefore display players currently in game in realtime.
            roboRally.createLobbyView(serialNumber,game);
            startLobbyThread(serialNumber);
            // Loop hvert 10 sekund.



        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public void setGame(Game game){
        this.game = game;
    }

    /**
     * This method starts a thread which every 2 seconds gets the game from the server and updates the view.
     * If the game is started by host, it will create a gameController object and start the game
     * @param serialNumber
     */
    private void startLobbyThread(String serialNumber){
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
                                    updateLobby(serialNumber);
                                    if (!game.getReadyToReceivePlayers()) {
                                        //start the game and if host->put the game
                                        try{
                                            if(getIsHost()) {
                                                GameClient.putGame(game.getSerialNumber(), JsonConverter.gameToJson(game));
                                            }

                                            // Game is replaced here instead of inserting the values, since we don't need to update the lobby view anymore.
                                            game = JsonConverter.jsonToGame(GameClient.getGame(serialNumber));
                                            gameController = new GameController(playerNumber, game);
                                            gameController.startProgrammingPhase();
                                            roboRally.createBoardView(gameController);
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                        //stop the thread


                                        System.out.println("THREAD STOPPERRRR");
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
     * @param serialNumber
     */
    public void updateLobby(String serialNumber){
        try {
            String gameJson = GameClient.getGame(serialNumber);
            Game newGame = JsonConverter.jsonToGame(gameJson);
            game.setBoard(newGame.getBoard());
            game.setReadyToReceivePlayers(newGame.getReadyToReceivePlayers());
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
            try {
                GameClient.putGame(game.getSerialNumber(),JsonConverter.gameToJson(game));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else return;







    }
    public void LoadHostGame (String serialNumber){
        try {


            String gameJson = GameClient.getGame(serialNumber);
            Game loadgame = JsonConverter.jsonToGame(gameJson);
            numOfPlayers = Optional.of(loadgame.getMaxAmountOfPlayers());
            Optional<String> fileNameResult = Optional.ofNullable(loadgame.getBoard().boardName);

            Board board = loadgame.getBoard();
            for (int i =0; i<numOfPlayers.get();i++){
                for (int j=0; j<5;j++){
                    board.getPlayer(i).getProgramField(j).setCard(loadgame.getBoard().getPlayer(i).getProgramField(j).getCard());
                }
            }
            for (int i = 0; i<numOfPlayers.get();i++){
                board.getPlayer(i).setName(null);
            }
            board.getPlayers().get(0).setName(name);

            loadgame = new Game(board,serialNumber,numOfPlayers.get(),true);

            String JsonString = JsonConverter.gameToJson(loadgame);
            try {


                GameClient.putGame(serialNumber, JsonString);
            }catch (Exception e){
                e.printStackTrace();
            }
            game = loadgame;
            roboRally.createLobbyView(serialNumber,loadgame);

            startLobbyThread(serialNumber);

        }catch (Exception e){
            e.printStackTrace();
        }
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
