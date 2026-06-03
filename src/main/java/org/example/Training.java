package org.example;

import java.time.LocalDate;

public class Training implements Comparable<Training> {
    private String name;
    private LocalDate date;
    private String time;
    private String coach;
    private int durationMin;
    private int efficiency;

    public Training(String name, LocalDate date, String time, String coach, int durationMin, int efficiency) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.coach = coach;
        this.durationMin = durationMin;s
        this.efficiency = efficiency;
    }

    public String getName() { return name; }
    public LocalDate getDate() { return date; }
    public String getTime() { return time; }
    public String getCoach() { return coach; }
    public int getDurationMin() { return durationMin; }
    public int getEfficiency() { return efficiency; }

    @Override
    public String toString() {
        return name + " (" + date + ")";
    }

    @Override
    public int compareTo(Training o) {
        return this.date.compareTo(o.date);
    }
}