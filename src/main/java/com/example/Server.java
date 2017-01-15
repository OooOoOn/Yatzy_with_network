package com.example;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * Created by OooOoOn on 27/12/2016.
 */

@RestController
public class Server {



    @RequestMapping("/highscore")
    public Player[] highScore() {

        return YatzyGame.players;
    }

}
