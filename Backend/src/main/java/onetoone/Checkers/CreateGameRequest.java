package onetoone.Checkers;

public class CreateGameRequest {
    private int player1Id;
    private int player2Id;
    private Integer player3Id;
    private Integer player4Id;
    private Boolean forceCaptures;
    private Boolean continuousCaptures;
    private Boolean teamsMode;
    private Integer numberOfMoves;

    public int getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(int player1Id) {
        this.player1Id = player1Id;
    }

    public int getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(int player2Id) {
        this.player2Id = player2Id;
    }

    public int getPlayer3Id() {
        return player3Id;
    }

    public void setPlayer3Id(Integer player3Id) {
        this.player3Id = player3Id;
    }

    public Integer getPlayer4Id() {
        return player4Id;
    }

    public void setPlayer4Id(Integer player4Id) {
        this.player4Id = player4Id;
    }

    public Boolean isForceCaptures() {
        return forceCaptures;
    }

    public void setForceCaptures(Boolean forceCaptures) {
        this.forceCaptures = forceCaptures;
    }

    public Boolean isContinuousCaptures() {
        return continuousCaptures;
    }

    public void setContinuousCaptures(Boolean continuousCaptures) {
        this.continuousCaptures = continuousCaptures;
    }

    public Boolean isTeamsMode() {
        return teamsMode;
    }

    public void setTeamsMode(Boolean teamsMode) {
        this.teamsMode = teamsMode;
    }

    public Integer getNumberOfMoves() {
        return numberOfMoves;
    }

    public void setNumberOfMoves(Integer numberOfMoves) {
        this.numberOfMoves = numberOfMoves;
    }
}