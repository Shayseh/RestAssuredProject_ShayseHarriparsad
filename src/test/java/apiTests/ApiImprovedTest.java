package apiTests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import payloadBuilder.Payload;
import utilities.Requests;

import static commonVariables.BaseURIs.baseURL;

public class ApiImprovedTest {
    static String token;
    static String userIdCode;
    static String newGroupID;

    Response response;

    @Test
    public void test() {
        String apiPath = "/APIDEV/login";
        String payload = Payload.loginUserPayload("SH@admin.com", "@12345678");

        response = Requests.post(baseURL + apiPath, payload);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        token = response.jsonPath().getString("data.token");
    }

    @Test(priority = 1)
    public void registerUserTest() {
        String apiPath = "/APIDEV/register";
        String registerUserPayload = Payload.registerUserPayload("Dayne", "Assignment", Payload.generateRandomEmail(), "Assignment@26", "1deae17a-c67a-4bb0-bdeb-df0fc9e2e526");

        response = Requests.post(baseURL + apiPath, registerUserPayload);

        Assert.assertEquals(response.getStatusCode(), 201, "Status code should be 201");
        userIdCode = response.jsonPath().getString("data.id");
    }

    @Test(priority = 2)
    public void userApprovalTest() {
        String apiPath = "/APIDEV/admin/users/" + userIdCode + "/approve";

        response = Requests.put(baseURL + apiPath, "", token);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    }

    @Test(priority = 3)
    public void makeUserAdminTest() {
        String apiPath = "/APIDEV/admin/users/" + userIdCode + "/role";
        String adminRolePayload = Payload.changeUserToAdminPayload("admin");

        response = Requests.put(baseURL + apiPath, adminRolePayload, token);

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

        response = Requests.get(baseURL + apiPath, token);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertTrue(response.jsonPath().getBoolean("success"), "Response should indicate success");
        newGroupID = Payload.findGroupByName(response, "Group T");
        Assert.assertNotNull(newGroupID, "Group T not found!");
    }

    @Test(priority = 7)
    public void assignGroupTest() {
        String apiPath = "/APIDEV/admin/users/" + userIdCode + "/group";
        String payload = Payload.assignUserToGroupPayload(newGroupID);

        response = Requests.put(baseURL + apiPath, payload, token);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    }

    @Test(priority = 8)
    public void loginToSeeGroupTest() {
        String apiPath = "/APIDEV/login";
        String payload = Payload.loginUserPayload(Payload.getRegisterEmail(), "Assignment@26");

        response = Requests.post(baseURL + apiPath, payload);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertTrue(response.jsonPath().getString("data.user.groupName").contains("Group T"), "User should be part of Group T");
    }

    @Test(priority = 9)
    public void deleteUserTest() {
        String apiPath = "/APIDEV/admin/users/" + userIdCode;

        response = Requests.delete(baseURL + apiPath, token);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    }

}
