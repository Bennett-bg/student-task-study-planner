package com.studentplanner.model;

public class UserStats {

    private int id;
    private int xp;
    private int level;
    private int currentStreak;
    private int longestStreak;
    private String lastActivityDate;

    public UserStats(
            int xp,
            int level,
            int currentStreak,
            int longestStreak,
            String lastActivityDate
    ) {
        this.xp = xp;
        this.level = level;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.lastActivityDate = lastActivityDate;
    }

    public UserStats(
            int id,
            int xp,
            int level,
            int currentStreak,
            int longestStreak,
            String lastActivityDate
    ) {
        this.id = id;
        this.xp = xp;
        this.level = level;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.lastActivityDate = lastActivityDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public String getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(String lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    @Override
    public String toString() {
        return "Level " + level + " — " + xp + " XP";
    }
}