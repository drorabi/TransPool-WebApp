package engine.exceptions;

public class NoMatchingRides   extends Exception {
    final String EXCEPTION_MESSAGE="There are no matching rides in the system";
    public NoMatchingRides(){}
    @Override
    public String getMessage() {return EXCEPTION_MESSAGE;}
}