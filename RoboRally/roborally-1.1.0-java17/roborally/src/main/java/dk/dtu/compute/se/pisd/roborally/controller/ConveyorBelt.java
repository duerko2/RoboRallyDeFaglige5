package dk.dtu.compute.se.pisd.roborally.controller;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import static dk.dtu.compute.se.pisd.roborally.model.Heading.*;
import dk.dtu.compute.se.pisd.roborally.model.Board;



public class ConveyorBelt extends FieldAction{
    
    Heading heading;
    Space space;
    boolean isDouble;

    public ConveyorBelt(Space space, Heading heading, boolean isDouble){
        this.heading = heading;
        this.space = space;
        this.isDouble = isDouble;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        Space nextSpace;
        switch (heading) {
            case EAST:
                if(isDouble) {
                    for(int i = 0; i < 2; i++) {
                        nextSpace = space.board.getSpace(space.getPlayer().getSpace().x + 1,space.getPlayer().getSpace().y);
                        gameController.checkForPush(space.getPlayer(), nextSpace, heading);
                        space = nextSpace;
                    }
                } else{
                    nextSpace = space.board.getSpace(space.getPlayer().getSpace().x + 1, space.getPlayer().getSpace().y);
                    gameController.checkForPush(space.getPlayer(), nextSpace, heading);
                }
                break;
            case WEST:
                if(isDouble) {
                    for (int i = 0; i < 2; i++){
                        nextSpace = space.board.getSpace(space.getPlayer().getSpace().x - 1, space.getPlayer().getSpace().y);
                        gameController.checkForPush(space.getPlayer(), nextSpace, heading);
                        space = nextSpace;
                    }
                } else{
                    nextSpace = space.board.getSpace(space.getPlayer().getSpace().x - 1, space.getPlayer().getSpace().y);
                    gameController.checkForPush(space.getPlayer(), nextSpace, heading);
                }
                break;
            case NORTH:
                if(isDouble) {
                    for(int i = 0; i < 2; i++) {
                        nextSpace = space.board.getSpace(space.getPlayer().getSpace().x, space.getPlayer().getSpace().y - 1);
                        gameController.checkForPush(space.getPlayer(), nextSpace, heading);
                        space = nextSpace;
                    }
                }else{
                    nextSpace = space.board.getSpace(space.getPlayer().getSpace().x, space.getPlayer().getSpace().y - 1);
                    gameController.checkForPush(space.getPlayer(), nextSpace, heading);
                }
                break;
            case SOUTH:
                if(isDouble) {
                    for(int i = 0; i < 2; i++) {
                        nextSpace = space.board.getSpace(space.getPlayer().getSpace().x, space.getPlayer().getSpace().y + 1);
                        gameController.checkForPush(space.getPlayer(), nextSpace, heading);
                        space = nextSpace;
                    }
                }else{
                    nextSpace = space.board.getSpace(space.getPlayer().getSpace().x, space.getPlayer().getSpace().y + 1);
                    gameController.checkForPush(space.getPlayer(), nextSpace, heading);
                }
                break;
        }

        return false;
    }
    public Heading getHeading(){
        return this.heading;
    }
    public boolean getisDouble(){
        return isDouble;
    }
}
