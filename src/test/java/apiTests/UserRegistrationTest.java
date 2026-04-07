package apiTests;

import com.github.javafaker.Faker;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import requestBuilder.APIRequestBuilder;
import utilities.DatabaseConnection;

import java.sql.SQLException;

import static org.hamcrest.Matchers.equalTo;
import static utilities.DatabaseConnection.getEmail;
import static utilities.DatabaseConnection.getPassword;


public class UserRegistrationTest {

    static String registerEmail;
    static String registerPassword;
    static String registerGroupID;

    @BeforeClass
    public void setup() throws SQLException {
        DatabaseConnection.connectToDatabase();
    }

    @Test
    public void adminLoginTest() {
        APIRequestBuilder.loginAdminResponse(getEmail, getPassword)
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test(priority = 1)
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
    public void userApprovalTest() {
        APIRequestBuilder.userRegistrationApprovalResponse()
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test(priority = 3)
    public void makeUserAdminTest() {
        APIRequestBuilder.makeUserAdminResponse("admin")
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test(priority = 4)
    public void userLoginTest() {
        APIRequestBuilder.userLoginResponse(registerEmail, registerPassword)
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.user.role", equalTo("admin"));
    }

    @Test(priority = 5)
    public void getGroupsTest() {
        APIRequestBuilder.getGroupsResponse("Group T")
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test(priority = 6)
    public void assignUserToGroupTest() {
        APIRequestBuilder.assignUserToGroupResponse()
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.groupName", equalTo("Group T"));
    }

    @Test(priority = 7)
    public void userLoginToSeeGroupChangeTest() {
        APIRequestBuilder.userLoginResponse(registerEmail, registerPassword)
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.user.groupName", equalTo("Group T"));
    }

    @Test(priority = 8)
    public void deleteUserTest() {
        APIRequestBuilder.deleteUserResponse()
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }
}
