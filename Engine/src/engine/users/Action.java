package engine.users;

public class Action {
    public enum Type{LOAD, RECEIVE, TRANSFER}
    private Type type;
    private String date;
    private int amount;
    private int balanceBefore;
    private int balanceAfter;

    public int getAmount() {
        return amount;
    }

    public int getBalanceAfter() {
        return balanceAfter;
    }

    public int getBalanceBefore() {
        return balanceBefore;
    }

    public String getDate() {
        return date;
    }

    public Type getType() {
        return type;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setBalanceAfter(int balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public void setBalanceBefore(int balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
