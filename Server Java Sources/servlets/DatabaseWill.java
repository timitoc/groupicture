/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utils.DBHandler;

/**
 *
 * @author timi
 */
public class DatabaseWill extends HttpServlet {

    PrintWriter out;
    String wantedConnection;
    
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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet DatabaseWill</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DatabaseWill at " + request.getContextPath() + "</h1>");
            wantedConnection = "remote";
            out.println("<h2> Request for " + wantedConnection + "</h2>");
            this.out = out;
            String result = DBHandler.getInstance().connectToDB(wantedConnection);
            out.println("<br><p>" + result + "</p>");
            out.println("<br>");
            if (DBHandler.getInstance().isConnected())
                showDatabase();
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    private void showDatabase() {
        ResultSet result = DBHandler.getInstance().selectAll();
        out.println("<div>");
        try {
            while (result.next()) {
                out.println("<br>");
                int id = result.getInt("id");
                String nume = result.getString("nume");
                String prenume = result.getString("prenume");
                String bonus = result.getString("bonus");

                out.print(id + "::");
                out.print(nume + "::");
                out.print(prenume + "::");
                out.print(bonus);
            }
        }
        catch(Exception e) {
            out.println(e.getMessage());
        }
        out.println("</div>");
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
