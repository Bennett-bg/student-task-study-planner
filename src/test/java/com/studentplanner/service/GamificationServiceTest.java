package com.studentplanner.service;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.DatabaseInitializer;
import com.studentplanner.model.Achievement;
import com.studentplanner.model.UserStats;
import com.studentplanner.repository.AchievementRepository;
import com.studentplanner.repository.ExamRepository;
import com.studentplanner.repository.FocusSessionRepository;
import com.studentplanner.repository.GamificationRewardRepository;
import com.studentplanner.repository.TaskRepository;
import com.studentplanner.repository.UserStatsRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GamificationServiceTest {

    private static GamificationRewardRepository rewardRepository;
    private static UserStatsRepository userStatsRepository;
    private static AchievementRepository achievementRepository;

    private static TaskRepository taskRepository;
    private static ExamRepository examRepository;
    private static FocusSessionRepository focusSessionRepository;

    private static UserStatsService userStatsService;
    private static AchievementService achievementService;

    private GamificationService gamificationService;

    @BeforeAll
    static void initializeDatabase() throws Exception {

        DatabaseInitializer.initialize();

        rewardRepository =
                new GamificationRewardRepository();

        userStatsRepository =
                new UserStatsRepository();

        achievementRepository =
                new AchievementRepository();

        taskRepository =
                new TaskRepository();

        examRepository =
                new ExamRepository();

        focusSessionRepository =
                new FocusSessionRepository();

        userStatsService =
                new UserStatsService(
                        userStatsRepository
                );

        achievementService =
                new AchievementService(
                        achievementRepository
                );
    }

    @BeforeEach
    void setUp() throws SQLException {

        clearTable("gamification_rewards");
        clearTable("achievements");
        clearTable("user_stats");
        clearTable("tasks");
        clearTable("exams");
        clearTable("focus_sessions");

        gamificationService =
                new GamificationService(
                        rewardRepository,
                        userStatsService,
                        achievementService,
                        taskRepository,
                        examRepository,
                        focusSessionRepository
                );
    }

    @Test
    void taskCompletionShouldAwardTenXp()
            throws SQLException {

        gamificationService.rewardTaskCompletion(1);

        UserStats stats =
                gamificationService.getStats();

        // 10 XP task reward
        assertEquals(10, stats.getXp());

        assertTrue(
                rewardRepository.exists(
                        "TASK",
                        1,
                        "COMPLETION"
                )
        );

        assertEquals(
                10,
                rewardRepository.find(
                        "TASK",
                        1,
                        "COMPLETION"
                ).getXpAmount()
        );
    }

    @Test
    void differentTasksShouldEachAwardXp()
            throws SQLException {

        gamificationService.rewardTaskCompletion(1);
        gamificationService.rewardTaskCompletion(2);

        UserStats stats =
                gamificationService.getStats();

        // Each distinct task awards 10 XP.
        // Total = 20
        assertEquals(20, stats.getXp());
    }

    @Test
    void sameTaskShouldNotAwardXpTwice()
            throws SQLException {

        gamificationService.rewardTaskCompletion(1);
        gamificationService.rewardTaskCompletion(1);

        UserStats stats =
                gamificationService.getStats();

        assertEquals(10, stats.getXp());

        assertTrue(
                rewardRepository.exists(
                        "TASK",
                        1,
                        "COMPLETION"
                )
        );
    }

    @Test
    void examCompletionShouldAwardTwentyFiveXp()
            throws SQLException {

        gamificationService.rewardExamCompletion(1);

        UserStats stats =
                gamificationService.getStats();

        // 25 XP exam reward
        assertEquals(25, stats.getXp());

        assertEquals(
                25,
                rewardRepository.find(
                        "EXAM",
                        1,
                        "COMPLETION"
                ).getXpAmount()
        );
    }

    @Test
    void sameExamShouldNotAwardXpTwice()
            throws SQLException {

        gamificationService.rewardExamCompletion(1);
        gamificationService.rewardExamCompletion(1);

        UserStats stats =
                gamificationService.getStats();

        assertEquals(25, stats.getXp());
    }

    @Test
    void focusSessionCompletionShouldAwardFiveXp()
            throws SQLException {

        gamificationService.rewardFocusSessionCompletion(1);

        UserStats stats =
                gamificationService.getStats();

        assertEquals(5, stats.getXp());

        assertEquals(
                5,
                rewardRepository.find(
                        "FOCUS",
                        1,
                        "COMPLETION"
                ).getXpAmount()
        );
    }

    @Test
    void sameFocusSessionShouldNotAwardXpTwice()
            throws SQLException {

        gamificationService.rewardFocusSessionCompletion(1);
        gamificationService.rewardFocusSessionCompletion(1);

        UserStats stats =
                gamificationService.getStats();

        assertEquals(5, stats.getXp());
    }

    @Test
    void firstTaskShouldAutomaticallyUnlockFirstStep()
            throws SQLException {

        insertCompletedTasks(1);

        gamificationService.rewardTaskCompletion(1);

        Achievement achievement =
                findAchievement("First Step");

        assertNotNull(achievement);
        assertTrue(achievement.isUnlocked());

        assertTrue(
                rewardRepository.exists(
                        "ACHIEVEMENT",
                        achievement.getId(),
                        "UNLOCK"
                )
        );

        assertEquals(
                35,
                gamificationService.getStats().getXp()
        );
    }

    @Test
    void tenCompletedTasksShouldUnlockGettingSerious()
            throws SQLException {

        insertCompletedTasks(10);

        gamificationService.rewardTaskCompletion(1);

        Achievement achievement =
                findAchievement("Getting Serious");

        assertNotNull(achievement);
        assertTrue(achievement.isUnlocked());
    }

    @Test
    void fiftyCompletedTasksShouldUnlockTaskMachine()
            throws SQLException {

        insertCompletedTasks(50);

        gamificationService.checkAchievements();

        Achievement achievement =
                findAchievement("Task Machine");

        assertNotNull(achievement);
        assertTrue(achievement.isUnlocked());
    }

    @Test
    void fiveCompletedFocusSessionsShouldUnlockFocused()
            throws SQLException {

        insertCompletedFocusSessions(5);

        gamificationService.checkAchievements();

        Achievement achievement =
                findAchievement("Focused");

        assertNotNull(achievement);
        assertTrue(achievement.isUnlocked());
    }

    @Test
    void twentyFiveCompletedFocusSessionsShouldUnlockDeepWork()
            throws SQLException {

        insertCompletedFocusSessions(25);

        gamificationService.checkAchievements();

        Achievement achievement =
                findAchievement("Deep Work");

        assertNotNull(achievement);
        assertTrue(achievement.isUnlocked());
    }

    @Test
    void oneCompletedExamShouldUnlockExamReady()
            throws SQLException {

        insertCompletedExams(1);

        gamificationService.checkAchievements();

        Achievement achievement =
                findAchievement("Exam Ready");

        assertNotNull(achievement);
        assertTrue(achievement.isUnlocked());
    }

    @Test
    void fiveCompletedExamsShouldUnlockAcademicGrinder()
            throws SQLException {

        insertCompletedExams(5);

        gamificationService.checkAchievements();

        Achievement achievement =
                findAchievement("Academic Grinder");

        assertNotNull(achievement);
        assertTrue(achievement.isUnlocked());
    }

    @Test
    void threeDayStreakShouldUnlockOnARoll()
            throws SQLException {

        LocalDate start =
                LocalDate.of(2026, 9, 10);

        gamificationService.recordProductiveActivity(start);

        gamificationService.recordProductiveActivity(
                start.plusDays(1)
        );

        gamificationService.recordProductiveActivity(
                start.plusDays(2)
        );

        Achievement achievement =
                findAchievement("On a Roll");

        assertNotNull(achievement);
        assertTrue(achievement.isUnlocked());
    }

    @Test
    void sevenDayStreakShouldUnlockDedicated()
            throws SQLException {

        LocalDate start =
                LocalDate.of(2026, 9, 6);

        for (int i = 0; i < 7; i++) {

            gamificationService.recordProductiveActivity(
                    start.plusDays(i)
            );
        }

        Achievement achievement =
                findAchievement("Dedicated");

        assertNotNull(achievement);
        assertTrue(achievement.isUnlocked());
    }

    @Test
    void levelTenShouldUnlockMonolithVeteran()
            throws SQLException {

        userStatsService.addXp(900);

        gamificationService.checkAchievements();

        Achievement achievement =
                findAchievement("Monolith Veteran");

        assertNotNull(achievement);
        assertTrue(achievement.isUnlocked());
    }

    @Test
    void achievementRewardShouldOnlyBeAwardedOnce()
            throws SQLException {

        insertCompletedTasks(1);

        gamificationService.rewardTaskCompletion(1);

        Achievement achievement =
                findAchievement("First Step");

        assertNotNull(achievement);

        int xpAfterFirst =
                gamificationService.getStats().getXp();

        gamificationService.rewardAchievementUnlock(
                achievement.getId()
        );

        int xpAfterSecond =
                gamificationService.getStats().getXp();

        assertEquals(
                xpAfterFirst,
                xpAfterSecond
        );
    }

    @Test
    void multipleActivitiesOnSameDayShouldOnlyIncreaseStreakOnce()
            throws SQLException {

        LocalDate date =
                LocalDate.of(2026, 9, 10);

        gamificationService.recordProductiveActivity(date);
        gamificationService.recordProductiveActivity(date);
        gamificationService.recordProductiveActivity(date);

        UserStats stats =
                gamificationService.getStats();

        assertEquals(
                1,
                stats.getCurrentStreak()
        );
    }

    @Test
    void consecutiveDaysShouldIncreaseStreak()
            throws SQLException {

        LocalDate first =
                LocalDate.of(2026, 9, 10);

        gamificationService.recordProductiveActivity(first);

        gamificationService.recordProductiveActivity(
                first.plusDays(1)
        );

        gamificationService.recordProductiveActivity(
                first.plusDays(2)
        );

        UserStats stats =
                gamificationService.getStats();

        assertEquals(
                3,
                stats.getCurrentStreak()
        );

        assertEquals(
                3,
                stats.getLongestStreak()
        );
    }

    @Test
    void missingDayShouldResetCurrentStreak()
            throws SQLException {

        LocalDate first =
                LocalDate.of(2026, 9, 10);

        gamificationService.recordProductiveActivity(first);

        gamificationService.recordProductiveActivity(
                first.plusDays(1)
        );

        gamificationService.recordProductiveActivity(
                first.plusDays(3)
        );

        UserStats stats =
                gamificationService.getStats();

        assertEquals(
                1,
                stats.getCurrentStreak()
        );

        assertEquals(
                2,
                stats.getLongestStreak()
        );
    }

    @Test
    void taskCompletionShouldRecordProductiveActivity()
            throws SQLException {

        insertCompletedTasks(1);

        gamificationService.rewardTaskCompletion(1);

        UserStats stats =
                gamificationService.getStats();

        assertEquals(
                1,
                stats.getCurrentStreak()
        );

        assertNotNull(
                stats.getLastActivityDate()
        );
    }

    @Test
    void repeatedTaskCompletionShouldNotIncreaseStreak()
            throws SQLException {

        insertCompletedTasks(2);

        gamificationService.rewardTaskCompletion(1);

        int firstStreak =
                gamificationService.getStats()
                        .getCurrentStreak();

        gamificationService.rewardTaskCompletion(1);

        int secondStreak =
                gamificationService.getStats()
                        .getCurrentStreak();

        assertEquals(
                firstStreak,
                secondStreak
        );
    }

    @Test
    void invalidTaskIdShouldBeRejected()
            throws SQLException {

        assertThrows(
                IllegalArgumentException.class,
                () -> gamificationService
                        .rewardTaskCompletion(0)
        );
    }

    @Test
    void invalidExamIdShouldBeRejected()
            throws SQLException {

        assertThrows(
                IllegalArgumentException.class,
                () -> gamificationService
                        .rewardExamCompletion(0)
        );
    }

    @Test
    void invalidFocusSessionIdShouldBeRejected()
            throws SQLException {

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
                        .rewardAchievementUnlock(999)
        );
    }

    @Test
    void hasRewardShouldDetectExistingReward()
            throws SQLException {

        gamificationService.rewardTaskCompletion(1);

        assertTrue(
                gamificationService.hasReward(
                        "TASK",
                        1,
                        "COMPLETION"
                )
        );

        assertFalse(
                gamificationService.hasReward(
                        "TASK",
                        2,
                        "COMPLETION"
                )
        );
    }

    private Achievement findAchievement(
            String name
    ) throws SQLException {

        return achievementService
                .getAllAchievements()
                .stream()
                .filter(achievement ->
                        achievement.getName()
                                .equalsIgnoreCase(name)
                )
                .findFirst()
                .orElse(null);
    }

    private void insertCompletedTasks(
            int count
    ) throws SQLException {

        try (Connection connection =
                     DatabaseConnection.connect()) {

            for (int i = 1; i <= count; i++) {

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     """
                                      INSERT INTO tasks
                                     (
                                          title,
                                          priority,
                                          completed,
                                          completed_at
                                     )
                                      VALUES (?, 3, 1, ?)
                                     """
                             )) {

                    statement.setString(
                            1,
                            "Completed Task " + i
                    );

                    statement.setString(
                            2,
                            java.time.LocalDateTime
                                    .now()
                                    .toString()
                    );

                    statement.executeUpdate();
                }
            }
        }
    }

    private void insertCompletedExams(
            int count
    ) throws SQLException {

        try (Connection connection =
                     DatabaseConnection.connect()) {

            int semesterId;

            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 """
                                  INSERT INTO semesters
                                  (name)
                                  VALUES (?)
                                 """,
                                 java.sql.Statement
                                         .RETURN_GENERATED_KEYS
                         )) {

                statement.setString(
                        1,
                        "Test Semester"
                );

                statement.executeUpdate();

                try (var keys =
                             statement.getGeneratedKeys()) {

                    keys.next();

                    semesterId =
                            keys.getInt(1);
                }
            }

            int subjectId;

            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 """
                                  INSERT INTO subjects
                                 (
                                      semester_id,
                                      name,
                                      color
                                 )
                                  VALUES (?, ?, ?)
                                 """,
                                 java.sql.Statement
                                         .RETURN_GENERATED_KEYS
                         )) {

                statement.setInt(
                        1,
                        semesterId
                );

                statement.setString(
                        2,
                        "Test Subject"
                );

                statement.setString(
                        3,
                        "#FFFFFF"
                );

                statement.executeUpdate();

                try (var keys =
                             statement.getGeneratedKeys()) {

                    keys.next();

                    subjectId =
                            keys.getInt(1);
                }
            }

            for (int i = 1; i <= count; i++) {

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     """
                                      INSERT INTO exams
                                     (
                                          title,
                                          subject_id,
                                          exam_date,
                                          completed,
                                          completed_at
                                     )
                                      VALUES (?, ?, ?, 1, ?)
                                     """
                             )) {

                    statement.setString(
                            1,
                            "Completed Exam " + i
                    );

                    statement.setInt(
                            2,
                            subjectId
                    );

                    statement.setString(
                            3,
                            "2026-09-13"
                    );

                    statement.setString(
                            4,
                            java.time.LocalDateTime
                                    .now()
                                    .toString()
                    );

                    statement.executeUpdate();
                }
            }
        }
    }

    private void insertCompletedFocusSessions(
            int count
    ) throws SQLException {

        try (Connection connection =
                     DatabaseConnection.connect()) {

            for (int i = 1; i <= count; i++) {

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     """
                                      INSERT INTO focus_sessions
                                     (
                                          started_at,
                                          ended_at,
                                          planned_minutes,
                                          actual_minutes,
                                          completed
                                     )
                                      VALUES (?, ?, ?, ?, 1)
                                     """
                             )) {

                    String now =
                            java.time.LocalDateTime
                                    .now()
                                    .toString();

                    statement.setString(
                            1,
                            now
                    );

                    statement.setString(
                            2,
                            now
                    );

                    statement.setInt(
                            3,
                            25
                    );

                    statement.setInt(
                            4,
                            25
                    );

                    statement.executeUpdate();
                }
            }
        }
    }

    private void clearTable(
            String tableName
    ) throws SQLException {

        try (Connection connection =
                     DatabaseConnection.connect();
             PreparedStatement statement =
                     connection.prepareStatement(
                             "DELETE FROM " + tableName
                     )) {

            statement.executeUpdate();
        }
    }
}