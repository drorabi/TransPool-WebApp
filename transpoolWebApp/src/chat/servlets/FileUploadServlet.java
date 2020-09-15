package chat.servlets;

//taken from: http://www.servletworld.com/servlet-tutorials/servlet3/multipartconfig-file-upload-example.html
// and http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html

import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import engine.data.DataLoader;
import engine.users.User;
import engine.users.UserManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

import static chat.constants.Constants.FILE_UPLOAD_ERROR;
import static chat.constants.Constants.USERNAME;


@WebServlet("/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class FileUploadServlet extends HttpServlet {
    private final String FILE_ERROR_URL = "/pages/mapUploaderror/mapUploaderror.jsp";  // must start with '/' since will be used in request dispatcher...

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/pages/profile/profile.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        User sessionUser=userManager.getUsers().get(username);
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/pages/signin/signin.html");
        }
        else{
            String mapNameFromParameter = request.getParameter("map_name");
            if(mapNameFromParameter == null || mapNameFromParameter.isEmpty()){
                sessionUser.addAlert("ERROR: Please enter a file name and try again");
                response.sendRedirect(request.getContextPath() + "/pages/profile/profile.html");
                //request.setAttribute(FILE_UPLOAD_ERROR, "Please enter a file name!");
                //getServletContext().getRequestDispatcher(FILE_ERROR_URL).forward(request, response);
                return;
            }

            Map<String, User> allUsers=userManager.getUsers();
            for(Map.Entry<String, User> singleUser: allUsers.entrySet()){
                if((singleUser.getValue().getEngine().getName()!=null) && (singleUser.getValue().getEngine().getName().equals(mapNameFromParameter))){
                    sessionUser.addAlert("ERROR: The map name \""+mapNameFromParameter+"\" already exist. Please enter a uniq file name and try again");
                    response.sendRedirect(request.getContextPath() + "/pages/profile/profile.html");
                    return;
                }
            }

            Part fileContent = request.getPart("file");
            String errorMessage = userManager.getUsers().get(SessionUtils.getUsername(request)).getEngine().loadData(fileContent.getInputStream());
            if(errorMessage!=null){
                sessionUser.addAlert("ERROR: "+errorMessage);
                response.sendRedirect(request.getContextPath() + "/pages/profile/profile.html");
                //request.setAttribute(FILE_UPLOAD_ERROR, errorMessage);
                ///getServletContext().getRequestDispatcher(FILE_ERROR_URL).forward(request, response);
            }
            else{
                sessionUser.addAlert("Map Uploaded successfully!");
                userManager.getUsers().get(SessionUtils.getUsername(request)).getEngine().setName(request.getParameter("map_name"));
                response.sendRedirect(request.getContextPath() + "/pages/profile/profile.html");
            }
        }
    }

}