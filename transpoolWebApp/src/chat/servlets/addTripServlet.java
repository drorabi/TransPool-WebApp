package chat.servlets;

import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import engine.exceptions.*;
import engine.users.User;
import engine.users.UserManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static chat.constants.Constants.ACTION_UPLOAD_ERROR;

@WebServlet(name = "addTrip", urlPatterns = {"/pages/map/addTrip"})

public class addTripServlet extends HttpServlet {

        private final String ACTION_ERROR_URL = "/pages/actionUploaderror/actionUploaderror.jsp";


        /**
         * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
         * methods.
         *
         * @param request servlet request
         * @param response servlet response
         * @throws ServletException if a servlet-specific error occurs
         * @throws IOException if an I/O error occurs
         */
        protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            String username = SessionUtils.getUsername(request);
            if (username == null) {
                response.sendRedirect(request.getContextPath() + "/pages/signin/signin.html");
            }
            else{
                String mapUser = request.getParameter("userFromMap");
                String capacity = request.getParameter("capacity");
                String ppk = request.getParameter("ppk");
                String day = request.getParameter("day");
                String timeString = request.getParameter("time");
                String recurrence = request.getParameter("recurrence");
                String[] time = timeString.split(":");
                String hour = time[0];
                String minutes = time[1];
                String route =request.getParameter("routeToSend");;
                User userOfMap=userManager.getUsers().get(mapUser);
                User sessionUser=userManager.getUsers().get(username);
                int minutesInFormat=userOfMap.getEngine().makeMinutesInFormat(minutes);
                int hourInFormat=Integer.parseInt(hour);
                int dayInFormat=Integer.parseInt(day);
                if(minutesInFormat==60){
                    minutesInFormat=minutesInFormat%60;
                    hourInFormat++;
                    if(hourInFormat==24){
                        hourInFormat=hourInFormat%24;
                        dayInFormat++;
                    }
                }
                try {
                    userOfMap.getEngine().addTrip(sessionUser.getName(),hourInFormat,minutesInFormat,route,Integer.parseInt(ppk),Integer.parseInt(capacity),dayInFormat,recurrence);
                    sessionUser.addTrip(userOfMap.getEngine().getData().getSerialNumberCounter()+mapUser,userOfMap.getEngine().getData().getPlannedTrips().getTrips().get(userOfMap.getEngine().getData().getSerialNumberCounter()));

                }catch( MissingTimeValue  | MissingNameField | InvalidStartDayValue | invalidCapacityValue | InvalidPpkValue | RouteIsEmpty | NameExsitInSystem e){
                    request.setAttribute(ACTION_UPLOAD_ERROR, e.getMessage());
                    getServletContext().getRequestDispatcher(ACTION_UPLOAD_ERROR).forward(request, response);
                }
            }

        }

        private void logServerMessage(String message) {
            System.out.println(message);
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


