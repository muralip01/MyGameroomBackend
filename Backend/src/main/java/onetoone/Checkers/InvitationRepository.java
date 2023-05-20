package onetoone.Checkers;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitationRepository extends CrudRepository<Invitation, Integer> {

    Invitation findByGameIdAndUserId(int gameId, int userId);

    Iterable<Invitation> findByUserId(int userId);

    void deleteByGameId(int gameId);

    List<Invitation> findByGameId(int gameId);
}