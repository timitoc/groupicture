/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import servlets.service.APIService;

/**
 *
 * @author timi
 */
public class Worker {
    
    public static int addUser(JSONObject param) {
        initDBHandler();
        System.out.println("Adding new user");
        JSONObject metadata = new JSONObject();
        //String[] entry = new String[]{"name", "metadata"};
        NameValuePair[] pairs = new BasicNameValuePair[2];
        pairs[0] = new BasicNameValuePair("name", param.getString("name"));
        pairs[1] = new BasicNameValuePair("metadata", metadata.toString());
        int result = DBHandler.getInstance().customInsert("users", pairs);
        System.out.println(result);
        return result;
    }
    
    public static int registerUser(String username, String password, String name) {
        initDBHandler();
        NameValuePair[] params = new NameValuePair[3];
        params[0] = new BasicNameValuePair("username", username);
        params[1] = new BasicNameValuePair("password", Encryptor.saltedHash(password));
        params[2] = new BasicNameValuePair("name", name);
        return DBHandler.getInstance().customInsert("users", params);
    }
    
    public static int getUserIdForAuthenticator(JSONObject parameters) {
        initDBHandler();
        String query;
        query = "SELECT * FROM users WHERE username = ?";
         try {
            String username = parameters.getString("user_username");
            String password = parameters.getString("user_password");
            ResultSet result = DBHandler.getInstance().getUser(query, username);
            if (result != null && result.next() && Encryptor.checkSaltedHash(password, result.getString("password")))
            {
                return result.getInt("id");
            }
        } catch (Exception e) {
             return -1;
        }
        return -1;
    }
    
    public static void getUser(JSONObject parameters, JSONObject response) {
        initDBHandler();
        String query;
        query = "SELECT * FROM users WHERE username = ?";
        
        try {
            String username = parameters.getString("username");
            String password = parameters.getString("password");
            response.put("username", username);
            response.put("password", password);
            ResultSet result = DBHandler.getInstance().getUser(query, username);
            if (result != null && result.next() && Encryptor.checkSaltedHash(password, result.getString("password"))) {
                response.put("status", "success");
                response.put("id", result.getInt("id"));
            }
            else {
                response.put("status", "invalid_not_found");
            }
        } catch (SQLException ex) {
            response.put("status", "invalid");
        }

    }
    
    public static int addSetToUser(JSONObject param) {
        initDBHandler();
        NameValuePair[] pairs = new BasicNameValuePair[1];
        pairs[0] = new BasicNameValuePair("name", param.getString("name"));
        
        return DBHandler.getInstance().addSetToUser(pairs, param.getInt("user_id"));
    }
    
    public static void removeSetFromUser(JSONObject param) {
        initDBHandler();
        
        DBHandler.getInstance().removeSetFromUser(param.getInt("user_id"), param.getInt("set_id"));
    }
    
    public static void getGroupsFromId(JSONObject param, JSONObject response) {
        initDBHandler();
        int id = param.getInt("id");
        ResultSet res = DBHandler.getInstance().getGroupsForId(id);
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; res.next(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("id", res.getInt("id"));
                obj.put("title", res.getString("title"));
                obj.put("description", res.getString("description"));
                array.put(i, obj);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
        response.put("groups", array);
    }
    
    public static void getFoldersFromGroup(JSONObject param, JSONObject response) {
        initDBHandler();
        int group_id = param.getInt("id");
        ResultSet res = DBHandler.getInstance().getFoldersFromGroup(group_id);
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; res.next(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("id", res.getInt("id"));
                obj.put("title", res.getString("title"));
                array.put(i, obj);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
        response.put("groups", array);
    }
    
    
    public static void uploadImageInFolder(JSONObject parameters, JSONObject response){
        initDBHandler();
        //response.put("req_id", parameters.getInt("id"));
        //response.put("req_image", parameters.getString("image"));
        try {
            DBHandler.getInstance().uploadImageInFolder(parameters.getInt("id"), Base64.decode(parameters.getString("image")));
        } catch (SQLException ex) {
            System.out.println("EXCEPTION: " + ex.getMessage());
            response.put("detail", ex.getMessage());
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void getImagesFromFolder(JSONObject parameters, JSONObject response) {
        initDBHandler();
        int folder_id = parameters.getInt("id");
        ResultSet res = DBHandler.getInstance().getImagesFromFolder(folder_id);
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; res.next(); i++) {
                array.put(i, res.getInt("id"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
        response.put("images", array);
    }
    
    public static Blob getImageWithId(JSONObject parameters) {
        initDBHandler();
        int image_id = parameters.getInt("id");
        ResultSet res = DBHandler.getInstance().getImageWithId(image_id);
        try {
            if (res != null && res.next()) {
                return res.getBlob("image");
            }
            else 
                return null;
        } catch (SQLException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
    }
    
    public static void createNewGroup(JSONObject parameters, JSONObject response) {
        initDBHandler();
        String name = parameters.getString("name");
        String description = parameters.getString("description");
        String password;
        if (parameters.has("password"))
            password = parameters.getString("password");
        else
            password = "";
        try {
            ResultSet res = DBHandler.getInstance().createNewGroup(name, description, password);
            if (res != null && res.next()) {
                response.put("status", "success");
                response.put("id", res.getInt(1));
                return;
            }
            response.put("detail", "nu tu rezultate");
        } catch (SQLException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            response.put("detail", ex.getMessage());
        }
        response.put("status", "failure");   
    }
    
    public static void mapGroupToUser(JSONObject parameters, JSONObject response) {
        initDBHandler();
        int user_id = parameters.getInt("user_id");
        int group_id = parameters.getInt("group_id");
        String password = parameters.getString("password");
        try {
            int updated = DBHandler.getInstance().mapGroupToUser(user_id, group_id, password);
            if (updated == 0) {
                response.put("status", "failure");
                response.put("detail", "incorrect_password");
                return;
            }
            response.put("status", "success");
            return;
        } catch (SQLException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            response.put("detail", ex.getMessage());
        }
        response.put("status", "failure");   
    }

    public static void unmapGroupFromUser(JSONObject parameters, JSONObject response) {
        initDBHandler();
        int user_id = parameters.getInt("user_id");
        int group_id = parameters.getInt("group_id");
        try {
            DBHandler.getInstance().unmapGroupFromUser(user_id, group_id);
            response.put("status", "success");
            return;
        } catch (SQLException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            response.put("detail", ex.getMessage());
        }
        response.put("status", "failure");   
    }
    
    public static ResultSet getUser(JSONObject param) {
        return getUser(param.getString("name"));
    }
    /*
        @deprecated Soon to be removed
    */
    public static ResultSet getUser(String userName) {
        initDBHandler();
        ResultSet result = DBHandler.getInstance().getRowAfterString("users", 
                new BasicNameValuePair("name", userName));
        return result;
    }
    
    public static void createFolder(JSONObject parameters, JSONObject response) {
        initDBHandler();
        String title = parameters.getString("title");
        int group_id = parameters.getInt("group_id");
        try {
            DBHandler.getInstance().createFolder(title, group_id);
            response.put("status", "success");
        } catch (SQLException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            response.put("status", "failure");
        }
    }
    
    public static void searchForGroups(JSONObject parameters, JSONObject response) {
        initDBHandler();
        String keyword = parameters.getString("query");
        try {
            ResultSet res;
            if (parameters.has("offset") && parameters.has("size"))
                res = DBHandler.getInstance().searchForGroups(keyword, parameters.getInt("offset"), parameters.getInt("size"));
            else
                res = DBHandler.getInstance().searchForGroups(keyword);
            JSONArray array = new JSONArray();
            for (int i = 0; res.next(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("id", res.getInt("id"));
                obj.put("title", res.getString("title"));
                obj.put("description", res.getString("description"));
                boolean hasPassword = true;
                String password = res.getString("password");
                if (password == null || password.isEmpty())
                    hasPassword = false;
                obj.put("hasPassword", hasPassword);
                array.put(i, obj);
            }
            response.put("status", "success");
            response.put("groups", array);
        } catch (SQLException ex) {
            response.put("status", "fail");
            response.put("detail", "cought sql exception " + ex.getMessage());
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static int deleteUser(JSONObject param) {
        return deleteUser(param.getString("name"));
    }
    
    public static int deleteUser(String userName) {
        initDBHandler();
        return DBHandler.getInstance().removeWithCondition("users", new BasicNameValuePair("name", userName));
    }
    
    public static int deleteUser(int userId) {
        initDBHandler();
        return DBHandler.getInstance().removeWithId("users", userId);
    }
    
    public static void initDBHandler() {
        if (!DBHandler.getInstance().isConnected())
            DBHandler.getInstance().connectToDB(APIService.workSpace);
    }


    
    
}
