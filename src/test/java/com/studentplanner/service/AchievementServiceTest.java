package com.studentplanner.service;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.model.Achievement;
import com.studentplanner.repository.AchievementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AchievementServiceTest {

    private AchievementService service;
    private AchievementRepository repository;

    @BeforeEach
    void setUp() throws Exception {

        service = new AchievementService();
        repository = new AchievementRepository();

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM achievements");
        }
    }

    @Test
    void createAchievementShouldCreateAchievement()
            throws Exception {

        Achievement achievement = service.createAchievement(
                "First Step",
                "Complete your first task.",
                25
        );

        assertNotNull(achievement);
        assertTrue(achievement.getId() > 0);
        assertEquals("First Step", achievement.getName());
        assertEquals(
                "Complete your first task.",
                achievement.getDescription()
        );
        assertEquals(25, achievement.getXpReward());
        assertFalse(achievement.isUnlocked());
        assertNull(achievement.getUnlockedAt());
    }

    @Test
    void createAchievementShouldTrimNameAndDescription()
            throws Exception {

        Achievement achievement = service.createAchievement(
                "  First Step  ",
                "  Complete your first task.  ",
                25
        );

        assertEquals("First Step", achievement.getName());
        assertEquals(
                "Complete your first task.",
                achievement.getDescription()
        );
    }

    @Test
    void createAchievementShouldRejectEmptyName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createAchievement(
                        "",
                        "Description",
                        25
                )
        );
    }

    @Test
    void createAchievementShouldRejectBlankName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createAchievement(
                        "   ",
                        "Description",
                        25
                )
        );
    }

    @Test
    void createAchievementShouldRejectNullName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createAchievement(
                        null,
                        "Description",
                        25
                )
        );
    }

    @Test
    void createAchievementShouldRejectEmptyDescription() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createAchievement(
                        "First Step",
                        "",
                        25
                )
        );
    }

    @Test
    void createAchievementShouldRejectBlankDescription() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createAchievement(
                        "First Step",
                        "   ",
                        25
                )
        );
    }

    @Test
    void createAchievementShouldRejectNullDescription() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createAchievement(
                        "First Step",
                        null,
                        25
                )
        );
    }

    @Test
    void createAchievementShouldRejectNegativeXpReward() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createAchievement(
                        "First Step",
                        "Description",
                        -1
                )
        );
    }

    @Test
    void zeroXpRewardShouldBeAllowed()
            throws Exception {

        Achievement achievement = service.createAchievement(
                "Free Achievement",
                "No XP reward.",
                0
        );

        assertEquals(0, achievement.getXpReward());
    }

    @Test
    void getAchievementShouldReturnAchievement()
            throws Exception {

        Achievement created = service.createAchievement(
                "First Step",
                "Complete your first task.",
                25
        );

        Achievement found =
                service.getAchievement(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("First Step", found.getName());
    }

    @Test
    void getAchievementShouldReturnNullForMissingId()
            throws Exception {

        assertNull(service.getAchievement(999999));
    }

    @Test
    void getAllAchievementsShouldReturnAllAchievements()
            throws Exception {

        service.createAchievement(
                "First Step",
                "Complete your first task.",
                25
        );

        service.createAchievement(
                "Focused",
                "Complete five focus sessions.",
                50
        );

        List<Achievement> achievements =
                service.getAllAchievements();

        assertEquals(2, achievements.size());
        assertEquals("First Step", achievements.get(0).getName());
        assertEquals("Focused", achievements.get(1).getName());
    }

    @Test
    void unlockAchievementShouldUnlockAchievement()
            throws Exception {

        Achievement achievement = service.createAchievement(
                "First Step",
                "Complete your first task.",
                25
        );

        Achievement unlocked =
                service.unlockAchievement(achievement.getId());

        assertTrue(unlocked.isUnlocked());
        assertNotNull(unlocked.getUnlockedAt());

        Achievement saved =
                service.getAchievement(achievement.getId());

        assertNotNull(saved);
        assertTrue(saved.isUnlocked());
        assertNotNull(saved.getUnlockedAt());
    }

    @Test
    void unlockingAlreadyUnlockedAchievementShouldDoNothing()
            throws Exception {

        Achievement achievement = service.createAchievement(
                "First Step",
                "Complete your first task.",
                25
        );

        Achievement firstUnlock =
                service.unlockAchievement(achievement.getId());

        String firstUnlockedAt =
                firstUnlock.getUnlockedAt();

        Achievement secondUnlock =
                service.unlockAchievement(achievement.getId());

        assertTrue(secondUnlock.isUnlocked());
        assertEquals(
                firstUnlockedAt,
                secondUnlock.getUnlockedAt()
        );
    }

    @Test
    void unlockAchievementShouldRejectMissingAchievement() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.unlockAchievement(999999)
        );
    }

    @Test
    void isUnlockedShouldReturnFalseForLockedAchievement()
            throws Exception {

        Achievement achievement = service.createAchievement(
                "First Step",
                "Complete your first task.",
                25
        );

        assertFalse(
                service.isUnlocked(achievement.getId())
        );
    }

    @Test
    void isUnlockedShouldReturnTrueForUnlockedAchievement()
            throws Exception {

        Achievement achievement = service.createAchievement(
                "First Step",
                "Complete your first task.",
                25
        );

        service.unlockAchievement(achievement.getId());

        assertTrue(
                service.isUnlocked(achievement.getId())
        );
    }

    @Test
    void isUnlockedShouldReturnFalseForMissingAchievement()
            throws Exception {

        assertFalse(service.isUnlocked(999999));
    }

    @Test
    void getUnlockedAchievementsShouldReturnOnlyUnlocked()
            throws Exception {

        Achievement first = service.createAchievement(
                "First Step",
                "Complete your first task.",
                25
        );

        service.createAchievement(
                "Focused",
                "Complete five focus sessions.",
                50
        );

        service.unlockAchievement(first.getId());

        List<Achievement> unlocked =
                service.getUnlockedAchievements();

        assertEquals(1, unlocked.size());
        assertEquals(
                first.getId(),
                unlocked.get(0).getId()
        );
        assertTrue(unlocked.get(0).isUnlocked());
    }

    @Test
    void hasUnlockedAchievementShouldReturnTrueForUnlockedName()
            throws Exception {

        Achievement achievement = service.createAchievement(
                "First Step",
                "Complete your first task.",
                25
        );

        service.unlockAchievement(achievement.getId());

        assertTrue(
                service.hasUnlockedAchievement("First Step")
        );
    }

    @Test
    void hasUnlockedAchievementShouldBeCaseInsensitive()
            throws Exception {

        Achievement achievement = service.createAchievement(
                "First Step",
                "Complete your first task.",
                25
        );

        service.unlockAchievement(achievement.getId());

        assertTrue(
                service.hasUnlockedAchievement("first step")
        );
    }

    @Test
    void hasUnlockedAchievementShouldReturnFalseForLockedAchievement()
            throws Exception {

        service.createAchievement(
                "First Step",
                "Complete your first task.",
                25
        );

        assertFalse(
                service.hasUnlockedAchievement("First Step")
        );
    }

    @Test
    void hasUnlockedAchievementShouldReturnFalseForMissingName()
            throws Exception {

        assertFalse(
                service.hasUnlockedAchievement("Does Not Exist")
        );
    }

    @Test
    void hasUnlockedAchievementShouldReturnFalseForNullName()
            throws Exception {

        assertFalse(
                service.hasUnlockedAchievement(null)
        );
    }

    @Test
    void hasUnlockedAchievementShouldReturnFalseForBlankName()
            throws Exception {

        assertFalse(
                service.hasUnlockedAchievement("   ")
        );
    }

    @Test
    void repositoryShouldStillContainAchievementAfterUnlock()
            throws Exception {

        Achievement achievement = service.createAchievement(
                "Dedicated",
                "Maintain a seven day streak.",
                100
        );

        service.unlockAchievement(achievement.getId());

        Achievement saved =
                repository.findById(achievement.getId());

        assertNotNull(saved);
        assertEquals("Dedicated", saved.getName());
        assertTrue(saved.isUnlocked());
        assertEquals(100, saved.getXpReward());
    }
}