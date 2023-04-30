package onetoone.Lobbies;

import onetoone.Users.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Lobby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int maxMembers;
    private String accessCode;
    private boolean isPrivate;
    private boolean emotesEnabled;
    private boolean chatEnabled;
    private int gameTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private User host;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "lobby_members",
            joinColumns = @JoinColumn(name = "lobby_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    public Lobby() {
    }

    public Lobby(String name, int maxMembers, boolean isPrivate, boolean emotesEnabled, boolean chatEnabled, int gameTime, User host) {
        this.name = name;
        this.maxMembers = maxMembers;
        this.isPrivate = isPrivate;
        this.emotesEnabled = emotesEnabled;
        this.chatEnabled = chatEnabled;
        this.gameTime = gameTime;
        this.host = host;
    }

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

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isEmotesEnabled() {
        return emotesEnabled;
    }

    public void setEmotesEnabled(boolean emotesEnabled) {
        this.emotesEnabled = emotesEnabled;
    }

    public boolean isChatEnabled() {
        return chatEnabled;
    }

    public void setChatEnabled(boolean chatEnabled) {
        this.chatEnabled = chatEnabled;
    }

    public int getGameTime() {
        return gameTime;
    }

    public void setGameTime(int gameTime) {
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