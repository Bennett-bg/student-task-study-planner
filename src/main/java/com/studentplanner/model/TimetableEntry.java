package com.studentplanner.model;

public class TimetableEntry {

    private int id;
    private int subjectId;
    private int dayOfWeek;
    private String startTime;
    private String endTime;
    private String room;
    private String notes;

    public TimetableEntry(
            int subjectId,
            int dayOfWeek,
            String startTime,
            String endTime,
            String room,
            String notes
    ) {
        this.subjectId = subjectId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.notes = notes;
    }

    public TimetableEntry(
            int id,
            int subjectId,
            int dayOfWeek,
            String startTime,
            String endTime,
            String room,
            String notes
    ) {
        this.id = id;
        this.subjectId = subjectId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return startTime + " - " + endTime;
    }
}