package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

public class PlayerTemplate {

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;

    public Board board;

    public String name;
    public String color;

    public Space space;
    public Heading heading = SOUTH;


}
