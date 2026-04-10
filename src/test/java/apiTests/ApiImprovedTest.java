package apiTests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utilities.AllureUtils;
import utilities.Payload;
import utilities.Requests;

import static commonVariables.BaseURIs.baseURL;

@Epic("User Management API")
@Feature("User Registration & Group Assignment Flow")
public class ApiImprovedTest {
    static String token;
    static String userIdCode;
    static String newGroupID;
    static String userToken;

    Response response;

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Description("Login as existing admin to obtain auth token")
    @Story("Admin Login")
    public void test() {
        String apiPath = "/APIDEV/login";
        String payload = Payload.loginUserPayload("SH@admin.com", "@12345678");

        response = Requests.post(baseURL + apiPath, payload);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        token = response.jsonPath().getString("data.token");
        AllureUtils.addStep("Token Extracted", "Admin token obtained successfully");
    }

    @Test(priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Register a new user with random email")
    @Story("User Registration")
    public void registerUserTest() {
        String apiPath = "/APIDEV/register";
        String registerUserPayload = Payload.registerUserPayload("Dayne", "Assignment", Payload.generateRandomEmail(), "Assignment@26", "1deae17a-c67a-4bb0-bdeb-df0fc9e2e526");

        response = Requests.post(baseURL + apiPath, registerUserPayload);

        Assert.assertEquals(response.getStatusCode(), 201, "Status code should be 201");
        userIdCode = response.jsonPath().getString("data.id");
        AllureUtils.addStep("User Created", "User ID: " + userIdCode);
    }

    @Test(priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Approve the newly registered user")
    @Story("User Approval")
    public void userApprovalTest() {
        String apiPath = "/APIDEV/admin/users/" + userIdCode + "/approve";

        response = Requests.put(baseURL + apiPath, "", token);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    }

    @Test(priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Login with newly registered user credentials")
    @Story("User Login")
    public void loginTest() {
        String apiPath = "/APIDEV/login";
        String payload = Payload.loginUserPayload(Payload.getRegisterEmail(), "Assignment@26");

        response = Requests.post(baseURL + apiPath, payload);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    }

    @Test(priority = 4)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Promote user to admin role")
    @Story("Role Management")
    public void makeUserAdminTest() {
        String apiPath = "/APIDEV/admin/users/" + userIdCode + "/role";
        String adminRolePayload = Payload.changeUserToAdminPayload("admin");

        response = Requests.put(baseURL + apiPath, adminRolePayload, token);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    }

    @Test(priority = 5)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Login as new admin and verify admin role")
    @Story("Role Verification")
    public void loginAsNewAdminTest() {
        String apiPath = "/APIDEV/login";
        String payload = Payload.loginUserPayload(Payload.getRegisterEmail(), "Assignment@26");

        response = Requests.post(baseURL + apiPath, payload);

        userToken = response.jsonPath().getString("data.token");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertEquals(response.jsonPath().getString("data.user.role"), "admin", "User role should be admin");
    }

    @Test(priority = 6)
    @Severity(SeverityLevel.NORMAL)
    @Description("Fetch all groups and find 'Group T'")
    @Story("Group Management")
    public void findGroupsTest() {
        String apiPath = "/APIDEV/groups";

        response = Requests.get(baseURL + apiPath, userToken);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertTrue(response.jsonPath().getBoolean("success"), "Response should indicate success");
        newGroupID = Payload.findGroupByName(response, "Group T");
        Assert.assertNotNull(newGroupID, "Group T not found!");
        AllureUtils.addStep("Group Found", "Group T ID: " + newGroupID);
    }

    @Test(priority = 7)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Assign user to 'Group T'")
    @Story("Group Assignment")
    public void assignGroupTest() {
        String apiPath = "/APIDEV/admin/users/" + userIdCode + "/group";
        String payload = Payload.assignUserToGroupPayload(newGroupID);

        response = Requests.put(baseURL + apiPath, payload, userToken);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    }

    @Test(priority = 8)
    @Severity(SeverityLevel.NORMAL)
    @Description("Login and verify user is now in 'Group T'")
    @Story("Group Verification")
    public void loginToSeeGroupTest() {
        String apiPath = "/APIDEV/login";
        String payload = Payload.loginUserPayload(Payload.getRegisterEmail(), "Assignment@26");

        response = Requests.post(baseURL + apiPath, payload);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertTrue(response.jsonPath().getString("data.user.groupName").contains("Group T"), "User should be part of Group T");
    }

    @Test(priority = 9)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Delete the test user for cleanup")
    @Story("User Cleanup")
    public void deleteUserTest() {
        String apiPath = "/APIDEV/admin/users/" + userIdCode;

        response = Requests.delete(baseURL + apiPath, token);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    }

}
