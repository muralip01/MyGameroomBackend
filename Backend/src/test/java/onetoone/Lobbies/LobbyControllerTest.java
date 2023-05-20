package onetoone.Lobbies;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import onetoone.Users.User;
import onetoone.Users.UserRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LobbyControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LobbyRepository lobbyRepository;

    private User host;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        host = new User("host", "host@email.com", "password");
        userRepository.save(host);
    }

    @Test
    public void testCreateLobby() {
        int hostId = host.getId();
        String lobbyName = "Test Lobby";
        int maxMembers = 10;
        String accessCode = "1234";

        String jsonBody = "{ \"hostId\": " + hostId + ", \"lobbyName\": \"" + lobbyName + "\", \"maxMembers\": \"" + maxMembers + "\", \"accessCode\": \"" + accessCode + "\" }";

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post("/lobbies/create");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("Lobby created successfully.", returnObj.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testListLobbies() {
        String jsonBody = "{ \"hostId\": " + host.getId() + ", \"lobbyName\": \"Test Lobby\", \"maxMembers\": 10 }";
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post("/lobbies/create");

        Response response = RestAssured.given()
                .when()
                .get("/lobbies/list");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try {
            JSONArray returnArr = new JSONArray(returnString);
            JSONObject createdLobby = null;

            for (int i = 0; i < returnArr.length(); i++) {
                JSONObject lobby = returnArr.getJSONObject(i);
                if (lobby.getString("name").equals("Test Lobby")) {
                    createdLobby = lobby;
                    break;
                }
            }

            assertNotNull(createdLobby, "Created lobby not found in the list of lobbies");
            assertEquals("Test Lobby", createdLobby.getString("name"));
            assertEquals(10, createdLobby.getInt("maxMembers"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateLobbySettings() {
        String createLobbyBody = "{ \"hostId\": " + host.getId() + ", \"lobbyName\": \"Test Lobby\", \"maxMembers\": 10 }";
        Response createLobbyResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(createLobbyBody)
                .when()
                .post("/lobbies/create");

        String lobbyId = createLobbyResponse.getBody().jsonPath().getString("id");

        String updateSettingsBody = "{ \"userId\": " + host.getId() + ", \"lobbyId\": " + lobbyId + ", \"lobbyDto\": { \"name\": \"Updated Lobby\", \"maxMembers\": 15, \"gameTime\": 180, \"emotesEnabled\": true, \"chatEnabled\": false, \"private\": true, \"accessCode\": \"5678\" } }";
        Response updateSettingsResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updateSettingsBody)
                .when()
                .put("/lobbies/update");

        assertEquals(200, updateSettingsResponse.getStatusCode());
        assertEquals("Lobby settings updated successfully.", updateSettingsResponse.getBody().asString());

        Response getLobbyResponse = RestAssured.given()
                .when()
                .get("/lobbies/list");

        String returnString = getLobbyResponse.getBody().asString();
        try {
            JSONArray returnArr = new JSONArray(returnString);
            JSONObject updatedLobby = null;

            for (int i = 0; i < returnArr.length(); i++) {
                JSONObject lobby = returnArr.getJSONObject(i);
                if (lobby.getString("id").equals(lobbyId)) {
                    updatedLobby = lobby;
                    break;
                }
            }

            assertNotNull(updatedLobby, "Updated lobby not found in the list of lobbies");
            assertEquals("Updated Lobby", updatedLobby.getString("name"));
            assertEquals(15, updatedLobby.getInt("maxMembers"));
            assertEquals(180, updatedLobby.getInt("gameTime"));
            assertTrue(updatedLobby.getBoolean("emotesEnabled"));
            assertFalse(updatedLobby.getBoolean("chatEnabled"));
            assertTrue(updatedLobby.getBoolean("private"));
            assertEquals("5678", updatedLobby.getString("accessCode"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


  /*  @Test
    public void testJoinLobby() {
        User user = new User("user", "user@email.com", "password");
        userRepository.save(user);

        String createLobbyBody = "{ \"hostId\": " + host.getId() + ", \"lobbyName\": \"Test Lobby\", \"maxMembers\": 10 }";
        Response createLobbyResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(createLobbyBody)
                .when()
                .post("/lobbies/create");

        String lobbyId = createLobbyResponse.getBody().jsonPath().getString("id");

        String joinLobbyBody = "{ \"userId\": " + user.getId() + ", \"lobbyId\": " + lobbyId + " }";
        Response joinLobbyResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(joinLobbyBody)
                .when()
                .post("/lobbies/join");

        assertEquals(200, joinLobbyResponse.getStatusCode());
        assertEquals("Joined lobby successfully.", joinLobbyResponse.getBody().asString());

        Optional<Lobby> lobbyOptional = lobbyRepository.findByIdWithMembers(Integer.parseInt(lobbyId));
        assertTrue(lobbyOptional.isPresent(), "Lobby not found");

        Lobby lobby = lobbyOptional.get();
        boolean userFound = false;
        for (User member : lobby.getMembers()) {
            if (member.getId() == user.getId()) {
                userFound = true;
                break;
            }
        }

        assertTrue(userFound, "User not found in the lobby members");

        userRepository.deleteById(user.getId());
    }*/

    @Test
    public void testLeaveLobby() {
        User user = new User("user", "user@email.com", "password");
        userRepository.save(user);

        String createLobbyBody = "{ \"hostId\": " + host.getId() + ", \"lobbyName\": \"Test Lobby\", \"maxMembers\": 10 }";
        Response createLobbyResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(createLobbyBody)
                .when()
                .post("/lobbies/create");

        String lobbyId = createLobbyResponse.getBody().jsonPath().getString("id");

        String joinLobbyBody = "{ \"userId\": " + user.getId() + ", \"lobbyId\": " + lobbyId + " }";
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(joinLobbyBody)
                .when()
                .put("/lobbies/join");

        String leaveLobbyBody = "{ \"userId\": " + user.getId() + ", \"lobbyId\": " + lobbyId + " }";
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(leaveLobbyBody)
                .when()
                .put("/lobbies/leave");

        String lobbyMembersBody = "{ \"userId\": " + user.getId() + ", \"lobbyId\": " + lobbyId + " }";
        Response getLobbyResponse = RestAssured.given()
                .body(lobbyMembersBody)
                .when()
                .get("/lobbyMembers/");

        assertFalse(getLobbyResponse.getBody().asString().contains(user.getName()));

        userRepository.deleteById(user.getId());
    }


    @After
    public void tearDown() {
        try {
            lobbyRepository.deleteAllLobbiesByHost(host);
        } catch (Exception e) {
            System.out.println("Error deleting lobbies: " + e.getMessage());
        }
        userRepository.deleteById(host.getId());
    }
}