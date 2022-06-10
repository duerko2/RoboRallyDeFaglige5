package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public abstract class FieldAction {

    public  abstract boolean doAction(GameController gameController, Space space);

}

