package com.studentplanner.repository;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.model.GamificationReward;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GamificationRewardRepository {

    public GamificationReward save(
            GamificationReward reward
    ) throws SQLException {

        String sql = """
                INSERT INTO gamification_rewards (
                    source_type,
                    source_id,
                    reward_type,
                    xp_amount,
                    awarded_at
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
            statement.setString(1, reward.getSourceType());
            statement.setInt(2, reward.getSourceId());
            statement.setString(3, reward.getRewardType());
            statement.setInt(4, reward.getXpAmount());
            statement.setString(5, reward.getAwardedAt());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    reward.setId(keys.getInt(1));
                }
            }
        }

        return reward;
    }

    public boolean exists(
            String sourceType,
            int sourceId,
            String rewardType
    ) throws SQLException {

        String sql = """
                SELECT 1
                FROM gamification_rewards
                WHERE source_type = ?
                  AND source_id = ?
                  AND reward_type = ?
                LIMIT 1
                """;

        try (
            Connection connection = DatabaseConnection.connect();
            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {
            statement.setString(1, sourceType);
            statement.setInt(2, sourceId);
            statement.setString(3, rewardType);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public GamificationReward find(
            String sourceType,
            int sourceId,
            String rewardType
    ) throws SQLException {

        String sql = """
                SELECT id,
                       source_type,
                       source_id,
                       reward_type,
                       xp_amount,
                       awarded_at
                FROM gamification_rewards
                WHERE source_type = ?
                  AND source_id = ?
                  AND reward_type = ?
                LIMIT 1
                """;

        try (
            Connection connection = DatabaseConnection.connect();
            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {
            statement.setString(1, sourceType);
            statement.setInt(2, sourceId);
            statement.setString(3, rewardType);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapRow(resultSet);
                }

                return null;
            }
        }
    }

    private GamificationReward mapRow(
            ResultSet resultSet
    ) throws SQLException {

        return new GamificationReward(
                resultSet.getInt("id"),
                resultSet.getString("source_type"),
                resultSet.getInt("source_id"),
                resultSet.getString("reward_type"),
                resultSet.getInt("xp_amount"),
                resultSet.getString("awarded_at")
        );
    }
}