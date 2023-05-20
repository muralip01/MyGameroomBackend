package onetoone.Friends;

import onetoone.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    Optional<FriendRequest> findBySenderIdAndReceiverIdAndStatus(int senderId, int receiverId, FriendRequest.FriendRequestStatus status);
    List<FriendRequest> findByReceiverIdAndStatus(int receiverId, FriendRequest.FriendRequestStatus status);
    List<FriendRequest> findBySenderIdAndStatus(int senderId, FriendRequest.FriendRequestStatus status);

    List<FriendRequest> findByReceiverId(int id);

    List<FriendRequest> findBySenderIdAndReceiverId(int id, int id1);

    @Transactional
    @Modifying
    @Query("DELETE FROM FriendRequest fr WHERE fr.sender = ?1 OR fr.receiver = ?1")
    void deleteAllFriendRequestsByUser(User user);
}
