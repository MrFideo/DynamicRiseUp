package com.tecmilenio.dynamicriseup.alarm;

public class AlarmModel {

    private int hour;
    private int minute;
    private boolean[] days;

    public AlarmModel(int hour, int minute, boolean[] days) {
        this.hour = hour;
        this.minute = minute;
        this.days = days;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean[] getDays() {
        return days;
    }
}
