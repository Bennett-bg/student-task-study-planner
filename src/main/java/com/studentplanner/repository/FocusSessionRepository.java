package com.studentplanner.repository;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.model.FocusSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FocusSessionRepository {

    public FocusSession save(FocusSession session) throws SQLException {

        String sql = """
                INSERT INTO focus_sessions (
                    task_id,
                    started_at,
                    ended_at,
                    planned_minutes,
                    actual_minutes,
                    completed
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            if (session.getTaskId() == null) {
                statement.setNull(1, java.sql.Types.INTEGER);
            } else {
                statement.setInt(1, session.getTaskId());
            }

            statement.setString(2, session.getStartedAt());
            statement.setString(3, session.getEndedAt());
            statement.setInt(4, session.getPlannedMinutes());

            if (session.getActualMinutes() == null) {
                statement.setNull(5, java.sql.Types.INTEGER);
            } else {
                statement.setInt(5, session.getActualMinutes());
            }

            statement.setInt(6, session.isCompleted() ? 1 : 0);

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    session.setId(keys.getInt(1));
                }
            }
        }

        return session;
    }

    public FocusSession findById(int id) throws SQLException {

        String sql = """
                SELECT id, task_id, started_at, ended_at,
                       planned_minutes, actual_minutes, completed
                FROM focus_sessions
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

    public List<FocusSession> findAll() throws SQLException {

        String sql = """
                SELECT id, task_id, started_at, ended_at,
                       planned_minutes, actual_minutes, completed
                FROM focus_sessions
                ORDER BY started_at DESC, id DESC
                """;

        List<FocusSession> sessions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                sessions.add(mapRow(resultSet));
            }
        }

        return sessions;
    }

    public List<FocusSession> findByTaskId(int taskId)
            throws SQLException {

        String sql = """
                SELECT id, task_id, started_at, ended_at,
                       planned_minutes, actual_minutes, completed
                FROM focus_sessions
                WHERE task_id = ?
                ORDER BY started_at DESC, id DESC
                """;

        List<FocusSession> sessions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, taskId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    sessions.add(mapRow(resultSet));
                }
            }
        }

        return sessions;
    }

    public List<FocusSession> findCompleted()
            throws SQLException {

        String sql = """
                SELECT id, task_id, started_at, ended_at,
                       planned_minutes, actual_minutes, completed
                FROM focus_sessions
                WHERE completed = 1
                ORDER BY started_at DESC, id DESC
                """;

        List<FocusSession> sessions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                sessions.add(mapRow(resultSet));
            }
        }

        return sessions;
    }

    public void update(FocusSession session) throws SQLException {

        String sql = """
                UPDATE focus_sessions
                SET task_id = ?,
                    started_at = ?,
                    ended_at = ?,
                    planned_minutes = ?,
                    actual_minutes = ?,
                    completed = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (session.getTaskId() == null) {
                statement.setNull(1, java.sql.Types.INTEGER);
            } else {
                statement.setInt(1, session.getTaskId());
            }

            statement.setString(2, session.getStartedAt());
            statement.setString(3, session.getEndedAt());
            statement.setInt(4, session.getPlannedMinutes());

            if (session.getActualMinutes() == null) {
                statement.setNull(5, java.sql.Types.INTEGER);
            } else {
                statement.setInt(5, session.getActualMinutes());
            }

            statement.setInt(6, session.isCompleted() ? 1 : 0);
            statement.setInt(7, session.getId());

            statement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {

        String sql = "DELETE FROM focus_sessions WHERE id = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private FocusSession mapRow(ResultSet resultSet)
            throws SQLException {

        int taskIdValue = resultSet.getInt("task_id");
        Integer taskId =
                resultSet.wasNull() ? null : taskIdValue;

        int actualMinutesValue =
                resultSet.getInt("actual_minutes");
        Integer actualMinutes =
                resultSet.wasNull() ? null : actualMinutesValue;

        return new FocusSession(
                resultSet.getInt("id"),
                taskId,
                resultSet.getString("started_at"),
                resultSet.getString("ended_at"),
                resultSet.getInt("planned_minutes"),
                actualMinutes,
                resultSet.getInt("completed") == 1
        );
    }
}