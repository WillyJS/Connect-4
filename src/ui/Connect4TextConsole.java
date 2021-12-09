package ui;

import core.Connect4;
import core.Connect4ComputerPlayer;
import java.util.Scanner;
import javafx.application.Application;

/** The connect4TextConsole class below creates a Console-based UI to test the game.
 * It introduces players to the game and prompts the user to select vs PVP or PVC
 * PVP alternates turns between player 1 and 2.
 * PVC alternates turns between player 1 and computer.
 * After the board is full or someone has won the game it declares either a winner or a tie.
 *
 * @author William J Sanchez
 * @version 4.0 Feb 15, 2020.
 */

public class Connect4TextConsole {

    private Connect4 game;


    public static void main(String[] args) {

        new Connect4TextConsole().startGame();
    }

    /**
     *Starts our game by gathering input from the user.
     */
    void startGame() {
        this.game = new Connect4();
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to Connect4!");
        System.out.println("Press \"C\" to play Connect4 in Console!.");
        System.out.println("Press \"G\" to play Connect4 in GUI!. ");
        String display = scan.next();

        if (display.equalsIgnoreCase("C")) {
            System.out.print(game);
            System.out.println("Begin Game.");
            System.out.println("Welcome to Connect Four!");
            System.out.println("Press \"P\" to play against another player!.");
            System.out.println("Press \"C\" to play against the computer!.");

            String selection = scan.next();

            if (selection.equalsIgnoreCase("P")) {
                System.out.println("Starting game against another player! .");
                System.out.println("Good luck!. ");

                while (!game.drawCheck()) {
                    System.out.println("Player X Please choose a number between 1-7. ");
                    boolean done = false;
                    int col = 0;

                    while (!done) {
                        if (!scan.hasNextInt()) {
                            System.out.println("Pay Attention!. Please choose a number between 1-7.");
                            scan.nextLine();
                            continue;
                        } else {
                            col = scan.nextInt();
                            if (!game.inBound(col)) {
                                System.out.println("What are you doing!?. Please select a number between 1-7");
                                scan.nextLine();
                                continue;
                            } else {
                                done = true;
                            }
                        }
                    }
                    game.dropPiece('X', col);
                    System.out.print(game);
                    if (game.checkWin()) {
                        System.out.println("CONGRATULATIONS!");
                        System.out.println("Player X Won the Game!");
                        break;
                    }
                    System.out.println("Player O Please choose a number between 1-7.");
                    done = false;
                    while (!done) {
                        if (!scan.hasNextInt()) {
                            System.out.println("Pay Attention!. Please choose a number between 1-7.");
                            scan.nextLine();
                            continue;
                        } else {
                            col = scan.nextInt();
                            if (!game.inBound(col)) {
                                System.out.println("What are you doing!?. Please select a number between 1-7");
                                scan.nextLine();
                                continue;
                            } else {
                                done = true;
                            }
                        }
                    }
                    game.dropPiece('O', col);
                    System.out.print(game);
                    if (game.checkWin()) {
                        System.out.println("CONGRATULATIONS!");
                        System.out.println("Player O Won the Game!");
                        break;
                    }
                }
            } else {
                System.out.println("Starting game against computer.");
                Connect4ComputerPlayer comp = new Connect4ComputerPlayer(game);
                while (!game.drawCheck()) {
                    System.out.println("You are up!,Please select a number between 1-7 ");
                    boolean done = false;
                    int col = 0;
                    while (!done) {
                        if (!scan.hasNextInt()) {
                            System.out.println("Pay Attention!. Please choose a number between 1-7.");
                            scan.nextLine();
                            continue;
                        } else {
                            col = scan.nextInt();
                            if (!game.inBound(col)) {
                                System.out.println("What are you doing!?. Please select a number between 1-7");
                                scan.nextLine();
                                continue;
                            } else {
                                done = true;
                            }
                        }
                    }
                    game.dropPiece('X', col);
                    System.out.print(game);
                    if (game.checkWin()) {
                        System.out.println("CONGRATULATIONS!");
                        System.out.println("YOU! Won the Game!");
                        break;
                    }
                    System.out.println("Computer is now choosing...");
                    comp.compPlay();
                    System.out.print(game);
                    if (game.checkWin()) {
                        System.out.println("The Computer has won! ");
                        break;
                    }
                }
            }
            if (game.drawCheck()) {
                System.out.println("It's a Draw! Better luck next time! ");
            }
            System.out.println("Thanks for playing!");
            scan.close();
        } else {
            Application.launch(Connect4GUI.class);
        }
    }
}
