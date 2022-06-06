package dk.dtu.compute.se.pisd.roborallyserver;

public class ServiceLayer {
    DataLayer dataLayer = new DataLayer();
    public ServiceLayer(){}


    public void putGame(String r, String id) {
        dataLayer.saveGame(r,id);
    }

    public String getGames() {
        return dataLayer.loadGames();
    }
}
