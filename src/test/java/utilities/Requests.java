package utilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Requests {

    public static RequestSpecification getRequestSpecification() {

        // You can set a base URI here if you have a common one for all requests
        // RestAssured.baseURI = "https://www.ndosiautomation.co.za";
        return RestAssured.given().contentType("application/json").urlEncodingEnabled(false);
    }


    public static Response get(String url, String authToken) {
        Response response = getRequestSpecification()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get(url)
                .then()
                .extract()
                .response();
        response.getBody().prettyPrint();
        return response;
    }

    public static Response post(String url, String body) {
        Response response = getRequestSpecification()
                .body(body)
                .when().log().all()
                .post(url)
                .then()
                .extract()
                .response();
        response.getBody().prettyPrint();
        return response;
    }


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
        return response;
    }

    public static Response delete(String url, String authToken) {
        Response response = getRequestSpecification()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete(url)
                .then()
                .extract()
                .response();
        response.getBody().prettyPrint();
        return response;
    }
}
