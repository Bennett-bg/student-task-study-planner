package com.studentplanner.service;

import com.studentplanner.model.Achievement;
import com.studentplanner.model.GamificationReward;
import com.studentplanner.model.UserStats;
import com.studentplanner.repository.ExamRepository;
import com.studentplanner.repository.FocusSessionRepository;
import com.studentplanner.repository.GamificationRewardRepository;
import com.studentplanner.repository.TaskRepository;

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

    private static final String FIRST_STEP = "First Step";
    private static final String GETTING_SERIOUS = "Getting Serious";
    private static final String TASK_MACHINE = "Task Machine";

    private static final String FOCUSED = "Focused";
    private static final String DEEP_WORK = "Deep Work";
    private static final String LOCKED_IN = "Locked In";

    private static final String ON_A_ROLL = "On a Roll";
    private static final String DEDICATED = "Dedicated";
    private static final String UNSTOPPABLE = "Unstoppable";

    private static final String EXAM_READY = "Exam Ready";
    private static final String ACADEMIC_GRINDER = "Academic Grinder";

    private static final String MONOLITH_VETERAN = "Monolith Veteran";

    private final GamificationRewardRepository rewardRepository;
    private final UserStatsService userStatsService;
    private final AchievementService achievementService;

    private final TaskRepository taskRepository;
    private final ExamRepository examRepository;
    private final FocusSessionRepository focusSessionRepository;

    public GamificationService() {

        this.rewardRepository =
                new GamificationRewardRepository();

        this.userStatsService =
                new UserStatsService();

        this.achievementService =
                new AchievementService();

        this.taskRepository =
                new TaskRepository();

        this.examRepository =
                new ExamRepository();

        this.focusSessionRepository =
                new FocusSessionRepository();
    }

    public GamificationService(
            GamificationRewardRepository rewardRepository,
            UserStatsService userStatsService,
            AchievementService achievementService,
            TaskRepository taskRepository,
            ExamRepository examRepository,
            FocusSessionRepository focusSessionRepository
    ) {

        this.rewardRepository = rewardRepository;
        this.userStatsService = userStatsService;
        this.achievementService = achievementService;

        this.taskRepository = taskRepository;
        this.examRepository = examRepository;
        this.focusSessionRepository = focusSessionRepository;
    }

    public UserStats rewardTaskCompletion(
            int taskId
    ) throws SQLException {

        validateSourceId(taskId, "Task");

        awardCompletionReward(
                TASK_SOURCE,
                taskId,
                TASK_XP
        );

        checkAchievements();

        return userStatsService.getOrCreateStats();
    }

    public UserStats rewardExamCompletion(
            int examId
    ) throws SQLException {

        validateSourceId(examId, "Exam");

        awardCompletionReward(
                EXAM_SOURCE,
                examId,
                EXAM_XP
        );

        checkAchievements();

        return userStatsService.getOrCreateStats();
    }

    public UserStats rewardFocusSessionCompletion(
            int focusSessionId
    ) throws SQLException {

        validateSourceId(
                focusSessionId,
                "Focus session"
        );

        awardCompletionReward(
                FOCUS_SOURCE,
                focusSessionId,
                FOCUS_XP
        );

        checkAchievements();

        return userStatsService.getOrCreateStats();
    }

    public UserStats rewardAchievementUnlock(
            int achievementId
    ) throws SQLException {

        validateSourceId(
                achievementId,
                "Achievement"
        );

        Achievement achievement =
                achievementService.getAchievement(
                        achievementId
                );

        if (achievement == null) {
            throw new IllegalArgumentException(
                    "Achievement not found."
            );
        }

        if (!achievement.isUnlocked()) {

            achievement =
                    achievementService.unlockAchievement(
                            achievementId
                    );
        }

        if (rewardRepository.exists(
                ACHIEVEMENT_SOURCE,
                achievementId,
                UNLOCK_REWARD
        )) {

            return userStatsService.getOrCreateStats();
        }

        int xpReward =
                achievement.getXpReward();

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

        UserStats stats =
                userStatsService.recordActivity(
                        activityDate
                );

        checkAchievements();

        return stats;
    }

    public UserStats recordTodayProductiveActivity()
            throws SQLException {

        UserStats stats =
                userStatsService.recordTodayActivity();

        checkAchievements();

        return stats;
    }

    public UserStats getStats()
            throws SQLException {

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

    public void checkAchievements()
            throws SQLException {

        /*
         * Make sure the standard Monolith achievements
         * exist before checking whether they should unlock.
         *
         * This is safe because AchievementService only
         * creates an achievement if it does not already exist.
         */
        achievementService.initializeDefaultAchievements();

        UserStats stats =
                userStatsService.getOrCreateStats();

        int completedTasks =
                (int) taskRepository.findAll()
                        .stream()
                        .filter(task -> task.isCompleted())
                        .count();

        int completedExams =
                (int) examRepository.findAll()
                        .stream()
                        .filter(exam -> exam.isCompleted())
                        .count();

        int completedFocusSessions =
                (int) focusSessionRepository.findAll()
                        .stream()
                        .filter(session -> session.isCompleted())
                        .count();

        checkTaskAchievements(
                completedTasks
        );

        checkFocusAchievements(
                completedFocusSessions
        );

        checkStreakAchievements(
                stats.getCurrentStreak()
        );

        checkExamAchievements(
                completedExams
        );

        checkLevelAchievements(
                stats.getLevel()
        );
    }

    private void checkTaskAchievements(
            int completedTasks
    ) throws SQLException {

        if (completedTasks >= 1) {
            unlockAchievementByName(
                    FIRST_STEP
            );
        }

        if (completedTasks >= 10) {
            unlockAchievementByName(
                    GETTING_SERIOUS
            );
        }

        if (completedTasks >= 50) {
            unlockAchievementByName(
                    TASK_MACHINE
            );
        }
    }

    private void checkFocusAchievements(
            int completedFocusSessions
    ) throws SQLException {

        if (completedFocusSessions >= 5) {
            unlockAchievementByName(
                    FOCUSED
            );
        }

        if (completedFocusSessions >= 25) {
            unlockAchievementByName(
                    DEEP_WORK
            );
        }

        if (completedFocusSessions >= 100) {
            unlockAchievementByName(
                    LOCKED_IN
            );
        }
    }

    private void checkStreakAchievements(
            int currentStreak
    ) throws SQLException {

        if (currentStreak >= 3) {
            unlockAchievementByName(
                    ON_A_ROLL
            );
        }

        if (currentStreak >= 7) {
            unlockAchievementByName(
                    DEDICATED
            );
        }

        if (currentStreak >= 30) {
            unlockAchievementByName(
                    UNSTOPPABLE
            );
        }
    }

    private void checkExamAchievements(
            int completedExams
    ) throws SQLException {

        if (completedExams >= 1) {
            unlockAchievementByName(
                    EXAM_READY
            );
        }

        if (completedExams >= 5) {
            unlockAchievementByName(
                    ACADEMIC_GRINDER
            );
        }
    }

    private void checkLevelAchievements(
            int level
    ) throws SQLException {

        if (level >= 10) {
            unlockAchievementByName(
                    MONOLITH_VETERAN
            );
        }
    }

    private void unlockAchievementByName(
            String achievementName
    ) throws SQLException {

        Achievement achievement =
                findAchievementByName(
                        achievementName
                );

        if (achievement == null) {
            return;
        }

        if (!achievement.isUnlocked()) {

            rewardAchievementUnlock(
                    achievement.getId()
            );

        } else if (!rewardRepository.exists(
                ACHIEVEMENT_SOURCE,
                achievement.getId(),
                UNLOCK_REWARD
        )) {

            /*
             * Recovery case:
             * achievement is already unlocked, but its
             * XP reward was never recorded.
             */
            rewardAchievementUnlock(
                    achievement.getId()
            );
        }
    }

    private Achievement findAchievementByName(
            String name
    ) throws SQLException {

        return achievementService
                .getAllAchievements()
                .stream()
                .filter(achievement ->
                        achievement.getName()
                                .equalsIgnoreCase(name)
                )
                .findFirst()
                .orElse(null);
    }

    private UserStats awardCompletionReward(
            String sourceType,
            int sourceId,
            int xpAmount
    ) throws SQLException {

        /*
         * DB-backed idempotency:
         * the same source cannot receive the same
         * completion reward twice.
         */
        if (rewardRepository.exists(
                sourceType,
                sourceId,
                COMPLETION_REWARD
        )) {

            return userStatsService.getOrCreateStats();
        }

        UserStats stats =
                userStatsService.addXp(
                        xpAmount
                );

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
                    sourceName
                            + " ID must be greater than zero."
            );
        }
    }

    private void validateRewardSource(
            String sourceType
    ) {

        if (sourceType == null
                || sourceType.isBlank()) {

            throw new IllegalArgumentException(
                    "Reward source type cannot be empty."
            );
        }
    }

    private void validateRewardType(
            String rewardType
    ) {

        if (rewardType == null
                || rewardType.isBlank()) {

            throw new IllegalArgumentException(
                    "Reward type cannot be empty."
            );
        }
    }
}