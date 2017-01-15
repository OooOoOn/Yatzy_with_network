package com.example;

/**
 * Created by OooOoOn on 27/12/2016.
 */
public class ComputerPlayer extends Player {

    private int Dicethrows;


    public ComputerPlayer(String name, int Dicethrows, String type, int finalScore) {

        super(name, type, finalScore);
        this.Dicethrows = Dicethrows;

    }
}
