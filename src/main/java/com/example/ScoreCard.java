package com.example;

/**
 * Created by OooOoOn on 27/12/2016.
 */
public class ScoreCard {


    public int[] row1 = new int[]{0, 0, 0, 0, 0, 0};
    String[] row1Names = new String[]{"ones", "twos", "threes", "fours", "fives", "sixes"};


    public ScoreCard(){


    }

    public int[] Row1(){

        return row1;
    }

    public void setRow1() {

        for(int i = 0; i < row1.length; i++){
            this.row1[i] = 0;
        }
    }






}
