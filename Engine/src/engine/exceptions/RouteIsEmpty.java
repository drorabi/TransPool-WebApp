package engine.exceptions;

public class RouteIsEmpty extends Exception {
    final String EXCEPTION_MESSAGE="Route must include at least 2 stations";
    @Override
    public String getMessage() {return EXCEPTION_MESSAGE;}
}
