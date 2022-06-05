package dk.dtu.compute.se.pisd.roborallyserver;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/games")
public class ServerController {

    @PutMapping
    public String putGame(@RequestBody String r){

        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "added";
    }

    @GetMapping(value = "/{id}")
    public String getGame(@PathVariable("id") String id){



        return "Board JSON file";
    }

}


