package utilities;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.restassured.response.Response;

/**
 * Allure reporting utilities for API testing
 * Provides reusable methods for attaching request/response details to Allure reports
 */
public class AllureUtils {

    /**
     * Attach request details to Allure report
     */
    @Attachment(value = "Request Details", type = "text/plain")
    public static String attachRequestDetails(String method, String url, String body, String authToken) {
        StringBuilder requestDetails = new StringBuilder();
        requestDetails.append("Method: ").append(method).append("\n");
        requestDetails.append("URL: ").append(url).append("\n");

        if (authToken != null && !authToken.isEmpty()) {
            requestDetails.append("Authorization: Bearer ").append(maskToken(authToken)).append("\n");
        }

        if (body != null && !body.trim().isEmpty()) {
            requestDetails.append("Body:\n").append(body);
        }

        return requestDetails.toString();
    }

    /**
     * Attach response details to Allure report
     */
    @Attachment(value = "Response Details", type = "text/plain")
    public static String attachResponseDetails(Response response) {
        StringBuilder responseDetails = new StringBuilder();
        responseDetails.append("Status Code: ").append(response.getStatusCode()).append("\n");
        responseDetails.append("Status Line: ").append(response.getStatusLine()).append("\n");
        responseDetails.append("Response Time: ").append(response.getTime()).append(" ms\n");

        // Add headers
        responseDetails.append("Headers:\n");
        response.getHeaders().forEach(header ->
            responseDetails.append("  ").append(header.getName()).append(": ").append(header.getValue()).append("\n")
        );

        // Add body
        responseDetails.append("Body:\n");
        try {
            responseDetails.append(response.getBody().asPrettyString());
        } catch (Exception e) {
            responseDetails.append(response.getBody().asString());
        }

        return responseDetails.toString();
    }

    /**
     * Attach response body as JSON to Allure report
     */
    @Attachment(value = "Response Body (JSON)", type = "application/json")
    public static String attachResponseBodyJson(Response response) {
        try {
            return response.getBody().asPrettyString();
        } catch (Exception e) {
            return response.getBody().asString();
        }
    }

    /**
     * Attach status code to Allure report
     */
    @Attachment(value = "Status Code", type = "text/plain")
    public static String attachStatusCode(int statusCode) {
        return String.valueOf(statusCode);
    }

    /**
     * Add a step to Allure report
     */
    public static void addStep(String stepName, String description) {
        Allure.step(stepName, () ->
            Allure.addAttachment("Step Description", "text/plain", description)
        );
    }

    /**
     * Mask sensitive token data for security
     */
    private static String maskToken(String token) {
        if (token == null || token.length() <= 10) {
            return token;
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }

    /**
     * Log API call with full details
     */
    public static void logApiCall(String method, String url, String body, String authToken, Response response) {
        attachRequestDetails(method, url, body, authToken);
        attachStatusCode(response.getStatusCode());
        attachResponseDetails(response);
        attachResponseBodyJson(response);
    }
}
