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
import javafx.scene.control.ChoiceDialog;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
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
            board.getCurrentPlayer().setSpace(space);
            board.nextPlayer();
        }

    }


    // XXX: V2
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
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
    }

    public void startProgrammingPhase(boolean gameLoaded, Player currentPlayer){
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(currentPlayer);
        board.setStep(0);
    }

    // XXX: V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: V2
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX: V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX: V2
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX: V2
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: V2
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    // XXX: V2
    public void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    if (!card.command.getOptions().isEmpty()) {
                        board.setPhase(Phase.PLAYER_INTERACTION);

                        return;
                    }
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    private void spaceAction(Board board){
        if(board.getCurrentPlayer().getSpace().getActions().size() > 0){
            for(int i = 0; i < board.getCurrentPlayer().getSpace().getActions().size();i++){
                board.getCurrentPlayer().getSpace().getActions().get(i).doAction(this, board.getCurrentPlayer().getSpace());
            }
        }
    }
    // XXX: V2
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
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
                default:
                    // DO NOTHING (for now)
            }
            spaceAction(board);
        }
    }

    /**
     * This method executes chosen command from a command option card and continues the execution of the game.
     *
     * @param command This is the command chosen from the command option card.
     */
    public void executeCommandOptionAndContinue(Command command) {
        board.setPhase(Phase.ACTIVATION);
        executeCommand(board.getCurrentPlayer(), command);
        int step = board.getStep();
        int nextPlayerNumber = board.getPlayerNumber(board.getCurrentPlayer()) + 1;
        if (nextPlayerNumber < board.getPlayersNumber()) {
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
        } else {
            step++;
            if (step < Player.NO_REGISTERS) {
                makeProgramFieldsVisible(step);
                board.setStep(step);
                board.setCurrentPlayer(board.getPlayer(0));
            } else {
                startProgrammingPhase();
            }
        }
    }
    public void  moveOne (@NotNull Player player){
        try {


            Space nextSpace;
                move(player);

        }catch (NullPointerException e){
            outOfBounds(player);
        }


    }

    // TODO Assignment V2
    public void move(@NotNull Player player) throws NullPointerException {

            Space nextSpace;
            Space currentSpace = player.getSpace();
            switch (player.getHeading()) {
                case SOUTH:
                    nextSpace = (board.getSpace(player.getSpace().x, player.getSpace().y + 1));
                    //checkForOutOfBounds();
                    if (checkForWall(currentSpace, nextSpace, player.getHeading(),player)) {
                        return;
                    }
                    checkForPush(player, nextSpace, player.getHeading());
                    break;
                case WEST:
                    nextSpace = (board.getSpace(player.getSpace().x - 1, player.getSpace().y));
                    if (checkForWall(currentSpace, nextSpace, player.getHeading(),player)) {
                        return;
                    }
                    checkForPush(player, nextSpace, player.getHeading());
                    break;
                case NORTH:
                    nextSpace = (board.getSpace(player.getSpace().x, player.getSpace().y - 1));
                    if (checkForWall(currentSpace, nextSpace, player.getHeading(),player)) {
                        return;
                    }
                    checkForPush(player, nextSpace, player.getHeading());
                    break;
                case EAST:
                    nextSpace = (board.getSpace(player.getSpace().x + 1, player.getSpace().y));
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



        public void backUp (@NotNull Player player){
        try {
            Space nextSpace;
            Space currentSpace = player.getSpace();
            Boolean backwards = true;
            switch (player.getHeading()) {
                case SOUTH:
                    nextSpace = (board.getSpace(player.getSpace().x, player.getSpace().y - 1));
                    if (checkForWall(currentSpace, nextSpace, player.getHeading(),player)) {
                        return;
                    }
                    checkForPush(player, nextSpace, player.getHeading().next().next());
                    break;
                case WEST:
                    nextSpace = (board.getSpace(player.getSpace().x + 1, player.getSpace().y));
                    if (checkForWall(currentSpace, nextSpace, player.getHeading(),player)) {
                        return;
                    }
                    checkForPush(player, nextSpace, player.getHeading().next().next());
                    break;
                case NORTH:
                    nextSpace = (board.getSpace(player.getSpace().x, player.getSpace().y + 1));
                    if (checkForWall(currentSpace, nextSpace, player.getHeading(),player)) {
                        return;
                    }
                    checkForPush(player, nextSpace, player.getHeading().next().next());
                    break;
                case EAST:
                    nextSpace = (board.getSpace(player.getSpace().x - 1, player.getSpace().y));
                    if (checkForWall(currentSpace, nextSpace, player.getHeading(),player)) {
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
    public void checkForPush(@NotNull Player player, @NotNull Space nextSpace, @NotNull Heading heading) {
        try {
            Heading orig = heading;
            Player other = nextSpace.getPlayer();
            if (other != null) {
                Space target = board.getNeighbour(other.getSpace(), orig);
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
                if (board.getSpace(2, j).getPlayer() == null) {
                    player2.setSpace(board.getSpace(2, j));
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
        public void outOfBounds ( Player player) {
            int j = 1;
            Boolean u = false;
            do {
                if (board.getSpace(2, j).getPlayer() == null) {
                    player.setSpace(board.getSpace(2,j));
                    u = true;
                }else {
                    j++;
                }
            }while (u == false );
            for (int i = 0; i< 5;i++) {
                player.getProgramField(i).setCard(null);
            }
        }
    }

