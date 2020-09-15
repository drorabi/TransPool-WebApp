package engine.exceptions;

public class FullCapacityException extends Exception {
    String owner;
    private final String EXCEPTION_MESSAGE;

    public FullCapacityException(String owner) {
        this.owner = owner;
        EXCEPTION_MESSAGE = "ERROR: " + this.owner + "'s ride is full";
    }

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }
}

