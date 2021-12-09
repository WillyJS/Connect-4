package core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Connect4Client handles player moves. It initiates a connection with the server
 * and then play the game once assigned to a game session.
 * The client program receives the board configuration from the server and updates its UI.
 *
 * @author William J Sanchez
 * @version 1.0 Feb 15,2020
 */

public class Connect4Client extends Application implements Connect4Constants{

    int ROW = 6;
    int COL = 7;
    private char choice = CONSOLE;
    private boolean switchTurn = false;
    private char myPiece = ' ';
    private char theirPiece = ' ';
    private Square[][] gameBoard = new Square[ROW][COL];
    private boolean continuePlay = true;
    private int userInput = 0;
    private DataInputStream serverInputStream;
    private DataOutputStream serverOutputStream;
    private boolean waitForTurn = true;
    private Label playerNumber = new Label();
    private Label header = new Label();
    private Stage primaryStage;
    private GridPane grid;
    private String updatedGame;
    private int gameType = 0;


    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to Connect4!");
        System.out.println("Press \"C\" to play Connect4 in Console!.");
        System.out.println("Press \"G\" to play Connect4 in GUI!. ");
        String display = scan.next();

        if (display.equalsIgnoreCase("G")) {
            Application.launch(Connect4Client.class);
        }

        else {
            new Connect4Client().connectToServer(0);
        }
    }

    /**
     * Starts our GUI by asking if the user wants a PVP or PVC game.
     * Then it requests a connection to the server.
\     */
    public void start(Stage mainStage) {

        choice = GUI;
        grid = new GridPane();
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                this.gameBoard[i][j] = new Square();
                grid.add(this.gameBoard[i][j], j, i + 1);
            }
        }

        ToggleGroup columns = new ToggleGroup();

        RadioButton column1 = new RadioButton();
        column1.setToggleGroup(columns);
        column1.setOnAction(e -> {handleSelection(1); column1.setSelected(false);});
        grid.add(column1, 0, 0);
        GridPane.setHalignment(column1, HPos.CENTER);

        RadioButton column2 = new RadioButton();
        column2.setToggleGroup(columns);
        column2.setOnAction(e -> {handleSelection(2); column2.setSelected(false);});
        grid.add(column2, 1, 0);
        GridPane.setHalignment(column2, HPos.CENTER);

        RadioButton column3 = new RadioButton();
        column3.setToggleGroup(columns);
        column3.setOnAction(e -> {handleSelection(3); column3.setSelected(false);});
        grid.add(column3, 2, 0);
        GridPane.setHalignment(column3, HPos.CENTER);

        RadioButton column4 = new RadioButton();
        column4.setToggleGroup(columns);
        column4.setOnAction(e -> {handleSelection(4); column4.setSelected(false);});
        grid.add(column4, 3, 0);
        GridPane.setHalignment(column4, HPos.CENTER);

        RadioButton column5 = new RadioButton();
        column5.setToggleGroup(columns);
        column5.setOnAction(e -> {handleSelection(5); column5.setSelected(false);});
        grid.add(column5, 4, 0);
        GridPane.setHalignment(column5, HPos.CENTER);

        RadioButton column6 = new RadioButton();
        column6.setToggleGroup(columns);
        column6.setOnAction(e -> {handleSelection(6); column6.setSelected(false);});
        grid.add(column6, 5, 0);
        GridPane.setHalignment(column6, HPos.CENTER);

        RadioButton column7 = new RadioButton();
        column7.setToggleGroup(columns);
        column7.setOnAction(e -> {handleSelection(7); column7.setSelected(false);});
        grid.add(column7, 6, 0);
        GridPane.setHalignment(column7, HPos.CENTER);

        VBox main = new VBox(10);
        main.getChildren().add(playerNumber);
        main.getChildren().add(header);
        main.getChildren().add(grid);
        main.setAlignment(Pos.CENTER);

        primaryStage = new Stage();
        Scene scene = new Scene(main);
        primaryStage.setTitle("Connect4!");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.initStyle(StageStyle.DECORATED);


        VBox promptPane = new VBox(20);
        promptPane.setPadding(new Insets(10,10,10,10));
        promptPane.setAlignment(Pos.TOP_CENTER);
        Label popupMsg = new Label("Please choose a game mode! ");
        promptPane.getChildren().add(popupMsg);

        HBox promptButtons = new HBox(8);
       promptButtons.setPadding(new Insets(10,10,10,10));
       promptButtons.setAlignment(Pos.CENTER);

        Button comp = new Button("Single Player");
        comp.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                connectToServer(COMPUTER);
                mainStage.close();
                primaryStage.show();
            }
        });
        promptButtons.getChildren().add(comp);
        Button aP = new Button("Two Players");
        aP.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                connectToServer(ALTERNATE_PLAYER);
                mainStage.close();
                primaryStage.show();
            }
        });
        promptButtons.getChildren().add(aP);
        promptButtons.setStyle("-fx-background-style: white;");
        promptPane.getChildren().add(promptButtons);
        promptPane.setAlignment(Pos.CENTER);
        promptPane.setStyle("-fx-background-color: #819dff;");
        Scene popupScene = new Scene(promptPane);
        mainStage.setTitle("Welcome to Connect4!");
        mainStage.setScene(popupScene);
        mainStage.sizeToScene();
        mainStage.initStyle(StageStyle.DECORATED);
        mainStage.show();
    }

    /**
     * Connects each client to the server.  Assigns clients to players.
     * @param i input used to connect to server.
     */
    public void connectToServer(int i) {
        try {
            Socket socket = new Socket("LocalHost", 8010);

            serverInputStream = new DataInputStream(socket.getInputStream());

            serverOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread( () -> {
            try {

                Scanner scan2 = new Scanner(System.in);

                if (choice == CONSOLE) {
                    Platform.runLater( () -> {
                        System.out.println("Press 'C' to play against the computer.");
                        System.out.println("Press 'P' to play against another player");
                        System.out.println("Don't forget to press 'Enter!! ");
                        String choice = scan2.next();

                        if (choice.equalsIgnoreCase("C")) {
                            this.gameType = COMPUTER;
                        } else {
                            this.gameType = ALTERNATE_PLAYER;
                        }
                        try {
                            serverOutputStream.writeInt(gameType);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    serverOutputStream.writeInt(i);
                }

                int player = serverInputStream.readInt();

                if (player == Player_1) {
                    myPiece = 'X';
                    theirPiece = 'O';
                    if (choice == CONSOLE) {
                        Platform.runLater( () -> {
                            System.out.println("Player 1 will have the 'X' Piece.");
                            System.out.println("Waiting for Player 2 to join.");
                        });
                    } else {
                        Platform.runLater( () -> {
                            playerNumber.setText("Player 1 will be the color RED!.");
                            header.setText("Waiting for Player 2 to join.");
                        });
                    }

                    serverInputStream.readInt();

                    if (choice == CONSOLE) {
                        Platform.runLater( () -> {
                            System.out.println();
                            System.out.println("Player 2 has joined. My turn to choose!.");
                            System.out.println("Please choose a column number from 1-7. Then press 'Enter'.");

                            while (!scan2.hasNextInt()) {
                                System.out.println("Pay Attention!. Please enter an integer between 1 and 7.");
                                scan2.nextLine();
                            }
                            userInput = scan2.nextInt();
                            switchTurn = false;
                            waitForTurn = false;
                        });
                    } else {
                        Platform.runLater( () -> {
                            header.setText("Player 2 has joined. My turn to choose!.");
                        });
                        switchTurn = true;
                    }
                } else if (player == Player_2) {
                    myPiece = 'O';
                    theirPiece = 'X';
                    if (choice == CONSOLE) {
                        Platform.runLater( () -> {
                            System.out.println("Player 2 will have the 'O' Piece.");
                            System.out.println("Waiting for Player 1 to move.");
                        });
                    } else {
                        Platform.runLater( () -> {
                            playerNumber.setText("Player 2 will be the color YELLOW!.");
                            header.setText("Waiting for Player 1 to move.");
                        });
                    }
                }

                while (continuePlay) {
                    if (player == Player_1) {
                        waitForPlayerMove();
                        sendPlayerMove();
                        receiveDataFromServer();
                    } else if (player == Player_2) {
                        receiveDataFromServer();
                        waitForPlayerMove();
                        sendPlayerMove();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Receives player's move from server and shows it
     *
     * @throws IOException throws exception when encountering socket issues.
     */
    public void receivePlayerMove() throws IOException {
        int row = serverInputStream.readInt();
        int column = serverInputStream.readInt();
        updatedGame = serverInputStream.readUTF();
        if (choice == GUI) {
            Platform.runLater( () -> gameBoard[row][column].setColor(theirPiece));
        } else {
            Platform.runLater( () -> System.out.println(updatedGame));
        }

    }

    /**
     * Sends the client's move to the server
     *
     * @throws IOException throws exception in case of a Socket issue.
     * @throws InterruptedException handles interruptions when a thread is waiting.
     */
    public void sendPlayerMove() throws IOException, InterruptedException {
        serverOutputStream.writeInt(userInput);
        boolean validNum = serverInputStream.readBoolean();
        while (!validNum) {
            switchTurn = true;
            Scanner scan3 = new Scanner(System.in);
            if (choice == CONSOLE) {
                Platform.runLater( () -> {
                    System.out.println("Pay Attention!. Please choose a number between 1-7. Then press Enter!");

                    while (!scan3.hasNextInt()) {
                        System.out.println("Pay Attention!. Please choose a number between 1-7. Then press Enter!");
                        scan3.nextLine();
                    }
                    userInput = scan3.nextInt();
                    switchTurn = false;
                    waitForTurn = false;
                });
            } else {
                Platform.runLater( () -> {
                    header.setText("Sorry, invalid move. Select a different column.");
                    switchTurn = true;
                });
            }
            waitForPlayerMove();
            serverOutputStream.writeInt(userInput);
            validNum = serverInputStream.readBoolean();
        }
        updatedGame = serverInputStream.readUTF();
        if (choice == CONSOLE) {
            Platform.runLater( () -> {
                System.out.println(updatedGame);
                System.out.println("Waiting for the other player to play.");
            });
        } else {
            int row = gameBoard.length - 1;
            char color = gameBoard[row][userInput - 1].getColor();
            while (color != ' ') {
                row--;
                color = gameBoard[row][userInput - 1].getColor();
            }
            gameBoard[row][userInput - 1].setColor(myPiece);
            Platform.runLater( () -> {
                header.setText("Waiting for other player to move.");
            });
        }
    }

    /**
     * Receives data from the server.  It includes the state of the game.
     *
     * @throws IOException throws exception if there is a socket communication issue.
     */
    private void receiveDataFromServer() throws IOException {
        int status = serverInputStream.readInt();

        if (status == Player1_WINS) {
            continuePlay = false;
            if (myPiece == 'X') {
                updatedGame = serverInputStream.readUTF();
                if (choice == CONSOLE) {
                    Platform.runLater( () -> {
                        System.out.println(updatedGame);
                        System.out.println("You have WON!");
                    });
                } else {
                    Platform.runLater( () -> {
                        header.setText("You HAVE WON!");
                        grid.setDisable(true);
                    });
                }
            } else if (myPiece == 'O') {
                receivePlayerMove();
                if (choice == CONSOLE) {
                    Platform.runLater( () -> {
                        System.out.println("You have LOST!");
                    });
                } else {
                    Platform.runLater( () -> {
                        header.setText("You have LOST!");
                        grid.setDisable(true);
                    });
                }
            }
        } else if (status == Player2_WINS) {
            continuePlay = false;
            if (myPiece == 'O') {
                updatedGame = serverInputStream.readUTF();
                if (choice == CONSOLE) {
                    Platform.runLater( () -> {
                        System.out.println(updatedGame);
                        System.out.println("You have WON!");
                    });
                } else {
                    Platform.runLater( () -> {
                        header.setText("You have WON!");
                        grid.setDisable(true);
                    });
                }
            } else if (myPiece == 'X') {
                receivePlayerMove();
                if (choice == CONSOLE) {
                    Platform.runLater( () -> {
                        System.out.println("You have LOST!");
                    });
                } else {
                    Platform.runLater( () -> {
                        header.setText("You have LOST!");
                        grid.setDisable(true);
                    });
                }
            }
        } else if (status == DRAW) {
            updatedGame = serverInputStream.readUTF();
            continuePlay = false;
            if (choice == CONSOLE) {
                Platform.runLater( () -> {
                    System.out.println(updatedGame);
                    System.out.println("It's a Draw! Better luck next time! ");
                });
            } else {
                Platform.runLater( () -> {
                    System.out.println("It's a Draw! Better luck next time! ");
                    grid.setDisable(true);
                });
            }
        } else {
            receivePlayerMove();
            if (choice == CONSOLE) {
                Scanner scan4 = new Scanner(System.in);
                Platform.runLater( () -> {
                    System.out.println("Your turn. Please choose a column number from 1-7. Then press 'Enter'.");

                    while (!scan4.hasNextInt()) {
                        System.out.println("Pay Attention!. Please choose a number between 1-7.");
                        scan4.nextLine();
                    }
                    userInput = scan4.nextInt();
                    switchTurn = false;
                    waitForTurn = false;
                });
            } else {
                Platform.runLater( () -> {
                    header.setText("Your turn.");
                });
            }
            switchTurn = true;
        }
    }

    /**
     * Puts the thread to sleep while waiting for the other player's move.
     * Resets the wait for turn state after.
     *
     * @throws InterruptedException handles interruptions when a thread is waiting.
     */
    public void waitForPlayerMove() throws InterruptedException {
        while (waitForTurn) {
            Thread.sleep(100);
        }
        waitForTurn = true;
    }

    /**
     * This builds our visual game board.
     */
    public static class Square extends StackPane {

        private char color;
        public Circle circle;

        /**
         * Constructor sets the style and size of the Square object
         * and adds a Circle object (window) to it.
         */
        public Square() {
            setStyle("-fx-background-color: #000000; -fx-border-color: #000000;");
            this.setPrefSize(100, 100);
            this.circle = new Circle(45, Color.WHITE);
            this.getChildren().add(circle);
            color = ' ';
        }

        public char getColor() {

            return color;
        }

        public void setColor(char symbol) {
            color = symbol;
            updateColor();
        }

        /**
         * Assigns player colors.
         */
        public void updateColor() {
            if (color == 'X') {
                this.circle.setFill(Color.RED);
            }
            else if (color == 'O' ) {
                this.circle.setFill(Color.YELLOW);
            }
        }
    }

    /**
     * Handles a radiobutton selection event
     *
     * @param col the column corresponding to the radiobutton selected
     */
    public void handleSelection(int col) {
        if(switchTurn) {
            switchTurn = false;
            userInput = col;
            waitForTurn = false;
        }
    }
}


