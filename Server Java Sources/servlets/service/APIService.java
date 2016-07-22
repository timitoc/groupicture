/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import utils.DBHandler;
import utils.Encryptor;
import utils.Worker;

/**
 *
 * @author timi
 */
public class APIService extends HttpServlet {
    
    public static final String workSpace = "remote";
    static JSONObject resp;
    static String data;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        String function, hash, public_key;
        JSONObject parameters;
        JSONObject jsonResponse = new JSONObject();
        resp = jsonResponse;
        try {
            function = request.getParameter("function");
            hash = request.getParameter("hash");
            public_key = request.getParameter("public_key");
            data = request.getParameter("data");
            parameters = new JSONObject(data);
            DBHandler.getInstance().connectToDB(workSpace);
            if (validDeveloperCredentials(public_key, data, hash))
                targetFunction(function, parameters, jsonResponse);
            else {
                jsonResponse.put("status", "fail");
                jsonResponse.put("detail", "Invalid Credentials"); // failed to auth
            }
        } catch (Exception ex) {
            Logger.getLogger(APIService.class.getName()).log(Level.SEVERE, null, ex);
            jsonResponse.put("status", "fail");
            jsonResponse.put("detail", ex.getMessage());
        }
        try (PrintWriter out = response.getWriter()) {
            out.write(jsonResponse.toString());
            out.close();
        }
    }
    
    public static boolean validDeveloperCredentials(String public_key, String data, String hash) {
        try {
            String public_key_hash = Encryptor.hash(public_key);
            ResultSet result = DBHandler.getInstance().getRowAfterString("developers", new BasicNameValuePair("public_key", public_key_hash));
            if (!result.next()) 
                throw new RuntimeException("There is no developer with this public key");
            String developerPrivateKey = Encryptor.decrypt(result.getString("private_key"));
            //Temporary:
//            resp.put("private", developerPrivateKey);
//            resp.put("hash_str", data+developerPrivateKey);
//            resp.put("expected_hash", Encryptor.hash(data+developerPrivateKey));
            if (!Encryptor.hash(data+developerPrivateKey).equals(hash))
                return false;
            return true;
        } catch (Exception ex) {
            Logger.getLogger(APIService.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    public void targetFunction(String function, JSONObject parameters, JSONObject response) throws Exception {
        if ("add_user".equals(function)) {
            Worker.addUser(parameters);
        }
        else if ("delete_user".equals(function)) {
            Worker.deleteUser(parameters);
        }
        else if ("get_user".equals(function)) {
            Worker.getUser(parameters);
            //TODO do smt woth this result
        }
        else if ("get_groups_from_id".equals(function)) {
            Worker.getGroupsForId(parameters, response);
        }
        else if ("get_folders_from_group".equals(function)) {
            Worker.getFoldersFromGroup(parameters, response);
        }
        else if ("upload_image_in_folder".equals(function)) {
            Worker.uploadImageInFolder(parameters, response);
        }
        else if ("get_images_from_folder".equals(function)) {
            Worker.getImagesFromFolder(parameters, response);
        }
        else if ("register_user".equals(function)) {
            int rescode = Worker.registerUser(parameters.getString("username"), parameters.getString("password"), parameters.getString("name"));
            if (rescode != 0) {
                response.put("status", "failure");
                response.put("detail", "Error code " + rescode);
                return;
            }
        }
        else if ("login_user".equals(function)) {
            Worker.getUser(parameters, response);
        }
        else if ("create_new_group".equals(function)) {
            Worker.createNewGroup(parameters, response);
        }
        else if ("map_group_to_user".equals(function)) {
            Worker.mapGroupToUser(parameters, response);
        }
        else if ("create_folder".equals(function)) {
            Worker.createFolder(parameters, response);
        }
        else if ("search_for_groups".equals(function)) {
            Worker.searchForGroups(parameters, response);
        }
        else if ("unmap_group_from_user".equals(function)) {
            Worker.unmapGroupFromUser(parameters, response);
        }
        else {
            throw new Exception("No such function: " + function);
        }
        if (!response.has("status"))
            response.put("status", "success");
        if (!response.has("detail"))
            response.put("detail", "no details available");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
