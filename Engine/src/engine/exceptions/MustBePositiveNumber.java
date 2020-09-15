package engine.exceptions;

public class MustBePositiveNumber  extends Exception {
    final String EXCEPTION_MESSAGE="Must be Positive Number";
    @Override
    public String getMessage() {return EXCEPTION_MESSAGE;}
}
