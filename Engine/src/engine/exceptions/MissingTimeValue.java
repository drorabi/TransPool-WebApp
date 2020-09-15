package engine.exceptions;

public class MissingTimeValue extends Exception {
    final String EXCEPTION_MESSAGE="Must give time value for hour and minutes";
    @Override
    public String getMessage() {return EXCEPTION_MESSAGE;}
}
