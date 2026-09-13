
package com.studentplanner.service;

import com.studentplanner.model.Task;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DeadlineRiskService {

    public enum RiskLevel {
        NONE,
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    private final TaskScoreService taskScoreService;

    public DeadlineRiskService() {
        this.taskScoreService = new TaskScoreService();
    }

    public DeadlineRiskService(TaskScoreService taskScoreService) {
        this.taskScoreService = taskScoreService;
    }

    public RiskLevel calculateRisk(Task task) {

        if (task == null) {
            throw new IllegalArgumentException(
                    "Task cannot be null."
            );
        }

        if (task.isCompleted()) {
            return RiskLevel.NONE;
        }

        if (taskScoreService.isOverdue(task)) {
            return RiskLevel.CRITICAL;
        }

        String dueDate = task.getDueDate();

        if (dueDate == null || dueDate.isBlank()) {
            return calculateRiskWithoutDeadline(task);
        }

        LocalDate deadline = parseDate(dueDate);
        LocalDate today = LocalDate.now();

        long daysUntilDeadline =
                ChronoUnit.DAYS.between(today, deadline);

        if (daysUntilDeadline <= 1) {
            return RiskLevel.HIGH;
        }

        if (daysUntilDeadline <= 3) {
            return RiskLevel.MEDIUM;
        }

        return RiskLevel.LOW;
    }

    public String getRiskLabel(Task task) {

        return calculateRisk(task)
                .name();
    }

    public String getRiskDescription(Task task) {

        RiskLevel risk = calculateRisk(task);

        return switch (risk) {
            case NONE ->
                    "This task has been completed.";

            case LOW ->
                    "This task is currently at low risk.";

            case MEDIUM ->
                    "This task is approaching its deadline.";

            case HIGH ->
                    "This task is due within 24 hours.";

            case CRITICAL ->
                    "This task is overdue.";
        };
    }

    public int getTaskScore(Task task) {

        return taskScoreService.calculateScore(task);
    }

    private RiskLevel calculateRiskWithoutDeadline(
            Task task
    ) {

        int priority = task.getPriority();

        if (priority >= 5) {
            return RiskLevel.MEDIUM;
        }

        return RiskLevel.LOW;
    }

    private LocalDate parseDate(String dueDate) {

        try {
            return LocalDate.parse(dueDate);
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Invalid due date. Expected format: YYYY-MM-DD.",
                    exception
            );
        }
    }
}

