package engine.exceptions;

public class MissingPushedTouggleButton  extends Exception {
    final String EXCEPTION_MESSAGE="You must choose one of the time units";
    @Override
    public String getMessage() {return EXCEPTION_MESSAGE;}
}