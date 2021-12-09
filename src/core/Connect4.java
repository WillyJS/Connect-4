package core;

/**
 * core.Connect4 is a 2-player turn-based game played on a vertical board that has
 * 6 rows and 7 columns.  Each column has a whole in the upper part of the board,
 * where pieces are introduced.
 *
 * The connect4 class below is a simplified version of this class.
 * it creates a board to play this game on as well as checks for
 * winner, and makes sure our players are playing within the board's bounds.
 *
 * @author William J Sanchez
 * @version 3.0 Feb,15,2020
 */

public class Connect4 {

    int ROW = 6;
    int COL = 7;
    public char[][] playingBoard;

    /**
     * Default constructor instantiates  to have 6 rows and 7 columns.
     * Initializes each index of the array to the default whitespace character.
     */
    public Connect4() {
        this.playingBoard = new char[ROW][COL];
        for (int i = 0; i < playingBoard.length; i++) {
            for (int j = 0; j < playingBoard[0].length; j++) {
                playingBoard[i][j] = ' ';
            }
        }
    }

    /**
     * The dropPiece method is used to drop a piece in a column
     * If the column chosen is within our bound
     * @param column Which column to drop the piece in.
     * @param player which player is dropping the piece
     */
    public void dropPiece(char player, int column) {
        if (!inBound(column)) {
        }
        else {
            column--;
            int row = playingBoard.length - 1;
            char space = playingBoard[row][column];
            while (space != ' ') {
                row--;
                space = playingBoard[row][column];
            }
            playingBoard[row][column] = player;
        }
    }

    /**
     * The inBounds method makes sure the column selected is on the board.
     * @param column is whatever column the user picks.
     * @return true if a move is allowed, or false if it's out of bounds.
     */
    public boolean inBound(int column) {
        if (column < 1 || column > playingBoard[0].length) {
            return false;
        } else if (playingBoard[0][column - 1] != ' ') {
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
        for (int i = playingBoard.length - 1; i >= 0; i--) {
            for (int j = 0; j < playingBoard[0].length - 3; j++) {
                char piece = playingBoard[i][j];
                if (piece == ' ') {
                    continue;
                } else {
                    if (piece == playingBoard[i][j+1] && piece == playingBoard[i][j+2] && piece == playingBoard[i][j+3]) {
                        return true;
                    }
                }
            }
        }
        //check vertical
        for (int i = 0; i < playingBoard[0].length; i++) {
            for (int j = playingBoard.length - 1; j > 2; j--) {
                char piece = playingBoard[j][i];
                if (piece == ' ') {
                    continue;
                } else {
                    if (piece == playingBoard[j-1][i] && piece == playingBoard[j-2][i] && piece == playingBoard[j-3][i]) {
                        return true;
                    }
                }
            }
        }
        //check left-to-right diagonal
        for (int i = playingBoard.length - 1; i > 2; i--) {
            for (int j = 0; j < playingBoard[0].length - 3; j++) {
                if (playingBoard[i][j] == ' ') {
                    continue;
                } else if (playingBoard[i][j] == playingBoard[i - 1][j + 1] && playingBoard[i][j] == playingBoard[i - 2][j + 2] && playingBoard[i][j] == playingBoard[i - 3][j + 3]) {
                    return true;
                }
            }
        }
        //check right-to-left diagonal
        for (int i = playingBoard.length - 1; i > 2; i--) {
            for (int j = playingBoard[0].length - 1; j > 2; j--) {
                if (playingBoard[i][j] == ' ') {
                    continue;
                } else if (playingBoard[i][j] == playingBoard[i - 1][j - 1] && playingBoard[i][j] == playingBoard[i - 2][j - 2] && playingBoard[i][j] == playingBoard[i - 3][j - 3]) {
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
        for (int i = 0; i < playingBoard[0].length; i++) {
            if (playingBoard[0][i] == ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     *Updates gameBoard with current pieces.
     */
    @Override
    public String toString() {
        String gameBoard = "|";
        for (int i = 0; i < playingBoard.length - 1; i++) {
            for (int j = 0; j < playingBoard[0].length; j++) {
                gameBoard = gameBoard + playingBoard[i][j] + "|";
            }

            gameBoard += '\n' + "|";
        }
        for (int k = 0; k < playingBoard[0].length; k++) {
            gameBoard = gameBoard + playingBoard[playingBoard.length - 1][k] + "|";
        }
        gameBoard += '\n';
        System.out.println(" 1 2 3 4 5 6 7");

        return gameBoard;
    }

    /**
     * Accessor method for
     *
     * @return  the main game data
     */
    public char[][] getBoard() {
        return this.playingBoard;
    }
}
