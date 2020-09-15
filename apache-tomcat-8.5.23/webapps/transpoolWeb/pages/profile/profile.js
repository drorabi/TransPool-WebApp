var alertRefreshRate = 1000;
var refreshRate = 2000; //milli seconds
var MAPS_LIST_URL = buildUrlWithContextPath("MapsData");
var BALANCE_LIST_URL = buildUrlWithContextPath("Balance");
var ALERT_URL=buildUrlWithContextPath("Alert");

function refreshMapsList(users) {
    //clear all current maps
    $("#MapsData").empty();

    $.each(users || [], function (index, user) {
        console.log("Adding map #" + index + ": " + user);
        var form = document.createElement("form");
        form.setAttribute("method", "post");
        form.setAttribute("action", "../map/map.html?name=" + user.name);
        form.setAttribute("id", "myform");
        var AT = document.createElement("input");
        AT.setAttribute("value", user.name);
        AT.setAttribute("type", "hidden");
        AT.setAttribute("name", "username");
        form.appendChild(AT);
        var FN = document.createElement("input");
        FN.setAttribute("value", "Go to map");
        FN.setAttribute("name", "username");
        FN.setAttribute("type", "submit");
        form.appendChild(FN);

            $('<tr>' +
                '<td id="'+index+'"></td>' +
                '<td colspan="2">' + user.name + '</td>' +
                '<td colspan="2">' + user.engine.name + '</td>' +
                '<td colspan="2">' + user.engine.numOfStations + '</td>' +
                '<td colspan="2">' + user.engine.numOfRoads + '</td>' +
                '<td colspan="2">' + user.engine.numOfOffers + '</td>' +
                '<td colspan="2">' + user.engine.numOfMatchedRequests+'\\'+user.engine.numOfRequests + '</td>' + '</tr>'
            ).appendTo($("#MapsData"));
        +document.getElementById(index).append(form)
    });
}

function appendToBalanceArea(allActions) {
     $("#balance").empty();
    $.each(allActions || [], function (index, action) {
    $('<tr>' +
    '<td colspan="2">' + action.type + '</td>' +
    '<td colspan="2">' + action.date + '</td>' +
    '<td colspan="2">' + action.amount + '</td>' +
    '<td colspan="2">' + action.balanceBefore + '</td>' +
    '<td colspan="2">' + action.balanceAfter + '</td>' +
    '</tr>').appendTo($("#balance"));
    });
}

function createBalanceEntry (entry){
    entry.chatString = entry.chatString.replace (":)", "<img class='smiley-image' src='../../common/images/smiley.png'/>");
    return $("<span class=\"success\">").append(entry.username + "> " + entry.chatString);
}


function ajaxMapsList() {
    $.ajax({
        url: MAPS_LIST_URL,
        success: function(users) {
            refreshMapsList(users);
        }
    });
}

//call the server and get the chat version
//we also send it the current chat version so in case there was a change
//in the chat content, we will get the new string as well
function ajaxBalanceContent() {
    $.ajax({
        url: BALANCE_LIST_URL,
        dataType: 'json',
        timeout: 1000,

        success: function(user) {
            appendToBalanceArea(user.allActions);
            $("#currentBalance").empty();
            $("#currentBalance").append(user.balance);
            triggerAjaxBalanceContent();
        },
        error: function(error) {
            //debugger;
            triggerAjaxBalanceContent();
        }
    });
}

//add a method to the button in order to make that form use AJAX
//and not actually submit the form
$(function() { // onload...do
    //add a function to the submit event
    $("#loadForm").submit(function() {
        $.ajax({
            data: $(this).serialize(),
            url: this.action,
            timeout:2000,
            error: function() {
                console.error("Failed to submit");
            },
            success: function(r) {
                //do not add the user string to the chat area
                //since it's going to be retrieved from the server
                //$("#result h1").text(r);

            }
        });

        $("#amount").val("");
        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    });
});



function triggerAjaxBalanceContent() {
    setTimeout(ajaxBalanceContent, refreshRate);
}

function refreshAlertsList(alerts) {

    $.each(alerts || [], function (index, alert) {
        if(alert.substr(0, 5).localeCompare("ERROR")===0){
            $('<div class="alert alert-danger fade in alert-dismissible">' +
                '<span type="button"class="close" data-dismiss="alert" aria-label="close">&times;</span>' +
                alert +
                '</div>').appendTo($("#alert"));
        }else{
            $('<div class="alert alert-success fade in alert-dismissible">' +
                '<span type="button"class="close" data-dismiss="alert" aria-label="close">&times;</span>' +
                alert +
                '</div>').appendTo($("#alert"));
        }

    })
}

function ajaxAlerts() {
    $.ajax({
        url: ALERT_URL,
        success: function(alerts) {
            refreshAlertsList(alerts);
        }
    });
}

//activate the timer calls after the page is loaded
$(function() {
    //The users list is refreshed automatically every second
    triggerAjaxBalanceContent();
    setInterval(ajaxMapsList, refreshRate);
    setInterval(ajaxAlerts, alertRefreshRate)
    //The chat content is refreshed only once (using a timeout) but
    //on each call it triggers another execution of itself later (1 second later)
});