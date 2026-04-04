package BasicTest;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserRegistration {

    static String authToken;
    static String baseURL = "https://www.ndosiautomation.co.za";
    static String UserID;

    @Test
    public void adminLoginTest() {

        String apiPath = "/APIDEV/login";
        String payLoad = """
                {
                  "email": "admin@gmail.com",
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
        String randomEmail = Faker.instance().internet().emailAddress(); // Generate a random email address using Java Faker
        String payLoad = String.format(""" 
                  {"firstName": "Dayne",
                  "lastName": "Assignment",
                  "email": "%s",
                  "password": "Assignment@26",
                  "confirmPassword": "Assignment@26",
                  "groupId": "1deae17a-c67a-4bb0-bdeb-df0fc9e2e526"
                  }
                  """, randomEmail); // Generate a random email address using Java Faker and insert it into the payload

        // Send the POST request and capture the response
        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer" + authToken)
                .body(payLoad)
                .log().all()
                .post().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        UserID = response.jsonPath().getString("data.id");
        Assert.assertEquals(actualStatusCode, 201, "Expected status code 201, but got " + actualStatusCode);
        System.out.println("User ID: " + UserID);

    }
}
