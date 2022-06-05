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
    private final String JSON_EXT = ".json";


    public String loadGame(String fileName){


        return null;
    }
    public void saveGame(String jsonString, String serialNumber){


        ClassLoader classLoader = this.getClass().getClassLoader();

        // File path
        String filename = classLoader.getResource(GAMESFOLDER).getPath() + "/" + serialNumber + "." + JSON_EXT;


        GsonBuilder simpleBuilder = new GsonBuilder().
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();


        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(jsonString);
            writer.close();

        } catch (IOException e1) {
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {}
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }
}
