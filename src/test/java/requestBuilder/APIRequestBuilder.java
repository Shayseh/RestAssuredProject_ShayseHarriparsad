package requestBuilder;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

import static commonVariables.BaseURIs.baseURL;
import static payloadBuilder.PayloadBuilder.*;

public class APIRequestBuilder {

    static String authToken;
    static String UserID;
    static String groupID;

    public static Response loginAdminResponse(String email, String password) {

        String apiPath = "/APIDEV/login";
        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(loginPayload(email, password))
                .log().all() // Log the request details for debugging
                .post()
                .then().extract().response(); // Extract the response to access the token
        authToken = response.jsonPath().getString("data.token");
        System.out.println("Admin Auth token: " + authToken);

        return response;
    }

    public static Response registerUserResponse(String firstName, String lastName, String email, String password, String groupID) {

        String apiPath = "/APIDEV/register";

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(registerUserPayload(firstName, lastName, email, password, groupID))
                .log().all()
                .post()
                .then().extract().response();
        UserID = response.jsonPath().getString("data.id");
        System.out.println("User ID: " + UserID);

        return response;
    }

    public static Response userRegistrationApprovalResponse() {

        String apiPath = "/APIDEV/admin/users/" + UserID + "/approve";
        return RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .log().all()
                .put()
                .then().extract().response();
    }

    public static Response makeUserAdminResponse(String role) {

        String apiPath = "/APIDEV/admin/users/" + UserID + "/role";

        return RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(changeUserToAdminPayload(role))
                .log().all()
                .put()
                .then().extract().response();
    }

    public static Response userLoginResponse(String email, String password) {

        String apiPath = "/APIDEV/login";
        return RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(loginPayload(email, password))
                .log().all()
                .post()
                .then().extract().response();
    }

    public static Response getGroupsResponse(String groupName) {

        String apiPath = "/APIDEV/groups";

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .log().all()
                .get()
                .then().extract().response();
        List<Map<String, Object>> groups = response.jsonPath().getList("data"); // Extract the list of groups from the response

        groupID = groups.stream()  // Stream the list of groups
                .filter(g -> g.get("Name").equals(groupName)) // Filter groups to find the one with the specified name
                .map(g -> g.get("Id").toString()) // Extract the ID of the matching group and convert it to a string
                .findFirst()
                .orElse(null);

        Assert.assertNotNull(groupID, groupName + " not found!");
        System.out.println("Group ID for " + groupName + ": " + groupID);

        return response;
    }

    public static Response assignUserToGroupResponse() {

        String apiPath = "/APIDEV/admin/users/" + UserID + "/group";

        return RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(assignUserToGroupPayload(groupID))
                .log().all()
                .put()
                .then().extract().response();
    }

    public static Response deleteUserResponse() {

        String apiPath = "/APIDEV/admin/users/" + UserID;

        return RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .log().all()
                .delete()
                .then().extract().response();
    }


}
