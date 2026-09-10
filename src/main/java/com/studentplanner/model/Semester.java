package com.studentplanner.model;

public class Semester {

    private int id;
    private String name;
    private String startDate;
    private String endDate;
    private boolean current;

    public Semester(String name, String startDate, String endDate, boolean current) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.current = current;
    }

    public Semester(int id, String name, String startDate, String endDate, boolean current) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.current = current;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    @Override
    public String toString() {
        return name;
    }
}