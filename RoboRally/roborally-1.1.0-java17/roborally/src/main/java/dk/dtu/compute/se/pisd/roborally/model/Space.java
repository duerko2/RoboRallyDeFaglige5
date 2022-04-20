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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;


/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Space extends Subject {

    public final Board board;

    public final int x;
    public final int y;

    private Player player;

    private Heading[] wall;

    private static LinkedList<LinkedList<Space>> Checkpoints = new LinkedList<LinkedList<Space>>();

    public Space(Board board, int x, int y, Heading[] wallHeading) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
        this.wall=wallHeading;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer &&
                (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }

    public Heading[] getWalls() {
        return wall;
    }

    public static void initializeCheckpoints(int amount){
        for(int i = 0; i < amount; i++){
            Checkpoints.add(new LinkedList<Space>());
        }
    }
    //LinkedList is Last-in-First-Out
    public void setCheckpoint(){
        for(int i = 0; i < Checkpoints.size(); i++) {
            Checkpoints.get(i).add(this);
        }
    }
    public void collectCheckpoint(int PlayerNumber){
        Checkpoints.get(PlayerNumber).removeFirst();
    }
    public void checkForCheckpoint(){
        if(Checkpoints.get(board.getPlayerNumber(board.getCurrentPlayer())).peekFirst() == this){
            board.getCurrentPlayer().getSpace().collectCheckpoint(board.getPlayerNumber(board.getCurrentPlayer()));
            board.getCurrentPlayer().getSpace().playerHasWon(board.getPlayerNumber(board.getCurrentPlayer()));
        }
    }
    public boolean playerHasWon(int PlayerNumber){
        if(Checkpoints.get(PlayerNumber).size() == 0){
            System.out.println("Spiller " + (PlayerNumber+1) + " har vundet!");
            return true;
        }
        return false;
    }

    public static LinkedList<LinkedList<Space>> getCheckpoints(){
        return Checkpoints;
    }


    public void setWall(Heading[] wall) {
        this.wall = wall;
    }

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

}
