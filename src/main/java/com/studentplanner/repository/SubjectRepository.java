package com.studentplanner.repository;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.model.Subject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SubjectRepository {

    public Subject save(Subject subject) throws SQLException {

        String sql = """
                INSERT INTO subjects (semester_id, name, code, color)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, subject.getSemesterId());
            statement.setString(2, subject.getName());
            statement.setString(3, subject.getCode());
            statement.setString(4, subject.getColor());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    subject.setId(keys.getInt(1));
                }
            }
        }

        return subject;
    }

    public Subject findById(int id) throws SQLException {

        String sql = """
                SELECT id, semester_id, name, code, color
                FROM subjects
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

    public List<Subject> findAll() throws SQLException {

        String sql = """
                SELECT id, semester_id, name, code, color
                FROM subjects
                ORDER BY id
                """;

        List<Subject> subjects = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                subjects.add(mapRow(resultSet));
            }
        }

        return subjects;
    }

    public List<Subject> findBySemesterId(int semesterId) throws SQLException {

        String sql = """
                SELECT id, semester_id, name, code, color
                FROM subjects
                WHERE semester_id = ?
                ORDER BY id
                """;

        List<Subject> subjects = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, semesterId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    subjects.add(mapRow(resultSet));
                }
            }
        }

        return subjects;
    }

    public void update(Subject subject) throws SQLException {

        String sql = """
                UPDATE subjects
                SET semester_id = ?, name = ?, code = ?, color = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subject.getSemesterId());
            statement.setString(2, subject.getName());
            statement.setString(3, subject.getCode());
            statement.setString(4, subject.getColor());
            statement.setInt(5, subject.getId());

            statement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {

        String sql = "DELETE FROM subjects WHERE id = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private Subject mapRow(ResultSet resultSet) throws SQLException {

        return new Subject(
                resultSet.getInt("id"),
                resultSet.getInt("semester_id"),
                resultSet.getString("name"),
                resultSet.getString("code"),
                resultSet.getString("color")
        );
    }
} 