package onetoone.Checkers;

public class CheckersBoard {
    private final Piece[][] board;
    private final boolean forceCaptures;
    private final boolean continuousCaptures;
    public CheckersBoard(boolean forceCaptures, boolean continuousCaptures) {
        this.forceCaptures = forceCaptures;
        this.continuousCaptures = continuousCaptures;
        board = new Piece[8][8];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 1) {
                    if (row < 3) {
                        board[row][col] = new Piece(Piece.Type.REGULAR, Piece.Color.RED);
                    } else if (row > 4) {
                        board[row][col] = new Piece(Piece.Type.REGULAR, Piece.Color.BLACK);
                    }
                }
            }
        }
    }

    public Piece getPiece(int row, int col) {
        return board[row][col];
    }

    public void setPiece(int row, int col, Piece piece) {
        board[row][col] = piece;
    }

    public String isValidMove(Move move, Piece.Color currentPlayer, boolean hasCaptured) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();

        if (fromRow < 0 || fromRow >= 8 || fromCol < 0 || fromCol >= 8 || toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8) {
            return "Invalid move: Out of board boundaries.";
        }

        Piece piece = getPiece(fromRow, fromCol);

        if (piece == null) {
            return "Invalid move: No piece at starting position.";
        } else if (piece.getColor() != currentPlayer) {
            return "Invalid move: Not this player/color's turn.";
        }

        if (getPiece(toRow, toCol) != null) {
            return "Invalid move: Destination square is not empty.";
        }

        int rowDistance = Math.abs(toRow - fromRow);
        int colDistance = Math.abs(toCol - fromCol);

        if (fromRow == toRow || fromCol == toCol) {
            return "Invalid move: Piece must move diagonally.";
        }

        boolean captureAvailable = isCaptureAvailable(currentPlayer);

        if (piece.getType() == Piece.Type.REGULAR) {
            if (rowDistance == 1 && colDistance == 1) {
                if (hasCaptured) {
                    return "Invalid move: Not a capture move.";
                }
                if (forceCaptures && captureAvailable) {
                    return "Invalid move: Must capture an opponent's piece.";
                }
                String directionValidation = isRegularMoveValid(piece, fromRow, toRow);
                return directionValidation;
            } else if (rowDistance == 2 && colDistance == 2) {
                int middleRow = (fromRow + toRow) / 2;
                int middleCol = (fromCol + toCol) / 2;
                Piece middlePiece = getPiece(middleRow, middleCol);
                String directionValidation = isRegularMoveValid(piece, fromRow, toRow);
                if (middlePiece != null && middlePiece.getColor() != currentPlayer && directionValidation == null) {
                    return null;
                } else {
                    return "Invalid move: Invalid jump.";
                }
            }
        } else if (piece.getType() == Piece.Type.KING) {
            if (rowDistance == 1 && colDistance == 1) {
                if (hasCaptured) {
                    return "Invalid move: Not a capture move.";
                }
                if (forceCaptures && captureAvailable) {
                    return "Invalid move: Must capture an opponent's piece.";
                }
                return null;
            } else if (rowDistance == 2 && colDistance == 2) {
                int middleRow = (fromRow + toRow) / 2;
                int middleCol = (fromCol + toCol) / 2;
                Piece middlePiece = getPiece(middleRow, middleCol);
                if (middlePiece != null && middlePiece.getColor() != currentPlayer) {
                    return null;
                } else {
                    return "Invalid move: Invalid jump.";
                }
            }
        }

        return "Invalid move: Unknown error.";
    }

    private boolean isCaptureAvailable(Piece.Color currentPlayer) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = getPiece(row, col);
                if (piece != null && piece.getColor() == currentPlayer) {
                    for (int newRow = 0; newRow < 8; newRow++) {
                        for (int newCol = 0; newCol < 8; newCol++) {
                            Move move = new Move(row, col, newRow, newCol);
                            String validationResult = isValidCaptureMove(move, currentPlayer);
                            if (validationResult == null) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public Piece.Color nextPlayer(Piece.Color currentPlayer) {
        if (currentPlayer == Piece.Color.RED) {
            return Piece.Color.BLACK;
        } else {
            return Piece.Color.RED;
        }
    }

    private String isValidCaptureMove(Move move, Piece.Color currentPlayer) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();

        Piece piece = getPiece(fromRow, fromCol);
        if (piece == null || piece.getColor() != currentPlayer) {
            return "Invalid move: Not a capture move.";
        }

        if (getPiece(toRow, toCol) != null) {
            return "Invalid move: Destination square is not empty.";
        }

        int rowDistance = Math.abs(toRow - fromRow);
        int colDistance = Math.abs(toCol - fromCol);

        if (rowDistance != 2 || colDistance != 2) {
            return "Invalid move: Not a capture move.";
        }

        int middleRow = (fromRow + toRow) / 2;
        int middleCol = (fromCol + toCol) / 2;
        Piece middlePiece = getPiece(middleRow, middleCol);

        if (piece.getType() == Piece.Type.REGULAR) {
            String directionValidation = isRegularMoveValid(piece, fromRow, toRow);
            if (middlePiece != null && middlePiece.getColor() != currentPlayer && directionValidation == null) {
                return null;
            } else {
                return "Invalid move: Invalid jump.";
            }
        } else if (piece.getType() == Piece.Type.KING) {
            if (middlePiece != null && middlePiece.getColor() != currentPlayer) {
                return null;
            } else {
                return "Invalid move: Invalid jump.";
            }
        }

        return "Invalid move: Unknown error.";
    }

    private String isRegularMoveValid(Piece piece, int fromRow, int toRow) {
        if (piece.getColor() == Piece.Color.RED) {
            if (toRow > fromRow) {
                return null;
            } else {
                return "Invalid move: Wrong direction.";
            }
        } else if (piece.getColor() == Piece.Color.BLACK) {
            if (toRow < fromRow) {
                return null;
            } else {
                return "Invalid move: Wrong direction.";
            }
        }
        return "Invalid move: Unknown error.";
    }

    //fixed the continuous captures method
    public boolean applyMove(Move move, Piece.Color currentPlayer, Piece.Color nextPlayer) {
        boolean hasCaptured = false;
        String validationResult = isValidMove(move, currentPlayer, false);
        if (validationResult != null) {
            return false;
        }

        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();
        Piece piece = getPiece(fromRow, fromCol);
        setPiece(toRow, toCol, piece);
        setPiece(fromRow, fromCol, null);

        int rowDistance = Math.abs(toRow - fromRow);
        int colDistance = Math.abs(toCol - fromCol);

        if (rowDistance == 2 && colDistance == 2) {
            int middleRow = (fromRow + toRow) / 2;
            int middleCol = (fromCol + toCol) / 2;
            setPiece(middleRow, middleCol, null);
            hasCaptured = true;
        }

        if (piece.getType() == Piece.Type.REGULAR && (toRow == 0 || toRow == 7)) {
            piece.setType(Piece.Type.KING);
        }

        if (continuousCaptures && hasCaptured) {
            boolean additionalCaptureAvailable = false;
            for (int newRow = 0; newRow < 8; newRow++) {
                for (int newCol = 0; newCol < 8; newCol++) {
                    Move nextMove = new Move(toRow, toCol, newRow, newCol);
                    String nextValidationResult = isValidCaptureMove(nextMove, currentPlayer);
                    if (nextValidationResult == null) {
                        additionalCaptureAvailable = true;
                        break;
                    }
                }
                if (additionalCaptureAvailable) {
                    break;
                }
            }
            if (additionalCaptureAvailable) {
                return false;
            }
        }

        return currentPlayer != nextPlayer;
    }

    public Piece[][] getBoard() {
        return board;
    }
}
