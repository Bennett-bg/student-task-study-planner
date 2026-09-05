package com.studentplanner;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class TaskManagerTest {

    @Test
    void testAddTask() {

        // Create a TaskManager
        TaskManager manager = new TaskManager();

        // Create a task
        Task task = new Task(
                "Study Java",
                "OOP",
                LocalDate.now(),
                Task.Priority.HIGH
        );

        // Add the task to the TaskManager
        manager.addTask(task);

        // Check that one task was added
        assertEquals(1, manager.getAllTasks().size());

        // Check that the correct task was added
        assertEquals(task, manager.getAllTasks().get(0));
    }

    @Test
    void testCompleteTask() {

        // Create a TaskManager
        TaskManager manager = new TaskManager();

        // Create a task
        Task task = new Task(
                "Study Java",
                "OOP",
                LocalDate.now(),
                Task.Priority.HIGH
        );

        // Add the task
        manager.addTask(task);

        // Complete the task
        manager.completeTask(task);

        // Check that the task is completed
        assertEquals(true, task.isCompleted());
    }

    @Test
    void testGetCompletedTasks() {

        // Create a TaskManager
        TaskManager manager = new TaskManager();

        // Create two tasks
        Task task1 = new Task(
                "Study Java",
                "OOP",
                LocalDate.now(),
                Task.Priority.HIGH
        );

        Task task2 = new Task(
                "Study Maths",
                "Maths",
                LocalDate.now(),
                Task.Priority.MEDIUM
        );

        // Add both tasks
        manager.addTask(task1);
        manager.addTask(task2);

        // Complete only the first task
        manager.completeTask(task1);

        // Check that only one completed task is returned
        assertEquals(1, manager.getCompletedTasks().size());

        // Check that the completed task is task1
        assertEquals(task1, manager.getCompletedTasks().get(0));
    }

    @Test
    void testGetTodayTasks() {

        // Create a TaskManager
        TaskManager manager = new TaskManager();

        // Create a task that is due today
        Task todayTask = new Task(
                "Study Java",
                "OOP",
                LocalDate.now(),
                Task.Priority.HIGH
        );

        // Add the task
        manager.addTask(todayTask);

        // Check that today's task is returned
        assertEquals(1, manager.getTodayTasks().size());

        // Check that the correct task is returned
        assertEquals(todayTask, manager.getTodayTasks().get(0));
    }

    @Test
    void testGetUpcomingTasks() {

        // Create a TaskManager
        TaskManager manager = new TaskManager();

        // Create a task that is due tomorrow
        Task upcomingTask = new Task(
                "Study Java",
                "OOP",
                LocalDate.now().plusDays(1),
                Task.Priority.HIGH
        );

        // Add the task
        manager.addTask(upcomingTask);

        // Check that the upcoming task is returned
        assertEquals(1, manager.getUpcomingTasks().size());

        // Check that the correct task is returned
        assertEquals(upcomingTask, manager.getUpcomingTasks().get(0));
    }

    @Test
    void testRemoveTask() {

        // Create a TaskManager
        TaskManager manager = new TaskManager();

        // Create a task
        Task task = new Task(
                "Study Java",
                "OOP",
                LocalDate.now(),
                Task.Priority.HIGH
        );

        // Add the task
        manager.addTask(task);

        // Remove the task
        manager.removeTask(task);

        // Check that the task was removed
        assertEquals(0, manager.getAllTasks().size());
    }
}

