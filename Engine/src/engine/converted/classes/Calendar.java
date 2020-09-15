package engine.converted.classes;

import engine.exceptions.UndefineTimeBC;

public class Calendar {

    private String[] week = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    private int day;

    private int month;

    private int hour;

    private int minutes;

    Calendar() {
        day = 1;
        ;
        hour = 0;
        minutes = 0;
    }

    public void forwardDay() {
        day++;
    }

    public void forwardTwoHours() {
        day = day + ((hour + 2) / 24);
        hour = (hour + 2) % 24;

    }

    public void forwardOneHour() {
        day = day + ((hour + 1) / 24);
        hour = (hour + 1) % 24;
    }

    public void forward30Minutes() {
        day = day + ((hour + ((minutes + 30) / 60)) / 24);
        hour = (hour + ((minutes + 30) / 60)) % 24;
        minutes = (minutes + 30) % 60;

    }

    public void forwardFiveMinutes() {
        day = day + ((hour + ((minutes + 5) / 60)) / 24);
        hour = (hour + ((minutes + 5) / 60)) % 24;
        minutes = (minutes + 5) % 60;
    }


    public void backwardDay() throws UndefineTimeBC {
        if (day == 1)
            throw new UndefineTimeBC();
        else
            day--;
    }

    public void backwardTwoHours() throws UndefineTimeBC {
        if (hour < 2) {
            if (day == 1)
                throw new UndefineTimeBC();
            day--;
            hour = (hour - 2) + 24;
        } else
            hour = hour - 2;
    }

    public void backwardOneHour() throws UndefineTimeBC {
        if (hour < 1) {
            if (day == 1)
                throw new UndefineTimeBC();
            day--;
            hour = 23;
        } else
            hour = hour - 1;
    }

    public void backward30Minutes() throws UndefineTimeBC {
        if (minutes < 30) {
            if (hour == 0) {
                if (day == 1)
                    throw new UndefineTimeBC();
                day--;
                hour = (hour - 1) + 24;
            } else
                hour = (hour - 1);
            minutes = (minutes - 30) + 60;
        } else
            minutes = minutes - 30;
    }

    public void backwardFiveMinutes() throws UndefineTimeBC {
        if (minutes < 5) {
            if (hour == 0) {
                if (day == 1)
                   throw new UndefineTimeBC();
                day--;
                hour = (hour - 1) + 24;
            } else
                hour = (hour - 1);
            minutes = (minutes - 5) + 60;
        } else
            minutes = minutes - 5;
    }


    public String toString() {
        String date = "Day #" + day + " " + week[day % 7] + " ";
        if (hour < 10)
            date = date + "0" + hour + ":";
        else
            date = date + hour + ":";

        if (minutes < 10)
            date = date + "0" + minutes;
        else
            date = date + minutes;

        return date;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getHour() {
        return hour;
    }

    public int getDay() {
        return day;
    }

}