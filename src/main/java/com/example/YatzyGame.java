package com.example;

/**
 * Created by OooOoOn on 27/12/2016.
 */

import java.io.IOException;
import java.lang.*;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by OooOoOn on 29/11/2016.
 */
public class YatzyGame {

    boolean mainmenu = true; // stops menu from looping when we are done with it
    boolean gameOver = false; // stops game after scorecards are full
    boolean endTurn = false; // stops player options each turn from looping
    int numberOfThrows = 0;  // makes sure player isn't throwing dices all day
    int i = 0;
    int j = 0;
    int count = 0; // checks when each player has saved to scorecard, used in all game modes
    String result; //used to check what points player got after each turn, needed for INSERT to dbo.turn

    int turnCounter = 0; //used to keep track of whether both players have finished their turn, thus increasing currentTurn
    int currentTurn = 1; //used to check which turn it is, needed for INSERT to dbo.turn

    Random rand = new Random();
    int roundID = rand.nextInt(500);  //creating random roundID

    boolean alreadyAdded = false;  //to make sure player name is only added once
    int playersAdded = 0; // keeping track of number of players
    int userInput = 0; // reused variable for replies given by players
    int gameMode = 0; // initial variable before player selects game mode
    Scanner in = new Scanner(System.in);
    Dice d = new Dice(0, "yes"); // we need dices don't we?

    //List<Player> players = new ArrayList<>();
    static Player[] players;  // array will be declared once we know amount of players
    Dice[] Dices = new Dice[5];  // array to keep all dices for loop purposes
    ScoreCard[] scoreCards;  // array containing all players score cards

    Connection conn = null; // we need a connection after all to connect to the database

    PreparedStatement pstmt = null;  //creating statement that we couldnt possibly be without
    CallableStatement cstmt = null; // callable statement used for stored procedure and transactions


    /**
     * Initial game logic, dice array etc
     *
     * @throws IOException
     */

    public void startGame() throws IOException {

        /**
         * connecting to database
         */

        try {
            conn = DriverManager.getConnection("jdbc:sqlserver://OOOOOON-PC:1433;databaseName=ECYatzy;user=jon2;password=test123");
        } catch (SQLException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < Dices.length; i++)

        {
            Dices[i] = new Dice(0, "yes");  // add all them dices
        }


        /**
         * SETTING UP THE GAME - USER CAN ENTER INFO IN PREFERRED ORDER BUT CANT START GAME WITHOUT SUBMITTING EVERYTHING FIRST
         */

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
                    System.out.println("Is it a computer player? [NOT YET IMPLEMENTED, SELECT NO]");
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

        gameOver = true;
        mainmenu = false;


        String winnerName = "";
        int winnerScore = 0;
        String looserName = "";
        int looserScore = 0;

        for (int i = 0; i < 1; i++) {
            if (players[i].getScore() > players[i + 1].getScore()) {
                winnerName = players[i].getName();
                winnerScore = players[i].getScore();
                looserName = players[i + 1].getName();
                looserScore = players[i + 1].getScore();
            } else {
                winnerName = players[i + 1].getName();
                winnerScore = players[i + 1].getScore();
                looserName = players[i].getName();
                looserScore = players[i].getScore();
            }
        }

        System.out.println("\nFinal Score: ");

        System.out.println("Winner: " + winnerName + "\nScore: " + winnerScore);
        System.out.println("Looser: " + looserName + "\nScore: " + looserScore);


        /**
         *
         *
         *
         *
         *
         *
         * GAME HAS FINISHED - DATABASE STUFF BELOW
         *
         *
         *
         *
         *
         *
         *
         *
         */


        /**
         * INSERT names to player table using callable statement and stored procedure
         */


        try {
            if (!alreadyAdded) {

                cstmt = conn.prepareCall("{call dbo.Player_SP(?)}");
                for (int i = 0; i < players.length; i++) {
                    cstmt.setString(1, players[i].getName());

                    cstmt.execute();
                }

                alreadyAdded = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        /**
         *
         *requesting both player ID's as we need them
         */


        try {

            pstmt = conn.prepareStatement("SELECT PlayerID FROM dbo.Player", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = pstmt.executeQuery();

            rs.afterLast();
            System.out.println(playersAdded);

            for (int i = (playersAdded - 1); i != -1; i--) {
                rs.previous();

                int playerID = rs.getInt("PlayerID");


                pstmt = conn.prepareStatement("INSERT INTO dbo.Listofrounds (RoundID, PlayerID, Score) VALUES (?, ?, ?)");


                pstmt.setInt(1, roundID);
                pstmt.setInt(2, playerID);
                pstmt.setInt(3, players[i].getScore());
                pstmt.executeUpdate();


            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


        /**
         * INSERT results to Round table using callable statement, stored procedure and transaction
         */

        try {


            cstmt = conn.prepareCall("{call dbo.Round_SP(?, ?, ?, ?, ?)}");
            for (int i = 0; i < players.length; i++) {

                if (i == 1) {
                    cstmt.setString(2, winnerName);
                    cstmt.setInt(5, winnerScore);
                    cstmt.setInt(1, roundID);

                    LocalDateTime currentTime = LocalDateTime.now();
                    LocalDate date1 = currentTime.toLocalDate();
                    cstmt.setDate(4, Date.valueOf(date1));

                } else {
                    cstmt.setString(3, looserName);

                }
            }

            cstmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }



        while (true) {


            System.out.println("Please choose from the following 3 options");
            System.out.println("[1] End game");
            System.out.println("[2] View URL mapped highscore");
            System.out.println("[3] SQL Connect to Database");
            userInput = in.nextInt();

            switch (userInput)

            {

                case 1:

                    System.out.println("See you soon!");
                    System.exit(0);
                    break;


                case 2: {

                    Client c = new Client();
                    c.getHighscore("http://localhost:8080/highscore");
                    break;


                }

                case 3: {


                    sqlConnect(conn);


                }
            }

        }
    }

    private void sqlConnect(Connection conn) throws IOException {


        while (gameOver) {

            System.out.println("\nPlease choose from the following 4 options");
            System.out.println("[1] View Highscore for all winners");
            System.out.println("[2] View player history");
            System.out.println("[3] View every turn for a specific round");
            System.out.println("[4] Play again");
            System.out.println("[5] End game");
            userInput = in.nextInt();

            switch (userInput) {

                case 1:

                    try {
                        System.out.println("**********HIGHSCORE**********");
                        PreparedStatement pstmt = conn.prepareStatement("SELECT Name, Score FROM dbo.Player INNER JOIN dbo.Listofrounds ON dbo.Listofrounds.PlayerID = dbo.Player.PlayerID ORDER BY Score desc");
                        ResultSet rs = pstmt.executeQuery();
                        while (rs.next()) {
                            String name = rs.getString("Name");
                            int score = rs.getInt("Score");

                            System.out.println("Name: " + name + "Score " + score);

                        }


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;

                case 2:

                    try {
                        System.out.println("Choose player history to view:");


                        System.out.println("\nLIST OF PLAYERS:");
                        PreparedStatement pstmt = conn.prepareStatement("SELECT Name FROM dbo.Player GROUP BY Name ORDER BY Name asc");
                        ResultSet rs = pstmt.executeQuery();
                        while (rs.next()) {
                            String name = rs.getString("Name");
                            System.out.println(name);

                        }


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                    System.out.println("\nWhich player would you like to view?");
                    String playerChoice = in.next().toLowerCase();


                    try {

                        System.out.println("\n**********HISTORY OF PLAYER " + playerChoice + "**********");

                        PreparedStatement pstmt = conn.prepareStatement("SELECT RoundID, Name FROM dbo.Player INNER JOIN dbo.Listofrounds ON dbo.Listofrounds.PlayerID = dbo.Player.PlayerID WHERE Name = ?");
                        pstmt.setString(1, playerChoice);
                        ResultSet rs = pstmt.executeQuery();

                        System.out.println("Round ID's:");

                        while (rs.next()) {


                            int roundID = rs.getInt("roundID");
                            System.out.println(roundID);


                        }


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                    System.out.println("\nWhich round would you like to view?");
                    int roundChoice = in.nextInt();


                    try {

                        System.out.println("\n**********Round " + roundChoice + "**********");
                        PreparedStatement pstmt = conn.prepareStatement("SELECT RoundID, Winner, Looser, Date, WinnerScore FROM dbo.Round WHERE roundID = ?");
                        pstmt.setInt(1, roundChoice);
                        ResultSet rs = pstmt.executeQuery();
                        while (rs.next()) {
                            String Winner = rs.getString("Winner");
                            String Looser = rs.getString("Looser");
                            String Date = rs.getString("Date");
                            int WinnerScore = rs.getInt("WinnerScore");
                            int roundID = rs.getInt("roundID");

                            System.out.println("Round ID: \n" + roundID);
                            System.out.println("Winner: \n" + Winner);
                            System.out.println("Looser: \n" + Looser);
                            System.out.println("Date: \n" + Date);
                            System.out.println("Winner Score: \n" + WinnerScore);


                        }


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;

                case 3:

                    System.out.println("\nWhich round would you like to see every turn of?");
                    System.out.println("\n**********List of rounds**********");

                    try {
                        pstmt = conn.prepareStatement("SELECT RoundID FROM dbo.Turn GROUP BY RoundID");
                        ResultSet rs = pstmt.executeQuery();
                        while (rs.next()) {
                            int roundID = rs.getInt("RoundID");
                            System.out.println(roundID);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    roundChoice = in.nextInt();


                    try {

                        System.out.println("\nROUND " + roundChoice + ":");
                        pstmt = conn.prepareStatement("SELECT RoundID, Name, Combination, Points, Turn, Result FROM dbo.Turn WHERE roundID = ?");
                        pstmt.setInt(1, roundChoice);
                        ResultSet rs = pstmt.executeQuery();
                        while (rs.next()) {
                            String name = rs.getString("Name");
                            String combination = rs.getString("Combination");
                            int points = rs.getInt("Points");
                            int turn = rs.getInt("Turn");
                            int roundID = rs.getInt("roundID");
                            String result = rs.getString("Result");

                            System.out.println("Turn: " + turn + " Name: " + name + " Result: " + result + " Combination: " + combination + " Points: " + points);

                        }


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;


                case 4:

                    gameOver = false;
                    System.out.println("\nRestarting!");

                    //resetting all variables so players get a clean slate

                    roundID = rand.nextInt(500);  //creating new random roundID
                    i = 0;
                    j = 0;
                    count = 0;
                    for (int i = 0; i < players.length; i++) {
                        players[i].setScore();
                        scoreCards[i].setRow1();
                    }

                    isPlaying();
                    break;


                case 5:


                    System.out.println("See you soon!");
                    System.exit(0);
                    break;


            }

        }

    }

    /**
     * @param i
     * @throws IOException ADD ROUND SCORE TO PLAYER SCORECARD - ALL GAME MODES
     */


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

            StringBuilder temp = new StringBuilder();  // needed for result after every throw


            for (Dice d : Dices) {
                temp.append(d.value);  // add all dices to result which is needed for INSERT to dbo.Turn
                if (d.value == j + 1) {
                    scoreCards[i].Row1()[j] += d.value;


                }


            }

            players[i].addPlayerScore(scoreCards[i].Row1()[j]);
            count++;

            try {

                if (turnCounter == 2) {
                    turnCounter = 0;
                    currentTurn++;
                }


                turnCounter++;
                int points = scoreCards[i].Row1()[j];
                String combination = scoreCards[i].row1Names[j];


                pstmt = conn.prepareStatement("INSERT INTO dbo.Turn (Points, Combination, Name, Result, roundID, Turn) VALUES (?, ?, ?, ?, ?, ?)");


                pstmt.setInt(1, points);
                pstmt.setString(2, combination);
                pstmt.setString(3, players[i].getName());
                pstmt.setString(4, temp.toString());
                pstmt.setInt(5, roundID);
                pstmt.setInt(6, currentTurn);

                pstmt.executeUpdate();


                temp.setLength(0);


            } catch (SQLException e) {
                e.printStackTrace();
            }


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

            players[i].addPlayerScore(scoreCards[i].Row1()[userInput - 1]);
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
