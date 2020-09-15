package engine.converted.classes;

import java.util.LinkedList;

public class CombinedTrip {
    private int fuel;
    private int price;
    private LinkedList<MatchedRide> trip=new LinkedList<>();

    CombinedTrip(LinkedList<MatchedRide> combinedTrip, int fuel, int price) {
        this.fuel=fuel;
        this.price=price;
        this.trip=combinedTrip;
    }

    public LinkedList<MatchedRide> getTrip() {
        return trip;
    }

    @Override
    public String toString() {

        String toString="";
        for(MatchedRide mr : trip){
            toString += "catching " + mr.getTrip().getOwner() + "'s (" + mr.getTrip().getSerialNumber()+ ") ride through the next stations:\n";
            for(Station st : mr.getRoute()){
                toString += (st.toString() +"\n");
            }
        }
        toString +=( "price:" + price + "\n" + "fuel consumption:" + fuel + "\n" );
        return toString;
    }

    public int getPrice() {
        return price;
    }

    public int getFuel() {
        return fuel;
    }

    @Override
    public boolean equals(Object o) {
        CombinedTrip other = (CombinedTrip)o;
        if(other.getTrip().size()==1 && this.getTrip().size()==1){
            if(other.getTrip().getFirst().getTrip().getSerialNumber() == this.getTrip().getFirst().getTrip().getSerialNumber())
                return true;
        }
        return false;
    }
}
