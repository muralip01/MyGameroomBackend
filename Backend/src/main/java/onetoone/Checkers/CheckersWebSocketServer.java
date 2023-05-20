package onetoone.Checkers;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ServerEndpoint("/checkers/{gameId}/{userId}")
@Component
public class CheckersWebSocketServer {


    private CheckersService checkersService = CheckersServiceHolder.getCheckersService();
    private static Map<Integer, Map<Integer, Session>> gameSessions = new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(CheckersWebSocketServer.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("gameId") Integer gameId, @PathParam("userId") Integer userId) {
        logger.info("Entered into Open");

        gameSessions.computeIfAbsent(gameId, k -> new Hashtable<>()).put(userId, session);
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("gameId") Integer gameId, @PathParam("userId") Integer userId) {
        logger.info("Entered into Message: Got Message:" + message);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Move move = objectMapper.readValue(message, Move.class);
            CheckersGame game = checkersService.getCheckersGameById(gameId);
            if (game.isCurrentPlayer(userId)) {
                String validationResult = game.isValidMove(move);
                if (validationResult == null) {
                    logger.info("Move is valid, applying move");
                    game.applyMove(move);
                    broadcastUpdatedBoard(game);
                    checkGameStatusAndSendInfo(session, game);
                } else {
                    logger.warn("Move is invalid: " + validationResult);
                    sendErrorMessage(session, validationResult);
                }
            } else {
                logger.warn("Not current player's turn");
                sendErrorMessage(session, "It's not your turn");
            }
        } catch (IOException e) {
            logger.error("Error parsing JSON move object", e);
            sendErrorMessage(session, "Error parsing JSON move object");
        }
    }


    @OnClose
    public void onClose(Session session, @PathParam("gameId") Integer gameId, @PathParam("userId") Integer userId) {
        logger.info("Entered into Close");

        Map<Integer, Session> sessions = gameSessions.get(gameId);
        if (sessions != null) {
            sessions.remove(userId);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("Error occurred in WebSocket server", throwable);
    }

    private void broadcastUpdatedBoard(CheckersGame game) {
        Map<Integer, Session> sessions = gameSessions.get(game.getGameId());

        if (sessions != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String boardStateJson = objectMapper.writeValueAsString(game.getBoardState());

                sessions.forEach((userId, session) -> {
                    try {
                        session.getBasicRemote().sendText(boardStateJson);
                    } catch (IOException e) {
                        logger.info("Exception: " + e.getMessage().toString());
                        e.printStackTrace();
                    }
                });
            } catch (JsonProcessingException e) {
                logger.error("Error converting board state to JSON", e);
            }
        }
    }

    private void checkGameStatusAndSendInfo(Session session, CheckersGame game) {
        if (game.isGameOver()) {
            String message;
            if (game.getStatus() == CheckersGame.GameStatus.FINISHED) {
                Piece.Color winnerColor = game.getCurrentPlayer() == Piece.Color.RED ? Piece.Color.BLACK : Piece.Color.RED;
                message = "Game over. Winner: " + winnerColor;
            } else if (game.getStatus() == CheckersGame.GameStatus.FORFEITED) {
                message = "Game forfeited.";
            } else {
                return;
            }
            sendInfoMessage(session, message);
        }
    }

    private void sendInfoMessage(Session session, String infoMessage) {
        try {
            session.getBasicRemote().sendText(infoMessage);
        } catch (IOException e) {
            logger.error("Failed to send info message", e);
        }
    }

    private void sendErrorMessage(Session session, String errorMessage) {
        try {
            session.getBasicRemote().sendText("Invalid: " + errorMessage);
        } catch (IOException e) {
            logger.error("Failed to send error message", e);
        }
    }
}