package engine.converted.classes;

import engine.exceptions.InvalidRideStartDay;
import engine.exceptions.InvalidRideStartHour;
import engine.exceptions.InvalidRideStartMinutes;
import engine.schema.generated.Route;
import engine.schema.generated.Scheduling;
import engine.schema.generated.TransPoolTrip;

import java.util.*;

public class Trip {
    protected int SerialNumber;
    protected String owner;
    protected int capacity;
    protected int ppk;
    protected String[] route;
    protected Schedule schedule;
    protected Map<String, String> rideInformation;
    protected Station[] ride;
    protected Set<Request> poolers;
    int price;
    int fuelConsumption;
    protected String feedbacks="";
    int starsRank=0;
    int numOfRanks=0;
    protected Map<String, Integer> capacityPerTime=new HashMap<>(); // String format is DayHourMinutes


    Trip(TransPoolTrip transPoolTrip, int serialNumber, Station[] ride, int price, int fuelConsumption) throws InvalidRideStartDay, InvalidRideStartHour, InvalidRideStartMinutes {
        setRoute(transPoolTrip.getRoute());
        this.ride = ride;
        owner = transPoolTrip.getOwner();
        ppk = transPoolTrip.getPPK();
        capacity = transPoolTrip.getCapacity();
        SerialNumber = serialNumber;
        setSchedule(transPoolTrip.getScheduling(), transPoolTrip.getOwner());
        rideInformation = new HashMap<>();
        poolers = new HashSet<>();
        this.price = price;
        this.fuelConsumption=fuelConsumption;
    }

    public int getPrice() {
        return price;
    }

    public Trip(Trip trip, Schedule schedule) {
        this.ppk = trip.ppk;
        this.owner = trip.owner;
        this.ride = trip.ride;
        this.capacity = trip.capacity;
        this.route = trip.route;
        this.schedule = schedule;
        this.SerialNumber = trip.SerialNumber;
        this.price=trip.price;
        this.fuelConsumption=trip.fuelConsumption;
        rideInformation = new HashMap<>();
        this.poolers = trip.poolers;
    }

    public Trip(String name, int serialNumber, Station[] ride, int price, int fuel, int ppk, String[] way, int minutes, int hour, int capacity, int startDay, String recurrences) {
        this.owner=name;
        this.SerialNumber=serialNumber;
        this.ppk=ppk;
        this.ride=ride;
        this.route=way;
        schedule=new Schedule(ride[ride.length-1].getDay(), startDay, hour,recurrences, minutes,  ride[ride.length-1].getHour(), ride[ride.length-1].getMinutes(),true);
        rideInformation = new HashMap<>();
        poolers = new HashSet<>();
        this.price = price;
        this.fuelConsumption=fuel;
        this.capacity=capacity;
    }

    //setter---------------------

    private void setSchedule(Scheduling scheduling, String name) throws InvalidRideStartHour, InvalidRideStartDay, InvalidRideStartMinutes {
        int minutes;
        if(scheduling.getMinuteStart()==null)
            minutes=0;
        else
            minutes=scheduling.getMinuteStart();
        checkTime(minutes, scheduling.getHourStart(),scheduling.getDayStart(), name);
        schedule = new Schedule(scheduling, ride[ride.length-1].getDay(), ride[ride.length-1].getHour(), ride[ride.length-1].getMinutes());
    }

    public void checkTime(int minuteStart, int hourStart, Integer dayStart, String name) throws InvalidRideStartDay, InvalidRideStartHour, InvalidRideStartMinutes {
        if(hourStart>23 || hourStart<0)
            throw new InvalidRideStartHour(name);
        if(dayStart<1)
            throw new InvalidRideStartDay(owner);
        if(minuteStart>59 || minuteStart > 0)
            throw new InvalidRideStartMinutes(name);
    }

    private void setRoute(Route route) {
        this.route = route.getPath().trim().toUpperCase().split(",");
    }

    //getters---------------


    public Set<Request> getPoolers() {
        return poolers;
    }

    public Map<String, String> getRideInformation() {
        return rideInformation;
    }

    public String[] getRoute() {
        return route;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getPpk() {
        return ppk;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public String getOwner() {
        return owner;
    }

    public int getSerialNumber() {
        return SerialNumber;
    }

    public Station[] getRide() {
        return ride;
    }

    public void updateTripCapacityPerTime(LinkedList<Station> route){
        for(Station station : route) {
            String timeKey = station.timeFormatKeyToString();
            if (!station.getName().equals(route.getLast().getName())) {
                if (capacityPerTime.containsKey(timeKey))
                    capacityPerTime.put(timeKey, capacityPerTime.get(timeKey) - 1);
                else
                    capacityPerTime.put(timeKey, capacity - 1);
            }
        }
    }

    @Override
    public String toString() {
        return "\nRide number " + SerialNumber + ":\nOwner:" + owner + "\n"
                + schedule.toString() + "\n" + poolersToString() + "route:\n" + rideToString()
                + capacityToString() + "Price for the whole trip: " + price + "\n"
                + "Average Fuel Consumption: " + fuelConsumption+"\n"+ feedbackToString();
    }

    private String rideToString() {
        int station;
        String toString = "Stations: \n";
        for (station = 0; station < ride.length; station++) {
            toString = toString + ride[station].toString();
            if (rideInformation.containsKey(route[station] + "pick"))
                toString = toString + rideInformation.get(route[station] + "pick");
            if (rideInformation.containsKey(route[station] + "drop"))
                toString = toString + rideInformation.get(route[station] + "drop");
            toString = toString + "\n";
        }
        return toString;
    }

    public void updateTrip(Request request) {
        if (rideInformation.containsKey(request.getFrom() + "pick")) {
            String temp = rideInformation.get(request.getFrom() + "pick");
            rideInformation.remove(request.getFrom() + "pick");
            rideInformation.put(request.getFrom() + "pick", temp + ", " + request.getName());
        } else
            rideInformation.put(request.getFrom() + "pick", " picking up " + request.getName());

        if (rideInformation.containsKey(request.getTo() + "drop")) {
            String temp = rideInformation.get(request.getTo() + "drop");
            rideInformation.remove(request.getTo() + "drop");
            rideInformation.put(request.getTo() + "drop", temp + ", " + request.getName());
        } else
            rideInformation.put(request.getTo() + "drop", " dropping " + request.getName());

        this.poolers.add(request);
    }

    private String capacityToString() {
            return "There is room for " + capacity + " passengers\n";
    }

    private String poolersToString() {
        if (poolers.isEmpty())
            return "";
        String toString = "Poolers:\n";
        for (Request single_request : poolers)
            toString = toString + single_request.serialNumber + ", " + single_request.getName() + "\n";
        return toString;
    }

    public void updateFeedback(int stars, String feedback) {
        if(!feedback.equals(""))
            this.feedbacks+=("\n-"+feedback);
        this.starsRank= ((this.starsRank*numOfRanks)+stars)/++numOfRanks;
    }

    private String feedbackToString(){
        String toString="Number of reviews: " + numOfRanks+"\nAverage rank: " + starsRank + feedbacks;
        return toString;
    }

    public Map<String, Integer> getCapacityPerTime() {
        return capacityPerTime;
    }
}
