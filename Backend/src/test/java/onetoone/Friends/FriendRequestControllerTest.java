package onetoone.Friends;

import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.restassured.http.ContentType;
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
import io.restassured.response.Response;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.util.List;



@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FriendRequestControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRequestRepository friendRequestRepository;

    private User sender;
    private User receiver;


    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        sender = new User("sender", "sender@email.com", "password");
        receiver = new User("receiver", "receiver@email.com", "password");
        userRepository.save(sender);
        userRepository.save(receiver);
    }

    @Test
    public void testGetPending() {
        int userId = receiver.getId();

        String jsonBody = "{ \"senderId\": \"" + sender.getId() + "\", \"receiverId\": \"" + receiver.getId() + "\" }";
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .put("/friends/send");

        Response response = RestAssured.given()
                .when()
                .get("/friends/{id}/pending", userId);

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try {
            JSONArray returnArr = new JSONArray(returnString);
            JSONObject pendingRequest = null;

            for (int i = 0; i < returnArr.length(); i++) {
                JSONObject request = returnArr.getJSONObject(i);
                if (request.getJSONObject("sender").getInt("id") == sender.getId() &&
                        request.getJSONObject("receiver").getInt("id") == receiver.getId()) {
                    pendingRequest = request;
                    break;
                }
            }

            assertNotNull(pendingRequest, "Pending friend request not found");
            assertEquals("PENDING", pendingRequest.getString("status"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAll() {
        int userId = receiver.getId();

        String jsonBody = "{ \"senderId\": \"" + sender.getId() + "\", \"receiverId\": \"" + receiver.getId() + "\" }";
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .put("/friends/send");

        Response response = RestAssured.given()
                .when()
                .get("/friends/{id}/all", userId);

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try {
            JSONArray returnArr = new JSONArray(returnString);
            JSONObject allRequest = null;

            for (int i = 0; i < returnArr.length(); i++) {
                JSONObject request = returnArr.getJSONObject(i);
                if (request.getJSONObject("sender").getInt("id") == sender.getId() &&
                        request.getJSONObject("receiver").getInt("id") == receiver.getId()) {
                    allRequest = request;
                    break;
                }
            }

            assertNotNull(allRequest, "Friend request not found");
            assertEquals("PENDING", allRequest.getString("status"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Test
    public void testGetFriends() {
        int userId = receiver.getId();

        String jsonBody = "{ \"senderId\": \"" + sender.getId() + "\", \"receiverId\": \"" + receiver.getId() + "\" }";
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .put("/friends/send");
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .put("/friends/accept");

        Response response = RestAssured.given()
                .when()
                .get("/friends/{id}/friends", userId);

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try {
            JSONArray returnArr = new JSONArray(returnString);
            JSONObject friend = null;

            for (int i = 0; i < returnArr.length(); i++) {
                JSONObject user = returnArr.getJSONObject(i);
                if (user.getInt("id") == sender.getId()) {
                    friend = user;
                    break;
                }
            }
            assertNotNull(friend, "Friends not found");
            assertEquals(sender.getId(), friend.getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSendFriendRequest() {
        int senderId = sender.getId();
        int receiverId = receiver.getId();

        String jsonBody = "{ \"senderId\": \"" + senderId + "\", \"receiverId\": \"" + receiverId + "\" }";

        Response response = RestAssured.given().
                contentType(ContentType.JSON).
                body(jsonBody).
                when().
                put("/friends/send");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        assertEquals("Friend request sent successfully.", returnString);

    }

    @Test
    public void testAcceptFriendRequest() {
        int senderId = sender.getId();
        int receiverId = receiver.getId();

        String jsonBody = "{ \"senderId\": \"" + senderId + "\", \"receiverId\": \"" + receiverId + "\" }";

        Response responsesend = RestAssured.given().
                contentType(ContentType.JSON).
                body(jsonBody).
                when().
                put("/friends/send");

        Response response = RestAssured.given().
                contentType(ContentType.JSON).
                body(jsonBody).
                when().
                put("/friends/accept");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        assertEquals("Friend request accepted successfully.", returnString);

    }

    @Test
    public void testRejectFriendRequest() {
        int receiverId = sender.getId();
        int senderId = receiver.getId();

        String jsonBody = "{ \"senderId\": \"" + senderId + "\", \"receiverId\": \"" + receiverId + "\" }";

        Response responsesend = RestAssured.given().
                contentType(ContentType.JSON).
                body(jsonBody).
                when().
                put("/friends/send");

        Response response = RestAssured.given().
                contentType(ContentType.JSON).
                body(jsonBody).
                when().
                put("/friends/reject");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        assertEquals("Friend request rejected successfully.", returnString);

    }

    @After
    public void tearDown() {
        try {
            friendRequestRepository.deleteAllFriendRequestsByUser(sender);
            friendRequestRepository.deleteAllFriendRequestsByUser(receiver);
        } catch (Exception e) {
            System.out.println("Error deleting friend requests: " + e.getMessage());
        }
        userRepository.deleteById(sender.getId());
        userRepository.deleteById(receiver.getId());
    }


}
