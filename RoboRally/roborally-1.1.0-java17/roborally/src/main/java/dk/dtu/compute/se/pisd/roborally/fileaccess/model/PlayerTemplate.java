package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.*;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

public class PlayerTemplate {

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 9;

    public String name;
    public String color;


    public int x;
    public int y;
    public Heading heading = SOUTH;

    public int checkPoint;

    public boolean currentPlayer=false;

    public CardTemplate[] program = new CardTemplate[Player.NO_REGISTERS];
    public CardTemplate[] cards = new CardTemplate[Player.NO_CARDS];




}
