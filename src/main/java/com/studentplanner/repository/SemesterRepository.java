package com.studentplanner.repository;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.model.Semester;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SemesterRepository {

    public Semester save(Semester semester) throws SQLException {

        String sql = """
                INSERT INTO semesters (name, start_date, end_date, is_current)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, semester.getName());
            statement.setString(2, semester.getStartDate());
            statement.setString(3, semester.getEndDate());
            statement.setInt(4, semester.isCurrent() ? 1 : 0);

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    semester.setId(keys.getInt(1));
                }
            }
        }

        return semester;
    }

    public Semester findById(int id) throws SQLException {

        String sql = """
                SELECT id, name, start_date, end_date, is_current
                FROM semesters
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

    public List<Semester> findAll() throws SQLException {

        String sql = """
                SELECT id, name, start_date, end_date, is_current
                FROM semesters
                ORDER BY id
                """;

        List<Semester> semesters = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                semesters.add(mapRow(resultSet));
            }
        }

        return semesters;
    }

    public void update(Semester semester) throws SQLException {

        String sql = """
                UPDATE semesters
                SET name = ?, start_date = ?, end_date = ?, is_current = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, semester.getName());
            statement.setString(2, semester.getStartDate());
            statement.setString(3, semester.getEndDate());
            statement.setInt(4, semester.isCurrent() ? 1 : 0);
            statement.setInt(5, semester.getId());

            statement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {

        String sql = "DELETE FROM semesters WHERE id = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private Semester mapRow(ResultSet resultSet) throws SQLException {

        return new Semester(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("start_date"),
                resultSet.getString("end_date"),
                resultSet.getInt("is_current") == 1
        );
    }
}