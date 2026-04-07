package apiTests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import payloadBuilder.Payload;
import utilities.Requests;

public class ApiImprovedTest {

    static String baseURL = "https://www.ndosiautomation.co.za";
    static String authToken;
    static String userID;
    static String groupID;

    Response response;

    @Test
    public void test() {
        String apiPath = "/APIDEV/login";
        String payload = Payload.loginUserPayload("SH@admin.com", "@12345678");

        response = Requests.post(baseURL + apiPath, payload);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        authToken = response.jsonPath().getString("data.token");
    }

    @Test(priority = 1)
    public void registerUserTest() {
        String apiPath = "/APIDEV/register";
        String registerUserPayload = Payload.registerUserPayload("Dayne", "Assignment", Payload.generateRandomEmail(), "Assignment@26", "1deae17a-c67a-4bb0-bdeb-df0fc9e2e526");

        response = Requests.post(baseURL + apiPath, registerUserPayload);

        Assert.assertEquals(response.getStatusCode(), 201, "Status code should be 201");
        userID = response.jsonPath().getString("data.id");
    }

    @Test(priority = 2)
    public void userApprovalTest() {
        String apiPath = "/APIDEV/admin/users/" + userID + "/approve";

        response = Requests.put(baseURL + apiPath, "", authToken);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    }

    @Test(priority = 3)
    public void makeUserAdminTest() {
        String apiPath = "/APIDEV/admin/users/" + userID + "/role";
        String adminRolePayload = Payload.changeUserToAdminPayload("admin");

        response = Requests.put(baseURL + apiPath, adminRolePayload, authToken);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    }

    @Test(priority = 5)
    public void loginAsNewAdminTest() {
        String apiPath = "/APIDEV/login";
        String payload = Payload.loginUserPayload(Payload.getRegisterEmail(), "Assignment@26");

        response = Requests.post(baseURL + apiPath, payload);
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertEquals(response.jsonPath().getString("data.user.role"), "admin", "User role should be admin");
    }

    @Test(priority = 6)
    public void findGroupsTest() {
        String apiPath = "/APIDEV/groups";

        response = Requests.get(baseURL + apiPath, authToken);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertTrue(response.jsonPath().getBoolean("success"), "Response should indicate success");
        String groupID = Payload.findGroupByName(response, "Group T");
        Assert.assertNotNull(groupID, "Group T not found!");
    }

    @Test(priority = 7)
    public void assignGroupTest() {
        String apiPath = "/APIDEV/admin/users/" + userID + "/group";
        String payload = Payload.assignUserToGroupPayload();

        response = Requests.put(baseURL + apiPath, payload, authToken);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    }

    @Test(priority = 8)
    public void loginToSeeGroupTest() {
        String apiPath = "/APIDEV/login";
        String payload = Payload.loginUserPayload(Payload.getRegisterEmail(), "Assignment@26");

        response = Requests.post(baseURL + apiPath, payload);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertTrue(response.jsonPath().getList("data.user.groups").contains("Group T"), "User should be part of Group T");
    }

    @Test(priority = 9)
    public void deleteUserTest() {
        String apiPath = "/APIDEV/admin/users/" + userID;

        response = Requests.delete(baseURL + apiPath, authToken);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    }

}
