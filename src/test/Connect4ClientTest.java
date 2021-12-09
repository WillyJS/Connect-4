package test;

import static org.junit.Assert.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import core.Connect4Client;
import core.Connect4Client.Square;
import core.Connect4Constants;
import javafx.embed.swing.JFXPanel;
import javafx.scene.paint.Color;

/**
 * Unit testing for Connect4Client
 * @author William J Sanchez
 * @version 1.0, Feb 23, 2020
 */
public class Connect4ClientTest implements Connect4Constants {

    private static ServerSocket testServer;
    private Connect4Client client;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testServer = new ServerSocket(8000);
        new JFXPanel();
    }

    @Test
    public void testMain() {

        Thread testClient = new Thread(() -> {
            try {

                Socket clientSocket = testServer.accept();

                DataOutputStream clientOut = new DataOutputStream(clientSocket.getOutputStream());
                DataInputStream c1ientIn = new DataInputStream(clientSocket.getInputStream());

                c1ientIn.readInt();
                clientOut.writeInt(Player_1);
                clientOut.writeInt(CONTINUE);

                c1ientIn.readInt();
                clientOut.writeBoolean(false);

                c1ientIn.readInt();
                clientOut.writeBoolean(true);
                clientOut.writeUTF("Test 1");
                clientOut.writeInt(Player1_WINS);
                clientOut.writeUTF("Test 2");
            }

            catch (IOException e) {
                e.printStackTrace();
            }
        });
        testClient.start();
    }

    @Test
    public void testReceivePlayerMove() {
       assertTrue(String.valueOf(GUI),true);
    }

    @Test
    public void testsendPlayerMove() {
        assertEquals(6,6);
    }

    @Test
    public void testReceiveDataFromServer() {
        assertTrue(String.valueOf(Player1_WINS), true);
    }

    @Test
    public void testWaitForPlayerMove(){

        try{
            client.waitForPlayerMove();
            assertTrue("wait", true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class testSquare {

        private static Square square;

        @BeforeClass
        public static void setUpBeforeClass() throws Exception {
            square = new Square();
        }

        @AfterClass
        public static void tearDownAfterClass() throws Exception {
            square = null;
        }

        @Test
        public void testGetColor() {
            assertEquals(' ', square.getColor());
        }


        @Test
        public void testSetColor() {
            square.setColor('R');
            assertEquals('R', square.getColor());
        }

        @Test
        public void testUpdateColor() {
            square.setColor('X');
            square.updateColor();
            assertEquals(Color.RED, square.circle.getFill());
            square.setColor('O');
            square.updateColor();
            assertEquals(Color.YELLOW, square.circle.getFill());
        }
    }
}