package onetoone.Checkers;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int gameId;
    private int userId;
    private String status;
    private String playerLabel;

    public Invitation(){
    }

    public Invitation(int gameId, int userId, String status, String playerLabel) {
        this.gameId = gameId;
        this.userId = userId;
        this.status = status;
        this.playerLabel = playerLabel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlayerLabel() {
        return playerLabel;
    }

    public void setPlayerLabel(String playerLabel) {
        this.playerLabel = playerLabel;
    }
}
