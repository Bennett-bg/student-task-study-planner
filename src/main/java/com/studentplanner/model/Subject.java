package com.studentplanner.model;

public class Subject {

    private int id;
    private int semesterId;
    private String name;
    private String code;
    private String color;

    public Subject(int semesterId, String name, String code, String color) {
        this.semesterId = semesterId;
        this.name = name;
        this.code = code;
        this.color = color;
    }

    public Subject(int id, int semesterId, String name, String code, String color) {
        this.id = id;
        this.semesterId = semesterId;
        this.name = name;
        this.code = code;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return name;
    }
}