package dk.dtu.compute.se.pisd.roborally.model;

public class Game {
    int serialNumber;
    int maxAmountOfPlayers;
    boolean readyToReceivePlayers;
    Board board;

    public Game(Board board, int serialNumber, int maxAmountOfPlayers, boolean readyToReceivePlayers){
        this.board = board;
        this.serialNumber = serialNumber;
        this.maxAmountOfPlayers = maxAmountOfPlayers;
        this.readyToReceivePlayers = readyToReceivePlayers;
    }

    //Setters
    public void setBoard(Board board){
        this.board = board;
    }
    public void setSerialNumber(int serialNumber){
        this.serialNumber = serialNumber;
    }
    public void setReadyToReceivePlayers(boolean bool){
        this.readyToReceivePlayers = bool;
    }
    public void setMaxAmountOfPlayers(int maxAmountOfPlayers){
        this.maxAmountOfPlayers = maxAmountOfPlayers;
    }

    //Getters
    public Board getBoard(){
        return this.board;
    }
    public int getSerialNumber(){
        return this.serialNumber;
    }
    public int getMaxAmountOfPlayers(){
        return this.maxAmountOfPlayers;
    }
    public boolean getReadyToReceivePlayers(){
        return this.readyToReceivePlayers;
    }
}
