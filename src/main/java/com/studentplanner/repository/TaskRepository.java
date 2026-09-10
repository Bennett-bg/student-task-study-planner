package com.studentplanner.repository;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {

    public Task save(Task task) throws SQLException {

        String sql = """
                INSERT INTO tasks (
                    title,
                    description,
                    subject_id,
                    priority,
                    due_date,
                    due_time,
                    estimated_minutes,
                    reminder_at,
                    completed,
                    completed_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());

            if (task.getSubjectId() == null) {
                statement.setNull(3, java.sql.Types.INTEGER);
            } else {
                statement.setInt(3, task.getSubjectId());
            }

            statement.setInt(4, task.getPriority());
            statement.setString(5, task.getDueDate());
            statement.setString(6, task.getDueTime());

            if (task.getEstimatedMinutes() == null) {
                statement.setNull(7, java.sql.Types.INTEGER);
            } else {
                statement.setInt(7, task.getEstimatedMinutes());
            }

            statement.setString(8, task.getReminderAt());
            statement.setInt(9, task.isCompleted() ? 1 : 0);
            statement.setString(10, task.getCompletedAt());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    task.setId(keys.getInt(1));
                }
            }
        }

        return task;
    }

    public Task findById(int id) throws SQLException {

        String sql = """
                SELECT id, title, description, subject_id, priority,
                       due_date, due_time, estimated_minutes, reminder_at,
                       completed, completed_at
                FROM tasks
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        }

        return null;
    }

    public List<Task> findAll() throws SQLException {

        String sql = """
                SELECT id, title, description, subject_id, priority,
                       due_date, due_time, estimated_minutes, reminder_at,
                       completed, completed_at
                FROM tasks
                ORDER BY id
                """;

        List<Task> tasks = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                tasks.add(mapRow(resultSet));
            }
        }

        return tasks;
    }

    public List<Task> findBySubjectId(int subjectId) throws SQLException {

        String sql = """
                SELECT id, title, description, subject_id, priority,
                       due_date, due_time, estimated_minutes, reminder_at,
                       completed, completed_at
                FROM tasks
                WHERE subject_id = ?
                ORDER BY id
                """;

        List<Task> tasks = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    tasks.add(mapRow(resultSet));
                }
            }
        }

        return tasks;
    }

    public void update(Task task) throws SQLException {

        String sql = """
                UPDATE tasks
                SET title = ?,
                    description = ?,
                    subject_id = ?,
                    priority = ?,
                    due_date = ?,
                    due_time = ?,
                    estimated_minutes = ?,
                    reminder_at = ?,
                    completed = ?,
                    completed_at = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());

            if (task.getSubjectId() == null) {
                statement.setNull(3, java.sql.Types.INTEGER);
            } else {
                statement.setInt(3, task.getSubjectId());
            }

            statement.setInt(4, task.getPriority());
            statement.setString(5, task.getDueDate());
            statement.setString(6, task.getDueTime());

            if (task.getEstimatedMinutes() == null) {
                statement.setNull(7, java.sql.Types.INTEGER);
            } else {
                statement.setInt(7, task.getEstimatedMinutes());
            }

            statement.setString(8, task.getReminderAt());
            statement.setInt(9, task.isCompleted() ? 1 : 0);
            statement.setString(10, task.getCompletedAt());
            statement.setInt(11, task.getId());

            statement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {

        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private Task mapRow(ResultSet resultSet) throws SQLException {

        int subjectId = resultSet.getInt("subject_id");

        Integer nullableSubjectId =
                resultSet.wasNull() ? null : subjectId;

        int estimatedMinutes =
                resultSet.getInt("estimated_minutes");

        Integer nullableEstimatedMinutes =
                resultSet.wasNull() ? null : estimatedMinutes;

        return new Task(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                nullableSubjectId,
                resultSet.getInt("priority"),
                resultSet.getString("due_date"),
                resultSet.getString("due_time"),
                nullableEstimatedMinutes,
                resultSet.getString("reminder_at"),
                resultSet.getInt("completed") == 1,
                resultSet.getString("completed_at")
        );
    }
}