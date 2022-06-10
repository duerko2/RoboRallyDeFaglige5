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

import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.springRequest.GameClient;
import dk.dtu.compute.se.pisd.roborally.springRequest.JsonConverter;
import javafx.application.Platform;
import javafx.scene.control.ChoiceDialog;
import org.jetbrains.annotations.NotNull;

import java.awt.font.TextMeasurer;
import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {
    private Game game;
    private int playerNumber;
    private Thread startActivationPhaseThread;
    private Thread activationPhaseThread;

    public GameController(int playerNumber, @NotNull Game game) {
        this.game = game;
        this.playerNumber=playerNumber;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        // TODO Assignment V1: method should be implemented by the students:
        //   - the current player should be moved to the given space
        //     (if it is free()
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if the player is moved

        if (space.getPlayer() == null) {
            game.getBoard().getCurrentPlayer().setSpace(space);
            game.getBoard().nextPlayer();
        }

    }

    /**
     * This starts the programming phase, and assigns new programming cards for each of the players.
     */
    // XXX: V2
    public void startProgrammingPhase() {
        game.getBoard().setPhase(Phase.PROGRAMMING);
        game.getBoard().setCurrentPlayer(game.getBoard().getPlayer(0));

        try {
            Game tempGame = JsonConverter.jsonToGame(GameClient.getGame(game.getSerialNumber()));
            if(tempGame.getBoard().getPlayer(playerNumber).getCardField(0).getCard() != null){
                applyGetGame();
            } else{
                for (int i = 0; i < game.getBoard().getPlayersNumber(); i++) {
                    Player player = game.getBoard().getPlayer(i);
                    if (player != null) {
                        for (int j = 0; j < Player.NO_REGISTERS; j++) {
                            CommandCardField field = player.getProgramField(j);
                            field.setCard(null);
                            field.setVisible(true);
                        }
                        for (int j = 0; j < Player.NO_CARDS; j++) {
                            CommandCardField field = player.getCardField(j);
                            field.setCard(generateRandomCommandCard());
                            field.setVisible(true);
                        }
                    }
                }
                GameClient.putGame(game.getSerialNumber(),JsonConverter.gameToJson(game));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * This starts programming phase when the game is loaded from a previously saved game.
     * This does not assign new programming cards.
     *
     * @param gameLoaded boolean to make sure this method only is used if the game is loaded.
     * @param currentPlayer selects the current player for the programming phase.
     */
    public void startProgrammingPhase(boolean gameLoaded, Player currentPlayer){
        game.getBoard().setPhase(Phase.PROGRAMMING);
        game.getBoard().setCurrentPlayer(currentPlayer);
        game.getBoard().setStep(0);
    }

    /**
     * Generates random programming cards
     *
     * @return returns the generated programming cards
     */
    // XXX: V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * Ending the programming phase, and starts activation phase for player 1.
     */
    // XXX: V2
    public void finishProgrammingPhase(){
            // Creates a new temporary player object.
            CommandCard[] tempCards = new CommandCard[Player.NO_CARDS];
            CommandCard[] tempProgram = new CommandCard[Player.NO_REGISTERS];
            for(int j = 0; j < Player.NO_REGISTERS; j++){
                tempProgram[j] = game.getBoard().getPlayer(playerNumber).getProgramField(j).getCard();
            }
            for(int j = 0; j < Player.NO_CARDS; j++){
                tempCards[j] = game.getBoard().getPlayer(playerNumber).getCardField(j).getCard();
            }

            try {
                applyGetGame();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Player player = game.getBoard().getPlayer(playerNumber);
            boolean full = true;
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(tempProgram[j]);
                    field.setVisible(true);
                    if(field.getCard() == null){
                        field.setCard(generateRandomCommandCard());
                    }
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(tempCards[j]);
                    field.setVisible(true);
                }
            }

            if(full) {
                if (checkForActivation()) {
                    game.getBoard().setPhase(Phase.ACTIVATION);
                    game.getBoard().setCurrentPlayer(game.getBoard().getPlayer(0));
                    game.getBoard().setStep(0);
                }

                try {
                    GameClient.putGame(game.getSerialNumber(), JsonConverter.gameToJson(game));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivationThread();
            } else{
                //find på noget med message
            }
        }

    /**
     * Updates client with the game from the server.
     */

    public void applyGetGame() throws Exception {
        Game serverGame = JsonConverter.jsonToGame(GameClient.getGame(game.getSerialNumber()));
        game.getBoard().setPhase(serverGame.getBoard().getPhase());
        game.getBoard().setPlayers(serverGame);
        game.getBoard().setCurrentPlayerIndex(serverGame.getBoard().getPlayerNumber(serverGame.getBoard().getCurrentPlayer()));
        game.getBoard().setStep(serverGame.getBoard().getStep());
    }

    /**
     * Checks if every player has programmed their robot.
     */

    public boolean checkForActivation(){
        for(int i = 0; i < game.getBoard().getPlayers().size();i++){
            for(int j = 0; j < Player.NO_REGISTERS; j++) {
                if (game.getBoard().getPlayer(i).getProgramField(j).getCard() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Makes the used programming cards visible one by one when they are used or about to be.
     *
     * @param register a parameter used for knowing what the current register is, and ensures that it does not loop forever
     */
    // XXX: V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < game.getBoard().getPlayersNumber(); i++) {
                Player player = game.getBoard().getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    /**
     * Makes all programming cards invisible for each player
     */
    // XXX: V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < game.getBoard().getPlayersNumber(); i++) {
            Player player = game.getBoard().getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    /**
     * Starts a thread that waits for activation phase to be current phase.
     */

    public void startActivationThread(){
        startActivationPhaseThread = new Thread(new Runnable() {
            boolean running;
            public void stopThread(){
                running = false;
                startActivationPhaseThread.interrupt();
            }
            public void run() {
                if(activationPhaseThread != null && !activationPhaseThread.isInterrupted()){
                    activationPhaseThread.interrupt();
                }
                running = true;
                while (running) {
                    try {
                        Thread.sleep(2000);
                        Game tempGame = JsonConverter.jsonToGame(GameClient.getGame(game.getSerialNumber()));
                        if (tempGame.getBoard().getPhase() == Phase.ACTIVATION) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    makeProgramFieldsVisible(0);
                                    try {
                                        applyGetGame();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    game.getBoard().setCurrentPlayer(game.getBoard().getPlayer(0));
                                    game.getBoard().setStep(0);
                                }
                            });
                            ActivationPhase();
                            stopThread();
                        }
                    } catch (Exception e) {
                        this.stopThread();
                    }
                }
            }
        });
        startActivationPhaseThread.start();
    }

    /**
     * Starts the activation phase, also checks if it's programming phase and starts that.
     */

    public void ActivationPhase(){
        activationPhaseThread = new Thread(new Runnable() {
            boolean running;
            public void stopThread() {
                running = false;
                activationPhaseThread.interrupt();
            }
            public void run() {
                if(startActivationPhaseThread != null && !startActivationPhaseThread.isInterrupted()){
                    startActivationPhaseThread.interrupt();
                }
                running = true;
                while (running) {
                    try {
                        Thread.sleep(2000);
                        if(game.getBoard().getStep() == 4){
                            System.out.println("rat");
                        }
                        Game tempGame = JsonConverter.jsonToGame(GameClient.getGame(game.getSerialNumber()));
                        //StartProgrammingPhase
                        if(tempGame.getBoard().getStep() >= Player.NO_REGISTERS){
                            applyGetGame();
                            startProgrammingPhase();
                            stopThread();
                        } else if(tempGame.getBoard().getPlayerNumber(tempGame.getBoard().getCurrentPlayer()) == playerNumber) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        applyGetGame();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            if(tempGame.getBoard().getStep() != Player.NO_REGISTERS) {
                                stopThread();
                            }
                        } else {
                            applyGetGame();
                        }
                    } catch (Exception e) {
                        this.stopThread();
                        ActivationPhase();
                    }
                }
            }
        });
        activationPhaseThread.start();
    }

    // XXX: V2
    public void executePrograms() {
        game.getBoard().setStepMode(false);
        continuePrograms();
    }

    // XXX: V2
    public void executeStep() {
        game.getBoard().setStepMode(true);
        executeNextStep();
    }

    // XXX: V2
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (game.getBoard().getPhase() == Phase.ACTIVATION && !game.getBoard().isStepMode());
    }

    /**
     * This gives the next player the turn after the current player has played their cards during the activation phase.
     * This also sets the phase to programming phase if all the players has used all their programming cards.
     */
    // XXX: V2
    public void executeNextStep() {
        // 1. execute my step
        // 2. Sets next player's turn and increases step if needed
        // 3. Upload my changes
        // 4. Start the thread that checks if it's my turn.

        // 1.
        Player currentPlayer = game.getBoard().getCurrentPlayer();
        int step = game.getBoard().getStep();
        if (step >= 0 && step < Player.NO_REGISTERS) {
            CommandCard card = currentPlayer.getProgramField(step).getCard();
            if (card != null) {
                if (!card.command.getOptions().isEmpty()) {
                    game.getBoard().setPhase(Phase.PLAYER_INTERACTION);
                    return;
                }
                // Executes my step
                Command command = card.command;
                executeCommand(currentPlayer, command);
            }

            // 2.
            int nextPlayerNumber = game.getBoard().getPlayerNumber(currentPlayer) + 1;
            if (nextPlayerNumber < game.getBoard().getPlayersNumber()) {
                game.getBoard().setCurrentPlayer(game.getBoard().getPlayer(nextPlayerNumber));
            } else {
                step++;
                if (step < Player.NO_REGISTERS) {
                    makeProgramFieldsVisible(step);
                }
                game.getBoard().setStep(step);
                game.getBoard().setCurrentPlayer(game.getBoard().getPlayer(0));
            }

            // 3.
            try {
                GameClient.putGame(game.getSerialNumber(),JsonConverter.gameToJson(game));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //4.
            ActivationPhase();
        }
    }

    /**
     * Checks if the player is on an action board retrieves all field actions on the current field.
     *
     * @param board Parameter used to access the board
     */
    private void spaceAction(Board board){
        if(board.getCurrentPlayer().getSpace().getActions().size() > 0){
            for(int i = 0; i < board.getCurrentPlayer().getSpace().getActions().size();i++){
                board.getCurrentPlayer().getSpace().getActions().get(i).doAction(this, board.getCurrentPlayer().getSpace());
            }
        }
    }

    /**
     * This method executes the command from the programming cards chosen,
     * and makes sure the right thing happens whenever a card has been played.
     *
     * @param player used to distinguish the players and execute the command on the current player.
     * @param command used to see which programming card has been played and which command to use.
     */
    // XXX: V2
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveOne(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.moveTwo(player);

                    break;
                case OPTION_LEFT_RIGHT:
                    executeNextStep();
                    /*ChoiceDialog<Command> dialog = new ChoiceDialog<>(command.getOptions().get(0),command.getOptions());
                    dialog.setTitle("Choose command");
                    dialog.setHeaderText("Select command left or right");
                    Optional<Command> chosenCommand=dialog.showAndWait();
                    executeCommand(player,chosenCommand.get());
                     */
                    break;
                case U_TURN:
                    this.uTurn(player);
                    break;
                case MOVE_THREE:
                    this.moveThree(player);
                    break;
                case BACK_UP:
                    this.backUp(player);
                    break;
                case WEASEL_ROUTINE:
                    executeNextStep();
                    break;
                case SANDBOX:
                    executeNextStep();
                    break;
                default:
                    // DO NOTHING (for now)
            }
            spaceAction(game.getBoard());
        }
    }

    /**
     * This method executes chosen command from a command option card and continues the execution of the game.
     *
     * @param command This is the command chosen from the command option card.
     */
    public void executeCommandOptionAndContinue(Command command) {
        executeCommand(game.getBoard().getCurrentPlayer(), command);
        Player currentPlayer = game.getBoard().getCurrentPlayer();
        int step = game.getBoard().getStep();
        int nextPlayerNumber = game.getBoard().getPlayerNumber(currentPlayer) + 1;
        if (nextPlayerNumber < game.getBoard().getPlayersNumber()) {
            game.getBoard().setCurrentPlayer(game.getBoard().getPlayer(nextPlayerNumber));
        } else {
            step++;
            if (step < Player.NO_REGISTERS) {
                makeProgramFieldsVisible(step);
            }
            game.getBoard().setStep(step);
            game.getBoard().setCurrentPlayer(game.getBoard().getPlayer(0));
        }

        // 3.
        game.getBoard().setPhase(Phase.ACTIVATION);
        try {
            GameClient.putGame(game.getSerialNumber(),JsonConverter.gameToJson(game));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //4.
        ActivationPhase();

        /*
        game.getBoard().setPhase(Phase.ACTIVATION);
        executeCommand(game.getBoard().getCurrentPlayer(), command);
        int step = game.getBoard().getStep();
        int nextPlayerNumber = game.getBoard().getPlayerNumber(game.getBoard().getCurrentPlayer()) + 1;
        if (nextPlayerNumber < game.getBoard().getPlayersNumber()) {
            game.getBoard().setCurrentPlayer(game.getBoard().getPlayer(nextPlayerNumber));
        } else {
            step++;
            if (step < Player.NO_REGISTERS) {
                makeProgramFieldsVisible(step);
                game.getBoard().setStep(step);
                game.getBoard().setCurrentPlayer(game.getBoard().getPlayer(0));
            } else {
                startProgrammingPhase();
            }
        }
         */
    }


    public void  moveOne (@NotNull Player player){
        try {


            Space nextSpace;
                move(player);

        }catch (NullPointerException e){
            outOfBounds(player);
        }


    }

    /**
     * Checks if there is a player or a wall in the way of the current player's robot.
     * If so makes sure to either push another player or stop for the wall
     *
     * @param player gets the player and executes action on players.
     * @throws NullPointerException throws exception when a field not on the board is chosen.
     */
    // TODO Assignment V2
    public void move(@NotNull Player player) throws NullPointerException {

            Space nextSpace;
            Space currentSpace = player.getSpace();
            switch (player.getHeading()) {
                case SOUTH:
                    nextSpace = (game.getBoard().getSpace(player.getSpace().x, player.getSpace().y + 1));
                    //checkForOutOfBounds();
                    if (checkForWall(currentSpace, nextSpace, player.getHeading(),player)) {
                        return;
                    }
                    checkForPush(player, nextSpace, player.getHeading());
                    break;
                case WEST:
                    nextSpace = (game.getBoard().getSpace(player.getSpace().x - 1, player.getSpace().y));
                    if (checkForWall(currentSpace, nextSpace, player.getHeading(),player)) {
                        return;
                    }
                    checkForPush(player, nextSpace, player.getHeading());
                    break;
                case NORTH:
                    nextSpace = (game.getBoard().getSpace(player.getSpace().x, player.getSpace().y - 1));
                    if (checkForWall(currentSpace, nextSpace, player.getHeading(),player)) {
                        return;
                    }
                    checkForPush(player, nextSpace, player.getHeading());
                    break;
                case EAST:
                    nextSpace = (game.getBoard().getSpace(player.getSpace().x + 1, player.getSpace().y));
                    if (checkForWall(currentSpace, nextSpace, player.getHeading(),player)) {
                        return;
                    }
                    checkForPush(player, nextSpace, player.getHeading());
                    break;
            }
        }



    // TODO Assignment V2
    public void moveTwo(@NotNull Player player) {
        try {
            Space nextSpace;

            for (int i = 0; i < 2; i++) {
                move(player);
            }
        }catch (NullPointerException e){
            outOfBounds(player);
        }
    }

    public void moveThree(@NotNull Player player) {
        try {
        Space nextSpace;
        for (int i = 0; i < 3; i++) {
            move(player);
        }
        }catch (NullPointerException e){
            outOfBounds(player);
        }

    }


    /**
     * Goes back 1 field, and checks for wall.
     *
     * @param player gets the current player
     */
        public void backUp (@NotNull Player player){
        try {
            Space nextSpace;
            Space currentSpace = player.getSpace();
            Boolean backwards = true;
            switch (player.getHeading()) {
                case SOUTH:
                    nextSpace = (game.getBoard().getSpace(player.getSpace().x, player.getSpace().y - 1));
                    if (checkForWall(currentSpace, nextSpace, player.getHeading().next().next(),player)) {
                        return;
                    }
                    checkForPush(player, nextSpace, player.getHeading().next().next());
                    break;
                case WEST:
                    nextSpace = (game.getBoard().getSpace(player.getSpace().x + 1, player.getSpace().y));
                    if (checkForWall(currentSpace, nextSpace, player.getHeading().next().next(),player)) {
                        return;
                    }
                    checkForPush(player, nextSpace, player.getHeading().next().next());
                    break;
                case NORTH:
                    nextSpace = (game.getBoard().getSpace(player.getSpace().x, player.getSpace().y + 1));
                    if (checkForWall(currentSpace, nextSpace, player.getHeading().next().next(),player)) {
                        return;
                    }
                    checkForPush(player, nextSpace, player.getHeading().next().next());
                    break;
                case EAST:
                    nextSpace = (game.getBoard().getSpace(player.getSpace().x - 1, player.getSpace().y));
                    if (checkForWall(currentSpace, nextSpace, player.getHeading().next().next(),player)) {
                        return;
                    }
                    checkForPush(player, nextSpace, player.getHeading().next().next());
                    break;
            }
        }catch (NullPointerException e){
            outOfBounds(player);
        }


        }

        public void uTurn (@NotNull Player player){
            player.setHeading((player.getHeading().next()));
            player.setHeading((player.getHeading().next()));
        }

        // TODO Assignment V2
        public void turnRight (@NotNull Player player){
            player.setHeading(player.getHeading().next());
        }

        // TODO Assignment V2
        public void turnLeft (@NotNull Player player){
            player.setHeading(player.getHeading().prev());
        }

        public boolean moveCards (@NotNull CommandCardField source, @NotNull CommandCardField target){
            CommandCard sourceCard = source.getCard();
            CommandCard targetCard = target.getCard();
            if (sourceCard != null && targetCard == null) {
                target.setCard(sourceCard);
                source.setCard(null);
                return true;
            } else {
                return false;
            }

        }

    /**
     * Checks if another player is in the trajectory of the current player.
     *
     * @param player gets the player moving and in the way of the current player.
     * @param nextSpace is the space after the movement occurs.
     * @param heading gets the players heading.
     */
    public void checkForPush(@NotNull Player player, @NotNull Space nextSpace, @NotNull Heading heading) {
        try {
            Heading orig = heading;
            Player other = nextSpace.getPlayer();
            if (other != null) {
                Space target = game.getBoard().getNeighbour(other.getSpace(), orig);
                if (checkForWall(nextSpace, target, orig,other)) {
                    return;
                }

                if (target != null) {
                    checkForPush(other, target, orig);
                }
            }
            if (nextSpace.getPlayer() == null) {
                player.setSpace(nextSpace);
            }
        }catch (NullPointerException e) {
            Player player2 = nextSpace.getPlayer();
            boolean u = false;
            int j = 1;
            do {
                if (game.getBoard().getSpace(2, j).getPlayer() == null) {
                    player2.setSpace(game.getBoard().getSpace(2, j));
                    u = true;
                }else {
                    j++;
                }
            }while (u == false);
            for (int i = 0; i < 5; i++) {
                player2.getProgramField(i).setCard(null);
            }
            checkForPush(player,nextSpace,heading);
        }
        }


    /**
     *
     * @param currentSpace
     * @param nextSpace
     * @param heading
     * @return returns true if there is a wall in front of the player
     */

    private boolean checkForWall(Space currentSpace, Space nextSpace, Heading heading, Player player) throws NullPointerException {


        if (currentSpace.getWalls().contains(heading)) {
                return true;
            }

        if (nextSpace.getWalls().contains(heading.next().next())) {
                return true;
            }

        return false;
    }


        /**
         * A method called when no corresponding controller operation is implemented yet. This
         * should eventually be removed.
         */
        public void notImplemented () {
            // XXX just for now to indicate that the actual method is not yet implemented
            assert false;
        }

    /**
     * Checks if the player is out of the map (out of bounds), then teleports to specific field on the board.
     * Also "reboots" the robot, discarding current programming registers.
     *
     * @param player
     */
    public void outOfBounds ( Player player) {
            int j = 1;
            Boolean u = false;
            do {
                if (game.getBoard().getSpace(2, j).getPlayer() == null) {
                    player.setSpace(game.getBoard().getSpace(2,j));
                    u = true;
                }else {
                    j++;
                }
            }while (u == false );
            for (int i = 0; i< 5;i++) {
                player.getProgramField(i).setCard(null);
            }
        }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public Game getGame() {
            return game;
    }
}

