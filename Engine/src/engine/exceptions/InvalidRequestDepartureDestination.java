package engine.exceptions;

public class InvalidRequestDepartureDestination extends Exception {
    String station;
    private final String EXCEPTION_MESSAGE;
    public InvalidRequestDepartureDestination(String station){
        this.station =station;
        EXCEPTION_MESSAGE ="ERROR: request can't define departure and destination as the same station";
    }
    @Override
    public String getMessage() {return EXCEPTION_MESSAGE;}
}