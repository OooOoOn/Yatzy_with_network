package com.example;

/**
 * Created by OooOoOn on 27/12/2016.
 */
public class Player {

    private String name;
    private String type;
    private int finalScore;



    public Player(String name, String type, int finalScore) {
        this.name = name;
        this.type = type;
        this.finalScore = finalScore;

    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return finalScore;
    }

    public void setScore() {

        this.finalScore = 0;
    }



    public String Type(){
        return type;
    }


    public void addPlayerScore(int score) {
        finalScore += score;
    }

}