package com.studentplanner.service;

import com.studentplanner.DatabaseInitializer;
import com.studentplanner.model.Achievement;
import com.studentplanner.model.GamificationReward;
import com.studentplanner.model.UserStats;
import com.studentplanner.repository.GamificationRewardRepository;
import com.studentplanner.repository.UserStatsRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GamificationServiceTest {

    private GamificationService gamificationService;
    private GamificationRewardRepository rewardRepository;
    private UserStatsRepository userStatsRepository;
    private AchievementService achievementService;

    @BeforeAll
    static void initializeDatabase() throws Exception {
        DatabaseInitializer.initialize();
    }

    @BeforeEach
    void setUp() throws Exception {

        rewardRepository = new GamificationRewardRepository();
        userStatsRepository = new UserStatsRepository();
        achievementService = new AchievementService();

        clearGamificationData();

        gamificationService = new GamificationService(
                rewardRepository,
                new UserStatsService(userStatsRepository),
                achievementService
        );
    }

    @Test
    void taskCompletionShouldAwardTenXp()
            throws SQLException {

        UserStats stats =
                gamificationService.rewardTaskCompletion(1);

        assertEquals(10, stats.getXp());
        assertEquals(1, stats.getLevel());

        assertTrue(
                rewardRepository.exists(
                        "TASK",
                        1,
                        "COMPLETION"
                )
        );
    }

    @Test
    void differentTasksShouldEachReceiveReward()
            throws SQLException {

        gamificationService.rewardTaskCompletion(1);
        gamificationService.rewardTaskCompletion(2);

        UserStats stats =
                gamificationService.getStats();

        assertEquals(20, stats.getXp());

        assertTrue(
                rewardRepository.exists(
                        "TASK",
                        1,
                        "COMPLETION"
                )
        );

        assertTrue(
                rewardRepository.exists(
                        "TASK",
                        2,
                        "COMPLETION"
                )
        );
    }

    @Test
    void sameTaskShouldNotReceiveRewardTwice()
            throws SQLException {

        gamificationService.rewardTaskCompletion(1);
        gamificationService.rewardTaskCompletion(1);

        UserStats stats =
                gamificationService.getStats();

        assertEquals(10, stats.getXp());

        assertEquals(
                1,
                countRewards(
                        "TASK",
                        1,
                        "COMPLETION"
                )
        );
    }

    @Test
    void examCompletionShouldAwardTwentyFiveXp()
            throws SQLException {

        UserStats stats =
                gamificationService.rewardExamCompletion(1);

        assertEquals(25, stats.getXp());

        assertTrue(
                rewardRepository.exists(
                        "EXAM",
                        1,
                        "COMPLETION"
                )
        );
    }

    @Test
    void sameExamShouldNotReceiveRewardTwice()
            throws SQLException {

        gamificationService.rewardExamCompletion(1);
        gamificationService.rewardExamCompletion(1);

        UserStats stats =
                gamificationService.getStats();

        assertEquals(25, stats.getXp());

        assertEquals(
                1,
                countRewards(
                        "EXAM",
                        1,
                        "COMPLETION"
                )
        );
    }

    @Test
    void focusSessionCompletionShouldAwardFiveXp()
            throws SQLException {

        UserStats stats =
                gamificationService
                        .rewardFocusSessionCompletion(1);

        assertEquals(5, stats.getXp());

        assertTrue(
                rewardRepository.exists(
                        "FOCUS",
                        1,
                        "COMPLETION"
                )
        );
    }

    @Test
    void sameFocusSessionShouldNotReceiveRewardTwice()
            throws SQLException {

        gamificationService
                .rewardFocusSessionCompletion(1);

        gamificationService
                .rewardFocusSessionCompletion(1);

        UserStats stats =
                gamificationService.getStats();

        assertEquals(5, stats.getXp());

        assertEquals(
                1,
                countRewards(
                        "FOCUS",
                        1,
                        "COMPLETION"
                )
        );
    }

    @Test
    void achievementUnlockShouldAwardConfiguredXp()
            throws SQLException {

        Achievement achievement =
                achievementService.createAchievement(
                        "Test Achievement",
                        "Test description",
                        75,
                        "TEST"
                );

        UserStats stats =
                gamificationService
                        .rewardAchievementUnlock(
                                achievement.getId()
                        );

        assertEquals(75, stats.getXp());

        assertTrue(
                achievementService.isUnlocked(
                        achievement.getId()
                )
        );

        assertTrue(
                rewardRepository.exists(
                        "ACHIEVEMENT",
                        achievement.getId(),
                        "UNLOCK"
                )
        );
    }

    @Test
    void sameAchievementShouldNotReceiveRewardTwice()
            throws SQLException {

        Achievement achievement =
                achievementService.createAchievement(
                        "Duplicate Test Achievement",
                        "Test description",
                        50,
                        "TEST"
                );

        gamificationService.rewardAchievementUnlock(
                achievement.getId()
        );

        gamificationService.rewardAchievementUnlock(
                achievement.getId()
        );

        UserStats stats =
                gamificationService.getStats();

        assertEquals(50, stats.getXp());

        assertEquals(
                1,
                countRewards(
                        "ACHIEVEMENT",
                        achievement.getId(),
                        "UNLOCK"
                )
        );
    }

    @Test
    void differentRewardTypesCanExistForSameSource()
            throws SQLException {

        gamificationService.rewardTaskCompletion(1);

        Achievement achievement =
                achievementService.createAchievement(
                        "Different Reward Type",
                        "Test description",
                        25,
                        "TEST"
                );

        assertTrue(
                rewardRepository.exists(
                        "TASK",
                        1,
                        "COMPLETION"
                )
        );

        assertFalse(
                rewardRepository.exists(
                        "TASK",
                        1,
                        "UNLOCK"
                )
        );

        assertNotNull(achievement);
    }

    @Test
    void productiveActivityShouldStartStreak()
            throws SQLException {

        LocalDate date =
                LocalDate.of(2026, 9, 1);

        UserStats stats =
                gamificationService
                        .recordProductiveActivity(date);

        assertEquals(1, stats.getCurrentStreak());
        assertEquals(1, stats.getLongestStreak());

        assertEquals(
                date.toString(),
                stats.getLastActivityDate()
        );
    }

    @Test
    void sameDayActivityShouldNotIncreaseStreakTwice()
            throws SQLException {

        LocalDate date =
                LocalDate.of(2026, 9, 1);

        gamificationService.recordProductiveActivity(date);
        gamificationService.recordProductiveActivity(date);

        UserStats stats =
                gamificationService.getStats();

        assertEquals(1, stats.getCurrentStreak());
        assertEquals(1, stats.getLongestStreak());
    }

    @Test
    void consecutiveDaysShouldIncreaseStreak()
            throws SQLException {

        gamificationService.recordProductiveActivity(
                LocalDate.of(2026, 9, 1)
        );

        gamificationService.recordProductiveActivity(
                LocalDate.of(2026, 9, 2)
        );

        gamificationService.recordProductiveActivity(
                LocalDate.of(2026, 9, 3)
        );

        UserStats stats =
                gamificationService.getStats();

        assertEquals(3, stats.getCurrentStreak());
        assertEquals(3, stats.getLongestStreak());
    }

    @Test
    void gapBetweenActivitiesShouldResetCurrentStreak()
            throws SQLException {

        gamificationService.recordProductiveActivity(
                LocalDate.of(2026, 9, 1)
        );

        gamificationService.recordProductiveActivity(
                LocalDate.of(2026, 9, 2)
        );

        gamificationService.recordProductiveActivity(
                LocalDate.of(2026, 9, 5)
        );

        UserStats stats =
                gamificationService.getStats();

        assertEquals(1, stats.getCurrentStreak());
        assertEquals(2, stats.getLongestStreak());
    }

    @Test
    void taskCompletionShouldAlsoRecordProductiveActivity()
            throws SQLException {

        gamificationService.rewardTaskCompletion(1);

        UserStats stats =
                gamificationService.getStats();

        assertEquals(1, stats.getCurrentStreak());
        assertNotNull(stats.getLastActivityDate());
    }

    @Test
    void repeatedTaskCompletionShouldNotCreateExtraStreakActivity()
            throws SQLException {

        gamificationService.rewardTaskCompletion(1);
        gamificationService.rewardTaskCompletion(1);

        UserStats stats =
                gamificationService.getStats();

        assertEquals(1, stats.getCurrentStreak());
        assertEquals(10, stats.getXp());
    }

    @Test
    void invalidTaskIdShouldBeRejected() {

        assertThrows(
                IllegalArgumentException.class,
                () -> gamificationService
                        .rewardTaskCompletion(0)
        );
    }

    @Test
    void invalidExamIdShouldBeRejected() {

        assertThrows(
                IllegalArgumentException.class,
                () -> gamificationService
                        .rewardExamCompletion(-1)
        );
    }

    @Test
    void invalidFocusSessionIdShouldBeRejected() {

        assertThrows(
                IllegalArgumentException.class,
                () -> gamificationService
                        .rewardFocusSessionCompletion(0)
        );
    }

    @Test
    void missingAchievementShouldBeRejected()
            throws SQLException {

        assertThrows(
                IllegalArgumentException.class,
                () -> gamificationService
                        .rewardAchievementUnlock(999999)
        );
    }

    @Test
    void hasRewardShouldReturnCorrectResult()
            throws SQLException {

        assertFalse(
                gamificationService.hasReward(
                        "TASK",
                        1,
                        "COMPLETION"
                )
        );

        gamificationService.rewardTaskCompletion(1);

        assertTrue(
                gamificationService.hasReward(
                        "TASK",
                        1,
                        "COMPLETION"
                )
        );
    }

    private int countRewards(
            String sourceType,
            int sourceId,
            String rewardType
    ) throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM gamification_rewards
                WHERE source_type = ?
                  AND source_id = ?
                  AND reward_type = ?
                """;

        try (
                Connection connection =
                        com.studentplanner.DatabaseConnection.connect();

                var statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, sourceType);
            statement.setInt(2, sourceId);
            statement.setString(3, rewardType);

            try (var resultSet =
                         statement.executeQuery()) {

                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    private void clearGamificationData()
            throws SQLException {

        try (
                Connection connection =
                        com.studentplanner.DatabaseConnection.connect();

                Statement statement =
                        connection.createStatement()
        ) {
            statement.executeUpdate(
                    "DELETE FROM gamification_rewards"
            );

            statement.executeUpdate(
                    "DELETE FROM achievements"
            );

            statement.executeUpdate(
                    "DELETE FROM user_stats"
            );
        }
    }
}