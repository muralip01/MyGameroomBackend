package onetoone.Checkers;

import onetoone.MatchHistories.MatchHistory;

import javax.persistence.*;

@Entity
@Table(name = "ongoing_games")
public class OngoingGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "player1_id")
    private int player1Id;

    @Column(name = "player2_id")
    private int player2Id;

    @Column(name = "player3_id")
    private int player3Id;

    @Column(name = "player4_id")
    private int player4Id;

//        @OneToOne(mappedBy = "match_board")
//    private MatchHistory matchHistory;

    public OngoingGame() {
    }

    public OngoingGame(int id, int player1Id, int player2Id, int player3Id, int player4Id) {
        this.id = id;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.player3Id = player3Id;
        this.player4Id = player4Id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public void setPlayer3Id(int player3Id) {
        this.player3Id = player3Id;
    }

    public int getPlayer4Id() {
        return player4Id;
    }

    public void setPlayer4Id(int player4Id) {
        this.player4Id = player4Id;
    }
}