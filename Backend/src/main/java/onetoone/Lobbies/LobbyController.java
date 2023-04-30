package onetoone.Lobbies;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import onetoone.Users.User;
import onetoone.Users.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 *
 * @author Muralikrishna Patibandla
 *
 */

@Api(value = "LobbyController", description = "REST APIs related to Lobbies | annotated by Muralikrishna Patibandla")
@RestController
@RequestMapping("/lobbies")
public class LobbyController {

    private final UserRepository userRepository;
    private final LobbyRepository lobbyRepository;
    private final LobbyService lobbyService;

    public LobbyController(UserRepository userRepository, LobbyRepository lobbyRepository, LobbyService lobbyService) {
        this.userRepository = userRepository;
        this.lobbyRepository = lobbyRepository;
        this.lobbyService = lobbyService;
    }

    @ApiOperation(value = "Create a new lobby", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lobby created successfully"),
            @ApiResponse(code = 400, message = "User not found | Error creating lobby")
    })
    @PostMapping("/create")
    public ResponseEntity<Object> createLobby(@RequestBody CreateLobbyRequest request) {
        Optional<User> hostOptional = Optional.ofNullable(userRepository.findById(request.getHostId()));
        if (!hostOptional.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User host = hostOptional.get();

        Lobby lobby = new Lobby();
        lobby.setHost(host);
        lobby.setName(request.getLobbyName());
        lobby.setMaxMembers(request.getMaxMembers());
        lobby.setPrivate(request.getAccessCode() != null);
        if (lobby.isPrivate()) {
            lobby.setAccessCode(request.getAccessCode());
        }
        lobby.setGameTime(15 * 60); // Default game time: 15 minutes
        lobby.setEmotesEnabled(true);// Default settings: emotes enabled.
        lobby.getMembers().add(host);
        lobbyRepository.save(lobby);

        return ResponseEntity.ok("{\"message\": \"Lobby created successfully.\", \"id\": " + lobby.getId() + "}");
    }

    @ApiOperation(value = "List available lobbies", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of lobbies")
    })
    @GetMapping("/list")
    public ResponseEntity<Object> listLobbies() {
        List<LobbyListDto> lobbyList = lobbyService.getLobbyList();
        return ResponseEntity.ok(lobbyList);
    }

    @ApiOperation(value = "Update lobby settings", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lobby settings updated successfully"),
            @ApiResponse(code = 400, message = "User or lobby not found | Bad request | Error updating lobby settings")
    })
    @PutMapping("/update")
    public ResponseEntity<Object> updateLobbySettings(@RequestBody UpdateLobbySettingsRequest request) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findById(request.getUserId()));
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        Optional<Lobby> lobbyOptional = lobbyRepository.findById(request.getLobbyId());
        if (!lobbyOptional.isPresent()) {
            return ResponseEntity.badRequest().body("Lobby not found");
        }
        Lobby lobby = lobbyOptional.get();

        // Ensure that the request is from the host
        if (lobby.getHost().getId()!=(request.getUserId())) {
            return ResponseEntity.badRequest().body("Only the host can update the lobby settings.");
        }

        if (request.getLobbyDto().getName() != null) {
            lobby.setName(request.getLobbyDto().getName());
        }
        if (request.getLobbyDto().getMaxMembers() != null) {
            lobby.setMaxMembers(request.getLobbyDto().getMaxMembers());
        }
        if (request.getLobbyDto().getGameTime() != null) {
            lobby.setGameTime(request.getLobbyDto().getGameTime());
        }
        if (request.getLobbyDto().isEmotesEnabled() != null) {
            lobby.setEmotesEnabled(request.getLobbyDto().isEmotesEnabled());
        }
        if (request.getLobbyDto().isChatEnabled() != null) {
            lobby.setChatEnabled(request.getLobbyDto().isChatEnabled());
        }
        if (request.getLobbyDto().isPrivate() != null) {
            lobby.setPrivate(request.getLobbyDto().isPrivate());
        }
        if (lobby.isPrivate()) {
            if (request.getLobbyDto().getAccessCode() != null) {
                lobby.setAccessCode(request.getLobbyDto().getAccessCode());
            }
        } else {
            lobby.setAccessCode(null);
        }

        lobbyRepository.save(lobby);

        return ResponseEntity.ok("Lobby settings updated successfully.");
    }

    @ApiOperation(value = "Join a lobby", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Joined lobby successfully"),
            @ApiResponse(code = 400, message = "User or lobby not found | Bad request | Error joining lobby")
    })
    @PostMapping("/join")
    public ResponseEntity<Object> joinLobby(@RequestBody JoinLobbyRequest request) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findById(request.getUserId()));
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOptional.get();

        Optional<Lobby> lobbyOptional = lobbyRepository.findById(request.getLobbyId());
        if (!lobbyOptional.isPresent()) {
            return ResponseEntity.badRequest().body("Lobby not found");
        }
        Lobby lobby = lobbyOptional.get();

        if (lobby.isPrivate() && (request.getAccessCode() == null || !request.getAccessCode().equals(lobby.getAccessCode()))) {
            return ResponseEntity.badRequest().body("Invalid access code.");
        }

        if (lobby.getMembers().contains(user)) {
            return ResponseEntity.badRequest().body("User is already in the lobby.");
        }

        if (lobby.getMembers().size() >= lobby.getMaxMembers())
            return ResponseEntity.badRequest().body("Lobby is full.");

        lobby.getMembers().add(user);
        lobbyRepository.save(lobby);

        return ResponseEntity.ok("Joined lobby successfully.");
    }

    @ApiOperation(value = "Leave a lobby", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Left lobby successfully"),
            @ApiResponse(code = 400, message = "User or lobby not found | Bad request | Error leaving lobby")
    })
    @PostMapping("/leave")
    public ResponseEntity<Object> leaveLobby(@RequestBody LeaveLobbyRequest request) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findById(request.getUserId()));
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOptional.get();

        Optional<Lobby> lobbyOptional = lobbyRepository.findById(request.getLobbyId());
        if (!lobbyOptional.isPresent()) {
            return ResponseEntity.badRequest().body("Lobby not found");
        }
        Lobby lobby = lobbyOptional.get();

        if (!lobby.getMembers().contains(user) && !user.equals(lobby.getHost())) {
            return ResponseEntity.badRequest().body("User is not in the lobby.");
        }

        if (user.equals(lobby.getHost())) {
            lobbyRepository.deleteById(lobby.getId());
            return ResponseEntity.ok("Lobby disbanded.");
        } else {
            lobby.getMembers().remove(user);
            lobbyRepository.save(lobby);
            return ResponseEntity.ok("Left lobby successfully.");
        }
    }

    @ApiOperation(value = "View lobby members", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved lobby members"),
            @ApiResponse(code = 400, message = "User or lobby not found | Error retrieving lobby members")
    })
    @PostMapping("/lobbyMembers")
    public ResponseEntity<Object> viewLobbyMembers(@RequestBody JoinLobbyRequest request) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findById(request.getUserId()));
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOptional.get();

        Optional<Lobby> lobbyOptional = lobbyRepository.findById(request.getLobbyId());
        if (!lobbyOptional.isPresent()) {
            return ResponseEntity.badRequest().body("Lobby not found");
        }
        Lobby lobby = lobbyOptional.get();

        if (!lobby.getMembers().contains(user) && !user.equals(lobby.getHost())) {
            return ResponseEntity.badRequest().body("User is not in the lobby.");
        }

        List<User> members = new ArrayList<>(lobby.getMembers());
        User host = lobby.getHost();

        Map<String, Object> response = new HashMap<>();
        response.put("host", host);
        response.put("members", members);

        return ResponseEntity.ok(response);
    }
}