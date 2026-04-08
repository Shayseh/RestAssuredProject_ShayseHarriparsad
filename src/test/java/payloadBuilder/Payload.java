package payloadBuilder;

import com.github.javafaker.Faker;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

public class Payload {

    static String registerEmail;

    public static String loginUserPayload(String email, String password) {
        return String.format("""
                {
                  "email": "%s",
                  "password": "%s"
                }""", email, password);
    }

    public static String registerUserPayload(String firstName, String lastName, String email, String password, String groupId) {

        return String.format(""" 
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "email": "%s",
                  "password": "%s",
                  "confirmPassword": "%s",
                  "groupId": "%s"
                }
                """, firstName, lastName, email, password, password, groupId);
    }

    public static String changeUserToAdminPayload(String role) {
        return String.format("""
                {
                  "role": "%s"
                }
                """, role);
    }

    public static String assignUserToGroupPayload(String groupID) {
        return String.format("""
                {
                  "groupId": "%s"
        }""", groupID);
    }

    public static String findGroupByName(Response resp, String groupName) {

        List<Map<String, Object>> groups = resp.jsonPath().getList("data");

        return groups.stream()
                .filter(group -> groupName.equals(group.get("Name"))) // safer null handling
                .map(group -> group.get("Id").toString())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupName));

    }

    // Method to generate a random email address using Java Faker
    public static String generateRandomEmail() {
        Faker faker = new Faker();
        registerEmail = faker.internet().emailAddress();
        return registerEmail;
    }

    public static String getRegisterEmail() {
        return registerEmail;
    }
}
