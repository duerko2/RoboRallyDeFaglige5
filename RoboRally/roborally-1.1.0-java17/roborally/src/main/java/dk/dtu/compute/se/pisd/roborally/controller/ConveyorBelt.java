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

    public ConveyorBelt(Space space, Heading heading){
        this.heading = heading;
        this.space = space;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        Space nextSpace;
        switch (heading) {
            case EAST:
                nextSpace = space.board.getSpace(space.getPlayer().getSpace().x + 1, space.getPlayer().getSpace().y);
                gameController.checkForPush(space.getPlayer(), nextSpace, heading);
                break;
            case WEST:
                nextSpace = space.board.getSpace(space.getPlayer().getSpace().x - 1, space.getPlayer().getSpace().y);
                gameController.checkForPush(space.getPlayer(), nextSpace, heading);
                break;
            case NORTH:
                nextSpace = space.board.getSpace(space.getPlayer().getSpace().x, space.getPlayer().getSpace().y - 1);
                gameController.checkForPush(space.getPlayer(), nextSpace, heading);
                break;
            case SOUTH:
                nextSpace = space.board.getSpace(space.getPlayer().getSpace().x, space.getPlayer().getSpace().y + 1);
                gameController.checkForPush(space.getPlayer(), nextSpace, heading);
                break;
        }

        return false;
    }

    public Heading getHeading() {
        return heading;
    }


    public boolean getIsDouble() {
        return isDouble;
    }
}
