package basicTest;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class UserRegistration {

    static String authToken;
    static String baseURL = "https://www.ndosiautomation.co.za";
    static String UserID;
    static String registerEmail = Faker.instance().internet().emailAddress(); // Generate a random email address using Java Faker
    static String groupID;

    @Test
    public void adminLoginTest() {

        String apiPath = "/APIDEV/login";
        String payLoad = """
                {
                  "email": "SH@admin.com",
                  "password": "@12345678"
                }""";

        // Send the POST request and capture the response
        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(payLoad)
                .log().all()
                .post().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Expected status code 200, but got " + actualStatusCode);
        authToken = response.jsonPath().getString("data.token");

        System.out.println("Admin Auth token: " + authToken);

    }

    @Test(priority = 1)
    public void userRegistrationTest() {

        String apiPath = "/APIDEV/register";
        String payLoad = String.format(""" 
                {"firstName": "Dayne",
                "lastName": "Assignment",
                "email": "%s",
                "password": "Assignment@26",
                "confirmPassword": "Assignment@26",
                "groupId": "1deae17a-c67a-4bb0-bdeb-df0fc9e2e526"
                }
                """, registerEmail); // Generate a random email address using Java Faker and insert it into the payload

        // Send the POST request and capture the response
        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(payLoad)
                .log().all()
                .post().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        UserID = response.jsonPath().getString("data.id");
        Assert.assertEquals(actualStatusCode, 201, "Expected status code 201, but got " + actualStatusCode);
        System.out.println("User ID: " + UserID);

    }

    @Test(priority = 2)
    public void userApprovalTest() {

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
    }

    @Test(priority = 3)
    public void makeUserAdminTest() {

        String apiPath = "/APIDEV/admin/users/" + UserID + "/role";
        String payLoad = """
                {
                  "role": "admin"
                }
                """;

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(payLoad)
                .log().all()
                .put().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Expected status code 200, but got " + actualStatusCode);
    }

    @Test(priority = 4)
    public void loginWithAdminStatusTest() {

        String apiPath = "/APIDEV/login";
        String payLoad = String.format("""
                {
                  "email": "%s",
                  "password": "Assignment@26"
                }
                """, registerEmail); // Use the same random email address generated for registration

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(payLoad)
                .log().all()
                .post().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Expected status code 200, but got " + actualStatusCode);

        // Extract role from response
        String actualRole = response.jsonPath().getString("data.user.role");

        // Assertion (THIS is your main verification)
        Assert.assertEquals(actualRole, "admin");
        System.out.println("New Admin Role: " + actualRole);
    }

    @Test(priority = 5)
    public void getGroupsIDTest() {

        String apiPath = "/APIDEV/groups";

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .log().all()
                .get().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Expected status code 200, but got " + actualStatusCode);

        // Extract the list of groups from the response
        List<Map<String, Object>> groups = response.jsonPath().getList("data");

        // Find the group with the name "Group T" and extract its ID
        groupID = groups.stream()
                .filter(g -> g.get("Name").equals("Group T"))
                .map(g -> g.get("Id").toString())
                .findFirst()
                .orElse(null);

        Assert.assertNotNull(groupID, "Group T not found!");
        System.out.println("Group ID: " + groupID);
    }

    @Test(priority = 6)
    public void assignUserGroupAsAdminTest() {

        String apiPath = "/APIDEV/admin/users/" + UserID + "/group";
        String payLoad = String.format("""
                {
                  "groupId": "%s"
                }
                """, groupID);

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(payLoad)
                .log().all()
                .put().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Expected status code 200, but got " + actualStatusCode);

        String groupName = response.jsonPath().getString("data.groupName");
        Assert.assertEquals(groupName, "Group T", "Expected group name 'Group T', but got " + groupName);
        System.out.println("Assigned Group Name: " + groupName);
    }

    @Test(priority = 7)
    public void loginToSeeGroupChangeTest() {

        String apiPath = "/APIDEV/login";
        String payLoad = String.format("""
                {
                  "email": "%s",
                  "password": "Assignment@26"
                }
                """, registerEmail); // Use the same random email address generated for registration

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(payLoad)
                .log().all()
                .post().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Expected status code 200, but got " + actualStatusCode);
        String actualGroupName = response.jsonPath().getString("data.user.groupName");
        Assert.assertEquals(actualGroupName, "Group T", "Expected group name 'Group T', but got " + actualGroupName);
        System.out.println("New Group Name: " + actualGroupName);
    }
        @Test(priority = 8)
        public void deleteUserTest () {

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
        }
    }
