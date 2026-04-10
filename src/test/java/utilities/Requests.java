package utilities;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Requests {

    public static RequestSpecification getRequestSpecification() {
        return RestAssured.given()
                .filter(new AllureRestAssured())  // Auto-captures request/response in Allure
                .contentType("application/json")
                .urlEncodingEnabled(false);
    }

    @Step("POST request to: {url}")
    public static Response post(String url, String body) {
        Response response = getRequestSpecification()
                .body(body)
                .when().log().all()
                .post(url)
                .then()
                .extract()
                .response();
        response.getBody().prettyPrint();
        AllureUtils.attachStatusCode(response.getStatusCode());
        return response;
    }

    @Step("GET request to: {url}")
    public static Response get(String url, String authToken) {
        Response response = getRequestSpecification()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get(url)
                .then()
                .extract()
                .response();
        response.getBody().prettyPrint();
        AllureUtils.attachStatusCode(response.getStatusCode());
        return response;
    }

    @Step("PUT request to: {url}")
    public static Response put(String url, String body, String authToken) {
        Response response = getRequestSpecification()
                .header("Authorization", "Bearer " + authToken)
                .body(body)
                .when()
                .put(url)
                .then()
                .extract()
                .response();
        response.getBody().prettyPrint();
        AllureUtils.attachStatusCode(response.getStatusCode());
        return response;
    }

    @Step("DELETE request to: {url}")
    public static Response delete(String url, String authToken) {
        Response response = getRequestSpecification()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete(url)
                .then()
                .extract()
                .response();
        response.getBody().prettyPrint();
        AllureUtils.attachStatusCode(response.getStatusCode());
        return response;
    }
}
