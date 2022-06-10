package dk.dtu.compute.se.pisd.roborally.model;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;


public class  ConveyorBelt extends FieldAction{
    
    Heading heading;
    Space space;
    boolean isDouble;

    public ConveyorBelt(Space space, Heading heading, boolean isDouble){
        this.heading = heading;
        this.space = space;
        this.isDouble = isDouble;
    }

    /**
     * Conveyor belt functionality, uses heading and position to decide where a player is placed.
     */

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
    public boolean getIsDouble(){
        return isDouble;
    }
}
