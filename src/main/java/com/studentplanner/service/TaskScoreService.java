
package com.studentplanner.service;

import com.studentplanner.model.Task;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TaskScoreService {

    private static final int MAX_PRIORITY = 5;

    public int calculateScore(Task task) {

        if (task == null) {
            throw new IllegalArgumentException(
                    "Task cannot be null."
            );
        }

        if (task.isCompleted()) {
            return 0;
        }

        int priorityScore =
                calculatePriorityScore(task.getPriority());

        int deadlineUrgency =
                calculateDeadlineUrgency(task.getDueDate());

        int overdueScore =
                isOverdue(task) ? 10 : 0;

        int remainingWorkScore =
                calculateRemainingWorkScore(
                        task.getEstimatedMinutes()
                );

        return priorityScore
                + (deadlineUrgency * 5)
                + overdueScore
                + (remainingWorkScore * 2);
    }

    public int calculatePriorityScore(int priority) {

        if (priority < 1 || priority > MAX_PRIORITY) {
            throw new IllegalArgumentException(
                    "Priority must be between 1 and 5."
            );
        }

        return priority * 4;
    }

    public int calculateDeadlineUrgency(String dueDate) {

        if (dueDate == null || dueDate.isBlank()) {
            return 0;
        }

        LocalDate deadline = parseDate(dueDate);
        LocalDate today = LocalDate.now();

        long daysUntilDeadline =
                ChronoUnit.DAYS.between(today, deadline);

        if (daysUntilDeadline > 7) {
            return 1;
        }

        if (daysUntilDeadline >= 3) {
            return 2;
        }

        if (daysUntilDeadline >= 1) {
            return 3;
        }

        return 5;
    }

    public boolean isOverdue(Task task) {

        if (task == null) {
            throw new IllegalArgumentException(
                    "Task cannot be null."
            );
        }

        if (task.isCompleted()) {
            return false;
        }

        if (task.getDueDate() == null
                || task.getDueDate().isBlank()) {
            return false;
        }

        LocalDate deadline = parseDate(task.getDueDate());

        return deadline.isBefore(LocalDate.now());
    }

    public int calculateRemainingWorkScore(
            Integer estimatedMinutes
    ) {

        if (estimatedMinutes == null
                || estimatedMinutes <= 0) {
            return 0;
        }

        if (estimatedMinutes <= 30) {
            return 1;
        }

        if (estimatedMinutes <= 60) {
            return 2;
        }

        if (estimatedMinutes <= 120) {
            return 3;
        }

        if (estimatedMinutes <= 240) {
            return 4;
        }

        return 5;
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
