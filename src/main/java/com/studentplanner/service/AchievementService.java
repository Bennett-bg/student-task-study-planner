package com.studentplanner.service;

import com.studentplanner.model.Achievement;
import com.studentplanner.repository.AchievementRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class AchievementService {

    private final AchievementRepository repository;

    public AchievementService() {
        this.repository = new AchievementRepository();
    }

    public AchievementService(AchievementRepository repository) {
        this.repository = repository;
    }

    public Achievement createAchievement(
            String name,
            String description,
            int xpReward
    ) throws SQLException {

        return createAchievement(
                name,
                description,
                xpReward,
                "DEFAULT"
        );
    }

    public Achievement createAchievement(
            String name,
            String description,
            int xpReward,
            String icon
    ) throws SQLException {

        validateAchievement(
                name,
                description,
                xpReward,
                icon
        );

        Achievement achievement = new Achievement(
                name.trim(),
                description.trim(),
                xpReward,
                icon.trim()
        );

        return repository.save(achievement);
    }

    public Achievement getAchievement(int id)
            throws SQLException {
        return repository.findById(id);
    }

    public List<Achievement> getAllAchievements()
            throws SQLException {
        return repository.findAll();
    }

    public List<Achievement> getUnlockedAchievements()
            throws SQLException {
        return repository.findUnlocked();
    }

    public Achievement unlockAchievement(int id)
            throws SQLException {

        Achievement achievement = repository.findById(id);

        if (achievement == null) {
            throw new IllegalArgumentException(
                    "Achievement not found."
            );
        }

        if (achievement.isUnlocked()) {
            return achievement;
        }

        achievement.setUnlocked(true);
        achievement.setUnlockedAt(
                LocalDateTime.now().toString()
        );

        repository.update(achievement);

        return achievement;
    }

    public boolean isUnlocked(int id)
            throws SQLException {

        Achievement achievement = repository.findById(id);

        return achievement != null && achievement.isUnlocked();
    }

    public boolean hasUnlockedAchievement(String name)
            throws SQLException {

        if (name == null || name.isBlank()) {
            return false;
        }

        return repository.findAll()
                .stream()
                .anyMatch(achievement ->
                        achievement.getName().equalsIgnoreCase(name)
                                && achievement.isUnlocked()
                );
    }

    /**
     * Creates Monolith's default achievement set if
     * those achievements do not already exist.
     *
     * This method is safe to call multiple times.
     */
    public void initializeDefaultAchievements()
            throws SQLException {

        List<Achievement> existingAchievements =
                repository.findAll();

        createIfMissing(
                existingAchievements,
                "First Step",
                "Complete your first task",
                25,
                "FIRST_STEP"
        );

        createIfMissing(
                existingAchievements,
                "Getting Serious",
                "Complete 10 tasks",
                50,
                "GETTING_SERIOUS"
        );

        createIfMissing(
                existingAchievements,
                "Task Machine",
                "Complete 50 tasks",
                100,
                "TASK_MACHINE"
        );

        createIfMissing(
                existingAchievements,
                "Focused",
                "Complete 5 focus sessions",
                50,
                "FOCUSED"
        );

        createIfMissing(
                existingAchievements,
                "Deep Work",
                "Complete 25 focus sessions",
                100,
                "DEEP_WORK"
        );

        createIfMissing(
                existingAchievements,
                "Locked In",
                "Complete 100 focus sessions",
                250,
                "LOCKED_IN"
        );

        createIfMissing(
                existingAchievements,
                "On a Roll",
                "Maintain a 3-day streak",
                50,
                "ON_A_ROLL"
        );

        createIfMissing(
                existingAchievements,
                "Dedicated",
                "Maintain a 7-day streak",
                100,
                "DEDICATED"
        );

        createIfMissing(
                existingAchievements,
                "Unstoppable",
                "Maintain a 30-day streak",
                250,
                "UNSTOPPABLE"
        );

        createIfMissing(
                existingAchievements,
                "Exam Ready",
                "Complete your first exam",
                50,
                "EXAM_READY"
        );

        createIfMissing(
                existingAchievements,
                "Academic Grinder",
                "Complete 5 exams",
                100,
                "ACADEMIC_GRINDER"
        );

        createIfMissing(
                existingAchievements,
                "Monolith Veteran",
                "Reach Level 10",
                250,
                "MONOLITH_VETERAN"
        );
    }

    private void createIfMissing(
            List<Achievement> existingAchievements,
            String name,
            String description,
            int xpReward,
            String icon
    ) throws SQLException {

        boolean alreadyExists = existingAchievements
                .stream()
                .anyMatch(achievement ->
                        achievement.getName()
                                .equalsIgnoreCase(name)
                );

        if (!alreadyExists) {
            Achievement achievement = createAchievement(
                    name,
                    description,
                    xpReward,
                    icon
            );

            existingAchievements.add(achievement);
        }
    }

    private void validateAchievement(
            String name,
            String description,
            int xpReward,
            String icon
    ) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Achievement name cannot be empty."
            );
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Achievement description cannot be empty."
            );
        }

        if (xpReward < 0) {
            throw new IllegalArgumentException(
                    "XP reward cannot be negative."
            );
        }

        if (icon == null || icon.isBlank()) {
            throw new IllegalArgumentException(
                    "Achievement icon cannot be empty."
            );
        }
    }
}