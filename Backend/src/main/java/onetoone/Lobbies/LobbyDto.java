package onetoone.Lobbies;

import onetoone.Users.User;

import java.util.Set;

public class LobbyDto {

    private int id;
    private String name;
    private Integer maxMembers;
    private String accessCode;
    private Boolean isPrivate;
    private Boolean emotesEnabled;
    private Boolean chatEnabled;
    private Integer gameTime;
    private User host;
    private Set<User> members;

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public Boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Boolean isEmotesEnabled() {
        return emotesEnabled;
    }

    public void setEmotesEnabled(Boolean emotesEnabled) {
        this.emotesEnabled = emotesEnabled;
    }

    public Boolean isChatEnabled() {
        return chatEnabled;
    }

    public void setChatEnabled(Boolean chatEnabled) {
        this.chatEnabled = chatEnabled;
    }

    public Integer getGameTime() {
        return gameTime;
    }

    public void setGameTime(Integer gameTime) {
        this.gameTime = gameTime;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }
}