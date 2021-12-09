package core;

import java.util.Random;

/**
 * Below is a class called Connect4ComputerPlayer.
 * It generates the moves for the computer player.
 * It does this by creating a random object within our bounds
 *
 * @author William J Sanchez
 * @version 2.0 Feb 15,2020.
 */
public class Connect4ComputerPlayer {

    private Connect4 game;

    /**
     * Default constructor
     */
    public Connect4ComputerPlayer() {
        //empty
    }
    public Connect4ComputerPlayer(Connect4 compGame) {
        this.game = compGame;
    }

    /**
     * Randomly generates a number within our domain
     */
    public void compPlay() {
        int compMove = 0;
        Random rand = new Random();
        while (!game.inBound(compMove)) {
            compMove = 1 + rand.nextInt(8);
        }
        game.dropPiece('O', compMove);
    }

    public int sendcompMove() {
        Random random = new Random();
        int nextcompMove = 1 + random.nextInt(7);
        return nextcompMove;
    }
}

