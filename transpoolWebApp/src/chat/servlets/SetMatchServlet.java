package chat.servlets;

import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import com.google.gson.Gson;
import engine.converted.classes.*;
import engine.exceptions.FullCapacityException;
import engine.users.User;
import engine.users.UserManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


@WebServlet(name = "setMatch", urlPatterns = {"/pages/map/setMatch"})


public class SetMatchServlet extends HttpServlet {
boolean isMatched=true;
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/pages/signin/signin.html");
        } else {
            User sessionUser = userManager.getUsers().get(username);
            String mapUser = request.getParameter("mapUser");
            String index = request.getParameter("index");
            String serialNumber = request.getParameter("serialNumber");
            User userMap = userManager.getUsers().get(mapUser);
            synchronized (this) {
                isMatched = userMap.getEngine().setMatchedRide(Integer.parseInt(serialNumber), sessionUser.getAllOptions().get(Integer.parseInt(index)));
            }
            if (!isMatched) {
                sessionUser.addAlert("ERROR: the trip if full, please choose other trip!");
            } else {
                // add transfer action to pooler
                CombinedTrip matchedTrip = sessionUser.getAllOptions().get(Integer.parseInt(index));
                sessionUser.addAction(true, matchedTrip.getPrice(), matchedTrip.getTrip().getFirst().getTrip().getSchedule().scheduleToActionString());
                //add receive action to trip owner
                for (MatchedRide single_ride : matchedTrip.getTrip()) {
                    String tripOwner = single_ride.getTrip().getOwner();
                    User tripOwnerUser = userManager.getUsers().get(tripOwner);
                    Station[] route = new Station[single_ride.getRoute().size()];
                    for (int i = 0; i < single_ride.getRoute().size(); i++) {
                        route[i] = single_ride.getRoute().get(i);
                    }
                    int price = userMap.getEngine().getData().priceCalculator(route, single_ride.getTrip().getPpk());
                    tripOwnerUser.addAction(false, price, single_ride.getTrip().getSchedule().scheduleToActionString());
                    // add alert to trip owner
                    tripOwnerUser.addAlert("NEW MATCH! map name: " + userMap.getEngine().getName() + ", uploaded by " + userMap.getName() + "; matched trip number: " + single_ride.getTrip().getSerialNumber() + ", price: " + price);
                    sessionUser.addAlert("Your request has been matched successfully!");
                }
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