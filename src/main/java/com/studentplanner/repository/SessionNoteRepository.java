package com.studentplanner.repository;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.model.SessionNote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SessionNoteRepository {

    public SessionNote save(SessionNote note) throws SQLException {

        String sql = """
                INSERT INTO session_notes (
                    focus_session_id,
                    note
                )
                VALUES (?, ?)
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, note.getFocusSessionId());
            statement.setString(2, note.getNote());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    note.setId(keys.getInt(1));
                }
            }
        }

        return note;
    }

    public SessionNote findById(int id) throws SQLException {

        String sql = """
                SELECT id, focus_session_id, note, created_at
                FROM session_notes
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

    public List<SessionNote> findAll() throws SQLException {

        String sql = """
                SELECT id, focus_session_id, note, created_at
                FROM session_notes
                ORDER BY created_at DESC, id DESC
                """;

        List<SessionNote> notes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                notes.add(mapRow(resultSet));
            }
        }

        return notes;
    }

    public List<SessionNote> findByFocusSessionId(int focusSessionId)
            throws SQLException {

        String sql = """
                SELECT id, focus_session_id, note, created_at
                FROM session_notes
                WHERE focus_session_id = ?
                ORDER BY created_at ASC, id ASC
                """;

        List<SessionNote> notes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, focusSessionId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    notes.add(mapRow(resultSet));
                }
            }
        }

        return notes;
    }

    public void update(SessionNote note) throws SQLException {

        String sql = """
                UPDATE session_notes
                SET focus_session_id = ?,
                    note = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, note.getFocusSessionId());
            statement.setString(2, note.getNote());
            statement.setInt(3, note.getId());

            statement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {

        String sql = "DELETE FROM session_notes WHERE id = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private SessionNote mapRow(ResultSet resultSet)
            throws SQLException {

        return new SessionNote(
                resultSet.getInt("id"),
                resultSet.getInt("focus_session_id"),
                resultSet.getString("note"),
                resultSet.getString("created_at")
        );
    }
}