package com.studentplanner.model;

public class SessionNote {

    private int id;
    private int focusSessionId;
    private String note;
    private String createdAt;

    public SessionNote(
            int focusSessionId,
            String note
    ) {
        this.focusSessionId = focusSessionId;
        this.note = note;
    }

    public SessionNote(
            int id,
            int focusSessionId,
            String note,
            String createdAt
    ) {
        this.id = id;
        this.focusSessionId = focusSessionId;
        this.note = note;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFocusSessionId() {
        return focusSessionId;
    }

    public void setFocusSessionId(int focusSessionId) {
        this.focusSessionId = focusSessionId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return note;
    }
}