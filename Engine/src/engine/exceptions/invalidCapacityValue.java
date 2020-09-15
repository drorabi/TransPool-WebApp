package engine.exceptions;

public class invalidCapacityValue extends Exception{
    final String EXCEPTION_MESSAGE="Capacity must be a positive number";
    @Override
    public String getMessage() {return EXCEPTION_MESSAGE;}
}
