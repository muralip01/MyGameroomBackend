package onetoone.Friends;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    Optional<FriendRequest> findBySenderIdAndReceiverIdAndStatus(int senderId, int receiverId, FriendRequest.FriendRequestStatus status);
    List<FriendRequest> findByReceiverIdAndStatus(int receiverId, FriendRequest.FriendRequestStatus status);
    List<FriendRequest> findBySenderIdAndStatus(int senderId, FriendRequest.FriendRequestStatus status);

    List<FriendRequest> findByReceiverId(int id);
}
