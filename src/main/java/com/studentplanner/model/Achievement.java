package com.studentplanner.model;

public class Achievement {

    private int id;
    private String name;
    private String description;
    private int xpReward;
    private boolean unlocked;
    private String unlockedAt;

    public Achievement(
            String name,
            String description,
            int xpReward
    ) {
        this.name = name;
        this.description = description;
        this.xpReward = xpReward;
        this.unlocked = false;
    }

    public Achievement(
            int id,
            String name,
            String description,
            int xpReward,
            boolean unlocked,
            String unlockedAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.xpReward = xpReward;
        this.unlocked = unlocked;
        this.unlockedAt = unlockedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getXpReward() {
        return xpReward;
    }

    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public String getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(String unlockedAt) {
        this.unlockedAt = unlockedAt;
    }

    @Override
    public String toString() {
        return name;
    }
}