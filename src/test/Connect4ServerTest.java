package test;

import static org.junit.Assert.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import core.Connect4Server;
import core.Connect4Constants;
import core.Connect4Server.HandleSession;

/**
 * Unit testing for Connect4Server
 * @author William J Sanchez
 * @version 1.0, Feb, 2020
 */
public class Connect4ServerTest implements Connect4Constants {

    private static Thread thread;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        new Connect4Server().launchServer(false, 8300);
    }

    @Test
    public void testStage() {

        thread = new Thread() {

            public void run() {
                try {
                    Socket testClient = new Socket("localhost", 8300);

                    DataOutputStream toServerStream = new DataOutputStream(testClient.getOutputStream());
                    DataInputStream fromServerStream = new DataInputStream(testClient.getInputStream());

                    toServerStream.writeInt(COMPUTER);
                    fromServerStream.readInt();

                    Socket testClient1 = new Socket("localhost", 8300);
                    DataOutputStream toServerStream1 = new DataOutputStream(testClient1.getOutputStream());
                    DataInputStream fromServerStream1 = new DataInputStream(testClient1.getInputStream());
                    toServerStream1.writeInt(ALTERNATE_PLAYER);
                    fromServerStream1.readInt();

                    testClient.close();
                    testClient1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    public static class HandleSessionTest implements Connect4Constants {

        private static Thread thread1, thread2, thread3;

        private static HandleSession handleSessionTest, handleSessionTest1, handleSessionTest2, handleSessionTest3,
        handleSessionTest4, handleSessionTest5, handleSessionTest6, hS8;

        @BeforeClass
        public static void setUpBeforeClass() throws Exception {
            handleSessionTest = (new Connect4Server()).new HandleSession(new Socket());
            handleSessionTest1 = (new Connect4Server()).new HandleSession(new Socket());//, new Socket());
            handleSessionTest2 = (new Connect4Server()).new HandleSession(new Socket());
            handleSessionTest3 = (new Connect4Server()).new HandleSession(new Socket());
            handleSessionTest4 = (new Connect4Server()).new HandleSession(new Socket());
            handleSessionTest5 = (new Connect4Server()).new HandleSession(new Socket());
            handleSessionTest6 = (new Connect4Server()).new HandleSession(new Socket());
//            hS8 = (new Connect4Server()).new HandleSession(new Socket());
        }

        @AfterClass
        public static void tearDownAfterClass() throws Exception {
            handleSessionTest = null;
            handleSessionTest1 = null;
            handleSessionTest2 = null;
            handleSessionTest3 = null;
            handleSessionTest4 = null;
            handleSessionTest5 = null;
            handleSessionTest6 = null;
//            hS8 = null;
        }

        @After
        public void clearThreads() {
            thread1 = null;
            thread2 = null;
            thread3 = null;
        }

        @Test
        public void testinBound() {
            int column = 0;
            assertFalse(handleSessionTest.inBounds(column));

            column = 9;
            assertFalse(handleSessionTest.inBounds(column));

            handleSessionTest.getGameBoard()[0][2] = 'X';
            handleSessionTest.getGameBoard()[1][2] = 'X';
            handleSessionTest.getGameBoard()[2][2] = 'X';
            handleSessionTest.getGameBoard()[3][2] = 'X';
            handleSessionTest.getGameBoard()[4][2] = 'X';
            handleSessionTest.getGameBoard()[5][2] = 'X';
            column = 3;
            assertFalse(handleSessionTest.inBounds(column));

            column = 5;
            assertTrue(handleSessionTest.inBounds(column));
        }

        @Test
        public void testCheckWin() {
            assertFalse(handleSessionTest1.checkWin());

            handleSessionTest1.getGameBoard()[1][1] = 'X';
            handleSessionTest1.getGameBoard()[1][2] = 'X';
            handleSessionTest1.getGameBoard()[1][3] = 'X';
            handleSessionTest1.getGameBoard()[1][4] = 'X';
            assertTrue(handleSessionTest1.checkWin());

            handleSessionTest2.getGameBoard()[2][3] = 'O';
            handleSessionTest2.getGameBoard()[3][3] = 'O';
            handleSessionTest2.getGameBoard()[4][3] = 'O';
            handleSessionTest2.getGameBoard()[5][3] = 'O';
            assertTrue(handleSessionTest2.checkWin());

            handleSessionTest3.getGameBoard()[4][3] = 'X';
            handleSessionTest3.getGameBoard()[3][4] = 'X';
            handleSessionTest3.getGameBoard()[2][5] = 'X';
            handleSessionTest3.getGameBoard()[1][6] = 'X';
            assertTrue(handleSessionTest3.checkWin());

            handleSessionTest4.getGameBoard()[5][5] = 'X';
            handleSessionTest4.getGameBoard()[4][4] = 'X';
            handleSessionTest4.getGameBoard()[3][3] = 'X';
            handleSessionTest4.getGameBoard()[2][2] = 'X';
            assertTrue(handleSessionTest4.checkWin());

            handleSessionTest5.getGameBoard()[4][4] = 'X';
            handleSessionTest5.getGameBoard()[3][4] = 'X';
            handleSessionTest5.getGameBoard()[2][4] = 'X';
            assertFalse(handleSessionTest5.checkWin());
        }

        @Test
        public void testDrawCheck() {
            assertFalse(handleSessionTest6.drawCheck());

            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 7; j++) {
                    handleSessionTest6.getGameBoard()[i][j] = 'X';
                }
            }
            handleSessionTest6.getGameBoard()[0][6] = ' ';
            assertFalse(handleSessionTest6.drawCheck());

            handleSessionTest6.getGameBoard()[0][6] = 'X';
            assertTrue(handleSessionTest6.drawCheck());
        }

        @Test
        public void testPlayer1Wins() {
            thread2 = new Thread(() -> {
                try {
                    Socket dummyP1 = new Socket("localhost", 8010);
                    DataInputStream p1FromServer = new DataInputStream(dummyP1.getInputStream());
                    DataOutputStream p1ToServer = new DataOutputStream(dummyP1.getOutputStream());
                    p1FromServer.readInt();
                    p1ToServer.writeInt(0);
                    boolean firstTry = p1FromServer.readBoolean();
                    assertFalse(firstTry);
                    p1ToServer.writeInt(1);
                    boolean secondTry = p1FromServer.readBoolean();
                    assertTrue(secondTry);
                    String expectedGame =
                                    "| | | | | | | |\n" +
                                    "| | | | | | | |\n" +
                                    "|X| | | | | | |\n" +
                                    "|X| | | | | | |\n" +
                                    "|X| | | | | | |\n" +
                                    "|X| | | | | | |\n";
                    String actualGame = p1FromServer.readUTF();
                    assertEquals(expectedGame, actualGame);
                    int readCondition = p1FromServer.readInt();
                    assertEquals(Player1_WINS, readCondition);
                    String actualGame2 = p1FromServer.readUTF();
                    assertEquals(expectedGame, actualGame2);
                    dummyP1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread2.start();
        }

        @Test
        public void testRunCase3() {

            thread2 = new Thread( () -> {
                try {
                    Socket dummyP1 = new Socket("localhost", 8001);
                    DataInputStream p1FromServer = new DataInputStream(dummyP1.getInputStream());
                    DataOutputStream p1ToServer = new DataOutputStream(dummyP1.getOutputStream());
                    p1FromServer.readInt();
                    p1ToServer.writeInt(1);
                    boolean firstTry = p1FromServer.readBoolean();
                    assertTrue(firstTry);
                    String expectedGame =
                                    "|X|O|X|O|X|O|O|\n" +
                                    "|X|O|X|O|X|O|X|\n" +
                                    "|O|X|O|X|O|X|O|\n" +
                                    "|O|X|O|X|O|X|X|\n" +
                                    "|X|O|X|O|X|O|O|\n" +
                                    "|X|O|X|O|X|O|X|\n";
                    String actualGame = p1FromServer.readUTF();
                    assertEquals(expectedGame, actualGame);
                    int readCondition = p1FromServer.readInt();
                    assertEquals(DRAW, readCondition);
                    String actualGame2 = p1FromServer.readUTF();
                    assertEquals(expectedGame, actualGame2);
                    dummyP1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread2.start();
        }

        @Test
        public void testRunCase4_5() {
            thread1 = new Thread( () -> {
                try {
                    ServerSocket testServer = new ServerSocket(8002);
                    Socket dummyClient1 = testServer.accept();
                    Socket dummyClient2 = testServer.accept();
                    HandleSession hS9 = (new Connect4Server()).new HandleSession(dummyClient1, dummyClient2);
                    hS9.getGameBoard()[5][0] = 'O';
                    hS9.getGameBoard()[4][0] = 'O';
                    hS9.getGameBoard()[3][0] = 'O';
                    hS9.run();
                    testServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread1.start();

            thread2 = new Thread( () -> {
                try {
                    Socket dummyP1 = new Socket("localhost", 8002);
                    DataInputStream p1FromServer = new DataInputStream(dummyP1.getInputStream());
                    DataOutputStream p1ToServer = new DataOutputStream(dummyP1.getOutputStream());
                    p1FromServer.readInt();
                    p1ToServer.writeInt(2);
                    boolean firstTry = p1FromServer.readBoolean();
                    assertTrue(firstTry);
                    String expectedGame =
                                    "| | | | | | | |\n" +
                                    "| | | | | | | |\n" +
                                    "| | | | | | | |\n" +
                                    "|O| | | | | | |\n" +
                                    "|O| | | | | | |\n" +
                                    "|O|X| | | | | |\n";
                    String actualGame = p1FromServer.readUTF();
                    assertEquals(expectedGame, actualGame);
                    int readCondition = p1FromServer.readInt();
                    assertEquals(Player2_WINS, readCondition);
                    int readRow = p1FromServer.readInt();
                    int readCol = p1FromServer.readInt();
                    assertEquals(2, readRow);
                    assertEquals(0, readCol);
                    String expectedGame2 =
                                    "| | | | | | | |\n" +
                                    "| | | | | | | |\n" +
                                    "|O| | | | | | |\n" +
                                    "|O| | | | | | |\n" +
                                    "|O| | | | | | |\n" +
                                    "|O|X| | | | | |\n";
                    String actualGame2 = p1FromServer.readUTF();
                    assertEquals(expectedGame2, actualGame2);
                    dummyP1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread2.start();

            thread3 = new Thread( () -> {
                try {
                    Socket dummyP2 = new Socket("localhost", 8002);
                    DataInputStream p2FromServer = new DataInputStream(dummyP2.getInputStream());
                    DataOutputStream p2ToServer = new DataOutputStream(dummyP2.getOutputStream());
                    int readCondition = p2FromServer.readInt();
                    assertEquals(CONTINUE, readCondition);
                    int readRow = p2FromServer.readInt();
                    int readCol = p2FromServer.readInt();
                    assertEquals(5, readRow);
                    assertEquals(1, readCol);
                    String expectedGame =
                                    "| | | | | | | |\n" +
                                    "| | | | | | | |\n" +
                                    "| | | | | | | |\n" +
                                    "|O| | | | | | |\n" +
                                    "|O| | | | | | |\n" +
                                    "|O|X| | | | | |\n";
                    String readGame = p2FromServer.readUTF();
                    assertEquals(expectedGame, readGame);
                    p2ToServer.writeInt(0);
                    boolean firstTry = p2FromServer.readBoolean();
                    assertFalse(firstTry);
                    p2ToServer.writeInt(1);
                    boolean secondTry = p2FromServer.readBoolean();
                    assertTrue(secondTry);
                    String expectedGame2 =
                                    "| | | | | | | |\n" +
                                    "| | | | | | | |\n" +
                                    "|O| | | | | | |\n" +
                                    "|O| | | | | | |\n" +
                                    "|O| | | | | | |\n" +
                                    "|O|X| | | | | |\n";
                    String readGame2 = p2FromServer.readUTF();
                    assertEquals(expectedGame2, readGame2);
                    int readCondition2 = p2FromServer.readInt();
                    assertEquals(Player2_WINS, readCondition2);
                    String readGame3 = p2FromServer.readUTF();
                    assertEquals(expectedGame2, readGame3);
                    dummyP2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread3.start();
        }

        @Test
        public void testRunCase6() {
            thread1 = new Thread( () -> {
                try {
                    ServerSocket testServer = new ServerSocket(8003);
                    Socket dummyClient1 = testServer.accept();
                    Socket dummyClient2 = testServer.accept();
                    HandleSession hS9 = (new Connect4Server()).new HandleSession(dummyClient1, dummyClient2);
                    hS9.getGameBoard()[0][0] = ' '; hS9.getGameBoard()[0][1] = ' '; hS9.getGameBoard()[0][2] = 'X'; hS9.getGameBoard()[0][3] = 'O';
                    hS9.getGameBoard()[0][4] = 'X'; hS9.getGameBoard()[0][5] = 'O'; hS9.getGameBoard()[0][6] = 'O'; hS9.getGameBoard()[1][0] = 'X';
                    hS9.getGameBoard()[1][1] = 'O'; hS9.getGameBoard()[1][2] = 'X'; hS9.getGameBoard()[1][3] = 'O'; hS9.getGameBoard()[1][4] = 'X';
                    hS9.getGameBoard()[1][5] = 'O'; hS9.getGameBoard()[1][6] = 'X'; hS9.getGameBoard()[2][0] = 'O'; hS9.getGameBoard()[2][1] = 'X';
                    hS9.getGameBoard()[2][2] = 'O'; hS9.getGameBoard()[2][3] = 'X'; hS9.getGameBoard()[2][4] = 'O'; hS9.getGameBoard()[2][5] = 'X';
                    hS9.getGameBoard()[2][6] = 'O'; hS9.getGameBoard()[3][0] = 'O'; hS9.getGameBoard()[3][1] = 'X'; hS9.getGameBoard()[3][2] = 'O';
                    hS9.getGameBoard()[3][3] = 'X'; hS9.getGameBoard()[3][4] = 'O'; hS9.getGameBoard()[3][5] = 'X'; hS9.getGameBoard()[3][6] = 'X';
                    hS9.getGameBoard()[4][0] = 'X'; hS9.getGameBoard()[4][1] = 'O'; hS9.getGameBoard()[4][2] = 'X'; hS9.getGameBoard()[4][3] = 'O';
                    hS9.getGameBoard()[4][4] = 'X'; hS9.getGameBoard()[4][5] = 'O'; hS9.getGameBoard()[4][6] = 'O'; hS9.getGameBoard()[5][0] = 'X';
                    hS9.getGameBoard()[5][1] = 'O'; hS9.getGameBoard()[5][2] = 'X'; hS9.getGameBoard()[5][3] = 'O'; hS9.getGameBoard()[5][4] = 'X';
                    hS9.getGameBoard()[5][5] = 'O'; hS9.getGameBoard()[5][6] = 'X';
                    hS9.run();
                    testServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread1.start();

            thread2 = new Thread( () -> {
                try {
                    Socket dummyP1 = new Socket("localhost", 8003);
                    DataInputStream p1FromServer = new DataInputStream(dummyP1.getInputStream());
                    DataOutputStream p1ToServer = new DataOutputStream(dummyP1.getOutputStream());
                    p1FromServer.readInt();
                    p1ToServer.writeInt(1);
                    boolean firstTry = p1FromServer.readBoolean();
                    assertTrue(firstTry);
                    String expectedGame =
                                    "|X| |X|O|X|O|O|\n" +
                                    "|X|O|X|O|X|O|X|\n" +
                                    "|O|X|O|X|O|X|O|\n" +
                                    "|O|X|O|X|O|X|X|\n" +
                                    "|X|O|X|O|X|O|O|\n" +
                                    "|X|O|X|O|X|O|X|\n";
                    String actualGame = p1FromServer.readUTF();
                    assertEquals(expectedGame, actualGame);
                    int readCondition = p1FromServer.readInt();
                    assertEquals(DRAW, readCondition);
                    int readRow = p1FromServer.readInt();
                    int readCol = p1FromServer.readInt();
                    assertEquals(0, readRow);
                    assertEquals(1, readCol);
                    String expectedGame2 =
                                    "|X|O|X|O|X|O|O|\n" +
                                    "|X|O|X|O|X|O|X|\n" +
                                    "|O|X|O|X|O|X|O|\n" +
                                    "|O|X|O|X|O|X|X|\n" +
                                    "|X|O|X|O|X|O|O|\n" +
                                    "|X|O|X|O|X|O|X|\n";
                    String actualGame2 = p1FromServer.readUTF();
                    assertEquals(expectedGame2, actualGame2);
                    dummyP1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread2.start();

            thread3 = new Thread( () -> {
                try {
                    Socket dummyP2 = new Socket("localhost", 8003);
                    DataInputStream p2FromServer = new DataInputStream(dummyP2.getInputStream());
                    DataOutputStream p2ToServer = new DataOutputStream(dummyP2.getOutputStream());
                    int readCondition = p2FromServer.readInt();
                    assertEquals(CONTINUE, readCondition);
                    int readRow = p2FromServer.readInt();
                    int readCol = p2FromServer.readInt();
                    assertEquals(0, readRow);
                    assertEquals(0, readCol);
                    String expectedGame =
                                    "|X| |X|O|X|O|O|\n" +
                                    "|X|O|X|O|X|O|X|\n" +
                                    "|O|X|O|X|O|X|O|\n" +
                                    "|O|X|O|X|O|X|X|\n" +
                                    "|X|O|X|O|X|O|O|\n" +
                                    "|X|O|X|O|X|O|X|\n";
                    String readGame = p2FromServer.readUTF();
                    assertEquals(expectedGame, readGame);
                    p2ToServer.writeInt(2);
                    boolean firstTry = p2FromServer.readBoolean();
                    assertTrue(firstTry);
                    String expectedGame2 =
                                    "|X|O|X|O|X|O|O|\n" +
                                    "|X|O|X|O|X|O|X|\n" +
                                    "|O|X|O|X|O|X|O|\n" +
                                    "|O|X|O|X|O|X|X|\n" +
                                    "|X|O|X|O|X|O|O|\n" +
                                    "|X|O|X|O|X|O|X|\n";
                    String readGame2 = p2FromServer.readUTF();
                    assertEquals(expectedGame2, readGame2);
                    int readCondition2 = p2FromServer.readInt();
                    assertEquals(DRAW, readCondition2);
                    String readGame3 = p2FromServer.readUTF();
                    assertEquals(expectedGame2, readGame3);
                    dummyP2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread3.start();
        }

        @Test
        public void testRunCase11_12() {
            thread1 = new Thread( () -> {
                try {
                    ServerSocket testServer = new ServerSocket(8007);
                    Socket dummyClient1 = testServer.accept();
                    HandleSession hS9 = (new Connect4Server()).new HandleSession(dummyClient1);
                    hS9.getGameBoard()[0][0] = 'X'; hS9.getGameBoard()[0][1] = 'O'; hS9.getGameBoard()[0][2] = 'X'; hS9.getGameBoard()[0][3] = 'O';
                    hS9.getGameBoard()[0][4] = 'X'; hS9.getGameBoard()[0][5] = ' '; hS9.getGameBoard()[0][6] = ' '; hS9.getGameBoard()[1][0] = 'X';
                    hS9.getGameBoard()[1][1] = 'O'; hS9.getGameBoard()[1][2] = 'X'; hS9.getGameBoard()[1][3] = 'O'; hS9.getGameBoard()[1][4] = 'X';
                    hS9.getGameBoard()[1][5] = 'O'; hS9.getGameBoard()[1][6] = 'O'; hS9.getGameBoard()[2][0] = 'O'; hS9.getGameBoard()[2][1] = 'X';
                    hS9.getGameBoard()[2][2] = 'O'; hS9.getGameBoard()[2][3] = 'X'; hS9.getGameBoard()[2][4] = 'O'; hS9.getGameBoard()[2][5] = 'X';
                    hS9.getGameBoard()[2][6] = 'O'; hS9.getGameBoard()[3][0] = 'O'; hS9.getGameBoard()[3][1] = 'X'; hS9.getGameBoard()[3][2] = 'O';
                    hS9.getGameBoard()[3][3] = 'X'; hS9.getGameBoard()[3][4] = 'O'; hS9.getGameBoard()[3][5] = 'X'; hS9.getGameBoard()[3][6] = 'O';
                    hS9.getGameBoard()[4][0] = 'X'; hS9.getGameBoard()[4][1] = 'O'; hS9.getGameBoard()[4][2] = 'X'; hS9.getGameBoard()[4][3] = 'O';
                    hS9.getGameBoard()[4][4] = 'X'; hS9.getGameBoard()[4][5] = 'O'; hS9.getGameBoard()[4][6] = 'X'; hS9.getGameBoard()[5][0] = 'X';
                    hS9.getGameBoard()[5][1] = 'O'; hS9.getGameBoard()[5][2] = 'X'; hS9.getGameBoard()[5][3] = 'O'; hS9.getGameBoard()[5][4] = 'X';
                    hS9.getGameBoard()[5][5] = 'O'; hS9.getGameBoard()[5][6] = 'X';
                    hS9.run();
                    testServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread1.start();

            thread2 = new Thread( () -> {
                try {
                    Socket dummyP1 = new Socket("localhost", 8007);
                    DataInputStream p1FromServer = new DataInputStream(dummyP1.getInputStream());
                    DataOutputStream p1ToServer = new DataOutputStream(dummyP1.getOutputStream());
                    p1FromServer.readInt();
                    p1ToServer.writeInt(6);
                    boolean firstTry = p1FromServer.readBoolean();
                    assertTrue(firstTry);
                    String expectedGame =
                                    "|X|O|X|O|X|X| |\n" +
                                    "|X|O|X|O|X|O|O|\n" +
                                    "|O|X|O|X|O|X|O|\n" +
                                    "|O|X|O|X|O|X|O|\n" +
                                    "|X|O|X|O|X|O|X|\n" +
                                    "|X|O|X|O|X|O|X|\n";
                    String actualGame = p1FromServer.readUTF();
                    assertEquals(expectedGame, actualGame);
                    int readCondition = p1FromServer.readInt();
                    assertEquals(Player2_WINS, readCondition);
                    int readRow = p1FromServer.readInt();
                    int readCol = p1FromServer.readInt();
                    assertEquals(0, readRow);
                    assertEquals(6, readCol);
                    String expectedGame2 =
                                    "|X|O|X|O|X|X|O|\n" +
                                    "|X|O|X|O|X|O|O|\n" +
                                    "|O|X|O|X|O|X|O|\n" +
                                    "|O|X|O|X|O|X|O|\n" +
                                    "|X|O|X|O|X|O|X|\n" +
                                    "|X|O|X|O|X|O|X|\n";
                    String actualGame2 = p1FromServer.readUTF();
                    assertEquals(expectedGame2, actualGame2);
                    dummyP1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread2.start();
        }
    }
}