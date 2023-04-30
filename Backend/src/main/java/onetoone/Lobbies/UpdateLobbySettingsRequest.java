package onetoone.Lobbies;

public class UpdateLobbySettingsRequest {
    private int lobbyId;

    private int userId;
    private LobbyDto lobbyDto;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    public LobbyDto getLobbyDto() {
        return lobbyDto;
    }

    public void setLobbyDto(LobbyDto lobbyDto) {
        this.lobbyDto = lobbyDto;
    }
}