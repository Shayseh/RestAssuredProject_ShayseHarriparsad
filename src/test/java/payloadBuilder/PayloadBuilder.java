package payloadBuilder;

import org.json.simple.JSONObject;

public class PayloadBuilder {

    public static JSONObject adminLoginPayload(String email, String password) {

        JSONObject loginUser = new JSONObject();
        loginUser.put("email", email);
        loginUser.put("password", password);

        return loginUser;
    }

    public static JSONObject registerUserPayload(String firstName, String lastName, String email, String password, String groupID) {

        JSONObject registerUser = new JSONObject();
        registerUser.put("firstName", firstName);
        registerUser.put("lastName", lastName);
        registerUser.put("email", email);
        registerUser.put("password", password);
        registerUser.put("confrimPassword", password);
        registerUser.put("groupId", groupID);

        return registerUser;
    }

    public static JSONObject changeUserToAdminPayload(String role) {

        JSONObject changeUserToAdmin = new JSONObject();
        changeUserToAdmin.put("role", role);

        return changeUserToAdmin;
    }

    public static JSONObject assignUserToGroupPayload(String groupID) {

        JSONObject assignUserToGroup = new JSONObject();
        assignUserToGroup.put("groupId", groupID);

        return assignUserToGroup;
    }


}


