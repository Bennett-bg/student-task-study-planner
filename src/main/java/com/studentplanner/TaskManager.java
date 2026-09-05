package com.studentplanner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    // Stores all the tasks in the application
    private ArrayList<Task> tasks;

    // Creates an empty list of tasks
    public TaskManager() {
        tasks = new ArrayList<>();
    }

    // Adds a new task to the list
    public void addTask(Task task) {
        tasks.add(task);
    }

    // Removes a task from the list
    public void removeTask(Task task) {
        tasks.remove(task);
    }

    // Marks a task as completed
    public void completeTask(Task task) {
        task.setCompleted(true);
    }

    // Returns all tasks
    public List<Task> getAllTasks() {
        return tasks;
    }

    // Returns only the completed tasks
    public List<Task> getCompletedTasks() {
        ArrayList<Task> completedTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.isCompleted()) {
                completedTasks.add(task);
            }
        }

        return completedTasks;
    }

    // Returns tasks that are due today
    public List<Task> getTodayTasks() {
        ArrayList<Task> todayTasks = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Task task : tasks) {
            if (task.getDueDate().equals(today)) {
                todayTasks.add(task);
            }
        }

        return todayTasks;
    }

    // Returns tasks that are due after today
    public List<Task> getUpcomingTasks() {
        ArrayList<Task> upcomingTasks = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Task task : tasks) {
            if (task.getDueDate().isAfter(today)) {
                upcomingTasks.add(task);
            }
        }

        return upcomingTasks;
    }
}