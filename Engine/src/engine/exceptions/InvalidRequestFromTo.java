package engine.exceptions;

public class InvalidRequestFromTo extends Exception {

    private final String EXCEPTION_MESSAGE;
    public InvalidRequestFromTo(){

        EXCEPTION_MESSAGE ="ERROR: request can't have the same station as departure and destination";
    }
    @Override
    public String getMessage() {return EXCEPTION_MESSAGE;}
}