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
import org.jetbrains.annotations.NotNull;

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
            board.setStep(board.getStep() + 1);
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
    private void executeNextStep() {
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

    // XXX: V2
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).
            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    board.getCurrentPlayer().getSpace().checkForCheckpoint();
                    break;
                case RIGHT:
                    this.turnRight(player);
                    board.getCurrentPlayer().getSpace().checkForCheckpoint();
                    break;
                case LEFT:
                    this.turnLeft(player);
                    board.getCurrentPlayer().getSpace().checkForCheckpoint();
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    board.getCurrentPlayer().getSpace().checkForCheckpoint();
                    break;
                case OPTION_LEFT_RIGHT:
                    executeNextStep();
                    /*ChoiceDialog<Command> dialog = new ChoiceDialog<>(command.getOptions().get(0),command.getOptions());
                    dialog.setTitle("Choose command");
                    dialog.setHeaderText("Select command left or right");
                    Optional<Command> chosenCommand=dialog.showAndWait();
                    executeCommand(player,chosenCommand.get());
                     */
                    board.getCurrentPlayer().getSpace().checkForCheckpoint();
                    break;
                case U_TURN:
                    this.uTurn(player);
                    board.getCurrentPlayer().getSpace().checkForCheckpoint();
                    break;
                case MOVE_THREE:
                    this.moveThree(player);
                    board.getCurrentPlayer().getSpace().checkForCheckpoint();
                    break;
                case BACK_UP:
                    this.backUp(player);
                    board.getCurrentPlayer().getSpace().checkForCheckpoint();
                    break;
                default:
                    // DO NOTHING (for now)
            }
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
        continuePrograms();
    }

    // TODO Assignment V2
    public void moveForward(@NotNull Player player) {
        Space nextSpace;
        Space currentScape = player.getSpace();
        switch (player.getHeading()) {
            case SOUTH:
                nextSpace = (board.getSpace(player.getSpace().x, player.getSpace().y + 1));
                checkForPush(player, nextSpace, player.getHeading());
                break;
            case WEST:
                nextSpace = (board.getSpace(player.getSpace().x - 1, player.getSpace().y));
                checkForPush(player, nextSpace, player.getHeading());
                break;
            case NORTH:
                nextSpace = (board.getSpace(player.getSpace().x, player.getSpace().y - 1));
                checkForPush(player, nextSpace, player.getHeading());
                break;
            case EAST:
                nextSpace = (board.getSpace(player.getSpace().x + 1, player.getSpace().y));
                checkForPush(player, nextSpace, player.getHeading());
                break;
        }
    }

    // TODO Assignment V2
    public void fastForward(@NotNull Player player) {
        Space nextSpace;
        Space currentScape = player.getSpace();
        for (int i = 0; i < 2; i++) {
            switch (player.getHeading()) {
                case SOUTH:
                    nextSpace = (board.getSpace(player.getSpace().x, player.getSpace().y + 1));
                    checkForPush(player, nextSpace, player.getHeading());
                    break;
                case WEST:
                    nextSpace = (board.getSpace(player.getSpace().x - 1, player.getSpace().y));
                    checkForPush(player, nextSpace, player.getHeading());
                    break;
                case NORTH:
                    nextSpace = (board.getSpace(player.getSpace().x, player.getSpace().y - 1));
                    checkForPush(player, nextSpace, player.getHeading());
                    break;
                case EAST:
                    nextSpace = (board.getSpace(player.getSpace().x + 1, player.getSpace().y));
                    checkForPush(player, nextSpace, player.getHeading());
                    break;
            }
        }

    }

    public void moveThree(@NotNull Player player) {
        Space nextSpace;
        Space currentScape = player.getSpace();

        for (int i = 0; i < 3; i++) {
                switch (player.getHeading()) {
                    case SOUTH:
                        nextSpace = (board.getSpace(player.getSpace().x, player.getSpace().y + 1));
                        checkForPush(player, nextSpace, player.getHeading());
                        break;
                    case WEST:
                        nextSpace = (board.getSpace(player.getSpace().x - 1, player.getSpace().y));
                        checkForPush(player, nextSpace, player.getHeading());
                        break;
                    case NORTH:
                        nextSpace = (board.getSpace(player.getSpace().x, player.getSpace().y - 1));
                        checkForPush(player, nextSpace, player.getHeading());
                        break;
                    case EAST:
                        nextSpace = (board.getSpace(player.getSpace().x + 1, player.getSpace().y));
                        checkForPush(player, nextSpace, player.getHeading());
                        break;
                }
            }
        }

        public void backUp (@NotNull Player player){
        Space nextSpace;
        Boolean backwards = true;
            switch (player.getHeading()) {
                case SOUTH:
                    nextSpace = (board.getSpace(player.getSpace().x, player.getSpace().y -1));
                    checkForPush(player, nextSpace,Heading.NORTH, backwards);
                    break;
                case WEST:
                    nextSpace = (board.getSpace(player.getSpace().x + 1, player.getSpace().y));
                    checkForPush(player, nextSpace, Heading.EAST, backwards);
                    break;
                case NORTH:
                    nextSpace = (board.getSpace(player.getSpace().x, player.getSpace().y + 1));
                    checkForPush(player, nextSpace, Heading.SOUTH, backwards);
                    break;
                case EAST:
                    nextSpace = (board.getSpace(player.getSpace().x - 1, player.getSpace().y));
                    checkForPush(player, nextSpace, Heading.WEST, backwards);
                    break;
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
    public void checkForPush(@NotNull Player player, @NotNull Space space, @NotNull Heading heading) {
        Heading orig = heading;
        Player other = space.getPlayer();
        if (other != null) {
            Space target = board.getNeighbour(other.getSpace(),orig);
            if (target != null) {
                    checkForPush(other, target, player.getHeading());

                }
            }

        player.setSpace(space);
    }
    public void checkForPush(@NotNull Player player, @NotNull Space space, @NotNull Heading heading, boolean backwards) {
        Player other = space.getPlayer();
        if (other != null) {
            Space target = board.getNeighbour(other.getSpace(),player.getHeading().next().next());
            if (target != null) {

                        checkForPush(other, target,player.getHeading().next().next(),backwards);





            }
        }
        player.setSpace(space);
    }


        /**
         * A method called when no corresponding controller operation is implemented yet. This
         * should eventually be removed.
         */
        public void notImplemented () {
            // XXX just for now to indicate that the actual method is not yet implemented
            assert false;
        }

    }

