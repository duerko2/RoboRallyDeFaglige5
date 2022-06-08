package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Space;

public class CheckPoint extends FieldAction {


    int number;
    Space space;
    static int amountOfCheckpoints = 0;

    public CheckPoint(Space space,int number){
        this.space = space;
        this.number=number;
        amountOfCheckpoints++;
    }

    /**
     * Checks how many checkpoints a player has reached, if the player has reached all the player wins.
     * If the player has not reached all checkpoints, it decrements the counter.
     */

    @Override
    public boolean doAction(GameController gameController, Space space) {
        if(space.getPlayer().getCurrentCheckPoint() == amountOfCheckpoints-1){
            System.out.println("Spiller " + space.getPlayer().getName() + " has won!");
        }
        else if(space.getPlayer().getCurrentCheckPoint() == number-1){
            space.getPlayer().setCurrentCheckPoint(space.getPlayer().getCurrentCheckPoint() + 1);
        }
        return false;
    }
    public int getNumber(){
        return number;
    }
    public Space getSpace(){ return space; }
}
