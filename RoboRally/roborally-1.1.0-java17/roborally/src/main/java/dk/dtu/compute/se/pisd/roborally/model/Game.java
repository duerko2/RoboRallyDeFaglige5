package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

public class Game extends Subject {
    String serialNumber;
    int maxAmountOfPlayers;
    boolean readyToReceivePlayers;
    Board board;
    int winner=-1;

    public Game(Board board, String serialNumber, int maxAmountOfPlayers, boolean readyToReceivePlayers){
        this.board = board;
        this.serialNumber = serialNumber;
        this.maxAmountOfPlayers = maxAmountOfPlayers;
        this.readyToReceivePlayers = readyToReceivePlayers;
    }

    //Setters
    public void setBoard(Board board){
        this.board = board;
        notifyChange();
    }
    public void setSerialNumber(String serialNumber){
        this.serialNumber = serialNumber;
        notifyChange();
    }
    public void setReadyToReceivePlayers(boolean bool){
        this.readyToReceivePlayers = bool;
        notifyChange();
    }
    public void setMaxAmountOfPlayers(int maxAmountOfPlayers){
        this.maxAmountOfPlayers = maxAmountOfPlayers;
    }

    //Getters
    public Board getBoard(){
        return this.board;
    }
    public String getSerialNumber(){
        return this.serialNumber;
    }
    public int getMaxAmountOfPlayers(){
        return this.maxAmountOfPlayers;
    }
    public boolean getReadyToReceivePlayers(){
        return this.readyToReceivePlayers;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public int getWinner() {
        return winner;
    }
}
