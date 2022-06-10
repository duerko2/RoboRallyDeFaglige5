package dk.dtu.compute.se.pisd.roborally.springRequest;

import com.google.gson.Gson;
import dk.dtu.compute.se.pisd.roborally.model.Board;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GameClient {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Method to get a saved game from the server, uses the saved game's serialnumber and creates a http GET request.
     * The game is returned to the client as a string..
     */

    public static String getGame(String serialNumber) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/games/"+serialNumber))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
        return result;
    }

    /**
     * Method to return all names of games on the server using a http GET request.
     */

    public static String getGames() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/games"))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
        return result;
    }

    /**
     * Method to save game to the server, uses the saved game's serialnumber, and the json file as a string and creates a http PUT request.
     * The game is returned to the server as a string.
     */

    public static String putGame(String serialNumber, String jsonString) throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(jsonString))
                .uri(URI.create("http://localhost:8080/games/"+serialNumber))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
        System.out.println(result);

        return result;
    }

    public static String getBoards() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(" http://localhost:8080/boards/"))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);

        return result;
    }



}
