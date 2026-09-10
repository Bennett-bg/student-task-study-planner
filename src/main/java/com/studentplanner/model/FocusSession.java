package com.studentplanner.model;

public class FocusSession {

    private int id;
    private Integer taskId;
    private String startedAt;
    private String endedAt;
    private int plannedMinutes;
    private Integer actualMinutes;
    private boolean completed;

    public FocusSession(
            Integer taskId,
            String startedAt,
            String endedAt,
            int plannedMinutes,
            Integer actualMinutes,
            boolean completed
    ) {
        this.taskId = taskId;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.plannedMinutes = plannedMinutes;
        this.actualMinutes = actualMinutes;
        this.completed = completed;
    }

    public FocusSession(
            int id,
            Integer taskId,
            String startedAt,
            String endedAt,
            int plannedMinutes,
            Integer actualMinutes,
            boolean completed
    ) {
        this.id = id;
        this.taskId = taskId;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.plannedMinutes = plannedMinutes;
        this.actualMinutes = actualMinutes;
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(String endedAt) {
        this.endedAt = endedAt;
    }

    public int getPlannedMinutes() {
        return plannedMinutes;
    }

    public void setPlannedMinutes(int plannedMinutes) {
        this.plannedMinutes = plannedMinutes;
    }

    public Integer getActualMinutes() {
        return actualMinutes;
    }

    public void setActualMinutes(Integer actualMinutes) {
        this.actualMinutes = actualMinutes;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return plannedMinutes + " minute focus session";
    }
}