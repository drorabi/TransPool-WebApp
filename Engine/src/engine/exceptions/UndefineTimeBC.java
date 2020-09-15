package engine.exceptions;

public class UndefineTimeBC extends Exception {
    final String EXCEPTION_MESSAGE="Can't go to BC time";
    @Override
    public String getMessage() {return EXCEPTION_MESSAGE;}
}
