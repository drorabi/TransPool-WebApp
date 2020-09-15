package chat.servlets;

import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import com.google.gson.Gson;
import engine.converted.classes.Request;
import engine.converted.classes.Trip;
import engine.users.User;
import engine.users.UserManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class AccordionDataServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            String username = SessionUtils.getUsername(request);
            if (username == null) {
                response.sendRedirect(request.getContextPath() + "/pages/signin/signin.html");
            }
            else{
                Gson gson = new Gson();
                Map<String, Map<String,String>> data = new HashMap<>();
                Map<String,String> trips =new HashMap();
                Map<String,String> requests =new HashMap();
                Map<String,String> type =new HashMap();

                User sessionUser = userManager.getUsers().get(SessionUtils.getUsername(request));
                String mapUsername=request.getParameter("name");
                User mapUser=userManager.getUsers().get(mapUsername);

                Map<Integer, Request> poolersForDriver = new HashMap<>();
                Map<String, Request> poolersForPooler = new HashMap<>();
                Map<String, Trip> rideForDriver = new HashMap<>();
                Map<Integer, Trip> rideForPooler = new HashMap<>();
                if(sessionUser.getUserType()==0) {// if its a driver
                    //create  request-see all
                    poolersForDriver=mapUser.getEngine().getData().getRequests();
                    for(Map.Entry<Integer,Request> entry : poolersForDriver.entrySet()){
                        requests.put(entry.getValue().getSerialNumber() + " " + entry.getValue().getName(),entry.getValue().toString());
                    }
                    //create rides-see only the driver's
                    rideForDriver=sessionUser.getTrips();
                    for(Map.Entry<String,Trip> entry : rideForDriver.entrySet()) {
                        if(entry.getKey().substring(4).equals(mapUsername))
                            trips.put(entry.getValue().getSerialNumber() + " " + entry.getValue().getOwner(), entry.getValue().toString());
                    }
                }
                else{ // if its a pooler
                    //create request-see only the pooler's
                    poolersForPooler=sessionUser.getRequests();
                    for(Map.Entry<String,Request> entry : poolersForPooler.entrySet()){
                        if(entry.getKey().substring(4).equals(mapUsername))
                            requests.put(entry.getValue().getSerialNumber() + " " + entry.getValue().getName(),entry.getValue().toString());
                    }
                    //create rides-see all offers
                    rideForPooler=mapUser.getEngine().getData().getPlannedTrips().getTrips();
                    for(Map.Entry<Integer, Trip> entry: rideForPooler.entrySet()){
                        trips.put(entry.getValue().getSerialNumber()+" "+entry.getValue().getOwner(), entry.getValue().toString());
                    }
                }


                type.put(""+sessionUser.getUserType(),""+sessionUser.getUserType());

                data.put("trips",trips);
                data.put("requests",requests);
                data.put("type",type);
                String json = gson.toJson(data);
                out.println(json);
                out.flush();
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
