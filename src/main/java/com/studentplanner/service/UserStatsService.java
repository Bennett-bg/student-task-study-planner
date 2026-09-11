package com.studentplanner.service;

import com.studentplanner.model.UserStats;
import com.studentplanner.repository.UserStatsRepository;

import java.sql.SQLException;
import java.time.LocalDate;

public class UserStatsService {

    private static final int XP_PER_LEVEL = 100;

    private final UserStatsRepository repository;

    public UserStatsService() {
        this.repository = new UserStatsRepository();
    }

    public UserStatsService(UserStatsRepository repository) {
        this.repository = repository;
    }

    public UserStats getStats() throws SQLException {
        return repository.get();
    }

    public UserStats getOrCreateStats() throws SQLException {

        UserStats stats = repository.get();

        if (stats != null) {
            return stats;
        }

        UserStats newStats = new UserStats(
                0,
                1,
                0,
                0,
                null
        );

        return repository.save(newStats);
    }

    public UserStats addXp(int amount) throws SQLException {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "XP amount must be greater than zero."
            );
        }

        UserStats stats = getOrCreateStats();

        int newXp = stats.getXp() + amount;
        int newLevel = calculateLevel(newXp);

        stats.setXp(newXp);
        stats.setLevel(newLevel);

        repository.update(stats);

        return stats;
    }

    public int calculateLevel(int xp) {

        if (xp < 0) {
            throw new IllegalArgumentException(
                    "XP cannot be negative."
            );
        }

        return (xp / XP_PER_LEVEL) + 1;
    }

    public UserStats recordActivity(LocalDate activityDate)
            throws SQLException {

        if (activityDate == null) {
            throw new IllegalArgumentException(
                    "Activity date cannot be null."
            );
        }

        UserStats stats = getOrCreateStats();

        String lastActivityString = stats.getLastActivityDate();

        if (lastActivityString == null) {

            stats.setCurrentStreak(1);
            stats.setLongestStreak(
                    Math.max(stats.getLongestStreak(), 1)
            );

        } else {

            LocalDate lastActivity =
                    LocalDate.parse(lastActivityString);

            if (activityDate.equals(lastActivity)) {
                return stats;
            }

            if (activityDate.equals(lastActivity.plusDays(1))) {

                int newStreak =
                        stats.getCurrentStreak() + 1;

                stats.setCurrentStreak(newStreak);

                if (newStreak > stats.getLongestStreak()) {
                    stats.setLongestStreak(newStreak);
                }

            } else if (activityDate.isAfter(lastActivity)) {

                stats.setCurrentStreak(1);

            } else {

                return stats;
            }
        }

        stats.setLastActivityDate(activityDate.toString());

        repository.update(stats);

        return stats;
    }

    public UserStats recordTodayActivity() throws SQLException {
        return recordActivity(LocalDate.now());
    }

    public int getXpForCurrentLevel(int xp) {

        if (xp < 0) {
            throw new IllegalArgumentException(
                    "XP cannot be negative."
            );
        }

        return xp % XP_PER_LEVEL;
    }

    public int getXpNeededForNextLevel(int xp) {

        if (xp < 0) {
            throw new IllegalArgumentException(
                    "XP cannot be negative."
            );
        }

        int currentLevelXp = getXpForCurrentLevel(xp);

        return XP_PER_LEVEL - currentLevelXp;
    }
}