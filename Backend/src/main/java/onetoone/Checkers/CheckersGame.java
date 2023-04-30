package onetoone.Checkers;

import onetoone.Checkers.Piece.Color;

public class CheckersGame {
    private final CheckersBoard board;
    private Color currentPlayer;
    private boolean forceCaptures;
    private boolean continuousCaptures;
    private int player1Id;
    private int player2Id;
    private int gameId;
    private boolean gameOver; //needed?

    public enum GameStatus {
        ONGOING,
        FORFEITED,
        FINISHED
    }

    private GameStatus status;
    private int player3Id;
    private int player4Id;
    private boolean teamsMode;
    private int numberOfMoves;
    private int moveCounter;

    public CheckersGame(int gameId, int player1Id, int player2Id, int player3Id, int player4Id, boolean forceCaptures, boolean continuousCaptures, boolean teamsMode, int numberOfMoves) {
        this.gameId = gameId;
        this.board = new CheckersBoard(forceCaptures, continuousCaptures);
        this.currentPlayer = Color.RED;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.status = GameStatus.ONGOING;
        this.gameOver = false;
        this.forceCaptures = forceCaptures;
        this.continuousCaptures = continuousCaptures;
        this.player3Id = player3Id;
        this.player4Id = player4Id;
        this.teamsMode = teamsMode;
        this.numberOfMoves = numberOfMoves;
        this.moveCounter = 0;
    }

    public CheckersBoard getBoard() {
        return board;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public int getPlayer1Id() {
        return player1Id;
    }

    public int getPlayer2Id() {
        return player2Id;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getGameId() {
        return gameId;
    }

    public String isValidMove(Move move) {
        return board.isValidMove(move, currentPlayer, false);
    }

    public void applyMove(Move move) {
        boolean moveApplied = board.applyMove(move, currentPlayer, board.nextPlayer(currentPlayer));
        if (moveApplied) {
            moveCounter++;

            if (teamsMode) {
                int currentMove = moveCounter % (2 * numberOfMoves);

                if (currentMove < numberOfMoves) {
                    if (currentPlayer == Color.RED) {
                        currentPlayer = Color.BLACK;
                    } else {
                        currentPlayer = Color.RED;
                    }
                } else {
                    if (currentPlayer == Color.RED) {
                        currentPlayer = Color.BLACK;
                    } else {
                        currentPlayer = Color.RED;
                    }
                }
            } else {
                switchCurrentPlayer();
            }
        }
    }

    public boolean isPlayerInGame(int userId) {
        if (teamsMode) {
            return userId == player1Id || userId == player2Id || userId == player3Id || userId == player4Id;
        } else {
            return userId == player1Id || userId == player2Id;
        }
    }

    public boolean isCurrentPlayer(int userId) {
        if (teamsMode) {
            int currentMove = moveCounter % (2 * numberOfMoves);

            if (currentMove < numberOfMoves) {
                if (currentPlayer == Color.RED) {
                    return userId == player1Id;
                } else {
                    return userId == player2Id;
                }
            } else {
                if (currentPlayer == Color.RED) {
                    return userId == player3Id;
                } else {
                    return userId == player4Id;
                }
            }
        } else {
            if (currentPlayer == Color.RED) {
                return userId == player1Id;
            } else {
                return userId == player2Id;
            }
        }
    }

    public Piece[][] getBoardState() {
        return board.getBoard();
    }

    public void switchCurrentPlayer() {
        currentPlayer = (currentPlayer == Color.RED) ? Color.BLACK : Color.RED;
    }

    public GameStatus getStatus() { return status; }

    public void setStatus(GameStatus status) { this.status = status; }

    public boolean isForceCaptures() {
        return forceCaptures;
    }

    public void setForceCaptures(boolean forceCaptures) {
        this.forceCaptures = forceCaptures;
    }

    public boolean isContinuousCaptures() {
        return continuousCaptures;
    }

    public void setContinuousCaptures(boolean continuousCaptures) {
        this.continuousCaptures = continuousCaptures;
    }

    public int getPlayer3Id() {
        return player3Id;
    }

    public void setPlayer3Id(int player3Id) {
        this.player3Id = player3Id;
    }

    public int getPlayer4Id() {
        return player4Id;
    }

    public void setPlayer4Id(int player4Id) {
        this.player4Id = player4Id;
    }

    public boolean isTeamsMode() {
        return teamsMode;
    }

    public void setTeamsMode(boolean teamsMode) {
        this.teamsMode = teamsMode;
    }

    public int getNumberOfMoves() {
        return numberOfMoves;
    }

    public void setNumberOfMoves(int numberOfMoves) {
        this.numberOfMoves = numberOfMoves;
    }
}