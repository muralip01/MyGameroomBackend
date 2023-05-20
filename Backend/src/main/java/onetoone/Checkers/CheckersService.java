package onetoone.Checkers;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CheckersService {

    private Map<Integer, CheckersGame> games = new HashMap<>();
    private Map<Integer, OngoingGame> ongoingGames = new HashMap<>();
    private AtomicInteger gameIdCounter = new AtomicInteger();

    public CheckersGame createGame(int player1Id, int player2Id, int player3Id, int player4Id, boolean forceCaptures, boolean continuousCaptures, boolean teamsMode, int numberOfMoves) {
        int gameId = gameIdCounter.incrementAndGet();
        CheckersGame game = new CheckersGame(gameId, player1Id, player2Id, player3Id, player4Id, forceCaptures, continuousCaptures, teamsMode, numberOfMoves);
        games.put(gameId, game);

        // Save the ongoing game
        OngoingGame ongoingGame = new OngoingGame(gameId, player1Id, player2Id, player3Id, player4Id);
        ongoingGames.put(gameId, ongoingGame);

        return game;
    }

    public CheckersGame getCheckersGameById(int gameId) {
        return games.get(gameId);
    }

    public Map<Integer, OngoingGame> getFilteredOngoingGames() {
        Map<Integer, OngoingGame> filteredOngoingGames = new HashMap<>();
        for (Map.Entry<Integer, OngoingGame> entry : ongoingGames.entrySet()) {
            if (games.get(entry.getKey()).getStatus() == CheckersGame.GameStatus.ONGOING) {
                filteredOngoingGames.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredOngoingGames;
    }

    public Map<Integer, CheckersGame> getAllGames() {
        return games;
    }

    public void removeOngoingGame(int gameId) {
        ongoingGames.remove(gameId);
    }

    public void forfeitGame(int gameId, int playerId) {
        CheckersGame game = getCheckersGameById(gameId);
        if (game != null && (game.getPlayer1Id() == playerId || game.getPlayer2Id() == playerId || game.getPlayer3Id() == playerId || game.getPlayer4Id() == playerId)) {
            game.setStatus(CheckersGame.GameStatus.FORFEITED);
        }
    }
}