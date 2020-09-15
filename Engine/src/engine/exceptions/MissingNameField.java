package engine.exceptions;

public class MissingNameField extends Exception {
    final String EXCEPTION_MESSAGE="Must give a name";
    @Override
    public String getMessage() {return EXCEPTION_MESSAGE;}
}
