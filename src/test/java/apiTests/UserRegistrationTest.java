package apiTests;

import com.github.javafaker.Faker;
import io.qameta.allure.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import requestBuilder.APIRequestBuilder;
import utilities.DatabaseConnection;

import java.sql.SQLException;

import static org.hamcrest.Matchers.equalTo;
import static utilities.DatabaseConnection.getEmail;
import static utilities.DatabaseConnection.getPassword;


@Epic("User Management API")
@Feature("User Registration Flow via Request Builder")
public class UserRegistrationTest {

    static String registerEmail;
    static String registerPassword;
    static String registerGroupID;

    @BeforeClass
    public void setup() throws SQLException {
        DatabaseConnection.connectToDatabase();
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Description("Login as admin using database credentials")
    @Story("Admin Login")
    public void adminLoginTest() {
        APIRequestBuilder.loginAdminResponse(getEmail, getPassword)
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test(priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Register a new user with random email")
    @Story("User Registration")
    public void userRegistrationTest() {
        registerEmail = Faker.instance().internet().emailAddress();
        registerPassword = "Assignment@26";
        registerGroupID = "1deae17a-c67a-4bb0-bdeb-df0fc9e2e526";
        APIRequestBuilder.registerUserResponse("Dayne", "Assignment", registerEmail, registerPassword, registerGroupID)
                .then()
                .log().all()
                .assertThat()
                .statusCode(201)
                .body("success", equalTo(true));
    }

    @Test(priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Approve the registered user")
    @Story("User Approval")
    public void userApprovalTest() {
        APIRequestBuilder.userRegistrationApprovalResponse()
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test(priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Login with newly registered user and verify role")
    @Story("User Login")
    public void userLoginTest() {
        APIRequestBuilder.userLoginResponse(registerEmail, registerPassword)
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.user.role", equalTo("user"));
    }

    @Test(priority = 4)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Promote user to admin role")
    @Story("Role Management")
    public void makeUserAdminTest() {
        APIRequestBuilder.makeUserAdminResponse("admin")
                .then()
               .log().all()
                .assertThat()
               .statusCode(200)
               .body("success", equalTo(true));
    }

    @Test(priority = 5)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Login as new admin and verify admin role")
    @Story("Role Verification")
    public void userAdminLoginTest() {
        APIRequestBuilder.userLoginResponse(registerEmail, registerPassword)
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.user.role", equalTo("admin"));
    }

    @Test(priority = 6)
    @Severity(SeverityLevel.NORMAL)
    @Description("Fetch groups and find 'Group T'")
    @Story("Group Management")
    public void getGroupsTest() {
        APIRequestBuilder.getGroupsResponse("Group T")
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test(priority = 7)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Assign user to 'Group T'")
    @Story("Group Assignment")
    public void assignUserToGroupTest() {
        APIRequestBuilder.assignUserToGroupResponse()
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.groupName", equalTo("Group T"));
    }

    @Test(priority = 8)
    @Severity(SeverityLevel.NORMAL)
    @Description("Login and verify user is now in 'Group T'")
    @Story("Group Verification")
    public void userLoginToSeeGroupChangeTest() {
        APIRequestBuilder.userLoginResponse(registerEmail, registerPassword)
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.user.groupName", equalTo("Group T"));
    }

    @Test(priority = 9)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Delete the test user for cleanup")
    @Story("User Cleanup")
    public void deleteUserTest() {
        APIRequestBuilder.deleteUserResponse()
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }
}
