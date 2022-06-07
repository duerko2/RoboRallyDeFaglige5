package dk.dtu.compute.se.pisd.roborallyserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataLayer {
    private final String GAMESFOLDER = "games";
    private final String BOARDSFOLDER = "boards";
    private final String JSON_EXT = "json";
    ClassLoader classLoader = this.getClass().getClassLoader();


    public String loadGame(String serialNumber){

        String game=null;

        // File path
        String fileName = classLoader.getResource(GAMESFOLDER).getPath() + "/" + serialNumber;

        Path filePath = Path.of(fileName);

        try {
            game = Files.readString(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return game;
    }

    public String loadGames(){
        // Gets the names of all the current game files.
        URL url = classLoader.getResource(GAMESFOLDER);
        String path = url.getPath();
        File[] files = new File(path).listFiles();


        String[] names= new String[files.length];
        for(int i=0;i<files.length;i++){
            names[i]=files[i].getName();
        }
        String namesOfGames=String.join("\n", names);

        return namesOfGames;
    }

    public void saveGame(String jsonString, String serialNumber){

        // File path
        String filename = classLoader.getResource(GAMESFOLDER).getPath() + "/" + serialNumber + "." + JSON_EXT;

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filename);
            fileWriter.write(jsonString);
            fileWriter.close();

        } catch (IOException e1) {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }
}
