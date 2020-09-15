<%@ page import="chat.constants.Constants" %><%--
  Created by IntelliJ IDEA.
  User: drorc
  Date: 14/07/2020
  Time: 16:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Online Transpool</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script src="../../common/jquery-2.0.3.min.js"></script>
    <script src="../../common/context-path-helper.js"></script>
    <script src="profile.js"></script>
    <link rel="stylesheet" href="profile.css">

    <!-- Link the Bootstrap (from twitter) CSS framework in order to use its classes-->
    <link rel="stylesheet" href="../../common/bootstrap.min.css">

    <!-- Link jQuery JavaScript library in order to use the $ (jQuery) method-->
    <!-- <script src="common/jquery-2.0.3.min.js"></script>-->
    <!-- and\or any other scripts you might need to operate the JSP file behind the scene once it arrives to the client-->
</head>
<body>
<div class="container">
    <div id="alert"></div>
    <h1>Welcome to the Online Traspool</h1>
    <hr>
    <br>
    <form action="upload" enctype="multipart/form-data" method="POST">
        <div>
            <h4 style="display: inline">Select a map name:</h4>
            <input type="text" name="map_name"/>
            <input type="file" name="file">
        </div>
        <br>
        <input type="Submit" value="Upload File">
    </form>
    <% Object errorMessage = request.getAttribute(Constants.FILE_UPLOAD_ERROR);%>
    <% if (errorMessage != null) {%>
    <span class="bg-danger" style="color:red;"><%=errorMessage%></span>
    <% } %>
    <br>
    <div class="table-responsive">
        <table class="grid" cellspacing="0" id="dataTable">
            <tbody>
            <tr>
                <th colspan="1.5">  </th>
                <th colspan="2"> User name </th>
                <th colspan="2"> Map name </th>
                <th colspan="2"> Number of stations </th>
                <th colspan="2"> Number of roads </th>
                <th colspan="2"> Number of offers </th>
                <th colspan="2"> Number of requests </th>
            </tr>
            </tbody>
            <tbody id="MapsData"></tbody>
        </table>
    </div>
    <br>
    <hr>
    <br>
    <div>
        <h4 style="display: inline">Balance: </h4>
        <h4 id="currentBalance" style="display: inline"> 0 </h4>
    </div>
    <form id="loadForm" method="POST" action="loadMoney">
        <input type="number" min="1" name="amount" id="amount" required/>
        <input type="submit" value="Load Money"/>
    </form>
    <br>
    <div class="table-responsive">
        <table class="grid" cellspacing="0">
            <tbody>
            <tr>
                <th colspan="2"> Type </th>
                <th colspan="2"> Date </th>
                <th colspan="2"> Amount </th>
                <th colspan="2"> Balance before action </th>
                <th colspan="2"> Balance after action </th>
            </tr>
            </tbody>
            <tbody id="balance"></tbody>
        </table>
    </div>
</div>
</body>
</html>



