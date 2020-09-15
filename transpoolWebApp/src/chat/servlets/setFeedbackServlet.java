package chat.servlets;

import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import engine.users.User;
import engine.users.UserManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(name = "setFeedbackServlet", urlPatterns = {"/pages/map/setFeedback"})


public class setFeedbackServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        User sessionUser=userManager.getUsers().get(username);
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/pages/signin/signin.html");
        }
        else{
            String mapUsername = request.getParameter("userFromMap");
            User userMap=userManager.getUsers().get(mapUsername);

            String trip = request.getParameter("trips");
            String stars = request.getParameter("stars");
            String feedback=request.getParameter("feedback");

            userMap.getEngine().addFeedbackToTrip(userMap.getEngine().getData().getPlannedTrips().getTrips().get(Integer.parseInt(trip)), Integer.parseInt(stars), feedback);
            String tripOwner=userMap.getEngine().getData().getPlannedTrips().getTrips().get(Integer.parseInt(trip)).getOwner();
            User tripOwnerUser=userManager.getUsers().get(tripOwner);
            tripOwnerUser.addFeedback(feedback, Integer.parseInt(stars), username);
            tripOwnerUser.addAlert("NEW FEEDBACK! given by "+username+" to trip number "+trip+", stars rate: "+stars+", text content:"+feedback);
            sessionUser.addAlert("Your feedback was added successfully!");
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