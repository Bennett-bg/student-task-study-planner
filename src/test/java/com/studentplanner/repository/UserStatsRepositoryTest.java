package com.studentplanner.repository;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.model.UserStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class UserStatsRepositoryTest {

    private UserStatsRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        repository = new UserStatsRepository();

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM user_stats");
        }
    }

    @Test
    void saveAndGetShouldWork() throws Exception {

        UserStats stats = new UserStats(
                100,
                2,
                3,
                5,
                "2026-09-10"
        );

        repository.save(stats);

        UserStats saved = repository.get();

        assertNotNull(saved);
        assertEquals(1, saved.getId());
        assertEquals(100, saved.getXp());
        assertEquals(2, saved.getLevel());
        assertEquals(3, saved.getCurrentStreak());
        assertEquals(5, saved.getLongestStreak());
        assertEquals("2026-09-10", saved.getLastActivityDate());
    }

    @Test
    void getShouldReturnNullWhenStatsDoNotExist() throws Exception {

        UserStats stats = repository.get();

        assertNull(stats);
    }

    @Test
    void updateShouldWork() throws Exception {

        UserStats stats = new UserStats(
                100,
                2,
                3,
                5,
                "2026-09-10"
        );

        repository.save(stats);

        stats.setXp(250);
        stats.setLevel(3);
        stats.setCurrentStreak(7);
        stats.setLongestStreak(10);
        stats.setLastActivityDate("2026-09-11");

        repository.update(stats);

        UserStats updated = repository.get();

        assertNotNull(updated);
        assertEquals(250, updated.getXp());
        assertEquals(3, updated.getLevel());
        assertEquals(7, updated.getCurrentStreak());
        assertEquals(10, updated.getLongestStreak());
        assertEquals("2026-09-11", updated.getLastActivityDate());
    }

    @Test
    void onlySingletonRowShouldBeAllowed() throws Exception {

        UserStats first = new UserStats(
                50,
                1,
                1,
                1,
                "2026-09-10"
        );

        repository.save(first);

        assertThrows(Exception.class, () -> {
            UserStats second = new UserStats(
                    100,
                    2,
                    2,
                    2,
                    "2026-09-11"
            );

            repository.save(second);
        });
    }
}
