package engine.converted.classes;


import engine.schema.generated.Scheduling;

public class Schedule {

    protected int startDay;
    protected int startHour;
    protected int startMinutes;
    protected int endDay;
    protected int endHour;
    protected int endMinutes;
    protected String recurrences;


    Schedule(Scheduling scheduling, int endDay, int endHour, int endMinutes)  {

        setStartDay(scheduling.getDayStart());
        setStartHour(scheduling.getHourStart());
        recurrences = scheduling.getRecurrences();
        setStartMinutes(scheduling.getMinuteStart());
        this.endDay=endDay;
        this.endHour = endHour;
        this.endMinutes = endMinutes;

    }



    public Schedule(int endDay, int startDay, int hour, String recurrences, Integer minutes, int endHour, int endMinutes, Boolean byDepaert)  {
        if(byDepaert) {
            setStartDay(startDay);
            this.endDay = (endDay);
        }
        else{
            setEndDay(endDay);
            this.startDay=startDay;
        }

        setStartHour(hour);
        setRecurrences(recurrences);
        setStartMinutes(minutes);
        this.endHour = endHour;
        this.endMinutes = endMinutes;
    }

    private void setRecurrences(String recurrences) {
        if(recurrences==null)
            this.recurrences="One Time";
        else
            this.recurrences=recurrences;
    }

    //setters---------------

    public void setStartMinutes(Integer startMinutes) {
        if (startMinutes == null)
            this.startMinutes = 0;
        else
            this.startMinutes = startMinutes;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public void setEndMinutes(int endMinutes) {
        this.endMinutes = endMinutes;
    }

    private void setStartDay(Integer dayStart) {
            startDay = dayStart;
    }

    public void setEndDay(int endDay) {
        this.endDay = endDay;
    }

    public void setStartHour(Integer hourStart)  {
        startHour = hourStart;
    }

    //getters----------------

    public int getStartDay() {
        return startDay;
    }

    public int getStartHour() {
        return startHour;
    }

    public String getRecurrences() {
        return recurrences;
    }

    public int getStartMinutes() {
        return startMinutes;
    }

    public int getEndDay() {
        return endDay;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinutes() {
        return endMinutes;
    }
    public  String scheduleToActionString(){
        String toString = "";
        if(startHour>-1 && startMinutes>-1) {
            if (startMinutes < 10)
                toString =   "Day " + startDay + " " + dayToString() + " hour " + startHour + ":0" + startMinutes ;
            else
                toString =   "Day " + startDay + " " + dayToString() + " houre " + startHour + ":" + startMinutes ;
        }
        return toString;
    }
    @Override
    public String toString() {
        String toString = "";
        if(startHour>-1 && startMinutes>-1) {
            if (startMinutes < 10)
                toString =   " Heading at " + "Day " + startDay + " " + dayToString() + " at " + startHour + ":0" + startMinutes + ", \n";
            else
                toString =   " Heading at " + "Day " + startDay + " " + dayToString() + " at " + startHour + ":" + startMinutes + ", \n";
        }

        if (endHour > -1 && endMinutes > -1) {
            if (endMinutes < 10)
                toString = toString + "arrival to last station on Day " + endDay + " " + endDayToString() + " at \n" + endHour + ":0" + endMinutes;
            else
                toString = toString + "arrival to last station on Day " + endDay + " " + endDayToString() + " at \n" + endHour + ":" + endMinutes;
        }
        return toString + ", recurring " +recurrences;
    }

    public int recurrencesToInteger(){
        if(recurrences.equals("Daily"))
            return 1;
        else if(recurrences.equals("BiDaily"))
            return 2;
        else if(recurrences.equals("Weekly"))
            return 7;
        else if(recurrences.equals("Monthly"))
            return 30;
        else
            return Integer.MAX_VALUE;
    }

   private String dayToString() {
        String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return weekDays[(startDay)%7];
    }

    private String endDayToString() {
        String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return weekDays[(endDay)%7];
    }
}
