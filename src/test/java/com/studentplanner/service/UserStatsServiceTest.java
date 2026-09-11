package com.studentplanner.service;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.model.UserStats;
import com.studentplanner.repository.UserStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserStatsServiceTest {

    private UserStatsService service;
    private UserStatsRepository repository;

    @BeforeEach
    void setUp() throws Exception {

        service = new UserStatsService();
        repository = new UserStatsRepository();

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM user_stats");
        }
    }

    @Test
    void getStatsShouldReturnNullWhenNoStatsExist()
            throws Exception {

        assertNull(service.getStats());
    }

    @Test
    void getOrCreateStatsShouldCreateDefaultStats()
            throws Exception {

        UserStats stats = service.getOrCreateStats();

        assertNotNull(stats);
        assertEquals(1, stats.getId());
        assertEquals(0, stats.getXp());
        assertEquals(1, stats.getLevel());
        assertEquals(0, stats.getCurrentStreak());
        assertEquals(0, stats.getLongestStreak());
        assertNull(stats.getLastActivityDate());
    }

    @Test
    void getOrCreateStatsShouldReturnExistingStats()
            throws Exception {

        UserStats first = service.getOrCreateStats();
        UserStats second = service.getOrCreateStats();

        assertEquals(first.getId(), second.getId());
        assertEquals(first.getXp(), second.getXp());
        assertEquals(first.getLevel(), second.getLevel());
    }

    @Test
    void calculateLevelShouldStartAtLevelOne() {

        assertEquals(1, service.calculateLevel(0));
        assertEquals(1, service.calculateLevel(50));
        assertEquals(1, service.calculateLevel(99));
    }

    @Test
    void calculateLevelShouldIncreaseEvery100Xp() {

        assertEquals(2, service.calculateLevel(100));
        assertEquals(2, service.calculateLevel(199));
        assertEquals(3, service.calculateLevel(200));
        assertEquals(4, service.calculateLevel(300));
    }

    @Test
    void calculateLevelShouldRejectNegativeXp() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateLevel(-1)
        );
    }

    @Test
    void addXpShouldCreateStatsAndAddXp()
            throws Exception {

        UserStats stats = service.addXp(25);

        assertNotNull(stats);
        assertEquals(25, stats.getXp());
        assertEquals(1, stats.getLevel());

        UserStats saved = service.getStats();

        assertNotNull(saved);
        assertEquals(25, saved.getXp());
        assertEquals(1, saved.getLevel());
    }

    @Test
    void addXpShouldIncreaseExistingXp()
            throws Exception {

        service.addXp(40);
        UserStats stats = service.addXp(30);

        assertEquals(70, stats.getXp());
        assertEquals(1, stats.getLevel());
    }

    @Test
    void addXpShouldLevelUpAt100Xp()
            throws Exception {

        UserStats stats = service.addXp(100);

        assertEquals(100, stats.getXp());
        assertEquals(2, stats.getLevel());
    }

    @Test
    void addXpShouldHandleMultipleLevels()
            throws Exception {

        UserStats stats = service.addXp(350);

        assertEquals(350, stats.getXp());
        assertEquals(4, stats.getLevel());
    }

    @Test
    void addXpShouldRejectZero() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addXp(0)
        );
    }

    @Test
    void addXpShouldRejectNegativeAmount() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addXp(-10)
        );
    }

    @Test
    void recordFirstActivityShouldStartStreak()
            throws Exception {

        LocalDate date = LocalDate.of(2026, 9, 1);

        UserStats stats = service.recordActivity(date);

        assertEquals(1, stats.getCurrentStreak());
        assertEquals(1, stats.getLongestStreak());
        assertEquals(date.toString(), stats.getLastActivityDate());
    }

    @Test
    void consecutiveActivityShouldIncreaseStreak()
            throws Exception {

        LocalDate day1 = LocalDate.of(2026, 9, 1);
        LocalDate day2 = LocalDate.of(2026, 9, 2);
        LocalDate day3 = LocalDate.of(2026, 9, 3);

        service.recordActivity(day1);
        service.recordActivity(day2);
        UserStats stats = service.recordActivity(day3);

        assertEquals(3, stats.getCurrentStreak());
        assertEquals(3, stats.getLongestStreak());
        assertEquals(day3.toString(), stats.getLastActivityDate());
    }

    @Test
    void brokenStreakShouldResetToOne()
            throws Exception {

        LocalDate day1 = LocalDate.of(2026, 9, 1);
        LocalDate day2 = LocalDate.of(2026, 9, 2);
        LocalDate day4 = LocalDate.of(2026, 9, 4);

        service.recordActivity(day1);
        service.recordActivity(day2);

        UserStats stats = service.recordActivity(day4);

        assertEquals(1, stats.getCurrentStreak());
        assertEquals(2, stats.getLongestStreak());
        assertEquals(day4.toString(), stats.getLastActivityDate());
    }

    @Test
    void longestStreakShouldBePreservedAfterBreak()
            throws Exception {

        LocalDate day1 = LocalDate.of(2026, 9, 1);
        LocalDate day2 = LocalDate.of(2026, 9, 2);
        LocalDate day3 = LocalDate.of(2026, 9, 3);
        LocalDate day5 = LocalDate.of(2026, 9, 5);
        LocalDate day6 = LocalDate.of(2026, 9, 6);

        service.recordActivity(day1);
        service.recordActivity(day2);
        service.recordActivity(day3);
        service.recordActivity(day5);
        UserStats stats = service.recordActivity(day6);

        assertEquals(2, stats.getCurrentStreak());
        assertEquals(3, stats.getLongestStreak());
    }

    @Test
    void duplicateActivityOnSameDayShouldNotIncreaseStreak()
            throws Exception {

        LocalDate date = LocalDate.of(2026, 9, 1);

        UserStats first = service.recordActivity(date);
        UserStats second = service.recordActivity(date);

        assertEquals(1, first.getCurrentStreak());
        assertEquals(1, second.getCurrentStreak());
        assertEquals(1, second.getLongestStreak());
        assertEquals(date.toString(), second.getLastActivityDate());
    }

    @Test
    void earlierActivityShouldNotChangeCurrentStreak()
            throws Exception {

        LocalDate day1 = LocalDate.of(2026, 9, 1);
        LocalDate day2 = LocalDate.of(2026, 9, 2);
        LocalDate earlier = LocalDate.of(2026, 8, 30);

        service.recordActivity(day1);
        UserStats before = service.recordActivity(day2);

        UserStats after = service.recordActivity(earlier);

        assertEquals(2, before.getCurrentStreak());
        assertEquals(2, after.getCurrentStreak());
        assertEquals(day2.toString(), after.getLastActivityDate());
    }

    @Test
    void recordActivityShouldRejectNullDate() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.recordActivity(null)
        );
    }

    @Test
    void getXpForCurrentLevelShouldReturnProgress()
            throws Exception {

        assertEquals(0, service.getXpForCurrentLevel(0));
        assertEquals(25, service.getXpForCurrentLevel(25));
        assertEquals(99, service.getXpForCurrentLevel(199));
        assertEquals(50, service.getXpForCurrentLevel(250));
    }

    @Test
    void getXpForCurrentLevelShouldRejectNegativeXp() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getXpForCurrentLevel(-1)
        );
    }

    @Test
    void getXpNeededForNextLevelShouldReturnRemainingXp() {

        assertEquals(100, service.getXpNeededForNextLevel(0));
        assertEquals(75, service.getXpNeededForNextLevel(25));
        assertEquals(1, service.getXpNeededForNextLevel(99));
        assertEquals(50, service.getXpNeededForNextLevel(250));
    }

    @Test
    void getXpNeededForNextLevelShouldRejectNegativeXp() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getXpNeededForNextLevel(-1)
        );
    }

    @Test
    void recordActivityShouldPersistStats()
            throws Exception {

        LocalDate date = LocalDate.of(2026, 9, 10);

        service.recordActivity(date);

        UserStats saved = repository.get();

        assertNotNull(saved);
        assertEquals(1, saved.getCurrentStreak());
        assertEquals(1, saved.getLongestStreak());
        assertEquals(date.toString(), saved.getLastActivityDate());
    }
}