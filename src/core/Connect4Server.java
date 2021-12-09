package core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Date;

/**
 * Network server to control game sessions and handle
 * game logic.
 *
 * @author William J Sanchez
 * @version 1.0 Feb 15,2020
 */
public class Connect4Server extends Application implements Connect4Constants {

    private int sessionNo = 1;
    private Socket waiting4Player;
    private int holdingSession;
    private TextArea taLog;

    /**
     * Generates the server GUI.
     * PRovides information about connectivity.
     *
     * @param primaryStage the main window
     */
    @Override
    public void start(Stage primaryStage) {
        taLog = new TextArea();
        ScrollPane infoWindow = new ScrollPane(taLog);
        VBox mainPane = new VBox();
        mainPane.getChildren().add(infoWindow);

        Scene scene = new Scene(mainPane, 450, 200);
        primaryStage.setTitle("Connect4 Server");
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.show();
        launchServer(true, 8010);
    }

    public void launchServer(boolean flag, int address) {
        //Main thread to handle server requests
        new Thread( () ->  {
            try {

                //Creating server socket at port 8010 and starting the log
                ServerSocket serverSocket = new ServerSocket(address);
                if (flag) {
                    Platform.runLater( () -> taLog.appendText(new Date() + ": Server started at socket 8010\n"));
                }

                //Setting up client connections and grouping them in pairs
                while(true) {
                    if (flag) {
                        Platform.runLater( () -> taLog.appendText(new Date() + ": Waiting for players to join session " + sessionNo + '\n'));
                    }

                    Socket player = serverSocket.accept();

                    int userInput = new DataInputStream(player.getInputStream()).readInt();

                    if (userInput == COMPUTER) {
                        if (flag) {
                            Platform.runLater( () -> { taLog.appendText( new Date() + ": Player 1 joined session " + sessionNo + '\n');
                                taLog.appendText("Player 1's IP address is: " + player.getInetAddress().getHostAddress() + '\n');
                            });
                        }

                        new DataOutputStream(player.getOutputStream()).writeInt(Player_1);

                        if (flag) {
                            Platform.runLater( () -> taLog.appendText(new Date() + ": Thread started for session " + sessionNo++ + '\n'));
                            new Thread(new HandleSession(player)).start();
                        }
                    } else  {
                        if (userInput == ALTERNATE_PLAYER && waiting4Player == null) {
                            if (flag) {
                                Platform.runLater( () -> { taLog.appendText( new Date() + ": Player 1 joined session " + sessionNo + '\n');
                                    taLog.appendText("Player 1's IP address is: " + player.getInetAddress().getHostAddress() + '\n');
                                });
                            }

                            new DataOutputStream(player.getOutputStream()).writeInt(Player_1);

                            waiting4Player = player;
                            holdingSession = sessionNo;
                            sessionNo++;

                            continue;
                        }

                        else if (userInput == ALTERNATE_PLAYER && waiting4Player != null) {
                            if (flag) {
                                Platform.runLater( () -> { taLog.appendText( new Date() + ": Player 2 joined session " + holdingSession + '\n');
                                    taLog.appendText("Player 2's IP address is: " + player.getInetAddress().getHostAddress() + '\n');
                                });
                            }

                            new DataOutputStream(player.getOutputStream()).writeInt(Player_2);

                            if (flag) {
                                Platform.runLater( () -> taLog.appendText(new Date() + ": Thread started for session " + holdingSession + '\n'));
                                new Thread(new HandleSession(waiting4Player, player)).start();
                            }

                            waiting4Player = null;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     *Defines the thread class for handling a new session for two players.
     */
    public class HandleSession implements Runnable, Connect4Constants {

        int ROW = 6;
        int COL = 7;
        private int playerInput;
        private Socket player_1;
        private Socket player_2;
        private Connect4ComputerPlayer compMove;
        private char[][] gameBoard = new char[ROW][COL];
        private DataInputStream player1_InputStream;
        private DataOutputStream player1_OutputStream;
        private DataInputStream player2_InputStream;
        private DataOutputStream player2_OutputStream;

        /**
         * Handles single player session
         *
         * @param player1 the socket for single player
         */
        public HandleSession(Socket player1) {
            this.playerInput = COMPUTER;
            this.player_1 = player1;

            for (int i = 0; i < gameBoard.length; i++) {
                for (int j = 0; j < gameBoard[0].length; j++) {
                    gameBoard[i][j] = ' ';
                }
            }
        }

        /**
         * Handles two player game.
         *
         * @param player1 the socket for first player
         * @param player2 the socket for second player
         */
        public HandleSession(Socket player1, Socket player2) {
            this.playerInput = ALTERNATE_PLAYER;
            this.player_1 = player1;
            this.player_2 = player2;

            for (int i = 0; i < gameBoard.length; i++) {
                for (int j = 0; j < gameBoard[0].length; j++) {
                    gameBoard[i][j] = ' ';
                }
            }
        }

        /**
         * Implement the run method for the thread.
         */
        public void run() {
            try {
                player1_InputStream = new DataInputStream(player_1.getInputStream());
                player1_OutputStream = new DataOutputStream(player_1.getOutputStream());

                if (this.playerInput == ALTERNATE_PLAYER) {
                    player2_InputStream = new DataInputStream(player_2.getInputStream());
                    player2_OutputStream = new DataOutputStream(player_2.getOutputStream());
                } else {
                    this.compMove = new Connect4ComputerPlayer();
                }

                player1_OutputStream.writeInt(1);

                if (this.playerInput == ALTERNATE_PLAYER) {
                    while(true) {
                        int col = player1_InputStream.readInt();
                        while (!inBounds(col)) {
                            player1_OutputStream.writeBoolean(false);
                            col = player1_InputStream.readInt();
                        }
                        player1_OutputStream.flush();
                        player1_OutputStream.writeBoolean(true);
                        col--;
                        int row = gameBoard.length - 1;
                        char space = gameBoard[row][col];
                        while (space != ' ') {
                            row--;
                            space = gameBoard[row][col];
                        }
                        gameBoard[row][col] = 'X';

                        mirrorBoard(player1_OutputStream);

                        if (checkWin()) {
                            player1_OutputStream.writeInt(Player1_WINS);
                            player2_OutputStream.writeInt(Player1_WINS);
                            moveUpdate(player2_OutputStream, row, col);
                            mirrorBoard(player1_OutputStream);
                            mirrorBoard(player2_OutputStream);
                            break;
                        }

                        else if (drawCheck()) {
                            player1_OutputStream.writeInt(DRAW);
                            player2_OutputStream.writeInt(DRAW);
                            moveUpdate(player2_OutputStream, row, col);
                            mirrorBoard(player1_OutputStream);
                            mirrorBoard(player2_OutputStream);
                            break;
                        }

                        else {
                            player2_OutputStream.writeInt(CONTINUE);
                            moveUpdate(player2_OutputStream, row, col);
                            mirrorBoard(player2_OutputStream);
                        }

                        col = player2_InputStream.readInt();
                        while (!inBounds(col)) {
                            player2_OutputStream.writeBoolean(false);
                            col = player2_InputStream.readInt();
                        }
                        player2_OutputStream.writeBoolean(true);
                        col--;
                        row = gameBoard.length - 1;
                        space = gameBoard[row][col];

                        while (space != ' ') {
                            row--;
                            space = gameBoard[row][col];
                        }
                        gameBoard[row][col] = 'O';

                        mirrorBoard(player2_OutputStream);

                        if (checkWin()) {
                            player2_OutputStream.flush();
                            player2_OutputStream.writeInt(Player2_WINS);
                            player1_OutputStream.writeInt(Player2_WINS);
                            moveUpdate(player1_OutputStream, row, col);
                            mirrorBoard(player1_OutputStream);
                            mirrorBoard(player2_OutputStream);
                            break;
                        }

                        else if (drawCheck()) {
                            player1_OutputStream.writeInt(DRAW);
                            player2_OutputStream.writeInt(DRAW);
                            moveUpdate(player1_OutputStream, row, col);
                            mirrorBoard(player1_OutputStream);
                            mirrorBoard(player2_OutputStream);
                            break;
                        }

                        else {
                            player1_OutputStream.writeInt(CONTINUE);
                            moveUpdate(player1_OutputStream, row, col);
                            mirrorBoard(player1_OutputStream);
                        }
                    }
                }

                else if (playerInput == COMPUTER) {

                    while(true) {
                        int col = player1_InputStream.readInt();
                        while (!inBounds(col)) {
                            player1_OutputStream.writeBoolean(false);
                            col = player1_InputStream.readInt();
                        }
                        player1_OutputStream.flush();
                        player1_OutputStream.writeBoolean(true);
                        col--;
                        int row = gameBoard.length - 1;
                        char space = gameBoard[row][col];

                        while (space != ' ') {
                            row--;
                            space = gameBoard[row][col];
                        }
                        gameBoard[row][col] = 'X';

                        mirrorBoard(player1_OutputStream);

                        if (checkWin()) {
                            player1_OutputStream.writeInt(Player1_WINS);
                            mirrorBoard(player1_OutputStream);
                            break;
                        }

                        else if (drawCheck()) {
                            player1_OutputStream.writeInt(DRAW);
                            mirrorBoard(player1_OutputStream);
                            break;
                        }

                        col = compMove.sendcompMove();

                        while (!inBounds(col)) {
                            col = compMove.sendcompMove();
                        }
                        col--;
                        row = gameBoard.length - 1;
                        space = gameBoard[row][col];
                        while (space != ' ') {
                            row--;
                            space = gameBoard[row][col];
                        }
                        gameBoard[row][col] = 'O';

                        if (checkWin()) {
                            player1_OutputStream.writeInt(Player2_WINS);
                            moveUpdate(player1_OutputStream, row, col);
                            mirrorBoard(player1_OutputStream);
                            break;
                        }

                        else if (drawCheck()) {
                            player1_OutputStream.writeInt(DRAW);
                            moveUpdate(player1_OutputStream, row, col);
                            mirrorBoard(player1_OutputStream);
                            break;
                        }

                        else {
                            player1_OutputStream.writeInt(CONTINUE);
                            moveUpdate(player1_OutputStream, row, col);
                            mirrorBoard(player1_OutputStream);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public char[][] getGameBoard() {
            return gameBoard;
        }

        /**
         * Makes sure that column selected is within our bounds.
         *
         * @param column is what we are checking.
         * @return true if column chosen is within our domain.
         */
        public boolean inBounds(int column) {
            if (column < 1 || column > gameBoard[0].length) {
                return false;
            } else if (gameBoard[0][column - 1] != ' ') {
                return false;
            } else {
                return true;
            }
        }

        /**
         * The checkWin method checks if there is a winner.
         * @return true if there is a winner, otherwise defaults to false.
         */
        public boolean checkWin() {
            //check horizontal
            for (int i = gameBoard.length - 1; i >= 0; i--) {
                for (int j = 0; j < gameBoard[0].length - 3; j++) {
                    char piece = gameBoard[i][j];
                    if (piece == ' ') {
                        continue;
                    } else {
                        if (piece == gameBoard[i][j+1] && piece == gameBoard[i][j+2] && piece == gameBoard[i][j+3]) {
                            return true;
                        }
                    }
                }
            }
            //check vertical
            for (int i = 0; i < gameBoard[0].length; i++) {
                for (int j = gameBoard.length - 1; j > 2; j--) {
                    char piece = gameBoard[j][i];
                    if (piece == ' ') {
                        continue;
                    } else {
                        if (piece == gameBoard[j-1][i] && piece == gameBoard[j-2][i] && piece == gameBoard[j-3][i]) {
                            return true;
                        }
                    }
                }
            }
            //check left-to-right diagonal
            for (int i = gameBoard.length - 1; i > 2; i--) {
                for (int j = 0; j < gameBoard[0].length - 3; j++) {
                    if (gameBoard[i][j] == ' ') {
                        continue;
                    } else if (gameBoard[i][j] == gameBoard[i - 1][j + 1] && gameBoard[i][j] == gameBoard[i - 2][j + 2] && gameBoard[i][j] == gameBoard[i - 3][j + 3]) {
                        return true;
                    }
                }
            }
            //check right-to-left diagonal
            for (int i = gameBoard.length - 1; i > 2; i--) {
                for (int j = gameBoard[0].length - 1; j > 2; j--) {
                    if (gameBoard[i][j] == ' ') {
                        continue;
                    } else if (gameBoard[i][j] == gameBoard[i - 1][j - 1] && gameBoard[i][j] == gameBoard[i - 2][j - 2] && gameBoard[i][j] == gameBoard[i - 3][j - 3]) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * The drawcheck method checks for draw conditions.
         * @return true if a draw condition is met otherwise defaults to false.
         */
        public boolean drawCheck() {
            for (int i = 0; i < gameBoard[0].length; i++) {
                if (gameBoard[0][i] == ' ') {
                    return false;
                }
            }
            return true;
        }

        /**
         * Updates client displays on piece chosen columns.
         *
         * @param out the DataOutputStream communicates with other client.
         * @param column the column updated.
         * @param row the row it now sits on.
         * @throws IOException throws exception if encounters Socket issues
         */
        public void moveUpdate(DataOutputStream out, int row, int column) throws IOException {
            out.writeInt(row);
            out.writeInt(column);
        }

        /**
         * Mirrors a gameboard out to additional client.
         *
         * @param out communicates with additional client.
         * @throws IOException throws exception if encounters socket issues
         */
        public void mirrorBoard(DataOutputStream out) throws IOException {
            String board = "|";
            for (int i = 0; i < gameBoard.length - 1; i++) {
                for (int j = 0; j < gameBoard[0].length; j++) {
                    board = board + Character.toString(gameBoard[i][j]) + "|";
                }
                board += '\n' + "|";
            }
            for (int k = 0; k < gameBoard[0].length; k++) {
                board = board + Character.toString(gameBoard[gameBoard.length-1][k]) + "|";
            }
            board += '\n';
            out.writeUTF(board);
        }
    }

    public static void main(String[] pArgs) {
        launch(pArgs);
    }
}
