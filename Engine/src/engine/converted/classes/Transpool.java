package engine.converted.classes;

import engine.exceptions.*;
import engine.schema.generated.MapDescriptor;
import engine.schema.generated.TransPool;
import engine.schema.generated.TransPoolTrip;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

// JAXB converted TransPool
public class Transpool {
    protected MapData mapData;
    protected Plannedtrips plannedTrips;
    protected Map<Integer, Request> requests;
    public static int serialNumberCounter;
    private Calendar calendar;
    private LinkedList<CombinedTrip> allOptions;

    public Transpool(TransPool transPool) throws InvalidPathNames, InvalidMap, InvalidPathDepatureDestination,
            InvalidStationsNames, InvalidStationsCoordinates, InvalidRoute, InvalidStationsLocation, InvalidRideStartDay,
            InvalidRideStartHour, InvalidRouteThroughTheStationTwice, InvalidRideStartMinutes, NameExsitInSystem {
        serialNumberCounter=999;
        calendar = new Calendar();
        setMapData(transPool.getMapDescriptor());
        setPlannedtrips(transPool.getPlannedTrips());
        setRequests();
    }

    //setters------------

    public void setPlannedtrips(engine.schema.generated.PlannedTrips plannedTrips) throws InvalidRoute, InvalidRideStartDay, InvalidRideStartHour, InvalidRouteThroughTheStationTwice, InvalidRideStartMinutes, NameExsitInSystem {
        Map<Integer, Trip> trips = new HashMap<>();
        if (plannedTrips != null) {
            for (TransPoolTrip single_trip : plannedTrips.getTransPoolTrip()) {
                Station[] route = checkTripFromXML(single_trip);
                int price = priceCalculator(route, single_trip.getPPK());
                int fuel = fuelConsumptionCalculator(route);
                int serialNumber = ++Transpool.serialNumberCounter;
                if(isNameExist(trips,single_trip.getOwner()))
                    throw new NameExsitInSystem(single_trip.getOwner());
                Trip temp = new Trip(single_trip, serialNumber, route, price, fuel);
                trips.put(serialNumber, temp);
            }
        }
        this.plannedTrips = new Plannedtrips(trips);
    }

    private void setRequests() {
        requests = new HashMap<>();
    }

    private void setMapData(MapDescriptor oldMap) throws InvalidStationsLocation, InvalidStationsNames, InvalidMap, InvalidPathNames,
            InvalidStationsCoordinates, InvalidPathDepatureDestination {
        this.mapData = new MapData(oldMap);
    }

    //checker-------------------

    private Station[] checkTripFromXML(TransPoolTrip trip) throws InvalidRoute, InvalidRideStartHour, InvalidRideStartDay, InvalidRideStartMinutes {
        int minutes;
        checkTime(trip.getScheduling().getMinuteStart(), trip.getScheduling().getHourStart(), trip.getScheduling().getDayStart(), trip.getOwner());
        int day = trip.getScheduling().getDayStart();
        if (trip.getScheduling().getMinuteStart() == null)
            minutes = 0;
        else
            minutes = trip.getScheduling().getMinuteStart();
        int hour = trip.getScheduling().getHourStart();

        String[] temp = trip.getRoute().getPath().toUpperCase().split(",");
        String ride[] = deleteSpacesFromString(temp);
        Station[] route = new Station[ride.length];
        int station;
        for (station = 0; station < ride.length - 1; station++) {
            if (!mapData.getTrails().getTrails().containsKey(ride[station] + ride[station + 1])) {
                if (!mapData.getTrails().getTrails().containsKey(ride[station + 1] + ride[station])
                        || mapData.getTrails().getTrails().get(ride[station + 1] + ride[station]).getOneWay())
                    throw new InvalidRoute(trip.getOwner(), ride[station] + " to " + ride[station + 1]);
            }
            route[station] = new Station(mapData.getStations().getStations().get(ride[station]), hour, minutes, day);
            if (mapData.getTrails().getTrails().containsKey(ride[station] + ride[station + 1]))
                minutes = minutes + mapData.getTrails().getTrails().get(ride[station] + ride[station + 1]).getHowMuchTime();
            else
                minutes = minutes + mapData.getTrails().getTrails().get(ride[station + 1] + ride[station]).getHowMuchTime();
            if (minutes >= 60) {
                hour = hour + (minutes / 60);
                if (hour > 23) {
                    day = day + (hour / 24);
                    hour = hour % 24;
                }
                minutes = minutes % 60;
            }

        }
        route[station] = new Station(mapData.getStations().getStations().get(ride[station]), hour, minutes, day);
        return route;
    }

    private void checkTime(Integer minuteStart, int hourStart, Integer dayStart, String owner) throws InvalidRideStartMinutes, InvalidRideStartDay, InvalidRideStartHour {
        if (hourStart > 23 || hourStart < 0)
            throw new InvalidRideStartHour(owner);
        if (dayStart < 1)
            throw new InvalidRideStartDay(owner);
        if (minuteStart == null)
            minuteStart = 0;
        if (minuteStart > 59 || minuteStart < 0)
            throw new InvalidRideStartMinutes(owner);
    }

    private void checkRideRequest(String from, String to) throws InvalidRequestDepartureDestination, MissingFromToValue {
        String checkF = from, checkT = to;
        if (checkF == null || checkT == null)
            throw new MissingFromToValue();
        if (!mapData.stations.getStations().containsKey(from))
            throw new InvalidRequestDepartureDestination(from);

        if (!mapData.stations.getStations().containsKey(to))
            throw new InvalidRequestDepartureDestination(to);
    }

    private String[] deleteSpacesFromString(String[] route) {
        int i;
        for (i = 0; i < route.length; i++) {
            route[i] = route[i].trim();
        }
        return route;
    }

    //add------------------------

    public void addRequest(String name, String from, String to, Integer day, Integer hour, Integer minutes, boolean byDeparture) throws InvalidRideStartMinutes, InvalidRideStartHour, InvalidRideStartDay, InvalidRequestDepartureDestination, MissingFromToValue, MissingNameField, MissingTimeValue {
        checkRideRequest(from, to);
        serialNumberCounter++;
        requests.put(serialNumberCounter, new Request(name, from, to, day, hour, minutes, serialNumberCounter, byDeparture));
    }

    public void UpdateRequest(int serialNumber, int price, int fuel, Trip trip) {
        Request temp = requests.get(serialNumber);
        temp.Update(price, fuel, trip);
    }

    public void addTrip(String name, Integer hour, Integer minutes, String route, int ppk, int capacity, int startDay, String recurrences) throws InvalidStartDayValue, invalidCapacityValue, MissingNameField, InvalidPpkValue, RouteIsEmpty, MissingTimeValue, NameExsitInSystem {
        checkTripFromUI(name, ppk, capacity, startDay, route);
        if (hour == null || minutes == null)
            throw new MissingTimeValue();
        String[] way = route.split(",");
        Station[] ride = makeStationArray(way, hour, minutes, startDay);
        int price = priceCalculator(ride, ppk);
        int fuel = fuelConsumptionCalculator(ride);
        int serialNumber = ++Transpool.serialNumberCounter;
        plannedTrips.getTrips().put(serialNumber, new Trip(name, serialNumber, ride, price, fuel, ppk, way, minutes, hour, capacity, startDay, recurrences));
    }

    private void checkTripFromUI(String name, int ppk, int capacity, int startDay, String route) throws MissingNameField, InvalidPpkValue, invalidCapacityValue, InvalidStartDayValue, RouteIsEmpty, NameExsitInSystem {
        if (!route.contains(",") || route.equals(""))
            throw new RouteIsEmpty();
        if (name.equals(""))
            throw new MissingNameField();
        if (ppk < 1)
            throw new InvalidPpkValue();
        if (capacity < 1)
            throw new invalidCapacityValue();
        if (startDay < 1)
            throw new InvalidStartDayValue();
        //if(isNameExist(plannedTrips.getTrips(),name))
          //  throw new NameExsitInSystem(name);
    }

    private boolean isNameExist(Map<Integer,Trip> trips,String name) {
        for(Map.Entry <Integer,Trip> entry : trips.entrySet()){
            if(entry.getValue().getOwner().equals(name))
                return true;
        }
        return false;
    }

    private Station[] makeStationArray(String[] way, int hour, int minutes, int day) {
        Station[] ride = new Station[way.length];
        int minutesToAdd = minutes;
        int newHour = hour;
        int newDay = day;
        for (int i = 0; i < way.length - 1; i++) {
            ride[i] = new Station(mapData.getStations().getStations().get(way[i]), newHour, minutesToAdd, newDay);
            if (mapData.getTrails().getTrails().containsKey(way[i] + way[i + 1]))
                minutesToAdd = minutesToAdd + mapData.getTrails().getTrails().get(way[i] + way[i + 1]).getHowMuchTime();
            else
                minutesToAdd = minutesToAdd + mapData.getTrails().getTrails().get(way[i + 1] + way[i]).getHowMuchTime();

            newDay = (newDay + (newHour + (minutesToAdd / 60)) / 24);
            newHour = (newHour + (minutesToAdd / 60)) % 24;
            minutesToAdd = minutesToAdd % 60;

        }
        ride[way.length - 1] = new Station(mapData.getStations().getStations().get(way[way.length - 1]), newHour, minutesToAdd, newDay);
        return ride;
    }

    private int fuelConsumptionCalculator(Station[] route) {
        int i, length = 0;
        double fuel = 0;
        for (i = 0; i < route.length - 1; i++) {
            if (mapData.getTrails().getTrails().containsKey(route[i].getName() + route[i + 1].getName())) {
                fuel += mapData.getTrails().getTrails().get(route[i].getName() + route[i + 1].getName()).getFuelUse();
                length = length + mapData.getTrails().getTrails().get(route[i].getName() + route[i + 1].getName()).getLength();
            } else {
                fuel += mapData.getTrails().getTrails().get(route[i + 1].getName() + route[i].getName()).getFuelUse();
                length = length + mapData.getTrails().getTrails().get(route[i + 1].getName() + route[i].getName()).getLength();
            }
        }
        if (fuel == 0)
            return 0;
        return (int) (length / fuel);
    }

    public int priceCalculator(Station[] route, int ppk) {
        int i, price = 0;
        for (i = 0; i < route.length - 1; i++) {
            if (mapData.getTrails().getTrails().containsKey(route[i].getName() + route[i + 1].getName()))
                price = price + mapData.getTrails().getTrails().get(route[i].getName() + route[i + 1].getName()).getLength() * ppk;
            else
                price = price + mapData.getTrails().getTrails().get(route[i + 1].getName() + route[i].getName()).getLength() * ppk;
        }

        return price;
    }


    //getters-----------------------

    public MapData getMapData() {
        return mapData;
    }

    public Plannedtrips getPlannedTrips() {
        return plannedTrips;
    }

    public Map<Integer, Request> getRequests() {
        return requests;
    }

    public static int getSerialNumberCounter() {
        return serialNumberCounter;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void makeAMatch(Request request, int numberOfTripsToOffer, boolean selected) {

        Matcher matcher = new Matcher(this);
        allOptions =new LinkedList<>();
        LinkedList<LinkedList<MatchedRide>> matchedRides = matcher.makeAMatch(request, numberOfTripsToOffer, selected);
        for (LinkedList<MatchedRide> listMr : matchedRides) {
            int fuel=0,price=0;
            for (MatchedRide mr : listMr) {
                fuel += fuelConsumptionCalculator(mr.getRoute());
                price += priceCalculator(mr.getRoute(),mr.getTrip().getPpk());
            }
            CombinedTrip combinedTrip=(new CombinedTrip(listMr,fuel,price));
            if(!allOptions.contains(combinedTrip))
                allOptions.add(combinedTrip);
        }
    }

    private int fuelConsumptionCalculator(LinkedList<Station> route) {
        int i, length = 0;
        double fuel = 0;
        for (i = 0; i < route.size() - 1; i++) {
            if (mapData.getTrails().getTrails().containsKey(route.get(i).getName() + route.get(i + 1).getName())) {
                fuel += mapData.getTrails().getTrails().get(route.get(i).getName() + route.get(i + 1).getName()).getFuelUse();
                length = length + mapData.getTrails().getTrails().get((route.get(i).getName() + route.get(i + 1).getName())).getLength();
            } else {
                fuel += mapData.getTrails().getTrails().get(route.get(i + 1).getName() + route.get(i).getName()).getFuelUse();
                length = length + mapData.getTrails().getTrails().get(route.get(i + 1).getName() + route.get(i).getName()).getLength();
            }
        }
        if (fuel == 0)
            return 0;
        return (int) (length / fuel);
    }

    private int priceCalculator(LinkedList<Station> route, int ppk) {
        int i, price = 0;
        for (i = 0; i < route.size() - 1; i++) {
            if (mapData.getTrails().getTrails().containsKey(route.get(i).getName() + route.get(i + 1).getName()))
                price = price + mapData.getTrails().getTrails().get(route.get(i).getName() + route.get(i + 1).getName()).getLength() * ppk;
            else
                price = price + mapData.getTrails().getTrails().get(route.get(i + 1).getName() + route.get(i).getName()).getLength() * ppk;
        }

        return price;
    }

    public LinkedList<CombinedTrip> getAllOptions() {
        return allOptions;
    }

    public boolean setMatchedRide(int serialNumber , CombinedTrip matchedRides)  {

        for(MatchedRide mr: matchedRides.getTrip()){
            for(Station station:mr.getRoute()) {
                if (plannedTrips.getTrips().get(mr.getTrip().getSerialNumber()).getCapacityPerTime().containsKey("" + station.getDay() + station.getHour() + station.getMinutes()) && plannedTrips.getTrips().get(mr.getTrip().getSerialNumber()).getCapacityPerTime().get("" + station.getDay() + station.getHour() + station.getMinutes()) == 0)
                    return false;
            }
            mr.getTrip().updateTripCapacityPerTime(mr.getRoute());
        }
        requests.get(serialNumber).setMatchedRide(matchedRides);
        return true;
    }

    public void emptyAllOptions() {
        allOptions=new LinkedList<>();
    }
}

