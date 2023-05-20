package onetoone.Checkers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Move {
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;

    public Move() {
    }

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    public static Move fromString(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, Move.class);
    }

    public int getFromRow() {
        return fromRow;
    }

    public void setFromRow(int fromRow) {
        this.fromRow = fromRow;
    }

    public int getFromCol() {
        return fromCol;
    }

    public void setFromCol(int fromCol) {
        this.fromCol = fromCol;
    }

    public int getToRow() {
        return toRow;
    }

    public void setToRow(int toRow) {
        this.toRow = toRow;
    }

    public int getToCol() {
        return toCol;
    }

    public void setToCol(int toCol) {
        this.toCol = toCol;
    }
}