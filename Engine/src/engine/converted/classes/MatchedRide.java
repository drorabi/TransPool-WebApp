package engine.converted.classes;

import java.util.LinkedList;

public class MatchedRide {

   private Trip trip;
   LinkedList<Station> route;

    public MatchedRide(Trip trip, Station start, Station end,int day) {
        route = new LinkedList<>();
        route.add(new Station(start, start.getHour(), start.getMinutes(), end.getDay() + day));
        route.add(new Station(end, end.getHour(), end.getMinutes(), end.getDay() + day));
        this.trip=trip;
        //setTrip(trip);
    }

    public MatchedRide(MatchedRide matchedRide, int day) {
        this.trip=matchedRide.trip;
        setRoute(matchedRide.getRoute(), day);
    }

    public MatchedRide(MatchedRide matchedRide) {
        this.trip=matchedRide.getTrip();
        //setTrip(matchedRide.getTrip());
        setRoute(matchedRide.getRoute(),0);
    }

    private void setRoute(LinkedList<Station> route, int day) {
        this.route = new LinkedList<>();
        for (Station station : route)
            this.route.add(new Station(station, station.getHour(), station.getMinutes(), station.getDay()+day));
    }

    public void setTrip(Trip trip) {
        this.trip =new Trip(trip,trip.getSchedule());
    }

    public Trip getTrip() {
        return trip;
    }

    public void setEndStationInRoute(Station end, int day){
        route.add(new Station(end,end.getHour(),end.getMinutes(),end.getDay()+day));
    }

    public void setStartStationInRoute(Station start, int day){
        route.addFirst(new Station(start,start.getHour(),start.getMinutes(),start.getDay()+day));
    }

    public LinkedList<Station> getRoute() {
        return route;
    }

}
