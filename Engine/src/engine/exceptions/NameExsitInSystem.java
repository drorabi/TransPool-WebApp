package engine.exceptions;

public class NameExsitInSystem  extends Exception {
    String station;
    private final String EXCEPTION_MESSAGE;
    public NameExsitInSystem(String name){
        this.station =name;
        EXCEPTION_MESSAGE ="Username can be used only once. name in use: " + name;
    }
    @Override
    public String getMessage() {return EXCEPTION_MESSAGE;}
}
