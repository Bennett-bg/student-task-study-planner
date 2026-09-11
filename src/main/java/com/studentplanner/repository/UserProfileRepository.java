package com.studentplanner.repository;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.model.UserProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserProfileRepository {

    public UserProfile get() throws SQLException {
        String sql = """
                SELECT id, display_name, avatar
                FROM user_profile
                WHERE id = 1
                """;

        try (
            Connection connection = DatabaseConnection.connect();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                return mapRow(resultSet);
            }

            return null;
        }
    }

    public UserProfile save(UserProfile profile) throws SQLException {
        String sql = """
                INSERT INTO user_profile (
                    id, display_name, avatar
                )
                VALUES (1, ?, ?)
                """;

        try (
            Connection connection = DatabaseConnection.connect();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, profile.getDisplayName());
            statement.setString(2, profile.getAvatar());

            statement.executeUpdate();
        }

        profile.setId(1);
        return profile;
    }

    public void update(UserProfile profile) throws SQLException {
        String sql = """
                UPDATE user_profile
                SET display_name = ?, avatar = ?
                WHERE id = 1
                """;

        try (
            Connection connection = DatabaseConnection.connect();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, profile.getDisplayName());
            statement.setString(2, profile.getAvatar());

            statement.executeUpdate();
        }
    }

    private UserProfile mapRow(ResultSet resultSet)
            throws SQLException {

        return new UserProfile(
                resultSet.getInt("id"),
                resultSet.getString("display_name"),
                resultSet.getString("avatar")
        );
    }
}