package engine.converted.classes;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;


public class Matcher {

    public Matcher(Transpool system) {
        data = system;
    }

    private Transpool data;

    public LinkedList<LinkedList<MatchedRide>> makeAMatch(Request request, int numberOfTripsToOffer, boolean selected) {  //By departure or arrival
        if (request.isByDeparture())
            return collectMatchingTripsByDepartureTime(request, numberOfTripsToOffer, selected);
        else
            return collectMatchingTripsByArrivalTime(request, numberOfTripsToOffer, selected);
    }

    private LinkedList<LinkedList<MatchedRide>> collectMatchingTripsByDepartureTime(Request request, int numberOfTripsToOffer, boolean selected) {

        int index;
        LinkedList<MatchedRide> combinedTrip = new LinkedList<>();
        LinkedList<LinkedList<MatchedRide>> res = new LinkedList<>();


        findSingleDriverTripsByDeparture(res,request,numberOfTripsToOffer);
        numberOfTripsToOffer-=res.size();

        if(numberOfTripsToOffer==0 || selected)
            return res;

        for (Map.Entry<Integer, Trip> entry : data.getPlannedTrips().getTrips().entrySet()) {
            for (index = 0; index < entry.getValue().getRide().length - 1; index++) {
                if (entry.getValue().getRide()[index].getName().equals(request.from) &&
                        isTripTimeEqual(request.getScheduling().startDay, request.getScheduling().startHour, request.getScheduling().startMinutes, entry.getValue().getRide()[index], entry.getValue())) {
                    combinedTrip.add(new MatchedRide(entry.getValue(), entry.getValue().getRide()[index], entry.getValue().getRide()[index + 1], findClosestDayFromAbove(entry.getValue(), entry.getValue().getSchedule().recurrencesToInteger(), entry.getValue().getRide()[index], request.getScheduling().getStartDay(), request.getScheduling().getStartHour(), request.getScheduling().getStartMinutes())));
                    buildMatchingTripsByDeparture(combinedTrip, request, res, numberOfTripsToOffer);
                    break;
                }
            }
        }
        return res;
    }

    private void buildMatchingTripsByDeparture(LinkedList<MatchedRide> ride, Request request, LinkedList<LinkedList<MatchedRide>> res, int numberOfTripsToOffer) {

        if (res.size() == numberOfTripsToOffer)
            return;

        Station current = ride.getLast().getRoute().getLast();
        if (request.getTo().equals(current.getName())) {
            res.add(copyLinkedList(ride));
        } else {
            LinkedList<MatchedRide> matchingRides = findTripsForNextStations(ride);
            for (MatchedRide matchedRide : matchingRides) {
                LinkedList<MatchedRide> newRide = copyLinkedList(ride);
                if (matchedRide.getTrip().getSerialNumber() == ride.getLast().getTrip().getSerialNumber()) {
                    newRide.getLast().setEndStationInRoute(matchedRide.getRoute().getLast(), findClosestDayFromAbove(matchedRide.getTrip(), matchedRide.getTrip().getSchedule().recurrencesToInteger(), matchedRide.getRoute().getFirst(), newRide.getLast().getRoute().getLast().getDay(), newRide.getLast().getRoute().getLast().getHour(), newRide.getLast().getRoute().getLast().getMinutes()));
                    buildMatchingTripsByDeparture(copyLinkedList(newRide), request, res, numberOfTripsToOffer);
                    newRide.getLast().getRoute().removeLast();
                } else {
                    newRide.add(new MatchedRide(matchedRide, findClosestDayFromAbove(matchedRide.getTrip(), matchedRide.getTrip().getSchedule().recurrencesToInteger(), matchedRide.getRoute().getFirst(), newRide.getLast().getRoute().getLast().getDay(), newRide.getLast().getRoute().getLast().getHour(), newRide.getLast().getRoute().getLast().getMinutes())));
                    buildMatchingTripsByDeparture(copyLinkedList(newRide), request, res, numberOfTripsToOffer);
                    newRide.removeLast();
                }
            }
        }
    }

    private LinkedList<MatchedRide> copyLinkedList(LinkedList<MatchedRide> ride) {
        LinkedList newList = new LinkedList<MatchedRide>();
        for (MatchedRide matchedRide : ride) {
            newList.add(new MatchedRide(matchedRide));
        }
        return newList;
    }


    public LinkedList<MatchedRide> findTripsForNextStations(LinkedList<MatchedRide> ride) {

        LinkedList<MatchedRide> matchingRides = new LinkedList<>();
        Station currentStation = ride.getLast().getRoute().getLast();
        int index;
        for (Map.Entry<Integer, Trip> entry : data.getPlannedTrips().getTrips().entrySet()) {
            for (index = 0; index < entry.getValue().getRide().length - 1; index++) {
                if (entry.getValue().getRide()[index].getName().equals(currentStation.getName()) &&
                        isTripTimeBigger(currentStation, entry.getValue().getRide()[index], entry.getValue()) &&
                        isTheStationExist(ride, entry.getValue().getRide()[index + 1]))
                    matchingRides.add(new MatchedRide(entry.getValue(), entry.getValue().getRide()[index], entry.getValue().getRide()[index + 1], findClosestDayFromAbove(entry.getValue(), entry.getValue().getSchedule().recurrencesToInteger(), entry.getValue().getRide()[index], ride.getLast().getRoute().getLast().getDay(), ride.getLast().getRoute().getLast().getHour(), ride.getLast().getRoute().getLast().getMinutes())));
            }
        }
        return matchingRides;
    }

    private boolean isTheStationExist(LinkedList<MatchedRide> ride, Station station) {
        for (MatchedRide mr : ride) {
            for (Station st : mr.getRoute()) {
                if (st.getName().equals(station.getName()))
                    return false;
            }
        }
        return true;
    }

    private boolean isTripTimeEqual(int day, int hour, int minutes, Station tripStation, Trip trip) {
        int recurrences = trip.getSchedule().recurrencesToInteger();
        if (tripStation.getDay() <= day) {
            if ((tripStation.getDay() * 24 * 60) + (tripStation.getHour() * 60) + (tripStation.getMinutes()) < ((day * 60 * 24) + (hour * 60) + (minutes)) && trip.getSchedule().getRecurrences().equals("One Time"))
                return false;
            if ((tripStation.getDay() % recurrences) == day % recurrences && tripStation.getHour() == hour && tripStation.getMinutes() == minutes && (!trip.getCapacityPerTime().containsKey("" + day + hour + minutes) || (trip.getCapacityPerTime().containsKey("" + day + hour + minutes) && trip.getCapacityPerTime().get("" + day + hour + minutes) > 0)))
                return true;
        }
        return false;
    }

    private boolean isTripTimeBigger(Station requestStation, Station tripStation, Trip trip) {
        if (requestStation.getDay() > tripStation.getDay() && trip.getSchedule().getRecurrences().equals("One Time"))
            return false;
        else if (trip.getSchedule().getRecurrences().equals("One Time") && (trip.getCapacityPerTime().containsKey("" + trip.getSchedule().getStartDay() + trip.getSchedule().getStartHour() + trip.getSchedule().getStartMinutes()) && trip.getCapacityPerTime().get("" + trip.getSchedule().getStartDay() + trip.getSchedule().getStartHour() + trip.getSchedule().getStartMinutes()) == 0))
            return false;
        else
            return true;
    }


    private void findSingleDriverTripsByDeparture(LinkedList<LinkedList<MatchedRide>> res,Request request,  int numberOfOffers){

        int index,secondIndex;

        for (Map.Entry<Integer, Trip> entry : data.getPlannedTrips().getTrips().entrySet()) {
            if(res.size()==numberOfOffers)
                return;
            for (index = 0; index < entry.getValue().getRide().length - 1; index++) {

                if (entry.getValue().getRide()[index].getName().equals(request.from) &&
                        isTripTimeEqual(request.getScheduling().startDay, request.getScheduling().startHour, request.getScheduling().startMinutes, entry.getValue().getRide()[index], entry.getValue())) {
                    MatchedRide singleDriverTrip =new MatchedRide(entry.getValue(), entry.getValue().getRide()[index], entry.getValue().getRide()[index + 1], findClosestDayFromAbove(entry.getValue(), entry.getValue().getSchedule().recurrencesToInteger(), entry.getValue().getRide()[index], request.getScheduling().getStartDay(), request.getScheduling().getStartHour(), request.getScheduling().getStartMinutes()));

                    if (entry.getValue().getRide()[index+1].getName().equals(request.getTo())) {
                        LinkedList<MatchedRide> singleTrip = new LinkedList<>();
                        singleTrip.add(singleDriverTrip);
                        res.add(copyLinkedList(singleTrip));
                        break;
                    }

                    for(secondIndex=index+2 ; secondIndex<entry.getValue().getRide().length ; secondIndex++) {
                        if (entry.getValue().getRide()[secondIndex].getName().equals(request.getTo())) {
                            singleDriverTrip.setEndStationInRoute(entry.getValue().getRide()[secondIndex], findClosestDayFromAbove(entry.getValue(), entry.getValue().getSchedule().recurrencesToInteger(), entry.getValue().getRide()[secondIndex], singleDriverTrip.getRoute().getLast().getDay(), singleDriverTrip.getRoute().getLast().getHour(), singleDriverTrip.getRoute().getLast().getMinutes()));
                            LinkedList<MatchedRide> singleTrip = new LinkedList<>();
                            singleTrip.add(singleDriverTrip);
                            res.add(copyLinkedList(singleTrip));
                            break;
                        }
                        singleDriverTrip.setEndStationInRoute(entry.getValue().getRide()[secondIndex], findClosestDayFromAbove(entry.getValue(), entry.getValue().getSchedule().recurrencesToInteger(), entry.getValue().getRide()[secondIndex], singleDriverTrip.getRoute().getLast().getDay(), singleDriverTrip.getRoute().getLast().getHour(), singleDriverTrip.getRoute().getLast().getMinutes()));
                    }
                    break;
                }
            }
        }
    }
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~arrival~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

    private LinkedList<LinkedList<MatchedRide>> collectMatchingTripsByArrivalTime(Request request, int numberOfTripsToOffer, boolean selected) {

        LinkedList<MatchedRide> combinedTrip = new LinkedList<>();
        LinkedList<LinkedList<MatchedRide>> res = new LinkedList<>(); // save all the optional result of combinedTrips

        findSingleDriverTripsByArrival(res,request,numberOfTripsToOffer);
        numberOfTripsToOffer-=res.size();

        if(numberOfTripsToOffer==0 || selected)
            return res;

        for (Map.Entry<Integer, Trip> entry : data.getPlannedTrips().getTrips().entrySet()) {
            for (int index = entry.getValue().getRide().length - 1; index > 0; index--) {
                if (isTripTimeEqual(request.getScheduling().endDay, request.getScheduling().endHour, request.getScheduling().endMinutes, entry.getValue().getRide()[index], entry.getValue())) {
                    combinedTrip.add(new MatchedRide(entry.getValue(), entry.getValue().getRide()[index - 1], entry.getValue().getRide()[index], findClosestDayFromBelow(entry.getValue(), entry.getValue().getSchedule().recurrencesToInteger(), entry.getValue().getRide()[index], request.getScheduling().getEndDay(), request.getScheduling().getEndHour(), request.getScheduling().getEndMinutes())));
                    buildMatchingTripsByArrival(combinedTrip, request, res, numberOfTripsToOffer); //send them to a rec function to find all optional trips
                    break;
                }
            }
        }
        return res;
    }

    private void buildMatchingTripsByArrival(LinkedList<MatchedRide> ride, Request request, LinkedList<LinkedList<MatchedRide>> res, int numberOfTripsToOffer) {

        if (res.size() == numberOfTripsToOffer)
            return;

        Station current = ride.getFirst().getRoute().getFirst();
        if (request.getFrom().equals(current.getName())) { // if we in the destination
            res.add(ride);
        } else {
            LinkedList<MatchedRide> matchingRides = findTripsForPrevStations(ride); //gating list of optional Plaid drive
            for (MatchedRide matchedRide : matchingRides) {
                LinkedList<MatchedRide> newRide = copyLinkedList(ride);
                if (matchedRide.getTrip().getSerialNumber() == ride.getFirst().getTrip().getSerialNumber()) {// if its the same drive just chang the end station
                    newRide.getFirst().setStartStationInRoute(matchedRide.getRoute().getFirst(), findClosestDayFromBelow(matchedRide.getTrip(), matchedRide.getTrip().getSchedule().recurrencesToInteger(), matchedRide.getRoute().getLast(), newRide.getLast().getRoute().getFirst().getDay(), newRide.getLast().getRoute().getFirst().getHour(), newRide.getLast().getRoute().getFirst().getMinutes()));
                    buildMatchingTripsByArrival(newRide, request, res, numberOfTripsToOffer);
                } else {
                    newRide.addFirst(new MatchedRide(matchedRide, findClosestDayFromBelow(matchedRide.getTrip(), matchedRide.getTrip().getSchedule().recurrencesToInteger(), matchedRide.getRoute().getLast(), newRide.getFirst().getRoute().getFirst().getDay(), newRide.getFirst().getRoute().getFirst().getHour(), newRide.getFirst().getRoute().getFirst().getMinutes())));
                    buildMatchingTripsByArrival(copyLinkedList(newRide), request, res, numberOfTripsToOffer);
                    newRide.removeFirst();
                }
            }
        }
    }

    private LinkedList<MatchedRide> findTripsForPrevStations(LinkedList<MatchedRide> ride) {

        LinkedList<MatchedRide> matchingRides = new LinkedList<>();
        Station current = ride.getFirst().getRoute().getFirst();
        for (Map.Entry<Integer, Trip> entry : data.getPlannedTrips().getTrips().entrySet()) {
            for (int index = entry.getValue().getRide().length - 1; index > 0; index--) {  // 0<i because if it is the lest station it cant continue withe driver
                if (entry.getValue().getRide()[index].getName().equals(current.getName()) &&
                        isTripTimeSmaller(entry.getValue(), current, entry.getValue().getRide()[index]) &&
                        isTheStationExist(ride, entry.getValue().getRide()[index - 1]))
                    matchingRides.add(new MatchedRide(entry.getValue(), entry.getValue().getRide()[index - 1], entry.getValue().getRide()[index], findClosestDayFromBelow(entry.getValue(), entry.getValue().getSchedule().recurrencesToInteger(), entry.getValue().getRide()[index], ride.getFirst().getRoute().getFirst().getDay(), ride.getFirst().getRoute().getFirst().getHour(), ride.getFirst().getRoute().getFirst().getMinutes())));
            }

        }
        return matchingRides;
    }

    private boolean isTripTimeSmaller(Trip trip, Station current, Station station) {
        int newDay = station.getDay();
        while (((current.getDay() * 24 * 60) + (current.getHour() * 60) + (current.getMinutes())) >= ((newDay * 24 * 60) + (station.getHour() * 60) + (station.getMinutes()))) {
            if ((!trip.getCapacityPerTime().containsKey("" + newDay + station.getHour() + station.getMinutes())) ||
                    (trip.getCapacityPerTime().containsKey("" + newDay + station.getHour() + station.getMinutes()) && trip.getCapacityPerTime().get("" + newDay + station.getHour() + station.getMinutes()) > 0))
                return true;
            else if (trip.getSchedule().getRecurrences().equals("One Time"))
                return false;
            else
                newDay += trip.getSchedule().recurrencesToInteger();
        }
        return false;
    }

    private int findClosestDayFromAbove(Trip trip, int recurrences, Station station, int day, int hour, int minutes) {
        int newDay = station.getDay();
        int time = (newDay * 24 * 60) + (station.getHour() * 60) + (station.getMinutes());
        while (time < ((day * 24 * 60) + (hour * 60) + (minutes))) {
            newDay += recurrences;
            time = newDay * 24 * 60 + station.getHour() * 60 + station.getMinutes();
        }
        while (trip.getCapacityPerTime().containsKey("" + newDay + station.getHour() + station.getMinutes()) && trip.getCapacityPerTime().get("" + newDay + station.getHour() + station.getMinutes()) == 0)
            newDay += recurrences;

        return newDay - station.getDay();
    }

    private int findClosestDayFromBelow(Trip trip, int recurrences, Station station, int day, int hour, int minutes) {
        int newDay = station.getDay();
        int time = (newDay * 24 * 60) + (station.getHour() * 60) + (station.getMinutes());

        if (trip.getSchedule().getRecurrences().equals("One Time"))
            return 0;

        while (time <= ((day * 24 * 60) + (hour * 60) + (minutes))) {
            newDay += recurrences;
            time = newDay * 24 * 60 + station.getHour() * 60 + station.getMinutes();
        }
        while (trip.getCapacityPerTime().containsKey("" + newDay + station.getHour() + station.getMinutes()) && trip.getCapacityPerTime().get("" + newDay + station.getHour() + station.getMinutes()) == 0)
            newDay -= recurrences;

        if (time > station.getDay() * 24 * 60 + station.getHour() * 60 + station.getMinutes())
            return (newDay - recurrences) - station.getDay();
        else
            return 0;
    }

    private void findSingleDriverTripsByArrival(LinkedList<LinkedList<MatchedRide>> res,Request request,  int numberOfOffers){

        int index,j;

        for (Map.Entry<Integer, Trip> entry : data.getPlannedTrips().getTrips().entrySet()) {
            if(res.size()==numberOfOffers)
                return;

            for (index = entry.getValue().getRide().length - 1; index > 0; index--) {
                if (isTripTimeEqual(request.getScheduling().endDay, request.getScheduling().endHour, request.getScheduling().endMinutes, entry.getValue().getRide()[index], entry.getValue())) {
                    MatchedRide singleDriverTrip = new MatchedRide(entry.getValue(), entry.getValue().getRide()[index - 1], entry.getValue().getRide()[index], findClosestDayFromBelow(entry.getValue(), entry.getValue().getSchedule().recurrencesToInteger(), entry.getValue().getRide()[index], request.getScheduling().getEndDay(), request.getScheduling().getEndHour(), request.getScheduling().getEndMinutes()));

                    if (entry.getValue().getRide()[index-1].getName().equals(request.getFrom())) {
                        LinkedList<MatchedRide> singleTrip = new LinkedList<>();
                        singleTrip.add(singleDriverTrip);
                        res.add(copyLinkedList(singleTrip));
                        break;
                    }

                    for(j=index-2 ; j >= 0 ; j --) {
                        if (entry.getValue().getRide()[j].getName().equals(request.getFrom())) {
                            singleDriverTrip.setStartStationInRoute(entry.getValue().getRide()[j], findClosestDayFromBelow(entry.getValue(), entry.getValue().getSchedule().recurrencesToInteger(), entry.getValue().getRide()[j], singleDriverTrip.getRoute().getFirst().getDay(), singleDriverTrip.getRoute().getFirst().getHour(), singleDriverTrip.getRoute().getFirst().getMinutes()));
                            LinkedList<MatchedRide> singleTrip = new LinkedList<>();
                            singleTrip.add(singleDriverTrip);
                            res.add(copyLinkedList(singleTrip));
                            break;
                        }
                        singleDriverTrip.setStartStationInRoute(entry.getValue().getRide()[j], findClosestDayFromBelow(entry.getValue(), entry.getValue().getSchedule().recurrencesToInteger(), entry.getValue().getRide()[j], singleDriverTrip.getRoute().getFirst().getDay(), singleDriverTrip.getRoute().getFirst().getHour(), singleDriverTrip.getRoute().getFirst().getMinutes()));
                    }
                    break;
                }
            }
        }
    }
}