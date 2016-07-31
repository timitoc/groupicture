package utils;

import com.sun.tracing.dtrace.FunctionName;
import org.json.JSONObject;

/**
 * 
 * @author Timi
 */
public class Authenticator {
    
    private static String[] secureFunctions = {"get_groups_from_id"}; 
    
    public static boolean authentic(String functionName, JSONObject parameters, JSONObject response) {
        if (isUserAuthenticationNeeded(functionName)) {
            if (Worker.getUserId(parameters) != parameters.getInt("id")) {
                response.put("status", "failure");
                response.put("detail", "error_auth_user_credentials");
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
