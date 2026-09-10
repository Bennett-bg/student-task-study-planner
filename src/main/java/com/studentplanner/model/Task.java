package com.studentplanner.model;

public class Task {

    private int id;
    private String title;
    private String description;
    private Integer subjectId;
    private int priority;
    private String dueDate;
    private String dueTime;
    private Integer estimatedMinutes;
    private String reminderAt;
    private boolean completed;
    private String completedAt;

    public Task(
            String title,
            String description,
            Integer subjectId,
            int priority,
            String dueDate,
            String dueTime,
            Integer estimatedMinutes,
            String reminderAt
    ) {
        this.title = title;
        this.description = description;
        this.subjectId = subjectId;
        this.priority = priority;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.estimatedMinutes = estimatedMinutes;
        this.reminderAt = reminderAt;
        this.completed = false;
    }

    public Task(
            int id,
            String title,
            String description,
            Integer subjectId,
            int priority,
            String dueDate,
            String dueTime,
            Integer estimatedMinutes,
            String reminderAt,
            boolean completed,
            String completedAt
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subjectId = subjectId;
        this.priority = priority;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.estimatedMinutes = estimatedMinutes;
        this.reminderAt = reminderAt;
        this.completed = completed;
        this.completedAt = completedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public String getReminderAt() {
        return reminderAt;
    }

    public void setReminderAt(String reminderAt) {
        this.reminderAt = reminderAt;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public String toString() {
        return title;
    }
}