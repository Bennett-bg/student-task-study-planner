package com.studentplanner;

import java.time.LocalDate;

public class Task {

    private String title;
    private String subject;
    private LocalDate dueDate;
    private Priority priority;
    private boolean completed;

    public Task(String title, String subject, LocalDate dueDate, Priority priority) {
        this.title = title;
        this.subject = subject;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = false;
    }

    @Override
    public String toString() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public enum Priority {
        VERY_LOW,
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
}