package com.studentplanner.repository;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.model.TimetableEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TimetableEntryRepository {

    public TimetableEntry save(TimetableEntry entry) throws SQLException {

        String sql = """
                INSERT INTO timetable_entries (
                    subject_id,
                    day_of_week,
                    start_time,
                    end_time,
                    room,
                    notes
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, entry.getSubjectId());
            statement.setInt(2, entry.getDayOfWeek());
            statement.setString(3, entry.getStartTime());
            statement.setString(4, entry.getEndTime());
            statement.setString(5, entry.getRoom());
            statement.setString(6, entry.getNotes());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    entry.setId(keys.getInt(1));
                }
            }
        }

        return entry;
    }

    public TimetableEntry findById(int id) throws SQLException {

        String sql = """
                SELECT id, subject_id, day_of_week,
                       start_time, end_time, room, notes
                FROM timetable_entries
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

    public List<TimetableEntry> findAll() throws SQLException {

        String sql = """
                SELECT id, subject_id, day_of_week,
                       start_time, end_time, room, notes
                FROM timetable_entries
                ORDER BY day_of_week, start_time, id
                """;

        List<TimetableEntry> entries = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                entries.add(mapRow(resultSet));
            }
        }

        return entries;
    }

    public List<TimetableEntry> findByDayOfWeek(int dayOfWeek)
            throws SQLException {

        String sql = """
                SELECT id, subject_id, day_of_week,
                       start_time, end_time, room, notes
                FROM timetable_entries
                WHERE day_of_week = ?
                ORDER BY start_time, id
                """;

        List<TimetableEntry> entries = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, dayOfWeek);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    entries.add(mapRow(resultSet));
                }
            }
        }

        return entries;
    }

    public List<TimetableEntry> findBySubjectId(int subjectId)
            throws SQLException {

        String sql = """
                SELECT id, subject_id, day_of_week,
                       start_time, end_time, room, notes
                FROM timetable_entries
                WHERE subject_id = ?
                ORDER BY day_of_week, start_time, id
                """;

        List<TimetableEntry> entries = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    entries.add(mapRow(resultSet));
                }
            }
        }

        return entries;
    }

    public void update(TimetableEntry entry) throws SQLException {

        String sql = """
                UPDATE timetable_entries
                SET subject_id = ?,
                    day_of_week = ?,
                    start_time = ?,
                    end_time = ?,
                    room = ?,
                    notes = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, entry.getSubjectId());
            statement.setInt(2, entry.getDayOfWeek());
            statement.setString(3, entry.getStartTime());
            statement.setString(4, entry.getEndTime());
            statement.setString(5, entry.getRoom());
            statement.setString(6, entry.getNotes());
            statement.setInt(7, entry.getId());

            statement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {

        String sql = "DELETE FROM timetable_entries WHERE id = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private TimetableEntry mapRow(ResultSet resultSet)
            throws SQLException {

        return new TimetableEntry(
                resultSet.getInt("id"),
                resultSet.getInt("subject_id"),
                resultSet.getInt("day_of_week"),
                resultSet.getString("start_time"),
                resultSet.getString("end_time"),
                resultSet.getString("room"),
                resultSet.getString("notes")
        );
    }
}