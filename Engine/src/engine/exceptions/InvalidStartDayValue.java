package engine.exceptions;

public class InvalidStartDayValue extends Exception {
    final String EXCEPTION_MESSAGE="day";
    @Override
    public String getMessage() {return EXCEPTION_MESSAGE;}
}
