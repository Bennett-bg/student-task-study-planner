package com.studentplanner.repository;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.model.UserStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserStatsRepository {

    public UserStats get() throws SQLException {

        String sql = """
                SELECT id, xp, level, current_streak, longest_streak, last_activity_date
                FROM user_stats
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

    public UserStats save(UserStats stats) throws SQLException {

        String sql = """
                INSERT INTO user_stats (
                    id,
                    xp,
                    level,
                    current_streak,
                    longest_streak,
                    last_activity_date
                )
                VALUES (1, ?, ?, ?, ?, ?)
                """;

        try (
            Connection connection = DatabaseConnection.connect();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, stats.getXp());
            statement.setInt(2, stats.getLevel());
            statement.setInt(3, stats.getCurrentStreak());
            statement.setInt(4, stats.getLongestStreak());
            statement.setString(5, stats.getLastActivityDate());

            statement.executeUpdate();
        }

        stats.setId(1);

        return stats;
    }

    public void update(UserStats stats) throws SQLException {

        String sql = """
                UPDATE user_stats
                SET xp = ?,
                    level = ?,
                    current_streak = ?,
                    longest_streak = ?,
                    last_activity_date = ?
                WHERE id = 1
                """;

        try (
            Connection connection = DatabaseConnection.connect();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, stats.getXp());
            statement.setInt(2, stats.getLevel());
            statement.setInt(3, stats.getCurrentStreak());
            statement.setInt(4, stats.getLongestStreak());
            statement.setString(5, stats.getLastActivityDate());

            statement.executeUpdate();
        }
    }

    private UserStats mapRow(ResultSet resultSet) throws SQLException {

        return new UserStats(
                resultSet.getInt("id"),
                resultSet.getInt("xp"),
                resultSet.getInt("level"),
                resultSet.getInt("current_streak"),
                resultSet.getInt("longest_streak"),
                resultSet.getString("last_activity_date")
        );
    }
}