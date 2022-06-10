package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Phase;

public class GameTemplate {
    public String serialNumber;
    public int maxAmountOfPlayers;
    public boolean readyToReceivePlayers;
    public int gameWinner;

    public BoardTemplate board;
}
