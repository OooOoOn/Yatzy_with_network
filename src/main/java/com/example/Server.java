package com.example;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




@RestController
public class Server {

    String test = "jon";

    @RequestMapping("/highscore")
    public String highScore() {


        return "hello jon!!!";

    }

}
















/*@RestController
public class Server {



    @RequestMapping("/highscore")
    public Player[] highScore() {


        return YatzyGame.players;

    }

}*/


