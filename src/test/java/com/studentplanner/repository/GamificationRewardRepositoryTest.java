package com.studentplanner.repository;

import com.studentplanner.DatabaseInitializer;
import com.studentplanner.model.GamificationReward;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GamificationRewardRepositoryTest {

    private GamificationRewardRepository repository;

    @BeforeAll
    static void initializeDatabase() throws Exception {
        DatabaseInitializer.initialize();
    }

    @BeforeEach
    void setUp() throws SQLException {
        repository = new GamificationRewardRepository();

        try (Connection connection =
                     com.studentplanner.DatabaseConnection.connect();
             var statement = connection.createStatement()) {

            statement.executeUpdate(
                    "DELETE FROM gamification_rewards"
            );
        }
    }

    @Test
    void shouldSaveReward() throws Exception {

        GamificationReward reward =
                new GamificationReward(
                        "TASK",
                        101,
                        "COMPLETION",
                        10,
                        LocalDateTime.now().toString()
                );

        GamificationReward saved =
                repository.save(reward);

        assertTrue(saved.getId() > 0);
        assertEquals("TASK", saved.getSourceType());
        assertEquals(101, saved.getSourceId());
        assertEquals("COMPLETION", saved.getRewardType());
        assertEquals(10, saved.getXpAmount());
    }

    @Test
    void shouldFindExistingReward() throws Exception {

        GamificationReward reward =
                new GamificationReward(
                        "TASK",
                        102,
                        "COMPLETION",
                        10,
                        LocalDateTime.now().toString()
                );

        repository.save(reward);

        GamificationReward found =
                repository.find(
                        "TASK",
                        102,
                        "COMPLETION"
                );

        assertNotNull(found);
        assertEquals(10, found.getXpAmount());
    }

    @Test
    void shouldReturnFalseWhenRewardDoesNotExist()
            throws Exception {

        assertFalse(
                repository.exists(
                        "TASK",
                        999,
                        "COMPLETION"
                )
        );
    }

    @Test
    void shouldReturnTrueWhenRewardExists()
            throws Exception {

        GamificationReward reward =
                new GamificationReward(
                        "TASK",
                        103,
                        "COMPLETION",
                        10,
                        LocalDateTime.now().toString()
                );

        repository.save(reward);

        assertTrue(
                repository.exists(
                        "TASK",
                        103,
                        "COMPLETION"
                )
        );
    }

    @Test
    void differentTasksCanHaveSeparateRewards()
            throws Exception {

        repository.save(
                new GamificationReward(
                        "TASK",
                        201,
                        "COMPLETION",
                        10,
                        LocalDateTime.now().toString()
                )
        );

        repository.save(
                new GamificationReward(
                        "TASK",
                        202,
                        "COMPLETION",
                        10,
                        LocalDateTime.now().toString()
                )
        );

        assertTrue(
                repository.exists(
                        "TASK",
                        201,
                        "COMPLETION"
                )
        );

        assertTrue(
                repository.exists(
                        "TASK",
                        202,
                        "COMPLETION"
                )
        );
    }

    @Test
    void sameTaskAndRewardTypeShouldBeUnique()
            throws Exception {

        GamificationReward first =
                new GamificationReward(
                        "TASK",
                        301,
                        "COMPLETION",
                        10,
                        LocalDateTime.now().toString()
                );

        GamificationReward second =
                new GamificationReward(
                        "TASK",
                        301,
                        "COMPLETION",
                        10,
                        LocalDateTime.now().toString()
                );

        repository.save(first);

        assertThrows(
                SQLException.class,
                () -> repository.save(second)
        );
    }

    @Test
    void sameSourceCanHaveDifferentRewardTypes()
            throws Exception {

        repository.save(
                new GamificationReward(
                        "TASK",
                        401,
                        "COMPLETION",
                        10,
                        LocalDateTime.now().toString()
                )
        );

        repository.save(
                new GamificationReward(
                        "TASK",
                        401,
                        "BONUS",
                        20,
                        LocalDateTime.now().toString()
                )
        );

        assertTrue(
                repository.exists(
                        "TASK",
                        401,
                        "COMPLETION"
                )
        );

        assertTrue(
                repository.exists(
                        "TASK",
                        401,
                        "BONUS"
                )
        );
    }
}