package dk.dtu.compute.se.pisd.roborallyserver;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/games")
public class ServerController {

    private ServiceLayer serviceLayer = new ServiceLayer();

    @PutMapping(value="/{id}")
    public String putGame(@RequestBody String r,@PathVariable("id") String id){
        serviceLayer.putGame(r,id);


        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "added";
    }


    /**
     *
     * @return names of games on the server.
     */
    @GetMapping()
    public String getGames(){
        String games = serviceLayer.getGames();
        return games;
    }

    @GetMapping(value = "/{id}")
    public String getGame(@PathVariable("id") String id){



        return "Board JSON file";
    }

}


