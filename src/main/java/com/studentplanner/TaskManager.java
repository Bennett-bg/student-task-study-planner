package com.studentplanner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private ArrayList<Task> tasks;

    // Normal application startup loads saved tasks
    public TaskManager() {
        tasks = TaskStorage.loadTasks();
    }

    // Creates an empty TaskManager for testing
    public TaskManager(boolean empty) {
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

    public void setTaskCompleted(Task task, boolean completed) {

        task.setCompleted(completed);
        save();
    }

    public List<Task> getAllTasks() {

        return tasks;
    }

    public List<Task> getCompletedTasks() {

        ArrayList<Task> completedTasks = new ArrayList<>();

        for (Task task : tasks) {

            if (task.isCompleted()) {
                completedTasks.add(task);
            }
        }

        return completedTasks;
    }

    public List<Task> getTodayTasks() {

        ArrayList<Task> todayTasks = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (Task task : tasks) {

            if (!task.isCompleted()
                    && task.getDueDate().equals(today)) {

                todayTasks.add(task);
            }
        }

        return todayTasks;
    }

    public List<Task> getUpcomingTasks() {

        ArrayList<Task> upcomingTasks = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (Task task : tasks) {

            if (!task.isCompleted()
                    && task.getDueDate().isAfter(today)) {

                upcomingTasks.add(task);
            }
        }

        return upcomingTasks;
    }

    private void save() {

        TaskStorage.saveTasks(tasks);
    }
}