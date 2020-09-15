package engine.exceptions;

public class InvalidPpkValue extends Exception {
    final String EXCEPTION_MESSAGE="PPk must be a positive number";
    @Override
    public String getMessage() {return EXCEPTION_MESSAGE;}
}
