var refreshAccordionRate=2000; //milli seconds
var alertRefreshRate = 1000;
var ACCORDION_URL = buildUrlWithContextPath("accordion");
var MAP_URL = buildUrlWithContextPath("map");
var FEEDBACK_URL=buildUrlWithContextPath("feedback");
var STATIONS_URL = buildUrlWithContextPath("addStation");
var MAKE_MATCH_URL = buildUrlWithContextPath("makeMatch");
var ADD_FEEDBACK_URL=buildUrlWithContextPath("addFeedback");
var SET_MATCH_SERVLET_URL=buildUrlWithContextPath("setMatch");
var ALERT_URL=buildUrlWithContextPath("Alert");
var RESTART_ALL_COLORS=buildUrlWithContextPath("restartAllColors");
var MARK_ROUTE=buildUrlWithContextPath("markRoute");
var MARK_STATIONS = buildUrlWithContextPath("markStations");
var MAKE_MATCH_CONTENT_URL= buildUrlWithContextPath("makeMatchContent");
var ARROW_HEAD_SIZE = 15;

function markStations(from,to) {

    var stopFrom=  document.getElementById(from);
    stopFrom.setAttribute('fill', 'yellow');
    var stopTo=  document.getElementById(to);
    stopTo.setAttribute('fill', 'yellow');
}

function restartAllColorsOnMap(user) {
    var i = 0;
    var map = user.engine.data.mapData;
    var stations=map.stations.stations;
    var trails= map.trails.trail;

    $.each(trails || [], function (index, trail) {
        var road=document.getElementById(Object.keys(trails)[i]);

        if(trail.oneWay===false || trail.oneWay===null)
            road.setAttribute('style', "stroke:blue;stroke-width:3");
        else {
            road.setAttribute('style', "stroke:green;stroke-width:3");
        }
        i++;
    })
    i = 0;

    $.each(stations || [], function (index, station) {
        var stop=  document.getElementById(Object.keys(stations)[i]);
        stop.setAttribute('fill', 'red');
        i++;
    })
}

function markRoute(route) {

    var from=document.getElementById(route[0].name);
    var to=document.getElementById(route[route.length-1].name);
    from.setAttribute('fill', 'yellow');
    to.setAttribute('fill', 'yellow');

    for(var i=0; i<route.length-1 ; i++){

        if(document.getElementById(route[i].name+route[i+1].name)!==null)
            document.getElementById(route[i].name+route[i+1].name).setAttribute('style', "stroke:yellow;stroke-width:3");
        else
            document.getElementById(route[i+1].name+route[i].name).setAttribute('style', "stroke:yellow;stroke-width:3");
    }

}

function createCircle(name, x, y) {
    var circle = document.createElementNS("http://www.w3.org/2000/svg", 'circle');
    circle.setAttribute('id', name);
    circle.setAttribute('cx', x);
    circle.setAttribute('cy', y);
    circle.setAttribute('r', '7');
    circle.setAttribute('stroke', "black");
    circle.setAttribute('stroke-width', '1');
    circle.setAttribute('fill', 'red');
    return circle;
}

function createRoad(x1, y1, x2, y2, oneWay,id) {
    var road = document.createElementNS("http://www.w3.org/2000/svg", 'line');
    road.setAttribute('x1', x1);
    road.setAttribute('y1', y1);
    road.setAttribute('x2', x2);
    road.setAttribute('y2', y2);
    road.setAttribute("id",id);
    if(oneWay===false || oneWay===null)
        road.setAttribute('style', "stroke:blue;stroke-width:3");
    else {
        road.setAttribute('style', "stroke:green;stroke-width:3");
    }
    return road;
}

function createTitle(name,x,y) {
    var textElem = document.createElementNS('http://www.w3.org/2000/svg', 'text');
    textElem.setAttributeNS(null, 'y', y-15);
    textElem.setAttributeNS(null, 'x', x);
    textElem.setAttribute("fill", "black");
    textElem.setAttribute("font-size", "10px");
    var txt = document.createTextNode(name);
    textElem.appendChild(txt);
    return textElem;
}



function refreshMap(user) {
    $("#map").empty();

    var map = user.engine.data.mapData;
    var boundaries=map.boundaries;
    var stations=map.stations.stations;
    var trails= map.trails.trail;
    var colSpace= 500/boundaries.width;
    var rowSpace=500/boundaries.length;
    var mapID=document.getElementById('map');
    var svgns = "http://www.w3.org/2000/svg";

    var rect = document.createElementNS(svgns, 'rect');
    rect.setAttributeNS(null, 'height', '500');
    rect.setAttributeNS(null, 'width', '500');
    rect.setAttributeNS(null, 'fill', 'lightblue');
    rect.setAttributeNS(null, 'stroke-width', '1');
    rect.setAttributeNS(null, 'stroke', 'rgb(0,0,0)');
    mapID.appendChild(rect);


    $.each(trails || [], function (index, trail) {
        var road=createRoad(trail.x1*colSpace, trail.y1*rowSpace, trail.x2*colSpace, trail.y2*rowSpace, trail.oneWay,trail.from+trail.to);
        mapID.appendChild(road);
        if(!(trail.oneWay===false || trail.oneWay===null)){
            var startX = trail.x1*colSpace;
            var startY = trail.y1*colSpace;
            var endX = trail.x2*colSpace;
            var endY = trail.y2*colSpace;

            var arrowHeadX = startX + ((endX - startX) / 2);
            var arrowHeadY = startY + ((endY - startY) / 2);

            var angle = Math.atan2((arrowHeadY - startY), (arrowHeadX - startX)) - Math.PI / 2.0;
            var sin = Math.sin(angle);
            var cos = Math.cos(angle);

            var tx2 = (- 1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * ARROW_HEAD_SIZE + arrowHeadX;
            var ty2 = (- 1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * ARROW_HEAD_SIZE + arrowHeadY;

            var tx3 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * ARROW_HEAD_SIZE + arrowHeadX;
            var ty3 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * ARROW_HEAD_SIZE + arrowHeadY;


            var triangle = document.createElementNS("http://www.w3.org/2000/svg", 'polygon');
            triangle.setAttribute("points", arrowHeadX+','+arrowHeadY+' '+tx2+','+ty2+' '+ tx3+','+ty3);
            triangle.setAttribute("style","fill:lime;stroke:purple;stroke-width:1");
            mapID.appendChild(triangle);
        }
    })

    $.each(stations || [], function (index, station) {
        var coordinate = station.coordinate;
        var circle = createCircle(station.name, coordinate.x * colSpace, coordinate.y*rowSpace);
        mapID.appendChild(circle);
        var title=createTitle(station.name, coordinate.x * colSpace, coordinate.y*rowSpace);
        mapID.appendChild(title);
    })
}

/////////////////////////////////////////
//-------   ADD TRIP   --------------------
/////////////////////////////////////////


function ajaxResetAddTrip() {

    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const mapUsername = urlParams.get('name');


    $.ajax({
        type: 'GET',
        url: STATIONS_URL,
        data: { route : "", mapUsername: mapUsername},
        success: function (data) {
            document.getElementById("addTripButton").disabled=true;
            document.getElementById("route").textContent="";
            var stationScroller = document.getElementById("stations");
            stationScroller.innerHTML='';
            $.each(data || [], function (index, station) {
                var option = document.createElement("option");
                option.appendChild(document.createTextNode(station.name));
                stationScroller.appendChild(option);

            })
            document.getElementById("addStation").disabled=false;
            document.getElementById("addStation").disabled=false;
        }
    })
}

function ajaxAddStationAddTrip() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const mapUsername = urlParams.get('name');
    var route = document.getElementById("route");
    var routeStr = route.textContent;
    var stationscroller = document.getElementById("stations");
    if(routeStr.localeCompare("")===0)
        routeStr=stationscroller.options[stationscroller.selectedIndex].value;
    else
        routeStr = routeStr + "," + stationscroller.options[stationscroller.selectedIndex].value;

    route.textContent=routeStr;
    document.getElementById("routeToSend").value = routeStr;

    var str = routeStr.split(",");
    if(str.length>1)
        document.getElementById("addTripButton").disabled = false;

    $.ajax({
        type: 'GET',
        url: STATIONS_URL,
        data: { route : routeStr, mapUsername: mapUsername},
        success: function (data) {

            var stationScroller = document.getElementById("stations");
            stationScroller.innerHTML='';

            $.each(data || [], function (index, station) {
                var option = document.createElement("option");
                option.appendChild(document.createTextNode(station));
                stationScroller.appendChild(option);

            })

            if(data[0].localeCompare("")===0){
                document.getElementById("addStation").disabled=true;
            }

        }
    })
}

function createActionsForDriver(user) {

    var space = document.createElement("br");

    $('<button name="accordion" class="accordion"> Add Trip </button>' +
        '<div class="panel">' +
        '<p id=' + '\"trip\"' + '></p>' +
        '</div>').appendTo($("#actions"));
    var trip=document.getElementById("trip");


    var addTrip = document.createElement("form");
    addTrip.setAttribute("method", "post");
    addTrip.setAttribute("action", "addTrip");
    addTrip.setAttribute("id", "addTrip");

    var userFromMap = document.createElement("input");
    userFromMap.setAttribute("type", "hidden");
    userFromMap.setAttribute("id", "userFromMap");
    userFromMap.setAttribute("name", "userFromMap");
    userFromMap.setAttribute("value", user.name);
    addTrip.appendChild(userFromMap);

    var capacityHeader=document.createElement("h6");
    capacityHeader.appendChild(document.createTextNode("Capacity:"));
    addTrip.appendChild(capacityHeader);
    var capacity = document.createElement("input");
    capacity.setAttribute("value", "");
    capacity.setAttribute("required", "");
    capacity.setAttribute("min", "1");
    capacity.setAttribute("name", "capacity");
    capacity.setAttribute("type", "number");
    addTrip.appendChild(capacity);
    addTrip.appendChild(space);

    var dayHeader=document.createElement("h6");
    dayHeader.appendChild(document.createTextNode("Day:"));
    addTrip.appendChild(dayHeader);
    var day = document.createElement("input");
    day.setAttribute("value", "");
    day.setAttribute("name", "day");
    day.setAttribute("min", "1");
    day.setAttribute("required", "");
    day.setAttribute("type", "number");
    addTrip.appendChild(day);
    addTrip.appendChild(space);

    var ppkHeader=document.createElement("h6");
    ppkHeader.appendChild(document.createTextNode("PPK:"));
    addTrip.appendChild(ppkHeader);
    var ppk = document.createElement("input");
    ppk.setAttribute("value", "");
    ppk.setAttribute("name", "ppk");
    ppk.setAttribute("required", "");
    ppk.setAttribute("min", "1");
    ppk.setAttribute("type", "number");
    addTrip.appendChild(ppk);
    addTrip.appendChild(space);

    var recurrenceHeader=document.createElement("h6");
    recurrenceHeader.appendChild(document.createTextNode("Recurrences:"));
    addTrip.appendChild(recurrenceHeader);
    var recurrence = document.createElement("select");
    recurrence.setAttribute("name", "recurrence");
    var oneTime = document.createElement("option");
    oneTime.appendChild(document.createTextNode("One time"));
    var daily = document.createElement("option");
    daily.appendChild(document.createTextNode("Daily"));
    var biDaily = document.createElement("option");
    biDaily.appendChild(document.createTextNode("BiDaily"));
    var weekly = document.createElement("option");
    weekly.appendChild(document.createTextNode("Weekly"));
    var monthly = document.createElement("option");
    monthly.appendChild(document.createTextNode("Monthly"));
    recurrence.appendChild(oneTime);
    recurrence.appendChild(daily);
    recurrence.appendChild(biDaily);
    recurrence.appendChild(weekly);
    recurrence.appendChild(monthly);
    addTrip.appendChild(recurrence);
    addTrip.appendChild(space);

    var timeHeader=document.createElement("h6");
    timeHeader.appendChild(document.createTextNode("Time:"));
    addTrip.appendChild(timeHeader);
    var time = document.createElement("input");
    time.setAttribute("type", "time");
    time.setAttribute("required", "");
    time.setAttribute("name", "time");
    addTrip.appendChild(time);
    addTrip.appendChild(space);

    //build route
    var routeHeader=document.createElement("h6");
    routeHeader.appendChild(document.createTextNode("Choose station to add to the route:"));
    addTrip.appendChild(routeHeader);
    addTrip.appendChild(space);

    var from = document.createElement("select");
    from.setAttribute("name", "stations");
    from.setAttribute("id", "stations");
    $.each(user.engine.data.mapData.stations.stations || [], function (i, item) {
        var option = document.createElement("option");
        option.appendChild(document.createTextNode(item.name));
        from.appendChild(option);
    });
    addTrip.appendChild(from);
    addTrip.appendChild(space);

    var row=document.createElement("div");
    var SObutton = document.createElement("button");
    SObutton.setAttribute("type", "button");
    SObutton.appendChild(document.createTextNode("Start over"));
    SObutton.onclick=function () {
        ajaxResetAddTrip();
    }

    var submit = document.createElement("button");
    submit.setAttribute("type", "button");
    submit.setAttribute("id","addStation");
    submit.appendChild(document.createTextNode("Add station to trip"));
    submit.onclick=function () {
        ajaxAddStationAddTrip();
    }

    row.appendChild(submit);
    row.appendChild(SObutton);
    addTrip.appendChild(row);

    var route=document.createElement("h6");
    route.setAttribute("id","route");
    route.setAttribute("name","route")
    route.appendChild(document.createTextNode(""));
    addTrip.appendChild(route);

    var routeToSend=document.createElement("input");
    routeToSend.setAttribute("name", "routeToSend");
    routeToSend.setAttribute("id", "routeToSend");
    routeToSend.setAttribute("type", "hidden");
    routeToSend.setAttribute("value", "");
    addTrip.appendChild(routeToSend);

    var submit = document.createElement("input");
    submit.setAttribute("id","addTripButton");
    submit.setAttribute("value", "Add!");
    submit.setAttribute("type", "submit");
    addTrip.appendChild(submit);
    trip.appendChild(addTrip);

    submit.disabled=true;

    overrideSubmitAddTripForm();
}

function ajaxFeedBacksContent(){
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const username = urlParams.get('name');
    $.ajax({
        type: 'GET',
        url: FEEDBACK_URL,
        data: {name:username},
        success: function (feedbacks) {
            createFeedbacksAccordion(feedbacks);
        }
    })
}


function overrideSubmitAddTripForm() {
    $('#addTrip').submit(function (e) {
        // e.preventDefault();
        var parameters = $(this).serialize();
        $.ajax({
            data: parameters,
            url: this.action,
            error: function() {
                console.error("Failed to submit");
            },
            success: function() {
                triggerAjaxMapContent();
            }
        });
        return false;
    });
}



/////////////////////////////////////////
//-------   ADD REQUEST   --------------------
/////////////////////////////////////////

function addRequestOnChange() {
    var fromElement=document.getElementById("from");
    var toElement=document.getElementById("to");
    var buttonElement=document.getElementById("addRequestButton");
    var errorMessage =document.getElementById("error");

    var from=fromElement.options[fromElement.selectedIndex].value;
    var to=toElement.options[toElement.selectedIndex].value;

    if(from.localeCompare(to)===0){
        buttonElement.disabled = true;
        errorMessage.style.visibility = "visible";
    }else{
        buttonElement.disabled = false;
        errorMessage.style.visibility = "hidden";

    }
}

function createActionsForPooler(user) {
    $("#actions").empty();
    var space = document.createElement("br");

    $('<button name="accordion" class="accordion"> Add Request </button>' +
        '<div class="panel">' +
        '<p id=' + '\"request\"' + '></p>' +
        '</div>').appendTo($("#actions"));
    var request=document.getElementById("request");


    var addRequest = document.createElement("form");
    addRequest.setAttribute("method", "POST");
    addRequest.setAttribute("action", "addRequest");
    addRequest.setAttribute("id", "addRequest");

    var userFromMap = document.createElement("input");
    userFromMap.setAttribute("type", "hidden");
    userFromMap.setAttribute("id", "userFromMap");
    userFromMap.setAttribute("name", "userFromMap");
    userFromMap.setAttribute("value", user.name);
    addRequest.appendChild(userFromMap);


    var fromHeader=document.createElement("h6");
    fromHeader.appendChild(document.createTextNode("From:"));
    addRequest.appendChild(fromHeader);
    var from = document.createElement("select");
    from.setAttribute("name", "from");
    from.setAttribute("id", "from");
    from.setAttribute("onchange","addRequestOnChange()");
    $.each(user.engine.data.mapData.stations.stations || [], function (i, item) {
        var option = document.createElement("option");
        option.appendChild(document.createTextNode(item.name));
        from.appendChild(option);
    });
    addRequest.appendChild(from);
    addRequest.appendChild(space);

    var toHeader=document.createElement("h6");
    toHeader.appendChild(document.createTextNode("To:"));
    addRequest.appendChild(toHeader);
    var to = document.createElement("select");
    to.setAttribute("name", "to");
    to.setAttribute("id", "to");
    to.setAttribute("onchange","addRequestOnChange()");
    $.each(user.engine.data.mapData.stations.stations || [], function (i, item) {
        var option = document.createElement("option");
        option.appendChild(document.createTextNode(item.name));
        to.appendChild(option);
    });
    addRequest.appendChild(to);
    addRequest.appendChild(space);

    var stationsError=document.createElement("h5");
    stationsError.appendChild(document.createTextNode("Departure and destination can't be the same station"));
    stationsError.setAttribute("style", "color: red;");
    stationsError.setAttribute("id", "error");
    addRequest.appendChild(stationsError);
    stationsError.style.visibility="hidden";
    addRequest.appendChild(space);


    var dayHeader=document.createElement("h6");
    dayHeader.appendChild(document.createTextNode("Day:"));
    addRequest.appendChild(dayHeader);
    var day = document.createElement("input");
    day.setAttribute("value", "");
    day.setAttribute("name", "day");
    day.setAttribute("min", "1");
    day.setAttribute("required", "");
    day.setAttribute("type", "number");
    addRequest.appendChild(day);
    addRequest.appendChild(space);

    var timeHeader=document.createElement("h6");
    timeHeader.appendChild(document.createTextNode("Time:"));
    addRequest.appendChild(timeHeader);
    var time = document.createElement("input");
    time.setAttribute("type", "time");
    time.setAttribute("required", "");
    time.setAttribute("name", "time");
    addRequest.appendChild(time);
    addRequest.appendChild(space);


    var setByHeader=document.createElement("h6");
    setByHeader.appendChild(document.createTextNode("Set by:"));
    addRequest.appendChild(setByHeader);
    var row = document.createElement('div');
    var arrival = document.createElement("input");
    arrival.setAttribute("type", "radio");
    arrival.setAttribute("id", "arrival");
    arrival.setAttribute("name", "setBy");
    arrival.setAttribute("value", "0");
    row.appendChild(arrival);
    var label = document.createElement("label");
    label.setAttribute("for", arrival.id);
    label.innerHTML="Arrival";
    row.appendChild(label);
    addRequest.appendChild(row);
    addRequest.appendChild(space);
    var anotherrow=document.createElement("div");
    var departure = document.createElement("input");
    departure.setAttribute("type", "radio");
    departure.setAttribute("id", "departure");
    departure.setAttribute("checked", "");
    departure.setAttribute("name", "setBy");
    departure.setAttribute("value", "1");
    anotherrow.appendChild(departure);
    var anotherlabel = document.createElement("label");
    anotherlabel.setAttribute("for", departure.id);
    anotherlabel.innerHTML="Departure";
    anotherrow.appendChild(anotherlabel);
    addRequest.appendChild(anotherrow);
    addRequest.appendChild(space);


    var some = document.createElement("input");
    some.setAttribute("value", "Add!");
    some.setAttribute("id","addRequestButton");
    some.setAttribute("type", "submit");
    some.setAttribute("disabled", "true")
    addRequest.appendChild(some);
    request.appendChild(addRequest);

    overrideSubmitAddRequestForm();
}

function overrideSubmitAddRequestForm() {
    $('#addRequest').submit(function (e) {
        // e.preventDefault();
        var parameters = $(this).serialize();
        $.ajax({
            data: parameters,
            url: this.action,
            error: function() {
                console.error("Failed to submit");
            },
            success: function() {
                triggerAjaxMapContent();
            }
        });
        return false;
    });
}

function createActions(users) {
    $("#actions").empty();
    if(users[1].userType===0) {
        createActionsForDriver(users[0]);
    }else {
        createActionsForPooler(users[0]);
    }
}

function createInfo(data) {
    createTripsAccordion(data[Object.keys(data)[0]]);
    createRequestAccordion(data[Object.keys(data)[1]],data[Object.keys(data)[2]]);
    accordionOrder();
}

function createTripsAccordion(trips) {
    //$("#rideInfo").empty();
    debugger;
    var i=0;
    $.each(trips || [], function (index, trip) {
        var singleTripAcc=document.getElementById(Object.keys(trips)[i].substr(0,4));
        if(singleTripAcc===null){
            $('<button id="'+Object.keys(trips)[i].substr(0,4)+'" name="accordionTrip" class="accordion">'+ Object.keys(trips)[i] + '</button>' +
                '<div name="panelTrip" class="panel" style="white-space: pre-line;" >' +
                '<p id="'+'data'+Object.keys(trips)[i].substr(0,4)+'">'+ trip + '</p>' +
                '</div>').appendTo($("#rideInfo"));
        }
        else if(singleTripAcc!==null){
            debugger;
            var elemName="data"+Object.keys(trips)[i].substr(0,4);
            var tripDataTag=document.getElementById(elemName);
            tripDataTag.innerHTML="";
            tripDataTag.appendChild(document.createTextNode(trip));
        }
        i++;
    })
}

function createFeedbacksAccordion(feedbacks) {
    var feedbackElement=document.getElementById("allFeedbacks");
    $("#allFeedbacks").empty();
    feedbackElement.appendChild(document.createTextNode(feedbacks))
}

function createRequestAccordion(requests,type) {
    //$("#requestInfo").empty();
    var i = 0;
    if (Object.keys(type)[0].localeCompare("0") === 0) {   // if its a driver
        $.each(requests || [], function (index, request) {
            var singleRequestAcc = document.getElementById(Object.keys(requests)[i].substr(0, 4));
            if (singleRequestAcc === null) { //doesn't exist
                if (request.substr(0, 4).localeCompare("true") === 0) { // if is matched
                    $('<button id="' + Object.keys(requests)[i].substr(0, 4) + '" name="accordionRequest" class="accordion">' + Object.keys(requests)[i] + '</button>' +
                        '<div name="panelRequest" class="panel" style="white-space: pre-line;">' +
                        '<p id="' + 'data' + Object.keys(requests)[i].substr(0, 4) + '">' + request.substr(4) + '</p>' +
                        '</div>').appendTo($("#requestInfo"));
                } else {
                    $('<button id="' + Object.keys(requests)[i].substr(0, 4) + '" name="accordionRequest" class="accordion">' + Object.keys(requests)[i] + '</button>' +
                        '<div name="panelRequest" class="panel" style="white-space: pre-line;">' +
                        '<p id="' + 'data' + Object.keys(requests)[i].substr(0, 4) + '">' + request.substr(5) + '</p>' +
                        '</div>').appendTo($("#requestInfo"));
                }
            }
            else if (singleRequestAcc !== null) { //exist
                var elemName = "data" + Object.keys(requests)[i].substr(0, 4);
                var tripDataTag = document.getElementById(elemName);
                tripDataTag.innerHTML="";
                if (request.substr(0, 4).localeCompare("true") === 0) { // if is matched
                    tripDataTag.appendChild(document.createTextNode(request.substr(4)));
                }
                else{
                    tripDataTag.appendChild(document.createTextNode(request.substr(5)));
                }
            }
            i++;
        })
    }
    else {  // if its a pooler
        $.each(requests || [], function (index, request) {
            var singleRequestAcc = document.getElementById(Object.keys(requests)[i].substr(0, 4));
            if (singleRequestAcc === null) {
                $('<button id="' + Object.keys(requests)[i].substr(0, 4) + '" name="accordionRequest" class="accordion">' + Object.keys(requests)[i] + '</button>' +
                    '<div name="panelRequest" class="panel" style="white-space: pre-line;">' +
                    '<p id="' + 'data' + Object.keys(requests)[i].substr(0, 4) + '">' + request.substr(5) + '</p>' +
                    '<div id="'+'action'+Object.keys(requests)[i].substr(0, 4) + '">'+
                    '<h5>Choose number of offers to see:</h5>' +
                    '<input value="" id="' + Object.keys(requests)[i] + '" min="1" required type="number" </input>' +
                    '<br><br>' +
                    '<input type="radio" id="multiDriver" name="numOfDrivers' + Object.keys(requests)[i].substr(0, 4) + '"' + 'value="1" checked>' +
                    '<label for="multiDriver"> Multiple Drivers trip </label><br>' +
                    '<input type="radio" id="singleDriver" name="numOfDrivers' + Object.keys(requests)[i].substr(0, 4) + '"' + 'value="0">' +
                    '<label for="singleDriver"> Single Driver trip </label><br>' +
                    '<br>' +
                    '<button type=button onclick="makeAMatch(this.name)" name="' + Object.keys(requests)[i] + '">' +
                    'Find A Match</button>' + '</div>'+
                    '</div>').appendTo($("#requestInfo"));
            } else if (singleRequestAcc !== null) { //exist
                if (request.substr(0, 4).localeCompare("true") === 0) { // if is matched
                    var elemName="data"+Object.keys(requests)[i].substr(0,4);
                    var tripDataTag=document.getElementById(elemName);
                    tripDataTag.innerHTML="";
                    var accName = "action"+Object.keys(requests)[i].substr(0, 4);
                    var accAction = document.getElementById(accName);
                    accAction.innerHTML="";
                    tripDataTag.appendChild(document.createTextNode(request.substr(4)));
                    accAction.innerHTML='<button type=button name="' + Object.keys(requests)[i].substr(0, 4) + '"onclick="addFeedback(name)" >Add Feedback</button>';
                }
            }
            i++;
        })
    }
}

function getInfoRestartAllColorsOnMap() {

    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const username = urlParams.get('name');
    $.ajax({
        type: 'GET',
        url: RESTART_ALL_COLORS,
        data: {mapUser: username},
        success: function (user) {
            restartAllColorsOnMap(user);
        }
    })
}

function getInfoMarkRoute(serialNumber) {

    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const username = urlParams.get('name');
    $.ajax({
        type: 'GET',
        url: MARK_ROUTE,
        data: {serialNumber: serialNumber,name:username},
        success: function (arr) {
            restartAllColorsOnMap(arr[1]);
            markRoute(arr[0]);
        }
    })

}

function getInfoMarkRequest(name) {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const username = urlParams.get('name');
    $.ajax({
        type: 'GET',
        url: MARK_STATIONS,
        data: {serialNumber: name,name:username},
        success: function (arr) {
            restartAllColorsOnMap(arr[0])
            markStations(arr[1],arr[2]);
        }
    })
}

function actionsAccordionOrder() {
    var acc = document.getElementsByName("accordion");
    var panel = document.getElementsByName('panel');

    for (var i = 0; i < acc.length; i++) {
        acc[i].onclick = function () {
            var setClasses = !this.classList.contains('active');
            setClass(acc, 'active', 'remove');
            setClass(panel, 'show', 'remove');

            if (setClasses) {
                this.classList.toggle("active");
                this.nextElementSibling.classList.toggle("show");
            }
        }
    }

    function setClass(els, className, fnName) {
        for (var i = 0; i < els.length; i++) {
            els[i].classList[fnName](className);
        }
    }

}

function tripAccordionOrder() {
    var acc = document.getElementsByName("accordionTrip");
    var panel = document.getElementsByName('panelTrip');

    for (var i = 0; i < acc.length; i++) {
        acc[i].onclick = function () {
            var idString=this.id;
            var setClasses = !this.classList.contains('active');
            setClass(acc, 'active', 'remove');
            setClass(panel, 'show', 'remove');

            if (setClasses) {
                this.classList.toggle("active");
                this.nextElementSibling.classList.toggle("show");
                getInfoMarkRoute(idString)
            }
            else{
                getInfoRestartAllColorsOnMap();
            }
        }
    }

    function setClass(els, className, fnName) {
        for (var i = 0; i < els.length; i++) {
            els[i].classList[fnName](className);
        }
    }

}

function requstAccordionOrder() {
    var acc = document.getElementsByName("accordionRequest");
    var panel = document.getElementsByName('panelRequest');

    for (var i = 0; i < acc.length; i++) {
        acc[i].onclick = function () {
            var idString=this.id;
            var setClasses = !this.classList.contains('active');
            setClass(acc, 'active', 'remove');
            setClass(panel, 'show', 'remove');

            if (setClasses) {
                this.classList.toggle("active");
                this.nextElementSibling.classList.toggle("show");
                getInfoMarkRequest(idString)
            }
            else{
                getInfoRestartAllColorsOnMap();
            }
        }
    }

    function setClass(els, className, fnName) {
        for (var i = 0; i < els.length; i++) {
            els[i].classList[fnName](className);
        }
    }
}

function accordionOrder() {
    actionsAccordionOrder();
    tripAccordionOrder();
    requstAccordionOrder();
}

function ajaxAccordionContent() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const username = urlParams.get('name');
    $.ajax({
        type: 'GET',
        url: ACCORDION_URL,
        data: {name: username},
        success: function (data) {
            createInfo(data);
        },
        error: function(error) {
            //debugger;
        }
})}

function ajaxMapContent() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const username = urlParams.get('name');
    $.ajax({
        type: 'GET',
        url: MAP_URL,
        dataType: "json",
        data: {name: username},
        success: function (users) {
            debugger;
            refreshMap(users[0]);
            createActions(users);

        },
        error: function (e) {
            debugger;
            console.log("oh no!!");
        }
    });
}
///////////////////////////////////////////////////////////////////////////////
////////////////////////////////////MAKE A MATCH//////////////////////////////
//////////////////////////////////////////////////////////////////////////////
function chooseTrip(name,serialNumber) {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const username = urlParams.get('name');

    $.ajax({
        type: 'POST',
        url: SET_MATCH_SERVLET_URL,
        data: {mapUser: username, index: name, serialNumber: serialNumber},
        success: function (e) {
            $("#firstCol").empty();
            triggerAjaxMapContent();
        },
        error: function (e) {
            debugger;
            console.log("make a match causing problems")
        }
    });
}

function createMakeMatchAccordionOptions(data, numOfOffers,serialNumber) {
    var space = document.createElement("br");

    $("#firstCol").empty();
    var firstColom=document.getElementById("firstCol");

    var i = 1;
    var u=0;
    if(data.length<numOfOffers && data.length>0) {
        var matchHeader=document.createElement("h2");
        matchHeader.appendChild(document.createTextNode("Find Your Ride:"));
        firstColom.appendChild(matchHeader);
        firstColom.appendChild(space);
        firstColom.appendChild(space);
        $.each(data || [], function (index, trip) {

            $('<button name="accordion" class="accordion">' + i + '</button>' +
                '<div name="panel" class="panel" style="white-space: pre-line;">' +
                '<p>' + trip + '</p>' + '<button name="'+ u +'" type=button onclick="chooseTrip(name,'+ serialNumber +')" >Choose Trip</button>' +
                '</div>').appendTo($("#firstCol"));
            i++;
            u++;
        })
    }else if(data.length===0) {
        $('<div class="alert alert-danger fade in alert-dismissible">' +
            '<span type="button"class="close" data-dismiss="alert" aria-label="close">&times;</span>' +
            'No available rides in this map!' +
            '</div>').appendTo($("#alert"));
        //var noMatch = document.createElement("h3");
       //noMatch.appendChild(document.createTextNode("NO AVAILABLE RIDES IN THE SYSTEM"));
       //firstColom.appendChild(noMatch);

    }else{
        var matchHeader=document.createElement("h2");
        matchHeader.appendChild(document.createTextNode("Find Your Ride:"));
        firstColom.appendChild(matchHeader);
        firstColom.appendChild(space);
        firstColom.appendChild(space);
        var j;
        for(j=0;j<data.length;j++){
            $('<button name="accordion" class="accordion">' + i + '</button>' +
                '<div name="panel" class="panel" style="white-space: pre-line;">' +
                '<p>' + data[j] + '</p>' + '<button name="'+ u +'"type=button onclick="chooseTrip(name,'+ serialNumber +')" >Choose Trip</button>' +
                '</div>').appendTo($("#firstCol"));
            i++;
            u++;
        }
    }
    triggerAjaxMapContent();
}

function createMakeMatch(data,numOfOffers,serialNumber) {
   createMakeMatchAccordionOptions(data,numOfOffers,serialNumber)
}

function ajaxCreateMakeMatch(serialNumber,numOfOffers) {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const username = urlParams.get('name');

    $.ajax({
        type: 'GET',
        url: MAKE_MATCH_CONTENT_URL,
        data: {mapUser: username },
        serialNumber:serialNumber,
        numOfOffers:numOfOffers,
        success: function (data) {
            debugger;
            createMakeMatch(data,this.numOfOffers,this.serialNumber);
        },
        error: function (data) {
            debugger;
            console.log("make a match causing problems")
        }
    })

}

function makeAMatch(requestname){
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        const username = urlParams.get('name');

        var serialNum = requestname.substr(0,4);
        var numOfOffers=document.getElementById(requestname).value.substr(0,4);

        var numOfDrivers="single";
        var elemName="numOfDrivers"+requestname.substr(0,4);
        var ele = document.getElementsByName(elemName);
        if(ele[0].checked)
            numOfDrivers="multi";
        $.ajax({
            type: 'POST',
            url: MAKE_MATCH_URL,
            data: {mapUser: username, serialNumber : serialNum, numOfOffers: numOfOffers, numOfDrivers: numOfDrivers},
            serialNumber:serialNum,
            numOfOffers:numOfOffers,
            success: function () {
                debugger;
                ajaxCreateMakeMatch(this.serialNumber,this.numOfOffers);
            },
            error: function (data) {
                debugger;
                console.log("make a match causing problems")
            }
        })
    }

///////////////////////////////////////////////////////////////////////////////
////////////////////////////////////ADD FEEDBACK/////////////////////////////
//////////////////////////////////////////////////////////////////////////////

function createAddFeedback(data) {
    $("#firstCol").empty();
    var space = document.createElement("br");
    var firstColom=document.getElementById("firstCol");


    var addFeedback = document.createElement("form");
    addFeedback.setAttribute("method", "post");
    addFeedback.setAttribute("action", "setFeedback");
    addFeedback.setAttribute("id", "addFeedback");

    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const username = urlParams.get('name');
    var userFromMap = document.createElement("input");
    userFromMap.setAttribute("type", "hidden");
    userFromMap.setAttribute("id", "userFromMap");
    userFromMap.setAttribute("name", "userFromMap");
    userFromMap.setAttribute("value", username);
    addFeedback.appendChild(userFromMap);

    var addFeedbackHeader=document.createElement("h2");
    addFeedbackHeader.appendChild(document.createTextNode("Add Feedback:"));
    addFeedback.appendChild(addFeedbackHeader);

    var row=document.createElement("div");
    var tripHeader=document.createElement("h6");
    tripHeader.appendChild(document.createTextNode("Choose trip to rate:"));
    row.appendChild(tripHeader);
    var trips = document.createElement("select");
    trips.setAttribute("name", "trips");
    trips.setAttribute("id", "trips");
    trips.setAttribute("required", "");
    $.each(data || [], function (i, item) {
        var option = document.createElement("option");
        option.appendChild(document.createTextNode(item));
        trips.appendChild(option);
    });
    row.appendChild(trips);
    addFeedback.appendChild(row);
    var anotherrow=document.createElement("div");
    var starsHeader=document.createElement("h6");
    starsHeader.appendChild(document.createTextNode("Stars rate:"));
    anotherrow.appendChild(starsHeader);
    var stars = document.createElement("select");
    stars.setAttribute("name", "stars");
    stars.setAttribute("id", "stars");
    stars.setAttribute("required","");
    var star1 = document.createElement("option");
    star1.appendChild(document.createTextNode("1"));
    stars.appendChild(star1);
    var star2 = document.createElement("option");
    star2.appendChild(document.createTextNode("2"));
    stars.appendChild(star2);
    var star3 = document.createElement("option");
    star3.appendChild(document.createTextNode("3"));
    stars.appendChild(star3);
    var star4 = document.createElement("option");
    star4.appendChild(document.createTextNode("4"));
    stars.appendChild(star4);
    var star5 = document.createElement("option");
    star5.appendChild(document.createTextNode("5"));
    stars.appendChild(star5);
    anotherrow.appendChild(stars);
    addFeedback.appendChild(anotherrow);
    addFeedback.appendChild(space);

    var feedbackHeader=document.createElement("h6");
    feedbackHeader.appendChild(document.createTextNode("Write your feedback:"));
    addFeedback.appendChild(feedbackHeader);
    var feedback = document.createElement("textarea");
    feedback.setAttribute("name", "feedback");
    feedback.setAttribute("id", "feedback");
    feedback.setAttribute("rows", "10");
    feedback.setAttribute("cols", "30");
    addFeedback.appendChild(feedback);
    addFeedback.appendChild(space);

    var submit = document.createElement("input");
    submit.setAttribute("value", "Add feedback");
    submit.setAttribute("id","addfeedback");
    submit.setAttribute("type", "submit");
    addFeedback.appendChild(submit);
    firstColom.appendChild(addFeedback);

    overrideSetFeedbackForm();
}

function overrideSetFeedbackForm() {
    $('#addFeedback').submit(function (e) {
        // e.preventDefault();
        var parameters = $(this).serialize();
        $.ajax({
            data: parameters,
            url: this.action,
            error: function() {
                debugger;
                console.error("Failed to submit");
            },
            success: function() {
                debugger;
                $("#firstCol").empty();
                triggerAjaxMapContent();
            }
        });
        return false;
    });
}

function addFeedback(name) {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const username = urlParams.get('name');
    $.ajax({
        type: 'GET',
        url: ADD_FEEDBACK_URL,
        data: {mapUser: username, serialNumber: name},
        success: function (data) {
            createAddFeedback(data);
        }
    })
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
            //debugger;
            refreshAlertsList(alerts);
        }
    });
}

function triggerAjaxMapContent() {
    ajaxMapContent();
}

function createFeedback() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const username = urlParams.get('name');
    $.ajax({
        type: 'GET',
        url: MAP_URL,
        dataType: "json",
        data: {name: username},
        success: function (users) {
            debugger;
            if(users[1].userType===0) {// if its a driver
                $('<h2>Feedbacks:</h2>\n' +
                    '<div id="feedbacks" class="feedbacks"></div>').appendTo($("#leftSide"));
                $('<button name="accordion"  class="accordion"> All Feedbacks </button>' +
                    '<div class="panel" style="white-space: pre-line;">' +
                    '<p id="allFeedbacks">No feedbacks</p>' +
                    '</div>').appendTo($("#feedbacks"));
                setInterval(ajaxFeedBacksContent, refreshAccordionRate);
            }
        },
        error: function (e) {
            debugger;
            console.log("oh no!!");
        }
    });
}


$(function () {
    triggerAjaxMapContent();
    setInterval(ajaxAlerts, alertRefreshRate);
    setInterval(ajaxAccordionContent,refreshAccordionRate);
    createFeedback();
});


