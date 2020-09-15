package engine.users;

import engine.converted.classes.CombinedTrip;
import engine.converted.classes.Feedback;
import engine.converted.classes.Request;
import engine.converted.classes.Trip;
import engine.ui.Engine;
import java.text.SimpleDateFormat;
import java.util.*;

public class User {
    private Map<String,Feedback> feedbacks = new HashMap<>();
    private Engine engine;
    private String name;
    private int userType;   // 0-driver, 1-pooler
    private int balance;
    private LinkedList<Action> allActions;
    private Map<String, Request> requests=new HashMap<>();
    private Map<String, Trip> trips=new HashMap<>();
    private Map<String, String> alerts = new HashMap<>();
    private  int alertID=100;
    private LinkedList<CombinedTrip> allOptions=new LinkedList<>();

    public Map<String, Trip> getTrips() {
        return trips;
    }

    public LinkedList<CombinedTrip> getAllOptions() {
        return allOptions;
    }

    public User(String name, int type){
        this.name=name;
        this.userType=type;
        engine=new Engine();
        balance=0;
        allActions=new LinkedList<>();
    }

    public int getBalance() {
        return balance;
    }

    public LinkedList<Action> getAllActions() {
        return allActions;
    }

    public Engine getEngine() {
        return engine;
    }

    public int getUserType() {
        return userType;
    }

    public String getName() {
        return name;
    }

    public Map<String, Request> getRequests() {
        return requests;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void setAllActions(LinkedList<Action> allActions) {
        this.allActions = allActions;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void loadMoney(String amount) {
        Integer add=Integer.parseInt(amount);
        Action newAct=new Action();
        newAct.setType(Action.Type.LOAD);
        newAct.setAmount(add);
        newAct.setBalanceAfter(this.balance+add);
        newAct.setBalanceBefore(this.balance);
        newAct.setDate("Day 0 hour 00:00");
        this.balance+=add;
        allActions.add(newAct);
    }

    public void addFeedback(String content, int rank, String writer){
        feedbacks.put(writer,new Feedback(content,rank,writer));
    }

    public Map<String, Feedback> getFeedbacks() {
        return feedbacks;
    }

    public String allUserFeedbacksToString(){
        String toReturn="";
        for(Map.Entry<String,Feedback> entry : feedbacks.entrySet()){
                toReturn+=entry.getValue().toString();
        }
        return toReturn;
    }

    public void addRequest(String username, Request request){
        requests.put(username,request);
    }

    public void addTrip(String username,Trip trip){
        trips.put(username,trip);
    }

    public void addAction(Boolean isTransfer, int price, String date){
        if(isTransfer){
            addTransferAction(price, date);
        }
        else{
            addReceiveAction(price, date);
        }
    }

    private void addReceiveAction(int price, String date) {
        Action newAct=new Action();
        newAct.setType(Action.Type.RECEIVE);
        newAct.setAmount(price);
        newAct.setBalanceAfter(this.balance+price);
        newAct.setBalanceBefore(this.balance);
        newAct.setDate(date);
        this.balance+=price;
        allActions.add(newAct);
    }

    private void addTransferAction(int price, String date) {
        Action newAct=new Action();
        newAct.setType(Action.Type.TRANSFER);
        newAct.setAmount(price);
        newAct.setBalanceAfter(this.balance-price);
        newAct.setBalanceBefore(this.balance);
        newAct.setDate(date);
        this.balance-=price;
        allActions.add(newAct);
    }

    public void addAlert(String alertText){
        alerts.put("0"+alertID, alertText);
        alertID++;
    }

    public Map<String, String> getAlerts() {
        return alerts;
    }

    public void setAllOptions(LinkedList<CombinedTrip> mapAllOptions) {
        this.allOptions=(LinkedList) mapAllOptions.clone();
    }
}
