package engine.converted.classes;

import engine.exceptions.*;

import java.util.LinkedList;

public class Request {
    protected String name;
    protected String from;
    protected String to;
    protected Schedule schedule;
    protected int serialNumber;
    protected int price;
    protected int fuelConsumption;
    protected boolean matched=false;
    protected Trip matchedTrip;
    protected boolean byDeparture;
    private CombinedTrip matchedRide;

    Request(String name, String from, String to,Integer day, Integer hour, Integer minutes, int serialNumber,boolean byDeparture) throws InvalidRideStartDay, InvalidRideStartHour, InvalidRideStartMinutes, MissingNameField, MissingTimeValue {
        setName(name);
        this.from = from;
        this.to = to;
        this.byDeparture=byDeparture;
        setSchedule(day, hour,"One Time", minutes);
        this.serialNumber = serialNumber;
        price = 0;
        this.fuelConsumption = 0;
        matchedTrip =null;
    }

    private void setName(String name) throws MissingNameField {
        if(name.equals(""))
            throw new MissingNameField();
        else
            this.name=name;
    }


    //setter---------------------

private void setSchedule(Integer day, Integer hour, String recurrences, Integer minutes) throws MissingTimeValue {
        Integer checkM=minutes,checkH=hour;
        if(checkH==null||checkM==null)
            throw new MissingTimeValue();
        if(day<1)
            throw new NumberFormatException();
        if(byDeparture)
            this.schedule=new Schedule(-1, day, hour, recurrences, minutes, -1, -1, true);
        else
            this.schedule=new Schedule(day, -1,  -1, recurrences,  -1, hour, minutes, false);

}
    //getters---------------


    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getName() {
        return name;
    }

    public Schedule getScheduling() {
        return schedule;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public int getFuelConsumption() {
        return fuelConsumption;
    }

    public int getPrice() {
        return price;
    }

    public boolean isMatched() {
        return matched;
    }

    public boolean isByDeparture() {
        return byDeparture;
    }

    public void Update(int price, int fuel, Trip trip) {
        Station temp;
        this.price = price;
        this.fuelConsumption = fuel;
        this.matchedTrip=trip;
        if(byDeparture) {
            temp = findDestination();
            schedule.setEndHour(temp.getHour());
            schedule.setEndMinutes(temp.getMinutes());
        }
        else{
            temp = findDeparture();
            schedule.setStartHour(temp.getHour());
            schedule.setStartMinutes(temp.getMinutes());
        }
        matched = true;
    }




    @Override
    public String toString() {
        String toString = matched + "Request number " + serialNumber + ":\nName:" + name
                + "\n" + schedule.toString() + "\nfrom "
                + from + " to " + to + "\n";
        if (matched)
            toString = toString + " " + matchedRide.toString();

        return toString;
    }

    private Station findDestination(){
        int i;
        for(i=0; i<matchedTrip.getRide().length;i++){
            if(matchedTrip.getRide()[i].getName().equals(to))
                return matchedTrip.getRide()[i];
        }
        return null;
    }

    private Station findDeparture() {
        int i;
        for(i=0; i<matchedTrip.getRide().length;i++){
            if(matchedTrip.getRide()[i].getName().equals(from))
                return matchedTrip.getRide()[i];
        }
        return null;
    }

    public void setMatchedRide(CombinedTrip matchedRide) {
        this.matchedRide =matchedRide ;
        this.price=matchedRide.getPrice();
        this.fuelConsumption=matchedRide.getFuel();
        matched=true;

    }

    public CombinedTrip getMatchedRide() {
        return matchedRide;
    }
}
