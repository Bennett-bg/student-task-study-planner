package com.studentplanner.repository;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.model.Achievement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AchievementRepositoryTest {

    private AchievementRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        repository = new AchievementRepository();

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM achievements");
        }
    }

    @Test
    void saveAndFindByIdShouldWork() throws Exception {

        Achievement achievement = new Achievement(
                "First Focus",
                "Complete your first focus session.",
                50
        );

        repository.save(achievement);

        assertTrue(achievement.getId() > 0);

        Achievement saved = repository.findById(achievement.getId());

        assertNotNull(saved);
        assertEquals(achievement.getId(), saved.getId());
        assertEquals("First Focus", saved.getName());
        assertEquals("Complete your first focus session.", saved.getDescription());
        assertEquals(50, saved.getXpReward());
        assertFalse(saved.isUnlocked());
        assertNull(saved.getUnlockedAt());
    }

    @Test
    void findByIdShouldReturnNullWhenAchievementDoesNotExist() throws Exception {

        Achievement achievement = repository.findById(9999);

        assertNull(achievement);
    }

    @Test
    void findAllShouldReturnAchievements() throws Exception {

        repository.save(new Achievement(
                "First Focus",
                "Complete your first focus session.",
                50
        ));

        repository.save(new Achievement(
                "Task Master",
                "Complete 10 tasks.",
                100
        ));

        List<Achievement> achievements = repository.findAll();

        assertEquals(2, achievements.size());
        assertEquals("First Focus", achievements.get(0).getName());
        assertEquals("Task Master", achievements.get(1).getName());
    }

    @Test
    void findUnlockedShouldReturnOnlyUnlockedAchievements() throws Exception {

        Achievement locked = new Achievement(
                "First Focus",
                "Complete your first focus session.",
                50
        );

        Achievement unlocked = new Achievement(
                "Task Master",
                "Complete 10 tasks.",
                100
        );

        unlocked.setUnlocked(true);
        unlocked.setUnlockedAt("2026-09-10 18:00:00");

        repository.save(locked);
        repository.save(unlocked);

        List<Achievement> achievements = repository.findUnlocked();

        assertEquals(1, achievements.size());
        assertEquals("Task Master", achievements.get(0).getName());
        assertTrue(achievements.get(0).isUnlocked());
        assertEquals("2026-09-10 18:00:00", achievements.get(0).getUnlockedAt());
    }

    @Test
    void updateShouldWork() throws Exception {

        Achievement achievement = new Achievement(
                "First Focus",
                "Complete your first focus session.",
                50
        );

        repository.save(achievement);

        achievement.setName("Focus Starter");
        achievement.setDescription("Complete your first focused session.");
        achievement.setXpReward(75);
        achievement.setUnlocked(true);
        achievement.setUnlockedAt("2026-09-10 19:00:00");

        repository.update(achievement);

        Achievement updated = repository.findById(achievement.getId());

        assertNotNull(updated);
        assertEquals("Focus Starter", updated.getName());
        assertEquals(
                "Complete your first focused session.",
                updated.getDescription()
        );
        assertEquals(75, updated.getXpReward());
        assertTrue(updated.isUnlocked());
        assertEquals("2026-09-10 19:00:00", updated.getUnlockedAt());
    }

    @Test
    void deleteShouldWork() throws Exception {

        Achievement achievement = new Achievement(
                "First Focus",
                "Complete your first focus session.",
                50
        );

        repository.save(achievement);

        int id = achievement.getId();

        assertNotNull(repository.findById(id));

        repository.delete(id);

        assertNull(repository.findById(id));
    }

    @Test
    void duplicateAchievementNamesShouldNotBeAllowed() throws Exception {

        repository.save(new Achievement(
                "First Focus",
                "Complete your first focus session.",
                50
        ));

        assertThrows(Exception.class, () -> {
            repository.save(new Achievement(
                    "First Focus",
                    "Another description.",
                    100
            ));
        });
    }
}
