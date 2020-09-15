package engine.ui;

import engine.converted.classes.*;
import engine.data.DataLoader;
import engine.exceptions.*;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Engine {
    public Transpool data;
    private String name;
    private int numOfStations;
    private int numOfRoads;
    private int numOfOffers;
    private int numOfRequests;
    private int numOfMatchedRequests;

    //Displays-----------------------

    public String displayOfferedRides() throws InvalidDisplayNoRides {
        if (data.getPlannedTrips().getTrips().isEmpty())
            throw new InvalidDisplayNoRides();
        return data.getPlannedTrips().toString();
    }

    public String displayNotMatchedRequests() throws InvalidDisplayNoRequests, NoAvailableRequests {
        int counter = 0;
        if (data.getRequests().isEmpty())
            throw new InvalidDisplayNoRequests();
        String toString = "Unmatched requests:\n";
        for (Map.Entry<Integer, Request> entry : data.getRequests().entrySet()) {
            if (!entry.getValue().isMatched()) {
                toString = toString + entry.getValue().toString() + "\n";
                counter++;
            }
        }
        if (counter == 0)
            throw new NoAvailableRequests();
        return toString;
    }

    public String displayRidesRequests() throws InvalidDisplayNoRequests {
        String toString = "Requests:\n";
        if (data.getRequests().isEmpty())
            throw new InvalidDisplayNoRequests();

        for (Map.Entry<Integer, Request> entry : data.getRequests().entrySet()) {
            toString = toString + entry.getValue().toString() + "\n";
        }
        return toString;
    }

    public String displayAllStations() {
        return data.getMapData().getStations().toString();
    }

    //operations functions------------------

    public void addRideRequest(String name, String from, String to, int day, int hour, int minutes, boolean byDeparture) throws InvalidRideStartHour, InvalidRideStartDay, InvalidRideStartMinutes, InvalidRequestDepartureDestination, MissingTimeValue, MissingNameField, MissingFromToValue {
        data.addRequest(name, from, to, day, hour, minutes, byDeparture);
    }

    public void updateTrip(int choice, int request) throws InvalidChoiceForSerialNumber {
        Trip single_trip;
        for (Map.Entry<Integer, Trip> entry : data.getPlannedTrips().getTrips().entrySet()) {
            single_trip = entry.getValue();
            if (single_trip.getSerialNumber() == choice) {
                single_trip.updateTrip(data.getRequests().get(request));
                return;
            }
        }
        throw new InvalidChoiceForSerialNumber(choice);
    }

    public void checkStation(String station) throws InvalidStationsNameRequest {
        if (!data.getMapData().getStations().getStations().containsKey(station))
            throw new InvalidStationsNameRequest(station);
    }

    public void checkTime(int hour, int minutes) throws InvalidRequestMinutes, InvalidRequestHour {
        if (hour > 23 || hour < 0)
            throw new InvalidRequestHour(hour);
        if (minutes > 59 || minutes < 0)
            throw new InvalidRequestMinutes(minutes);
    }

    public void updateRequest(int requestNumber, int tripNumber) {
        Trip trip = data.getPlannedTrips().getTrips().get(tripNumber);
        int price = calculatePriceForRequest(data.getRequests().get(requestNumber).getFrom(), data.getRequests().get(requestNumber).getTo(), trip);
        int fuel = calculateFuelForRequest(data.getRequests().get(requestNumber).getFrom(), data.getRequests().get(requestNumber).getTo(), trip);
        data.UpdateRequest(requestNumber, price, fuel, trip);
    }

    private int calculateFuelForRequest(String from, String to, Trip trip) {
        int i, leanth = 0;
        double fuelConsumption = 0;
        for (i = 0; i < trip.getRide().length - 1; i++) {
            if (trip.getRide()[i].getName().equals(from)) {
                while (!trip.getRide()[i + 1].getName().equals(to)) {
                    if (data.getMapData().getTrails().getTrails().containsKey(trip.getRide()[i].getName() + trip.getRide()[i + 1].getName())) {
                        fuelConsumption = fuelConsumption + data.getMapData().getTrails().getTrails().get(trip.getRide()[i].getName() + trip.getRide()[i + 1].getName()).getFuelUse();
                        leanth = leanth + data.getMapData().getTrails().getTrails().get(trip.getRide()[i].getName() + trip.getRide()[i + 1].getName()).getLength();
                    } else {
                        fuelConsumption = fuelConsumption + data.getMapData().getTrails().getTrails().get(trip.getRide()[i + 1].getName() + trip.getRide()[i].getName()).getFuelUse();
                        leanth = leanth + data.getMapData().getTrails().getTrails().get(trip.getRide()[i + 1].getName() + trip.getRide()[i].getName()).getLength();
                    }
                    i++;
                }
                if (data.getMapData().getTrails().getTrails().containsKey(trip.getRide()[i].getName() + trip.getRide()[i + 1].getName())) {
                    fuelConsumption = fuelConsumption + data.getMapData().getTrails().getTrails().get(trip.getRide()[i].getName() + trip.getRide()[i + 1].getName()).getFuelUse();
                    leanth = leanth + data.getMapData().getTrails().getTrails().get(trip.getRide()[i].getName() + trip.getRide()[i + 1].getName()).getLength();
                } else {
                    fuelConsumption = fuelConsumption + data.getMapData().getTrails().getTrails().get(trip.getRide()[i + 1].getName() + trip.getRide()[i].getName()).getFuelUse();
                    leanth = leanth + data.getMapData().getTrails().getTrails().get(trip.getRide()[i + 1].getName() + trip.getRide()[i].getName()).getLength();
                }
                if (fuelConsumption == 0)
                    return 0;
                return (int) (leanth / fuelConsumption);
            }
        }
        if (fuelConsumption == 0)
            return 0;
        return (int) (leanth / fuelConsumption);
    }

    private int calculatePriceForRequest(String from, String to, Trip trip) {
        int i, price = 0;
        for (i = 0; i < trip.getRide().length - 1; i++) {
            if (trip.getRide()[i].getName().equals(from)) {
                while (!trip.getRide()[i + 1].getName().equals(to)) {
                    if (data.getMapData().getTrails().getTrails().containsKey(trip.getRide()[i].getName() + trip.getRide()[i + 1].getName()))
                        price = price + data.getMapData().getTrails().getTrails().get(trip.getRide()[i].getName() + trip.getRide()[i + 1].getName()).getLength() * trip.getPpk();
                    else
                        price = price + data.getMapData().getTrails().getTrails().get(trip.getRide()[i + 1].getName() + trip.getRide()[i].getName()).getLength() * trip.getPpk();
                    i++;
                }
                if (data.getMapData().getTrails().getTrails().containsKey(trip.getRide()[i].getName() + trip.getRide()[i + 1].getName()))
                    price = price + data.getMapData().getTrails().getTrails().get(trip.getRide()[i].getName() + trip.getRide()[i + 1].getName()).getLength() * trip.getPpk();
                else
                    price = price + data.getMapData().getTrails().getTrails().get(trip.getRide()[i + 1].getName() + trip.getRide()[i].getName()).getLength() * trip.getPpk();
                return price;
            }
        }
        return price;
    }

    public String displayOptionalStations(String from, String route) {
        String optionalStations = "";
        for (Map.Entry<String, Station> entry : data.getMapData().getStations().getStations().entrySet()) {
            if (!route.contains(entry.getValue().getName())) {
                if (data.getMapData().getTrails().getTrails().containsKey(from + entry.getValue().getName()))
                    optionalStations = optionalStations + entry.getValue().getName() + ",";
                if (data.getMapData().getTrails().getTrails().containsKey(entry.getValue().getName() + from) &&
                        (!data.getMapData().getTrails().getTrails().get(entry.getValue().getName() + from).getOneWay()))
                    optionalStations = optionalStations + entry.getValue().getName() + ",";
            }
        }

        return optionalStations;
    }

    public void checkTrail(String curr, String next) throws InvalidPathForTrip {
        if (!data.getMapData().getTrails().getTrails().containsKey(curr + next) && !data.getMapData().getTrails().getTrails().containsKey(next + curr))
            throw new InvalidPathForTrip(curr + " to " + next);

        if (data.getMapData().getTrails().getTrails().containsKey(next + curr) && data.getMapData().getTrails().getTrails().get(next + curr).getOneWay())
            throw new InvalidPathForTrip(curr + " to " + next);
    }

    public void checkRoute(String next, String route) throws InvalidRouteThroughTheStationTwice {
        if (route.contains(next))
            throw new InvalidRouteThroughTheStationTwice("this", next);
    }

    public void addTrip(String name, Integer hour, Integer minutes, String route, int ppk, int capacity, int startDay, String recurrences) throws InvalidStartDayValue, invalidCapacityValue, MissingNameField, InvalidPpkValue, RouteIsEmpty, MissingTimeValue, NameExsitInSystem {
        data.addTrip(name, hour, minutes, route, ppk, capacity, startDay, recurrences);
        numOfOffers++;
    }

    public void checkIfEmpty() throws NoTripsInTheSystem, InvalidDisplayNoRequests {
        if (data.getRequests().isEmpty())
            throw new InvalidDisplayNoRequests();
        if (data.getPlannedTrips().getTrips().isEmpty())
            throw new NoTripsInTheSystem();
    }

    public void checkRequest(int serialNumber) throws InvalidRequestName {
        if (!data.getRequests().containsKey(serialNumber))
            throw new InvalidRequestName(serialNumber);
    }

    public String displayMtachingTrips(Set<Trip> matchingTrips, int serialNumber) {
        String display = "";
        for (Trip single_trip : matchingTrips) {
            display = display + single_trip.toString() +
                    "\nAverage Fuel Consumption for your request:" + calculateFuelForRequest((data.getRequests().get(serialNumber).getFrom()), data.getRequests().get(serialNumber).getTo(), single_trip) +
                    "\nPrice for your request:" + calculatePriceForRequest(data.getRequests().get(serialNumber).getFrom(), data.getRequests().get(serialNumber).getTo(), single_trip) + "\n";
        }
        return display;
    }

    public void checkTrip(int choiceRide, Set<Trip> matchingTrips) throws InvalidChoiceForSerialNumber {
        if (!data.getPlannedTrips().getTrips().containsKey(choiceRide))
            throw new InvalidChoiceForSerialNumber(choiceRide);
        for (Trip single_trip : matchingTrips) {
            if (single_trip.getSerialNumber() == data.getPlannedTrips().getTrips().get(choiceRide).getSerialNumber())
                return;
        }
        throw new InvalidChoiceForSerialNumber(choiceRide);
    }

    public Transpool getData() {
        return data;
    }

    public void addFeedbackToTrip(Trip trip, int startsRank, String feedback) {
        data.getPlannedTrips().getTrips().get(trip.getSerialNumber()).updateFeedback(startsRank, feedback);
    }

    public String loadData(InputStream inputStream) {
        DataLoader dataLoader = new DataLoader();
        data = dataLoader.call(inputStream);
        if (!dataLoader.IsFileUploadSuccessfully()) {
            return dataLoader.getMassage();
        }
        numOfStations = data.getMapData().getStations().getStations_coordinates().size();
        numOfRoads = data.getMapData().getTrails().getTrails().size();
        numOfOffers = 0;
        numOfRequests = 0;
        numOfMatchedRequests=0;
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getNumOfOffers() {
        return numOfOffers;
    }

    public int getNumOfRequests() {
        return numOfRequests;
    }

    public int getNumOfRoads() {
        return numOfRoads;
    }

    public int getNumOfStations() {
        return numOfStations;
    }


    public void addRequest(String name, String from, String to, int day, int hour, int minutes, boolean byDepart) throws InvalidRideStartHour, InvalidRideStartDay, InvalidRequestDepartureDestination, MissingTimeValue, MissingNameField, MissingFromToValue, InvalidRideStartMinutes {
        data.addRequest(name, from, to, day, hour, minutes, byDepart);
        numOfRequests++;
    }

    public void makeAMatch(Request request, Integer numOfOffers, boolean b) {
        data.makeAMatch(request, numOfOffers, b);

    }

    public boolean setMatchedRide(int serialNumber , CombinedTrip matchedRides){
        numOfMatchedRequests++;
        return data.setMatchedRide(serialNumber, matchedRides);
    }

    public LinkedList<CombinedTrip> getAllOptions() {
        return data.getAllOptions();
    }

    public int makeMinutesInFormat(String minutes) {
        int minutesInInteger = Integer.parseInt(minutes);

        if(minutesInInteger%5==0)
            return minutesInInteger;
        else if(minutesInInteger%5>2){
            minutesInInteger+=(5-(minutesInInteger%5));
            return minutesInInteger;
        } else{
            minutesInInteger-=(minutesInInteger%5);
            return minutesInInteger;
        }
    }
}