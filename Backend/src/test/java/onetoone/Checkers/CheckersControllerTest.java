package onetoone.Checkers;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CheckersControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private UserRepository userRepository;

    private User player1;
    private User player2;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        player1 = new User("player1", "player1@email.com", "password");
        player2 = new User("player2", "player2@email.com", "password");
        userRepository.save(player1);
        userRepository.save(player2);
    }

    @Test
    public void testCreateNewGame() {
        int player1Id = player1.getId();
        int player2Id = player2.getId();

        String jsonBody = "{ \"player1Id\": \"" + player1Id + "\", \"player2Id\": \"" + player2Id + "\" }";

        Response response = given().
                contentType(ContentType.JSON).
                body(jsonBody).
                when().
                post("/checkers/createGame");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        JSONObject returnObj;
        try {
            returnObj = new JSONObject(returnString);
            assertEquals(player1Id, returnObj.getJSONObject("player1").getInt("id"));
            assertEquals(player2Id, returnObj.getJSONObject("player2").getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @After
    public void tearDown() {
        try {
        } catch (Exception e) {
            System.out.println("Error deleting checker games: " + e.getMessage());
        }
        userRepository.deleteById(player1.getId());
        userRepository.deleteById(player2.getId());
    }
}