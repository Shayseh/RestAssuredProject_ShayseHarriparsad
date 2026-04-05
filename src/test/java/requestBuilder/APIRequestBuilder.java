package requestBuilder;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;

import static commonVariables.BaseURIs.baseURL;
import static payloadBuilder.PayloadBuilder.adminLoginPayload;

public class APIRequestBuilder {

    static String authToken;

    public static Response loginUserResponse(String email, String password) {

        String apiPath = "/APIDEV/login";
        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(adminLoginPayload(email, password))
                .log().all()
                .post().prettyPeek();
        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Expected status code 200, but got " + actualStatusCode);
        authToken = response.jsonPath().getString("data.token");

        System.out.println("Admin Auth token: " + authToken);

        return response;
    }


}
