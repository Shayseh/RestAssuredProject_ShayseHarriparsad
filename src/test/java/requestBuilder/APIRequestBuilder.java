package requestBuilder;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import utilities.AllureUtils;

import java.util.List;
import java.util.Map;

import static commonVariables.BaseURIs.baseURL;
import static payloadBuilder.PayloadBuilder.*;

public class APIRequestBuilder {

    static String authToken;
    static String UserID;
    static String groupID;
    static String newAuthToken;

    @Step("Login as Admin with email: {email}")
    public static Response loginAdminResponse(String email, String password) {

        String apiPath = "/APIDEV/login";
        Response response = RestAssured.given()
                .filter(new AllureRestAssured())
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(loginPayload(email, password))
                .log().all()
                .post()
                .then().extract().response();
        authToken = response.jsonPath().getString("data.token");
        System.out.println("Admin Auth token: " + authToken);
        AllureUtils.attachStatusCode(response.getStatusCode());

        return response;
    }

    @Step("Register user: {firstName} {lastName} with email: {email}")
    public static Response registerUserResponse(String firstName, String lastName, String email, String password, String groupID) {

        String apiPath = "/APIDEV/register";

        Response response = RestAssured.given()
                .filter(new AllureRestAssured())
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(registerUserPayload(firstName, lastName, email, password, groupID))
                .log().all()
                .post()
                .then().extract().response();
        UserID = response.jsonPath().getString("data.id");
        System.out.println("User ID: " + UserID);
        AllureUtils.attachStatusCode(response.getStatusCode());

        return response;
    }

    @Step("Approve user registration for user ID: " + "dynamic")
    public static Response userRegistrationApprovalResponse() {

        String apiPath = "/APIDEV/admin/users/" + UserID + "/approve";
        Response response = RestAssured.given()
                .filter(new AllureRestAssured())
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .log().all()
                .put()
                .then().extract().response();
        AllureUtils.attachStatusCode(response.getStatusCode());
        return response;
    }

    @Step("Change user role to: {role}")
    public static Response makeUserAdminResponse(String role) {

         String apiPath = "/APIDEV/admin/users/" + UserID + "/role";

         Response response = RestAssured.given()
                .filter(new AllureRestAssured())
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(changeUserToAdminPayload(role))
                .log().all()
                .put()
                .then().extract().response();
        AllureUtils.attachStatusCode(response.getStatusCode());
        return response;
    }

    @Step("Login as user with email: {email}")
    public static Response userLoginResponse(String email, String password) {

        String apiPath = "/APIDEV/login";
        Response response = RestAssured.given()
                .filter(new AllureRestAssured())
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(loginPayload(email, password))
                .log().all()
                .post()
                .then().extract().response();
        newAuthToken = response.jsonPath().getString("data.token");
        System.out.println("Admin Auth token: " + newAuthToken);
        AllureUtils.attachStatusCode(response.getStatusCode());

        return response;
    }

    @Step("Get groups and find group: {groupName}")
    public static Response getGroupsResponse(String groupName) {

        String apiPath = "/APIDEV/groups";

        Response response = RestAssured.given()
                .filter(new AllureRestAssured())
                .baseUri(baseURL)
                .basePath(apiPath)
                .log().all()
                .get()
                .then().extract().response();
        List<Map<String, Object>> groups = response.jsonPath().getList("data");

        groupID = groups.stream()
                .filter(g -> g.get("Name").equals(groupName))
                .map(g -> g.get("Id").toString())
                .findFirst()
                .orElse(null);

        Assert.assertNotNull(groupID, groupName + " not found!");
        System.out.println("Group ID for " + groupName + ": " + groupID);
        AllureUtils.addStep("Group Found", "Group: " + groupName + " | ID: " + groupID);

        return response;
    }

    @Step("Assign user to group")
    public static Response assignUserToGroupResponse() {

        String apiPath = "/APIDEV/admin/users/" + UserID + "/group";

        Response response = RestAssured.given()
                .filter(new AllureRestAssured())
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + newAuthToken)
                .body(assignUserToGroupPayload(groupID))
                .log().all()
                .put()
                .then().extract().response();
        AllureUtils.attachStatusCode(response.getStatusCode());
        return response;
    }

    @Step("Delete user")
    public static Response deleteUserResponse() {

        String apiPath = "/APIDEV/admin/users/" + UserID;

        Response response = RestAssured.given()
                .filter(new AllureRestAssured())
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .log().all()
                .delete()
                .then().extract().response();
        AllureUtils.attachStatusCode(response.getStatusCode());
        return response;
    }


}
