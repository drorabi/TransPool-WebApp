package chat.servlets;

import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import engine.users.User;
import engine.users.UserManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MakeMatchServlet extends HttpServlet {


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //returning JSON objects, not HTML

        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        User sessionUser=userManager.getUsers().get(username);
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/pages/signin/signin.html");
        } else {
            synchronized (this) {
                String mapUser = request.getParameter("mapUser");
                String serialNumber = request.getParameter("serialNumber");
                String numOfOffers = request.getParameter("numOfOffers");
                String numOfDrivers=request.getParameter("numOfDrivers");

                Boolean res=true;
                if(numOfDrivers.equals("multi"))
                    res=false;
                User userMap = userManager.getUsers().get(mapUser);
                userMap.getEngine().makeAMatch(userMap.getEngine().getData().getRequests().get(Integer.parseInt(serialNumber)), Integer.parseInt(numOfOffers),res);
                sessionUser.setAllOptions(userMap.getEngine().getData().getAllOptions());
                userMap.getEngine().getData().emptyAllOptions();
            }
        }
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