package onetoone.Friends;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import onetoone.Users.User;
import onetoone.Users.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Muralikrishna Patibandla
 *
 */

@Api(value = "FriendRequestController", description = "REST APIs related to Friend Requests | annotated by Muralikrishna Patibandla")
@RestController
@RequestMapping("/friends")

public class FriendRequestController {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public FriendRequestController(UserRepository userRepository, FriendRequestRepository friendRequestRepository) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    @ApiOperation(value = "Get pending friend requests", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved pending friend requests"),
            @ApiResponse(code = 400, message = "User not found | Error retrieving pending requests")
    })
    @GetMapping("/{id}/pending")
    public ResponseEntity<Object> getPendingRequests(@PathVariable int id) {
        try {
            User user = userRepository.findById(id);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            List<FriendRequest> requests = friendRequestRepository.findByReceiverIdAndStatus(id, FriendRequest.FriendRequestStatus.PENDING);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting pending friend requests: " + e.getMessage());
        }

    }

    @ApiOperation(value = "Get all friend requests", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved all friend requests"),
    })
    @GetMapping("/{id}/all")
    public List<FriendRequest> getAll(@PathVariable int id) {
        return friendRequestRepository.findByReceiverId(id);
    }

    @ApiOperation(value = "Get friends list", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved friends list"),
            @ApiResponse(code = 400, message = "User not found | Error retrieving friends list")
    })
    @GetMapping("/{id}/friends")
    public ResponseEntity<Object> getFriends(@PathVariable int id) {
        try {
            User user = userRepository.findById(id);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            List<User> friends = new ArrayList<>();
            List<FriendRequest> acceptedRequests = friendRequestRepository.findBySenderIdAndStatus(id, FriendRequest.FriendRequestStatus.ACCEPTED);
            for (FriendRequest request : acceptedRequests) {
                friends.add(request.getReceiver());
            }
            acceptedRequests = friendRequestRepository.findByReceiverIdAndStatus(id, FriendRequest.FriendRequestStatus.ACCEPTED);
            for (FriendRequest request : acceptedRequests) {
                friends.add(request.getSender());
            }
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting friends list: " + e.getMessage());
        }
    }

    @ApiOperation(value = "Send friend request", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Friend request sent successfully"),
            @ApiResponse(code = 400, message = "User or friend request not found | Error sending friend request")
    })
    @PutMapping("/send")
    public String sendFriendRequest(@RequestBody FriendRequestDto friendRequestDto) {
        try {
            User sender = userRepository.findById(friendRequestDto.getSenderId());
            if (sender == null) {
                throw new RuntimeException("Sender not found");
            }
            User receiver = userRepository.findById(friendRequestDto.getReceiverId());
            if (receiver == null) {
                throw new RuntimeException("Receiver not found");
            }
            FriendRequest request = new FriendRequest();
            request.setSender(sender);
            request.setReceiver(receiver);
            request.setStatus(FriendRequest.FriendRequestStatus.PENDING);
            request.setRequestDate(LocalDateTime.now());
            friendRequestRepository.save(request);
            return "Friend request sent successfully.";
        }
        catch (Exception e) {
            return "Error sending friend request: " + e.getMessage();
        }
    }

    @ApiOperation(value = "Accept friend request", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Friend request accepted successfully"),
            @ApiResponse(code = 400, message = "User or friend request not found | Error accepting friend request")
    })
    @PutMapping("/accept")
    public String acceptFriendRequest(@RequestBody FriendRequestDto friendRequestDto) {
        try {
            FriendRequest request = friendRequestRepository.findBySenderIdAndReceiverIdAndStatus(friendRequestDto.getSenderId(), friendRequestDto.getReceiverId(), FriendRequest.FriendRequestStatus.PENDING)
                    .orElseThrow(() -> new RuntimeException("Friend request not found"));
            request.setStatus(FriendRequest.FriendRequestStatus.ACCEPTED);
            friendRequestRepository.save(request);
            return "Friend request accepted successfully.";
        }
        catch (Exception e) {
            return "Error accepting friend request: " + e.getMessage();
        }
    }

    @ApiOperation(value = "Reject friend request", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Friend request rejected successfully"),
            @ApiResponse(code = 400, message = "User or friend request not found | Error rejecting friend request")
    })
    @PutMapping("/reject")
    public String rejectFriendRequest(@RequestBody FriendRequestDto friendRequestDto) {
        try {
            FriendRequest request = friendRequestRepository.findBySenderIdAndReceiverIdAndStatus(friendRequestDto.getSenderId(), friendRequestDto.getReceiverId(), FriendRequest.FriendRequestStatus.PENDING)
                    .orElseThrow(() -> new RuntimeException("Friend request not found"));
            request.setStatus(FriendRequest.FriendRequestStatus.REJECTED);
            friendRequestRepository.save(request);
            return "Friend request rejected successfully.";
        }
        catch (Exception e) {
            return "Error rejecting friend request: " + e.getMessage();
        }
    }

    @ApiOperation(value = "Delete friend request", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Friend request deleted successfully"),
            @ApiResponse(code = 400, message = "User or friend request not found | Error deleting friend request")
    })
    @DeleteMapping("/{receiverId}/delete/{senderId}")
    public String deleteFriendRequest(@PathVariable int receiverId, @PathVariable int senderId) {
        try {
            List<FriendRequest> requests = friendRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId);
            if (requests == null || requests.isEmpty()) {
                throw new RuntimeException("Friend request not found");
            }
            friendRequestRepository.deleteAll(requests);
            return "Friend request deleted successfully.";
        }
        catch (Exception e) {
            return "Error deleting friend request: " + e.getMessage();
        }
    }
}

