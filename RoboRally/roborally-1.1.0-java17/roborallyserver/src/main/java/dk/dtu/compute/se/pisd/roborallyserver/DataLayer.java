package dk.dtu.compute.se.pisd.roborallyserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataLayer {
    private final String GAMESFOLDER = "games";
    private final String BOARDSFOLDER = "boards";
    private final String JSON_EXT = "json";


    public String loadGame(String fileName){


        return null;
    }
    public void saveGame(String jsonString, String serialNumber){


        ClassLoader classLoader = this.getClass().getClassLoader();

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
