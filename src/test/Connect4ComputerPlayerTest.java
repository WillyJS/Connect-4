package test;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import core.Connect4ComputerPlayer;
import core.Connect4;

/**
 * Unit testing for Connect4ComputerPlayer
 * @author William J Sanchez
 * @version 1.0, Feb, 2020
 */
public class Connect4ComputerPlayerTest {

    private static Connect4ComputerPlayer computerPlayer;
    private static Connect4 game;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        computerPlayer = new Connect4ComputerPlayer(game);
        game = new Connect4();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        game = null;
        computerPlayer = null;
    }

    @Test
    public void testCompPlay() {

        for (int i = 0; i < 1000; i++){
            int move = computerPlayer.sendcompMove();

            assertTrue(move >=1 && move <=7);
        }
    }

    @Test
    public void testSendCompMove() {
        for (int i = 0; i < 50; i++) {
            int num = computerPlayer.sendcompMove();
            assertTrue(num > 0 && num < 8);
        }
    }
}