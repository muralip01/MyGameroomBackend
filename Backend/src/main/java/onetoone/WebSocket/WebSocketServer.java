package onetoone.WebSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import onetoone.Checkers.CheckersController;
import onetoone.Checkers.CheckersGame;
import onetoone.Checkers.CheckersBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

@ServerEndpoint("/websocket/{username}")
@Component
public class WebSocketServer {

    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();
    private static Map<String, CheckersGame> checkersGames = new Hashtable<>();
    private final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);
    }

    @OnMessage
    public synchronized void onMessage(Session session, String message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode msgObject = mapper.readValue(message, ObjectNode.class);

        if (msgObject.has("gameId")) {
            String gameId = msgObject.get("gameId").asText();
            int fromRow = msgObject.get("fromRow").asInt();
            int fromCol = msgObject.get("fromCol").asInt();
            int toRow = msgObject.get("toRow").asInt();
            int toCol = msgObject.get("toCol").asInt();

            CheckersGame game = checkersGames.get(gameId);

            if (game == null) {
                game = new CheckersGame();
                checkersGames.put(gameId, game);
            }

            CheckersController controller = new CheckersController(game);
            controller.move(fromRow, fromCol, toRow, toCol);

            broadcast(message);
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        String username = sessionUsernameMap.get(session);
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {}

    private void broadcast(String message) {
        sessionUsernameMap.forEach((session, username) -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}