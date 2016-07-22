/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;

/**
 *
 * @author timi
 */
public class DBHandler {
    
    private static DBHandler instance;
    
    private Connection connection;
    private boolean connected = false;
    private String tableName;
    
    private DBHandler()
    {
        
    }
    
    public static DBHandler getInstance()
    {
        if (instance == null)
            instance = new DBHandler();
        return instance;
    }
    
    public static byte[] getByteArray(InputStream is) throws IOException {
        
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        
        while ((nRead = is.read(data, 0, data.length)) != -1) {
          buffer.write(data, 0, nRead);
        }
        buffer.flush();

        return buffer.toByteArray();
    }
    
    public String connectToDB(String wantedConnection)
    {
        String host = "127.8.17.2";
        String port = "3306";
        String dbName = "groupicture";
        String username = "adminulQSh82";
        String password = "Lw3n7wIMMVX8";
        String s;
        if ("remote".equals(wantedConnection)) {
            tableName = "remote";
            s = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
            return connectToDB(s, username, password);
        }
        else {
            tableName = "local";
            s = "jdbc:mysql://" + "localhost" + ":" + "3306" + "/" + "Groupicture";
            return connectToDB(s, "root", "123parola");
        }
    }
    
    private String connectToDB(String connectionString, String username, String password) {
        String s = connectionString;
        String result;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
        }

        try {
            connection = DriverManager.getConnection(s, username, password);
            if (connection == null)
                result = "Entered but not successful";
            else {
                result = "Awesome ! Success";
                connected = true;
            }
        }
        catch (SQLException e) {
            result = e.getMessage();
            //System.out.println(result);
        }
        return result;
    }
    
    public static String getInsertionQueryString(String tableName, NameValuePair[] params) {
        String[] forward = new String[params.length];
        for (int i = 0; i < params.length; i++)
            forward[i] = params[i].getName();
        return getInsertionQueryString(tableName, forward);
    }
    
    public static String getInsertionQueryString(String tableName, String[] params) {
        String query;
        query = "INSERT INTO " + tableName  + " (";
        for (int i = 0; i < params.length; i++) {
            query = query + params[i];
            if (i < params.length-1)
                query = query + ", ";
        }
        query = query + ") VALUES (";
        for (int i = 0; i < params.length; i++) {
            query = query + '?';
            if (i < params.length-1)
                query = query + ", ";
        }
        query = query + ")";
        return query;
    }
    
    public int customInsert(String tableName, NameValuePair[] params) {
        String queryString = getInsertionQueryString(tableName, params);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(queryString);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setString(i+1, params[i].getValue());
            }
            preparedStatement.executeUpdate(); 
            return 0;
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Exception cought " + ex.getMessage());
            return 0x50;
        }
    }
    
    public int addSetToUser(NameValuePair[] params, int userId)
    {
        String queryString = getInsertionQueryString("sets", params);
         try {
            PreparedStatement preparedStatement = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setString(i+1, params[i].getValue());
            }
            int setId;
            
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()){
                setId=rs.getInt(1);
            }
            else 
                return 0x50;
            createMapping(userId, setId);
            return 0;
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Exception cought " + ex.getMessage());
            return 0x50;
        }
    }
    
    public void createMapping(int userId, int setId) throws SQLException {
        String queryString = getInsertionQueryString("user_set_mapping", 
                new String[]{"user_id", "set_id"});
        PreparedStatement preparedStatement = connection.prepareStatement(queryString);
        preparedStatement.setInt(1, userId);
        preparedStatement.setInt(2, setId);
        preparedStatement.executeUpdate();
    }
    
    public void removeSetFromUser(int user_id, int set_id) {
        String query = "DELETE FROM " + "user_set_mapping" + " WHERE " + 
                "user_id = ?" + " AND " + "set_id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, user_id);
            statement.setInt(2, set_id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public int removeWithId(String tableName, int id) {
        String query = "DELETE FROM " + tableName  + " WHERE " + "id" + " = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
            return 0;
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            return 0x50;
        } 
    }
    
    public int removeWithCondition(String tableName, NameValuePair condition) {
        String query = "DELETE FROM " + tableName  + " WHERE " + 
                condition.getName() + " = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, condition.getValue());
            statement.executeUpdate();
            return 0;
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            return 0x50;
        } 
    }
    
    public PreparedStatement getInsertionPreparedStatement(String tableName, String[] paramNames) {
        String queryString = getInsertionQueryString(tableName, paramNames);
        try {
            return connection.prepareStatement(queryString);
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
       
    public ResultSet getUser(String query, String username, String password) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            return preparedStatement.executeQuery(); 
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public ResultSet getRowAfterString(String tableName, NameValuePair condition) {
        String query;
        query = "SELECT * FROM " + tableName  + " WHERE " + condition.getName() + " = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, condition.getValue());
            return preparedStatement.executeQuery(); 
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public ResultSet getRowAfterId(String tableName, int id) {
        String query;
        query = "SELECT * FROM " + tableName  + " WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            return preparedStatement.executeQuery(); 
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public String insert(String[] params) {
        String query;
        boolean withImage = params.length > 3;
        if (!withImage)
            query = "INSERT INTO " + tableName  + " (nume, prenume, bonus)" + "VALUES (?, ?, ?)";
        else
            query = "INSERT INTO " + tableName  + " (nume, prenume, bonus, image)" + "VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, params[0]);
            preparedStatement.setString(2, params[1]);
            preparedStatement.setString(3, params[2]);
            if (withImage)
                preparedStatement.setString(4, params[3]);
            preparedStatement.executeUpdate(); 
            return preparedStatement.toString();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
    }
    
    public ResultSet getGroupsForId(int id) {
        String query = "SELECT g.*\n" +
"FROM users u\n" +
"INNER JOIN user_group_mapping map on map.user_id = u.id\n" +
"INNER JOIN groups g on map.group_id = g.id\n" +
"WHERE u.id=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            return preparedStatement.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public ResultSet getFoldersFromGroup(int group_id) {
        String query = "SELECT * FROM `folders` WHERE group_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, group_id);
            return preparedStatement.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
 
    public ResultSet getImagesFromFolder(int folder_id) {
        String query = "SELECT id FROM images WHERE folder_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, folder_id);
            return preparedStatement.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public ResultSet getImageWithId(int image_id) {
        String query = "SELECT image FROM images WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, image_id);
            return preparedStatement.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public void uploadImageInFolder(int id, byte[] imageBytes) throws SQLException {
        String query = "INSERT INTO images (id, folder_id, image) VALUES (NULL, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.setBytes(2, imageBytes);
        preparedStatement.executeUpdate(); 

    }
    
    public ResultSet createNewGroup(String title, String description, String password) throws SQLException {
        String query = "INSERT INTO groups VALUES (NULL, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, description);
        preparedStatement.setString(3, password);
        preparedStatement.executeUpdate();
        ResultSet res = preparedStatement.getGeneratedKeys();
        return res;
    }
    
    public void mapGroupToUser(int user_id, int group_id) throws SQLException {
        String query = "INSERT INTO user_group_mapping VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, user_id);
        preparedStatement.setInt(2, group_id);
        preparedStatement.executeUpdate();
    }
    
    /**
     * 
     * @param user_id
     * @param group_id
     * @param password
     * @return Number of modified records
     * @throws SQLException 
     */
    public int mapGroupToUser(int user_id, int group_id, String password) throws SQLException {
        String query = "INSERT INTO user_group_mapping(user_id, group_id) \n" +
                        "SELECT ?, id\n" +
                        "FROM groups\n" +
                        "WHERE id=? AND password=?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, user_id);
        preparedStatement.setInt(2, group_id);
        preparedStatement.setString(3, password);
        return preparedStatement.executeUpdate();
    }
    
    public void unmapGroupFromUser(int user_id, int group_id) throws SQLException {
        String query = "DELETE FROM user_group_mapping WHERE user_id=? AND group_id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, user_id);
        preparedStatement.setInt(2, group_id);
        preparedStatement.executeUpdate();
    }
    
    public ResultSet searchForGroups(String keyword) throws SQLException {
        String query = "SELECT *\n" +
"FROM `groups`\n" +
"WHERE title LIKE ? OR description LIKE ? \n" +
"LIMIT 10";
        String regex = "%" + keyword + "%";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, regex);
        preparedStatement.setString(2, regex);
        return preparedStatement.executeQuery();
    }
    
    public void createFolder(String title, int group_id) throws SQLException {
        String query = "INSERT INTO folders VALUES (NULL, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, title);
        preparedStatement.setInt(2, group_id);
        preparedStatement.executeUpdate();
    }
    
    public ResultSet getEntryAt(int id) {
        return getEntryAt(String.valueOf(id));
    }
    
    public ResultSet getEntryAt(String id) {
        String query;
        query = "SELECT * FROM " + tableName  + " WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, String.valueOf(id));
            return preparedStatement.executeQuery(); 
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public ResultSet selectAll() {
        Statement stmt;
        try {
            stmt = connection.createStatement();
            String query = "SELECT * FROM " + tableName + ";";
            ResultSet result = stmt.executeQuery(query);
            return result;
        }
        catch(Exception e) {
            return null;
        }
    }
    
    public boolean isConnected() {
        return connected;
    }

   

    
}
