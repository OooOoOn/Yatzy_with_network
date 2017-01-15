package com.example;

import java.util.Random;

/**
 * Created by OooOoOn on 27/12/2016.
 */
public class Dice {

    int value;
    String shuffle = null;


    public Dice(int value, String shuffle) {

        this.value = value;
        this.shuffle = shuffle;

    }

    public void throwDices(Dice[] dices) {


        for (int i = 0; i < 5; i++) {
            Random rand = new Random();
            if (dices[i].shuffle == "yes")
                dices[i].value = rand.nextInt(6) + 1;

        }
    }
}
