package onetoone.Checkers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import onetoone.Users.User;
import onetoone.Users.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Muralikrishna Patibandla
 *
 */

@Api(value = "CheckersController", description = "REST APIs related to Checkers Game | annotated by Muralikrishna Patibandla")
@RestController
@RequestMapping("/checkers")
public class CheckersController {

    @Autowired
    private CheckersService checkersService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @ApiOperation(value = "Create a new checkers game, and specify players and rules", response = CheckersGame.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Game created"),
            @ApiResponse(code = 400, message = "One or both players not found")
    })
    @PostMapping("/createGame")
    public ResponseEntity<?> createCheckersGame(@RequestBody CreateGameRequest request) {
        /*
        * Only 1 and 2 are mandatory. if 3 and 4 are not entered, teamsMode is set to false and 3 and 4 are set to -1.
        * force default value is false and continuous is true. numberofmoves is when doing teamsMode,
        * sets number of moves before switching player on a team.
        * 1->2->3->4
        * 4,2 black team
        * 1,3 red team
        * red goes first
        */

        int player1Id = request.getPlayer1Id();
        int player2Id = request.getPlayer2Id();
        User player1 = userRepository.findById(player1Id);
        User player2 = userRepository.findById(player2Id);

        if (player1 == null || player2 == null) {
            return generateErrorResponse(HttpStatus.BAD_REQUEST, "One or more players not found.");
        }

        boolean teamsMode = request.isTeamsMode() != null ? request.isTeamsMode() : false;
        boolean forceCaptures = request.isForceCaptures() != null ? request.isForceCaptures() : false;
        boolean continuousCaptures = request.isContinuousCaptures() != null ? request.isContinuousCaptures() : true;

        int player3Id = -1;
        int player4Id = -1;
        int numberOfMoves = 1;

        if (teamsMode) {
            player3Id = request.getPlayer3Id();
            player4Id = request.getPlayer4Id();
            numberOfMoves = request.getNumberOfMoves() != null ? request.getNumberOfMoves() : 1;

            User player3 = userRepository.findById(player3Id);
            User player4 = userRepository.findById(player4Id);

            if (player3 == null || player4 == null) {
                return generateErrorResponse(HttpStatus.BAD_REQUEST, "One or more players not found.");
            }
        }

        CheckersGame game = checkersService.createGame(player1Id, player2Id, player3Id, player4Id, forceCaptures, continuousCaptures, teamsMode, numberOfMoves);
        //after creating the game, create and save the invitations
        Invitation invitation1 = new Invitation(game.getGameId(), player2.getId(), "invited", "player2");
        invitationRepository.save(invitation1);
        if (teamsMode) {
            Invitation invitation2 = new Invitation(game.getGameId(), game.getPlayer3Id(), "invited", "player3");
            Invitation invitation3 = new Invitation(game.getGameId(), game.getPlayer4Id(), "invited", "player4");
            invitationRepository.save(invitation2);
            invitationRepository.save(invitation3);
        }
        return ResponseEntity.ok(game);
    }

    @ApiOperation(value = "Accept an invitation", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Invitation accepted")
    })
    @PostMapping("/acceptInvitation/{invitationId}")
    public ResponseEntity<String> acceptInvitation(@PathVariable int invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId).orElse(null);
        if (invitation == null) {
            return new ResponseEntity<>("Invitation not found with ID: " + invitationId, HttpStatus.NOT_FOUND);
        }
        // invitation.setStatus("accepted"); //redundant
        // invitationRepository.save(invitation); //redundant
        int gameId = invitation.getGameId();

        invitationRepository.deleteById(invitationId);

        return ResponseEntity.ok(Integer.toString(gameId));
    }

    @ApiOperation(value = "Reject an invitation", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Invitation rejected")
    })
    @PostMapping("/rejectInvitation/{invitationId}")
    public ResponseEntity<?> rejectInvitation(@PathVariable int invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId).orElse(null);
        if (invitation == null) {
            return generateErrorResponse(HttpStatus.NOT_FOUND, "Invitation not found with ID: " + invitationId);
        }
        invitationRepository.deleteById(invitationId);
        return ResponseEntity.ok("Invitation with ID: " + invitationId + " has been rejected.");
    }

    @ApiOperation(value = "Delete all existing invitations", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All invitations deleted")
    })
    @DeleteMapping("/deleteAllInvitations")
    public ResponseEntity<String> deleteAllInvitations() {
        invitationRepository.deleteAll();
        return ResponseEntity.ok("All invitations have been deleted.");
    }

    @ApiOperation(value = "Get all invitations received by the user", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Invitations retrieved")
    })
    @GetMapping("/getInvitations/{userId}")
    public ResponseEntity<?> getInvitations(@PathVariable int userId) {
        Iterable<Invitation> invitations = invitationRepository.findByUserId(userId);
        return ResponseEntity.ok(invitations);
    }

    @ApiOperation(value = "Get a checkers game by ID", response = CheckersGame.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Game found"),
            @ApiResponse(code = 404, message = "Game not found with ID"),
            @ApiResponse(code = 410, message = "Game has been forfeited")
    })
    @GetMapping("/getGame/{gameId}")
    public ResponseEntity<?> getCheckersGame(@PathVariable int gameId) {
        CheckersGame game = checkersService.getCheckersGameById(gameId);
        if (game != null) {
            if (game.getStatus() == CheckersGame.GameStatus.FORFEITED) {
                return generateErrorResponse(HttpStatus.GONE, "Game with ID: " + gameId + " has been forfeited.");
            }
            return ResponseEntity.ok(game);
        }
        return generateErrorResponse(HttpStatus.NOT_FOUND, "Game not found with ID: " + gameId);
    }

    @ApiOperation(value = "Make a move in the checkers game", response = CheckersGame.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Move made"),
            @ApiResponse(code = 400, message = "Bad Request | Invalid move"),
            @ApiResponse(code = 410, message = "Game has been forfeited. No further moves allowed.")
    })
    @PostMapping("/makeMove/{gameId}/{userId}")
    public ResponseEntity<?> makeMove(@PathVariable int gameId, @PathVariable int userId, @RequestBody Move move) {
        CheckersGame game = checkersService.getCheckersGameById(gameId);
        if (game == null) {
            return generateErrorResponse(HttpStatus.BAD_REQUEST, "Game not found with ID: " + gameId);
        }

        User player = userRepository.findById(userId);
        if (player == null) {
            return generateErrorResponse(HttpStatus.BAD_REQUEST, "User not found with ID: " + userId);
        }

        if (!game.isPlayerInGame(userId)) {
            return generateErrorResponse(HttpStatus.BAD_REQUEST, "User ID: " + userId + " is not part of the game with ID: " + gameId);
        }

        if (!game.isCurrentPlayer(userId)) {
            return generateErrorResponse(HttpStatus.BAD_REQUEST, "It's not your turn.");
        }

        if (game.getStatus() == CheckersGame.GameStatus.FORFEITED) {
            return generateErrorResponse(HttpStatus.GONE, "Game with ID: " + gameId + " has been forfeited. No further moves allowed.");
        }

        String validationMessage = game.isValidMove(move);
        if (validationMessage != null) {
            return generateErrorResponse(HttpStatus.BAD_REQUEST, validationMessage);
        }

        game.applyMove(move);
        if (game.getStatus() == CheckersGame.GameStatus.FINISHED) {
            deleteInvitationsByGameId(gameId);
        }

        return ResponseEntity.ok(game);
    }

    @ApiOperation(value = "Get the board state for a checkers game", response = Piece[][].class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Board state retrieved"),
            @ApiResponse(code = 404, message = "Game not found with ID"),
            @ApiResponse(code = 410, message = "Game has been forfeited. No further moves allowed.")
    })
    @GetMapping("/getBoardState/{gameId}")
    public ResponseEntity<?> getBoardState(@PathVariable int gameId) {
        CheckersGame game = checkersService.getCheckersGameById(gameId);
        if (game != null) {
            if (game.getStatus() == CheckersGame.GameStatus.FORFEITED) {
                return generateErrorResponse(HttpStatus.GONE, "Game with ID: " + gameId + " has been forfeited. No further moves allowed.");
            }
            Piece[][] boardState = game.getBoardState();
            return ResponseEntity.ok(boardState);
        }
        return generateErrorResponse(HttpStatus.NOT_FOUND, "Game not found with ID: " + gameId);
    }

    private ResponseEntity<String> generateErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(message);
    }

    @ApiOperation(value = "Get all ongoing games", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ongoing games retrieved")
    })
    @GetMapping("/getAllOngoingGames")
    public ResponseEntity<Map<Integer, OngoingGame>> getAllOngoingGames() {
        Map<Integer, OngoingGame> ongoingGames = checkersService.getFilteredOngoingGames();
        return ResponseEntity.ok(ongoingGames);
    }

    @ApiOperation(value = "Get all games", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All games retrieved")
    })
    @GetMapping("/getAllGames")
    public ResponseEntity<Map<Integer, CheckersGame>> getAllGames() {
        Map<Integer, CheckersGame> allGames = checkersService.getAllGames();
        return ResponseEntity.ok(allGames);
    }

    @ApiOperation(value = "Forfeit a checkers game", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Game forfeited"),
            @ApiResponse(code = 400, message = "Game or player not found | Error forfeiting game")
    })
    @PostMapping("/forfeitGame/{gameId}/{playerId}")
    public ResponseEntity<?> forfeitGame(@PathVariable int gameId, @PathVariable int playerId) {
        CheckersGame game = checkersService.getCheckersGameById(gameId);
        if (game == null) {
            return generateErrorResponse(HttpStatus.BAD_REQUEST, "Game not found with ID: " + gameId);
        }

        if (game.getPlayer1Id() != playerId && game.getPlayer2Id() != playerId && game.getPlayer3Id() != playerId && game.getPlayer4Id() != playerId) {
            return generateErrorResponse(HttpStatus.BAD_REQUEST, "Player ID: " + playerId + " is not part of the game with ID: " + gameId);
        }

        checkersService.forfeitGame(gameId, playerId); // Call the forfeitGame method
        deleteInvitationsByGameId(gameId);
        return ResponseEntity.ok("Game with ID: " + gameId + " has been forfeited by player ID: " + playerId);
    }

    private void deleteInvitationsByGameId(int gameId) {
        List<Invitation> invitations = invitationRepository.findByGameId(gameId);
        invitationRepository.deleteAll(invitations);
    }
}