package com.studentplanner.repository;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.model.Achievement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AchievementRepository {

    public Achievement save(Achievement achievement) throws SQLException {

        String sql = """
                INSERT INTO achievements (
                    name,
                    description,
                    xp_reward,
                    unlocked,
                    unlocked_at
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
            Connection connection = DatabaseConnection.connect();
            PreparedStatement statement = connection.prepareStatement(
                    sql,
                    java.sql.Statement.RETURN_GENERATED_KEYS
            )
        ) {
            statement.setString(1, achievement.getName());
            statement.setString(2, achievement.getDescription());
            statement.setInt(3, achievement.getXpReward());
            statement.setInt(4, achievement.isUnlocked() ? 1 : 0);
            statement.setString(5, achievement.getUnlockedAt());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    achievement.setId(keys.getInt(1));
                }
            }
        }

        return achievement;
    }

    public Achievement findById(int id) throws SQLException {

        String sql = """
                SELECT id, name, description, xp_reward, unlocked, unlocked_at
                FROM achievements
                WHERE id = ?
                """;

        try (
            Connection connection = DatabaseConnection.connect();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        }

        return null;
    }

    public List<Achievement> findAll() throws SQLException {

        String sql = """
                SELECT id, name, description, xp_reward, unlocked, unlocked_at
                FROM achievements
                ORDER BY id
                """;

        List<Achievement> achievements = new ArrayList<>();

        try (
            Connection connection = DatabaseConnection.connect();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                achievements.add(mapRow(resultSet));
            }
        }

        return achievements;
    }

    public List<Achievement> findUnlocked() throws SQLException {

        String sql = """
                SELECT id, name, description, xp_reward, unlocked, unlocked_at
                FROM achievements
                WHERE unlocked = 1
                ORDER BY unlocked_at, id
                """;

        List<Achievement> achievements = new ArrayList<>();

        try (
            Connection connection = DatabaseConnection.connect();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                achievements.add(mapRow(resultSet));
            }
        }

        return achievements;
    }

    public void update(Achievement achievement) throws SQLException {

        String sql = """
                UPDATE achievements
                SET name = ?,
                    description = ?,
                    xp_reward = ?,
                    unlocked = ?,
                    unlocked_at = ?
                WHERE id = ?
                """;

        try (
            Connection connection = DatabaseConnection.connect();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, achievement.getName());
            statement.setString(2, achievement.getDescription());
            statement.setInt(3, achievement.getXpReward());
            statement.setInt(4, achievement.isUnlocked() ? 1 : 0);
            statement.setString(5, achievement.getUnlockedAt());
            statement.setInt(6, achievement.getId());

            statement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {

        String sql = """
                DELETE FROM achievements
                WHERE id = ?
                """;

        try (
            Connection connection = DatabaseConnection.connect();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private Achievement mapRow(ResultSet resultSet) throws SQLException {

        return new Achievement(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getInt("xp_reward"),
                resultSet.getInt("unlocked") == 1,
                resultSet.getString("unlocked_at")
        );
    }
}