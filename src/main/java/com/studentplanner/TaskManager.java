package com.studentplanner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private ArrayList<Task> tasks;

    private final boolean testMode;

    // Normal application startup
    public TaskManager() {

        testMode = false;
        tasks = TaskStorage.loadTasks();
    }

    // Empty TaskManager for testing
    public TaskManager(boolean empty) {

        testMode = true;
        tasks = new ArrayList<>();
    }

    public void addTask(Task task) {

        tasks.add(task);

        save();
    }

    public void removeTask(Task task) {

        tasks.remove(task);

        save();
    }

    public void completeTask(Task task) {

        task.setCompleted(true);

        save();
    }

    public void setTaskCompleted(
            Task task,
            boolean completed
    ) {

        task.setCompleted(completed);

        save();
    }

    public List<Task> getAllTasks() {

        return tasks;
    }

    // Returns completed tasks from the last 30 days
    public List<Task> getCompletedTasks() {

        ArrayList<Task> completedTasks =
                new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.minusDays(30);

        for (Task task : tasks) {

            if (task.isCompleted()
                    && task.getCompletedDate() != null
                    && !task.getCompletedDate()
                            .isBefore(cutoff)
                    && !task.getCompletedDate()
                            .isAfter(today)) {

                completedTasks.add(task);
            }
        }

        return completedTasks;
    }

    public List<Task> getTodayTasks() {

        ArrayList<Task> todayTasks =
                new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (Task task : tasks) {

            if (!task.isCompleted()
                    && task.getDueDate()
                            .equals(today)) {

                todayTasks.add(task);
            }
        }

        return todayTasks;
    }

    public List<Task> getUpcomingTasks() {

        ArrayList<Task> upcomingTasks =
                new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (Task task : tasks) {

            if (!task.isCompleted()
                    && task.getDueDate()
                            .isAfter(today)) {

                upcomingTasks.add(task);
            }
        }

        return upcomingTasks;
    }

    private void save() {

        // Tests must never modify the real tasks.dat
        if (!testMode) {
            TaskStorage.saveTasks(tasks);
        }
    }
}