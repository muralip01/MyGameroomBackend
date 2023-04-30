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

    @ApiOperation(value = "Create a new checkers game, and specify players and rules", response = CheckersGame.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Game created"),
            @ApiResponse(code = 400, message = "One or both players not found")
    })
    @PostMapping("/createGame")
    public ResponseEntity<?> createCheckersGame(@RequestBody CreateGameRequest request) {
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
        return ResponseEntity.ok(game);
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

        if (game.getPlayer1Id() != playerId && game.getPlayer2Id() != playerId) {
            return generateErrorResponse(HttpStatus.BAD_REQUEST, "Player ID: " + playerId + " is not part of the game with ID: " + gameId);
        }

        checkersService.forfeitGame(gameId, playerId); // Call the forfeitGame method
        return ResponseEntity.ok("Game with ID: " + gameId + " has been forfeited by player ID: " + playerId);
    }

}