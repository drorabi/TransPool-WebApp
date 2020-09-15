package engine.exceptions;

public class MissingFromToValue extends Exception {
    final String EXCEPTION_MESSAGE="Must give departure and destination stations";
    @Override
    public String getMessage() {return EXCEPTION_MESSAGE;}
}
