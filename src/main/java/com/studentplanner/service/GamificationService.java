package com.studentplanner.service;

import com.studentplanner.model.Achievement;
import com.studentplanner.model.GamificationReward;
import com.studentplanner.model.UserStats;
import com.studentplanner.repository.GamificationRewardRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GamificationService {

    private static final String TASK_SOURCE = "TASK";
    private static final String EXAM_SOURCE = "EXAM";
    private static final String FOCUS_SOURCE = "FOCUS";
    private static final String ACHIEVEMENT_SOURCE = "ACHIEVEMENT";

    private static final String COMPLETION_REWARD = "COMPLETION";
    private static final String UNLOCK_REWARD = "UNLOCK";

    private static final int TASK_XP = 10;
    private static final int EXAM_XP = 25;
    private static final int FOCUS_XP = 5;

    private final GamificationRewardRepository rewardRepository;
    private final UserStatsService userStatsService;
    private final AchievementService achievementService;

    public GamificationService() {
        this.rewardRepository = new GamificationRewardRepository();
        this.userStatsService = new UserStatsService();
        this.achievementService = new AchievementService();
    }

    public GamificationService(
            GamificationRewardRepository rewardRepository,
            UserStatsService userStatsService,
            AchievementService achievementService
    ) {
        this.rewardRepository = rewardRepository;
        this.userStatsService = userStatsService;
        this.achievementService = achievementService;
    }

    public UserStats rewardTaskCompletion(int taskId)
            throws SQLException {

        validateSourceId(taskId, "Task");

        return awardCompletionReward(
                TASK_SOURCE,
                taskId,
                TASK_XP
        );
    }

    public UserStats rewardExamCompletion(int examId)
            throws SQLException {

        validateSourceId(examId, "Exam");

        return awardCompletionReward(
                EXAM_SOURCE,
                examId,
                EXAM_XP
        );
    }

    public UserStats rewardFocusSessionCompletion(int focusSessionId)
            throws SQLException {

        validateSourceId(focusSessionId, "Focus session");

        return awardCompletionReward(
                FOCUS_SOURCE,
                focusSessionId,
                FOCUS_XP
        );
    }

    public UserStats rewardAchievementUnlock(int achievementId)
            throws SQLException {

        validateSourceId(achievementId, "Achievement");

        Achievement achievement =
                achievementService.getAchievement(achievementId);

        if (achievement == null) {
            throw new IllegalArgumentException(
                    "Achievement not found."
            );
        }

        if (!achievement.isUnlocked()) {
            achievement =
                    achievementService.unlockAchievement(achievementId);
        }

        if (rewardRepository.exists(
                ACHIEVEMENT_SOURCE,
                achievementId,
                UNLOCK_REWARD
        )) {
            return userStatsService.getOrCreateStats();
        }

        int xpReward = achievement.getXpReward();

        if (xpReward > 0) {
            userStatsService.addXp(xpReward);
        }

        saveReward(
                ACHIEVEMENT_SOURCE,
                achievementId,
                UNLOCK_REWARD,
                xpReward
        );

        return userStatsService.getOrCreateStats();
    }

    public UserStats recordProductiveActivity(
            LocalDate activityDate
    ) throws SQLException {

        if (activityDate == null) {
            throw new IllegalArgumentException(
                    "Activity date cannot be null."
            );
        }

        return userStatsService.recordActivity(activityDate);
    }

    public UserStats recordTodayProductiveActivity()
            throws SQLException {

        return userStatsService.recordTodayActivity();
    }

    public UserStats getStats() throws SQLException {
        return userStatsService.getOrCreateStats();
    }

    public boolean hasReward(
            String sourceType,
            int sourceId,
            String rewardType
    ) throws SQLException {

        validateRewardSource(sourceType);
        validateSourceId(sourceId, "Source");
        validateRewardType(rewardType);

        return rewardRepository.exists(
                sourceType,
                sourceId,
                rewardType
        );
    }

    private UserStats awardCompletionReward(
            String sourceType,
            int sourceId,
            int xpAmount
    ) throws SQLException {

        if (rewardRepository.exists(
                sourceType,
                sourceId,
                COMPLETION_REWARD
        )) {
            return userStatsService.getOrCreateStats();
        }

        UserStats stats =
                userStatsService.addXp(xpAmount);

        userStatsService.recordTodayActivity();

        saveReward(
                sourceType,
                sourceId,
                COMPLETION_REWARD,
                xpAmount
        );

        return stats;
    }

    private void saveReward(
            String sourceType,
            int sourceId,
            String rewardType,
            int xpAmount
    ) throws SQLException {

        GamificationReward reward =
                new GamificationReward(
                        sourceType,
                        sourceId,
                        rewardType,
                        xpAmount,
                        LocalDateTime.now().toString()
                );

        rewardRepository.save(reward);
    }

    private void validateSourceId(
            int sourceId,
            String sourceName
    ) {

        if (sourceId <= 0) {
            throw new IllegalArgumentException(
                    sourceName + " ID must be greater than zero."
            );
        }
    }

    private void validateRewardSource(
            String sourceType
    ) {

        if (sourceType == null || sourceType.isBlank()) {
            throw new IllegalArgumentException(
                    "Reward source type cannot be empty."
            );
        }
    }

    private void validateRewardType(
            String rewardType
    ) {

        if (rewardType == null || rewardType.isBlank()) {
            throw new IllegalArgumentException(
                    "Reward type cannot be empty."
            );
        }
    }
}