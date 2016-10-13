package com.curious.dina.goals.Model;

/**
 * Stores data for each goal in the list view.
 */
public class ListViewItem {
    public final String name, date;
    public final int goalType, day, week, month, year;
    private boolean completed;

    public ListViewItem(String name, String date, int goalType, int day,
                        int week, int month, int year, boolean completed) {
        this.name = name;
        this.date = date;
        this.goalType = goalType;
        this.day = day;
        this.week = week;
        this.month = month;
        this.year = year;
        this.completed = completed;
    }

    public String getTitle(){
        return name;
    }

    public boolean isCompleted(){
        return completed;
    }
}