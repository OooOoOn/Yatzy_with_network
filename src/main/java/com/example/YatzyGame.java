package com.example;

/**
 * Created by OooOoOn on 27/12/2016.
 */

import java.io.IOException;
import java.lang.*;
import java.util.Scanner;

/**
 * Created by OooOoOn on 29/11/2016.
 */
public class YatzyGame {

    boolean mainmenu = true; // stops menu from looping when we are doing with it
    boolean gameOver = false; // stops game after scorecards are full
    boolean endTurn = false; // stops player options each turn from looping
    int numberOfThrows = 0;  // makes sure player isn't throwing dices all day
    int i = 0;
    int j = 0;
    int count = 0; // checks when each player has saved to scorecard, used in all game modes

    int playersAdded = 0; // keeping track of number of players
    int userInput = 0; // reused variable for replies given by players
    int gameMode = 0; // initial variable before player selects game mode
    Scanner in = new Scanner(System.in);
    Dice d = new Dice(0, "yes"); // we need dices don't we?

    //List<Player> players = new ArrayList<>();
    static Player[] players;  // array will be declared once we know amount of players
    Dice[] Dices = new Dice[5];  // array to keep all dices for loop purposes
    ScoreCard[] scoreCards;  // array containing all players score cards


    //GAME STARTS AND LOOPS MAIN MENU

    public void startGame() throws IOException {


        for (int i = 0; i < Dices.length; i++)

        {
            Dices[i] = new Dice(0, "yes");  // add all them dices
        }

        //SETTING UP THE GAME - USER CAN ENTER INFO IN PREFERRED ORDER BUT CANT START GAME WITHOUT SUBMITTING EVERYTHING FIRST


        System.out.println("\nLet's play YATZY!\n");
        System.out.println("How many players?");
        playersAdded = in.nextInt();
        players = new Player[playersAdded];


        while (mainmenu) {
            System.out.println("***MAIN MENU***");
            if (i != playersAdded) {
                System.out.println("[1] Add player info");
            }
            if (gameMode == 0) {
                System.out.println("[2] Choose game mode");
            }
            if (i > 0 && gameMode != 0) {
                System.out.print("[3] Start game\n");

            }
            try {
                userInput = in.nextInt();
            } catch (Exception e) {

                System.out.println("You need to enter a number!");
            }

            switch (userInput) {

                case 1:

                    System.out.println("What is the player name?");
                    String playerName = in.next();
                    System.out.println("Is it a computer player?");
                    System.out.println("[1] Yes ");
                    System.out.println("[2] No ");
                    userInput = in.nextInt();

                    if (userInput == 1) {
                        System.out.println("Select difficulty level of computer player:");
                        System.out.println("[1] Easy ");
                        System.out.println("[2] Hard ");
                        userInput = in.nextInt();
                        Player computer = new ComputerPlayer(playerName, (userInput + 1), "A.I", 0); // laid ground work for adding A.I players in the future.
                        players[i] = computer;
                    } else if (userInput == 2) {
                        Player human = new humanPlayer(playerName, "human", 0);
                        players[i] = human;


                    }

                    i++;
                    break;

                case 2: {


                    System.out.println("[1] In order, top to bottom");
                    System.out.println("[2] Top half, followed by bottom half[NOT YET IMPLEMENTED]");
                    System.out.println("[3] Free for all");
                    gameMode = in.nextInt();
                    break;
                }

                //EVERY PLAYER GETS A SCORECARD WHICH IS ADDED TO AN ARRAY OF SCORECARDS

                case 3: {

                    scoreCards = new ScoreCard[playersAdded];
                    for (int i = 0; i < playersAdded; i++) {
                        ScoreCard playerCard = new ScoreCard();
                        scoreCards[i] = playerCard;
                    }
                    i = 0;
                    isPlaying();
                    break;
                }


            }

        }

    }


    //LOOPING THROUGH EACH PLAYER UNTIL SOMEONE WINS.

    public void isPlaying() throws IOException {


        while (!gameOver) {

            for (int i = 0; i < players.length; i++) {
                numberOfThrows = 0;
                endTurn = false;
                System.out.println("\n" + players[i].getName() + "'s turn!\n");
                if (players[i].Type() == "human") {

                    humanTurn(i);

                } else {

                    computerTurn();  // A.I turn will function slightly different since all their decisions are automated. (NOT IMPLEMENTED YET)
                }
            }
        }
    }

    //A.I PLAYER TURNING PROCESS

    private void computerTurn() {

        //Have left the option here to implement a computer player algoritm in the future.


    }

    //HUMAN PLAYER TURNING PROCESS

    private void humanTurn(int i) throws IOException {

        d.throwDices(Dices);

        while (endTurn == false) {
            System.out.println("Result of dice roll: ");
            for (Dice dice : Dices) {
                System.out.print(dice.value + ", ");
            }
            {
                System.out.println("\n\n[1] Save to scoreboard: ");
                if (numberOfThrows < 2) {
                    System.out.println("[2] Save dices: ");
                    System.out.println("[3] Throw all dices: ");
                    System.out.println("[4] Throw again: (keep saved dices) ");

                }

                userInput = in.nextInt();


                switch (userInput) {

                    case 1:
                        endTurn = true;
                        addScore(i);
                        break;

                    case 2:
                        int j;
                        for (j = 0; j < Dices.length; j++) {
                            System.out.println("Save dice " + (j + 1) + "? y/n");
                            String userReply = in.next();
                            if (userReply.equals("y")) {
                                Dices[j].shuffle = "no";
                            } else {
                                Dices[j].shuffle = "yes";
                            }

                        }

                    case 3:

                        for (Dice d : Dices) {
                            d.shuffle = "yes";
                        }
                        d.throwDices(Dices);
                        numberOfThrows++;
                        break;


                    case 4:

                        d.throwDices(Dices);
                        numberOfThrows++;
                        break;


                }

            }

        }

    }


    //SORT PLAYERS SO THAT THEY CAN BE DISPLAYED ASCENDING AT END OF GAME AS WELL AS THEIR SCORE

    public void sortPlayerScore() throws IOException {


        for (int i = 0; i < players.length; i++) {
            for (int j = i + 1; j < players.length; j++) {
                if (players[j].getScore() > players[i].getScore()) {
                    Player temp = players[j];
                    players[j] = players[i];
                    players[i] = temp;
                }
            }
        }


        System.out.println("\nFinal Score: ");
        for (int i = 0; i < players.length; i++) {
            System.out.println(players[i].getName() + ": " + players[i].getScore());
        }

        gameOver = true;
        mainmenu = false;

        System.out.println("Please choose from the following 2 options");
        System.out.println("[1] End game");
        System.out.println("[2] View highscore");
        userInput = in.nextInt();

        switch (userInput) {

            case 1:

                System.out.println("See you soon!");
                break;


            case 2: {

                Client c = new Client();
                c.getHighscore("http://localhost:8080/highscore");
                break;


            }
        }


    }


    //ADD TURN SCORE TO PLAYER SCORECARD - ALL GAME MODES

    public void addScore(int i) throws IOException {

        for (Dice d : Dices) {
            d.shuffle = "yes";     //to reset all dices for next throw
        }


        //GAME MODE 1 - TOP TO BOTTOM

        if (gameMode == 1) {

            if (count == playersAdded) {
                count = 0;
                j++;
            }


            for (Dice d : Dices) {
                if (d.value == j + 1) {
                    scoreCards[i].Row1()[j] += d.value;
                }
            }

            //OBS!!!! ENCAPSULATION FUNGERAR ER FÖR FINALSCORE 2 TVÅ PLATSER AV OKÄND ANLEDNING
            //players[i].FinalScore() += scoreCards[i].Row1()[j];
            players[i].addPlayerScore(scoreCards[i].Row1()[j]);
            //players[i].finalScore += scoreCards[i].Row1()[j];
            count++;


            if (j == 5 && count == playersAdded) {
                sortPlayerScore();
            }

            //GAME MODE 2 - TOP HALF, BOTTOM HALF

        } else if (gameMode == 2) {

            //NOT IMPLEMENTED YET

        }

        //GAME MODE 3 - FREE FOR ALL

        else if (gameMode == 3) {

            System.out.println("The following positions have no value saved:\n");
            for (int j = 0; j < scoreCards[i].Row1().length; j++) {
                if (scoreCards[i].Row1()[j] == 0) {
                    System.out.println("[" + (j + 1) + "]" + scoreCards[i].row1Names[j]);
                }
            }

            System.out.println("\nSelect which position to save dice value. Here is a reminder of what you got:");
            for (Dice d : Dices) {
                System.out.print(d.value + ", ");
            }

            userInput = in.nextInt();
            scoreCards[i].Row1()[userInput - 1] = userInput;

            //OBS!!!! ENCAPSULATION FUNGERAR ER FÖR FINALSCORE 2 TVÅ PLATSER AV OKÄND ANLEDNING
            //players[i].FinalScore() += scoreCards[i].Row1()[userInput - 1];
            players[i].addPlayerScore(scoreCards[i].Row1()[userInput - 1]);

            //players[i].finalScore += scoreCards[i].Row1()[userInput - 1];
            System.out.println("\nSaved!");

            count++;

            if (count == (6 * playersAdded)) {
                sortPlayerScore();
            }
        }


    }


    //PRINTING EVERYONE'S CURRENT SCORE TO KEEP TRACK - NOT IMPLEMENTED YET

    public void printScore() {

        System.out.println("\nCurrent ranking: ");
        for (Player p : players) {
            System.out.println(p.getName() + ": " + p.getScore());
        }
    }


}
