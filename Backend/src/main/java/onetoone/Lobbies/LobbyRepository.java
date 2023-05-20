package onetoone.Lobbies;

import onetoone.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface LobbyRepository extends JpaRepository<Lobby, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Lobby l WHERE l.host = ?1")
    void deleteAllLobbiesByHost(User host);

    @Query("SELECT l FROM Lobby l JOIN FETCH l.members WHERE l.id = :lobbyId")
    Optional<Lobby> findByIdWithMembers(@Param("lobbyId") Integer lobbyId);


}