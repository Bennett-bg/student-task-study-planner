
package com.studentplanner.service;

import com.studentplanner.model.Task;
import com.studentplanner.repository.TaskRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class TaskService {

    private final TaskRepository repository;

    public TaskService() {
        this.repository = new TaskRepository();
    }

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Task createTask(
            String title,
            String description,
            Integer subjectId,
            int priority,
            String dueDate,
            String dueTime,
            Integer estimatedMinutes,
            String reminderAt
    ) throws SQLException {

        validateTitle(title);
        validatePriority(priority);
        validateEstimatedMinutes(estimatedMinutes);

        Task task = new Task(
                title.trim(),
                description,
                subjectId,
                priority,
                dueDate,
                dueTime,
                estimatedMinutes,
                reminderAt
        );

        return repository.save(task);
    }

    public Task getTask(int id) throws SQLException {
        validateId(id);

        return repository.findById(id);
    }

    public List<Task> getAllTasks() throws SQLException {
        return repository.findAll();
    }

    public List<Task> getTasksForSubject(int subjectId)
            throws SQLException {

        validateId(subjectId);

        return repository.findBySubjectId(subjectId);
    }

    public void updateTask(Task task) throws SQLException {

        validateTask(task);

        Task existingTask = repository.findById(task.getId());

        if (existingTask == null) {
            throw new IllegalStateException(
                    "Task does not exist."
            );
        }

        repository.update(task);
    }

    public void deleteTask(int id) throws SQLException {

        validateId(id);

        Task existingTask = repository.findById(id);

        if (existingTask == null) {
            throw new IllegalStateException(
                    "Task does not exist."
            );
        }

        repository.delete(id);
    }

    public Task completeTask(int id) throws SQLException {

        validateId(id);

        Task task = repository.findById(id);

        if (task == null) {
            throw new IllegalStateException(
                    "Task does not exist."
            );
        }

        if (task.isCompleted()) {
            throw new IllegalStateException(
                    "Task is already completed."
            );
        }

        task.setCompleted(true);
        task.setCompletedAt(LocalDateTime.now().toString());

        repository.update(task);

        return task;
    }

    public Task uncompleteTask(int id) throws SQLException {

        validateId(id);

        Task task = repository.findById(id);

        if (task == null) {
            throw new IllegalStateException(
                    "Task does not exist."
            );
        }

        if (!task.isCompleted()) {
            throw new IllegalStateException(
                    "Task is already incomplete."
            );
        }

        task.setCompleted(false);
        task.setCompletedAt(null);

        repository.update(task);

        return task;
    }

    private void validateTask(Task task) {

        if (task == null) {
            throw new IllegalArgumentException(
                    "Task cannot be null."
            );
        }

        validateId(task.getId());
        validateTitle(task.getTitle());
        validatePriority(task.getPriority());
        validateEstimatedMinutes(task.getEstimatedMinutes());

        if (!task.isCompleted() && task.getCompletedAt() != null) {
            throw new IllegalArgumentException(
                    "Incomplete task cannot have a completion time."
            );
        }

        if (task.isCompleted() && task.getCompletedAt() == null) {
            throw new IllegalArgumentException(
                    "Completed task must have a completion time."
            );
        }
    }

    private void validateTitle(String title) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Task title cannot be empty."
            );
        }
    }

    private void validatePriority(int priority) {

        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException(
                    "Task priority must be between 1 and 5."
            );
        }
    }

    private void validateEstimatedMinutes(Integer estimatedMinutes) {

        if (estimatedMinutes != null && estimatedMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Estimated minutes must be greater than zero."
            );
        }
    }

    private void validateId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "ID must be greater than zero."
            );
        }
    }
}

