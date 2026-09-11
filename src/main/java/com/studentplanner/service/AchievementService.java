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

        Achievement achievement = new Achievement(
                name.trim(),
                description.trim(),
                xpReward
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
}