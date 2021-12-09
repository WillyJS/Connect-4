package ui;

import core.Connect4;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

/**
 * The Connect4GUI class below is a graphical representation of our connect4 game.
 * Connect4GUI prompts a player to choose between a GUI and a console game.
 * Upon his choice the GUI or console version of the game will launch.
 *
 * @author William J Sanchez
 * @version 1.0 Feb 9, 2020
 */

public class Connect4GUI extends Application {

    static int overlaySize = 80;
    int ROW = 6;
    int COL = 7;

    private Piece[][] gameBoard = new Piece[COL][ROW];
    private Scene beginGUI;
    private Pane pieceLoc = new Pane();
    private boolean compPlayer = false;
    private Stage hostWindow;
    private boolean redPiece = false;
    private List<Piece> moves = new ArrayList<>();

    /**
     * The purpose of this private inner class is to initialize the piece object used with our game board.
     */
    private static class Piece extends Circle {

        private final boolean red;

        /**
         * @param red is a boolean that determines the color of our pieces.
         */
        Piece(boolean red) {
            super(overlaySize / 2, red ? Color.YELLOW : Color.RED);
            this.red = red;
            setCenterX(overlaySize / 2);
            setCenterY(overlaySize / 2);
        }
    }

    /**
     * This method is used to introduce the start menu of our game.
     * @param mainStage here is where our players will see their options.
     */
    private void startGame(Stage mainStage) {
        hostWindow = mainStage;
        Label beginLabel = new Label("Welcome to Connect 4!\n");
        Button playButton = new Button("Play");
        playButton.setOnMouseClicked(e -> begin(mainStage));
        playButton.setOnAction(e -> {
            begin(hostWindow);
            mainStage.show();
        });

        Button quitButton = new Button("Quit");
        quitButton.setOnAction(e -> {
            System.out.println("Goodbye! ");
            System.exit(0);
        });

        //start GUI scene
        HBox label = new HBox(20);
        label.getChildren().add(beginLabel);
        label.setPadding(new Insets(10, 10, 10, 10));
        label.setAlignment(Pos.TOP_CENTER);

        // Here are our buttons options.
        HBox startButton = new HBox(20);
        startButton.getChildren().addAll(playButton);
        startButton.getChildren().addAll(quitButton);
        startButton.setPadding(new Insets(10, 10, 10, 10));
        startButton.setAlignment(Pos.CENTER);

        // introduces boxes and color background for our border pane.
        BorderPane borderp = new BorderPane();
        borderp.setStyle("-fx-background-color: #6982c6");
        borderp.setTop(label);
        borderp.setCenter(startButton);

        // here we set our scene and show it.
        beginGUI = new Scene(borderp, 300, 150);
        hostWindow.setScene(beginGUI);
        hostWindow.setTitle("Connect 4 Game");
        hostWindow.show();

    }

    /**
     * This method will execute the connect4 game.
     * @param mainStage will ask players for a selection between the GUI or Console versions of our game.
     */
    private void begin(Stage mainStage) {
        hostWindow = mainStage;
        Label beginLabel = new Label("Which version would you like to play?\n");
        Button consoleButton = new Button("Console");
        consoleButton.setOnAction(e -> {
            System.out.println("Switching to Text Console...");
            Connect4TextConsole start = new Connect4TextConsole();
            mainStage.close();
            start.startGame();

        });

        Button guiButton = new Button("GUI");
        guiButton.setOnMouseClicked(e -> {
            executeGUI(hostWindow);
            mainStage.show();
        });

        //start GUI scene
        HBox textLabel = new HBox(8);
        textLabel.getChildren().add(beginLabel);
        textLabel.setPadding(new Insets(10, 10, 10, 10));
        textLabel.setAlignment(Pos.TOP_CENTER);

        // Here are our buttons options
        HBox buttonBox = new HBox(8);
        buttonBox.getChildren().addAll(guiButton, consoleButton);
        buttonBox.setPadding(new Insets(10, 10, 10, 10));
        buttonBox.setAlignment(Pos.CENTER);

        // introduces boxes and color background for our border pane.
        BorderPane borderp = new BorderPane();
        borderp.setStyle("-fx-background-color: #6982c6");
        borderp.setTop(textLabel);
        borderp.setCenter(buttonBox);

        // here we set our scene and show it.
        beginGUI = new Scene(borderp, 300, 150);
        hostWindow.setScene(beginGUI);
        hostWindow.setTitle("Connect 4 Game");
        hostWindow.show();
    }

    /**
     * This will introduce options for player mode selection.
     * @param mainStage creates a new window for player to choose between 1 or 2 player game.
     */
    private void executeGUI(Stage mainStage) {
        hostWindow = mainStage;

        //introduces buttons along with boxes to our scene
        Label startLabel = new Label("How many players will be playing?\n");
        Button compPlaying = new Button("1 Player");
        Button two_player = new Button("2 Players");

        // box used for text.
        HBox textLabel = new HBox(8);
        textLabel.getChildren().add(startLabel);
        textLabel.setPadding(new Insets(10, 10, 10, 10));
        textLabel.setAlignment(Pos.TOP_CENTER);

        // box used for buttons.
        HBox buttonBox = new HBox(8);
        buttonBox.getChildren().addAll(compPlaying, two_player);
        buttonBox.setPadding(new Insets(10, 10, 10, 10));
        buttonBox.setAlignment(Pos.CENTER);

        // boxes for our border plane.
        BorderPane borderp = new BorderPane();
        borderp.setStyle("-fx-background-color: #6982c6");
        borderp.setTop(textLabel);
        borderp.setCenter(buttonBox);

        // here we set our scene and show it.
        beginGUI = new Scene(borderp, 300, 150);
        hostWindow.setScene(beginGUI);
        hostWindow.setTitle("Connect 4 Game");
        hostWindow.show();

        // if 2 player mode is selected.
        two_player.setOnAction(e -> twoPlayers(new Stage()));

        // if 1 player mode is selected.
        compPlaying.setOnAction(e
                -> {
            compPlayer = true;
            singlePlayer(new Stage());
        });
    }

    /**
     * This void method launches a 2-player game in the GUI version.
     * @param mainStage creates a new stage to build a game window
     */
    private void twoPlayers(Stage mainStage) {
        mainStage.setOnCloseRequest(e -> {
            e.consume();
            closeApp();
        });
        mainStage.setTitle("Connect 4 - Player Vs Player");
        mainStage.setScene(new Scene(gameBoard()));
        mainStage.getScene().getWindow();
        mainStage.show();
        hostWindow.close();
    }

    /**
     * This void method launches a 1-player game in the GUI version.
     * @param mainStage creates a new stage to build a game window
     */
    private void singlePlayer(Stage mainStage) {
        mainStage.setOnCloseRequest(e -> {
            e.consume();
            closeApp();
        });

        mainStage.setTitle("Connect 4 - Player Vs Computer");
        mainStage.setScene(new Scene(gameBoard()));
        mainStage.getScene().getWindow();
        mainStage.show();
        hostWindow.close();
    }

    /**
     * This method creates a grid visual representation of the game board.
     * @return gameboard returns our game board during play.
     */
    private Parent gameBoard() {
        Pane gameboard = new Pane();
        System.out.println("Creating Game Board....");
        gameboard.getChildren().add(pieceLoc);
        Shape gridShape = launchGrid();
        gameboard.getChildren().add(gridShape);
        gameboard.getChildren().addAll(columnOverlay());

        return gameboard;
    }

    /**
     * This method creates a grid on game board.
     * We use the rectangle shape to create the grid, and we use the circle shape to create holes in the grid.
     * @return board returns the updated grid board to manage the placement of pieces.
     */
    private Shape launchGrid() {
        Shape board = new Rectangle((COL + 1) * overlaySize, (ROW + 1) * overlaySize);
        for (int y = 0; y < ROW; y++) {
            for (int x = 0; x < COL; x++) {
                Circle pieceCircle = new Circle(overlaySize / 2);
                pieceCircle.setCenterX(overlaySize / 2);
                pieceCircle.setCenterY(overlaySize / 2);
                //small space between the circles
                pieceCircle.setTranslateX(x * (overlaySize + 6) + overlaySize / 3);
                pieceCircle.setTranslateY(y * (overlaySize + 6) + overlaySize / 3);

                board = Shape.subtract(board, pieceCircle);
            }
        }
        return board;
    }

    /**
     * This method shows the user which columns they chose.
     * @return columns returns the columns player have chosen.
     */
    private List<Rectangle> columnOverlay() {
        List<Rectangle> columns = new ArrayList<>();
        for (int X = 0; X < COL; X++) {
            Rectangle rect = new Rectangle(overlaySize, (ROW + 1) * overlaySize);
            rect.setTranslateX(X * (overlaySize + 6) + overlaySize / 3);
            rect.setFill(Color.TRANSPARENT);
            final int column = X;
            rect.setOnMouseClicked(e
                    -> {
                putPiece(new Piece(redPiece), column);

                if (compPlayer) {
                    putCompPiece(new Piece(true));
                }
            });
            columns.add(rect);
        }
        return columns;

    }

    /**
     * This method returns the piece appropriate to the column chosen.
     * @param row row that's been selected.
     * @param column column that's been selected.
     * @return returns the piece to be used on the game board, or nothing if player doesn't choose a column.
     */
    private Optional<Piece> getPiece(int column, int row) {
        if (column < 0 || column >= COL || row < 0 || row >= ROW) {
            return Optional.empty();
        }return Optional.ofNullable(gameBoard[column][row]);
    }

    /**
     * This method places the column piece selected.
     * @param piece Piece object.
     * @param column column chosen by player.
     */
    private void putPiece(Piece piece, int column) {
        int row = ROW - 1;
        do {
            if (getPiece(column, row).isPresent()) {
                row--;
            } else {
                break;
            }
        } while (row >= 0);

        if (row < 0) {
            return;
        }
        //add piece to the game board
        gameBoard[column][row] = piece;
        moves.add(piece);
        //visual representation of piece chosen to the game board
        pieceLoc.getChildren().add(piece);
        piece.setTranslateX(column * (overlaySize + 6) + overlaySize / 3);
        piece.setTranslateY(row * (overlaySize + 6) + overlaySize / 3);
        //checks row for win check
        final int checkWin = row;
        //win check.
        if (winner(column, checkWin)) {
            gameWinner();
        }
        if (compPlayer) {
            redPiece = !redPiece;
        } else {
            //switches player
            redPiece = !redPiece;
        }
    }

    /**
     * This method places computer piece when playing single player.
     * @param piece computer player piece to be placed on the game board.
     */
    private void putCompPiece(Piece piece) {
        int row = ROW - 1;
        Random ran = new Random();
        int col = ran.nextInt(7);
        do {
            if (getPiece(col, row).isPresent()) {
                row--;
            } else {
                break;
            }
        } while (row >= 0);

        if (row < 0) {
            return;
        }
        gameBoard[col][row] = piece;
        moves.add(piece);
        pieceLoc.getChildren().add(piece);
        piece.setTranslateX(col * (overlaySize + 6) + overlaySize / 3);
        piece.setTranslateY(row * (overlaySize + 6) + overlaySize / 3);
        //checks row for win check
        final int checkWin = row;
        //win check
        if (winner(col, checkWin)) {
            gameWinner();
        }
        //player turns
        redPiece = !redPiece;


    }

    /**
     * This method checks win either horizontally, vertically, or diagonally.
     * @param row checks pieces horizontally.
     * @param column checks pieces vertically.
     * @return true or false depending if there is a winner.
     */
    private boolean winner(int column, int row) {
        //Check win by vertical
        List<Point2D> vertical = IntStream.rangeClosed(row - 3, row + 3).mapToObj(r -> new Point2D(column, r)).collect(Collectors.toList());
        //Checks win by horizontal
        List<Point2D> horizontal = IntStream.rangeClosed(column - 3, column + 3).mapToObj(c -> new Point2D(c, row)).collect(Collectors.toList());
        //Checks win by diagonal right
        Point2D dStartPointR = new Point2D(column - 3, row + 3);
        List<Point2D> diagonalupRight = IntStream.rangeClosed(0, 6).mapToObj(i -> dStartPointR.add(i, -i)).collect(Collectors.toList());
        //Checks win by diagonal left
        Point2D dStartPointL = new Point2D(column - 3, row - 3);
        List<Point2D> diagonalupLeft = IntStream.rangeClosed(0, 6).mapToObj(i -> dStartPointL.add(i, i)).collect(Collectors.toList());

        return conditionCheck(horizontal) || conditionCheck(vertical) || conditionCheck(diagonalupLeft) || conditionCheck(diagonalupRight);
    }

    /**
     * This method checks tokens that are 4 in a row.
     * @param position are piece positions on the game board to check winning condition.
     * @return true or false if a winner is found.
     */
    private boolean conditionCheck(List<Point2D> position) {
        int pieceMatch = 0;
        for (Point2D s : position) {
            int col = (int) s.getX();
            int row = (int) s.getY();
            Piece piece = getPiece(col, row).orElse(new Piece(!redPiece));
            if (piece.red == redPiece) {
                pieceMatch++;
                if (pieceMatch == 4) {
                    return true;
                }
            } else {
                pieceMatch = 0;
            }
        }
        return false;
    }

    /**
     * This method announces the winner.
     */
    private void gameWinner() {
        Stage stage = new Stage();
        stage.setTitle("Game Over");
        Label goodjob = new Label("Well done! ");
        Button exit = new Button("Exit");
        VBox winPane = new VBox(8);

        if (redPiece) {
            Label yellowLabel = new Label("Player 2 has won this round! ");
            winPane.getChildren().addAll(goodjob, yellowLabel, exit);
            winPane.setAlignment(Pos.CENTER);
            exit.setOnAction(a
                    -> {
                System.out.println("Exiting Game.....");
                System.exit(0);
            });
        } else {
            Label redLabel = new Label("Player 1 has won this round!");
            winPane.getChildren().addAll(goodjob, redLabel, exit);
            winPane.setAlignment(Pos.CENTER);
            exit.setOnAction(a
                    -> {
                System.out.println("Exiting Game....");
                System.exit(0);
            });
        }
        Scene winScene = new Scene(winPane, 300, 150);
        stage.setScene(winScene);
        stage.show();
    }

    /**
     * This method asks the user would like to leave the game.
     */
    private void closeApp() {
        Alert exitMessage = new Alert(Alert.AlertType.CONFIRMATION);
        exitMessage.setTitle("Exit Game");
        exitMessage.setHeaderText("Are you sure you want to leave the game?!");
        ButtonType Yes = new ButtonType("Yes");
        ButtonType No = new ButtonType("No");
        exitMessage.getButtonTypes().setAll(Yes, No);
        Optional<ButtonType> res = exitMessage.showAndWait();
        if (res.get() == Yes) {
            Platform.exit();
        } else if (res.get() == No) {
            exitMessage.close();
        }
    }

    /**
     * This is the main method that launches our game.
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This launches the startGame method.
     * @param primaryStage main stage of game.
     * @throws Exception general exception.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        startGame(primaryStage);
    }
}