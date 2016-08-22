package utils;

import org.json.JSONObject;

/**
 * 
 * @author Timi
 */
public class Authenticator {
    
    private static String[] secureFunctions = {"get_groups_from_id", "upload_image_in_folder", "create_new_group"}; 
    
    public static boolean authentic(String functionName, JSONObject parameters, JSONObject response) {
        if (isUserAuthenticationNeeded(functionName)) {
            if (Worker.getUserIdForAuthenticator(parameters) != parameters.getInt("user_id")) {
                response.put("status", "failure");
                response.put("detail", "error_auth_user_credentials");
                response.put("bonus", "received: " + parameters.getString("user_username") + " " + parameters.getString("user_password"));
                return false;
            }
            else
                return true;
        }
        else
            return true;
    }
    
    public static boolean isUserAuthenticationNeeded(String functionName) {
        for (String s : secureFunctions)
            if (s.equals(functionName))
                return true;
        return false;
    }
}
