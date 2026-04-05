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

    public static Response loginUserResponse(String email, String password) {

        String apiPath = "/APIDEV/login";
        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(loginPayload(email, password))
                .log().all()
                .post().prettyPeek();
        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Expected status code 200, but got " + actualStatusCode);
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
                .post().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        UserID = response.jsonPath().getString("data.id");
        Assert.assertEquals(actualStatusCode, 201, "Expected status code 201, but got " + actualStatusCode);
        System.out.println("User ID: " + UserID);


        return response;
    }

    public static Response userApprovalResponse() {

        String apiPath = "/APIDEV/admin/users/" + UserID + "/approve";

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .log().all()
                .put().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Expected status code 200, but got " + actualStatusCode);

        return response;
    }

    public static Response makeUserAdminResponse(String role) {

        String apiPath = "/APIDEV/admin/users/" + UserID + "/role";

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(loginPayload(role, role))
                .log().all()
                .put().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Expected status code 200, but got " + actualStatusCode);

        return response;
    }

    public static Response loginWithAdminStatusResponse(String email, String password) {

        String apiPath = "/APIDEV/login";
        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(loginPayload(email, password))
                .log().all()
                .post().prettyPeek();
        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Expected status code 200, but got " + actualStatusCode);
        authToken = response.jsonPath().getString("data.token");

        System.out.println("Admin Auth token: " + authToken);

        return response;
    }

    public static Response getGroupsResponse() {

        String apiPath = "/APIDEV/groups";

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .log().all()
                .get().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Expected status code 200, but got " + actualStatusCode);

        List<Map<String, Object>> groups = response.jsonPath().getList("data");

        groupID = groups.stream()
                .filter(g -> g.get("Name").equals("Group T"))
                .map(g -> g.get("Id").toString())
                .findFirst()
                .orElse(null);

        Assert.assertNotNull(groupID, "Group T not found!");
        System.out.println("Group ID: " + groupID);

        return response;
    }

    public static Response assignUserToGroupResponse() {

        String apiPath = "/APIDEV/admin/users/" + UserID + "/group";

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(assignUserToGroupPayload(groupID))
                .log().all()
                .put().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Expected status code 200, but got " + actualStatusCode);

        String groupName = response.jsonPath().getString("data.groupName");
        Assert.assertEquals(groupName, "Group T", "Expected group name 'Group T', but got " + groupName);
        System.out.println("Assigned Group Name: " + groupName);

        return response;
    }

    public static Response loginToSeeGroupChangeResponse(String email, String password) {

        String apiPath = "/APIDEV/login";
        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(loginPayload(email, password))
                .log().all()
                .post().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Expected status code 200, but got " + actualStatusCode);
        String actualGroupName = response.jsonPath().getString("data.user.groupName");
        Assert.assertEquals(actualGroupName, "Group T", "Expected group name 'Group T', but got " + actualGroupName);
        System.out.println("New Group Name: " + actualGroupName);

        return response;
    }

    public static Response deleteUserResponse() {

        String apiPath = "/APIDEV/admin/users/" + UserID;

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .log().all()
                .delete().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Expected status code 200, but got " + actualStatusCode);

        return response;
    }


}
