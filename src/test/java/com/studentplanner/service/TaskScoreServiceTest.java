
package com.studentplanner.service;

import com.studentplanner.model.Task;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TaskScoreServiceTest {

    private final TaskScoreService service =
            new TaskScoreService();

    @Test
    void nullTaskShouldBeRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateScore(null)
        );
    }

    @Test
    void completedTaskShouldHaveZeroScore() {

        Task task = createTask(
                5,
                LocalDate.now().plusDays(1),
                120
        );

        task.setCompleted(true);

        assertEquals(
                0,
                service.calculateScore(task)
        );
    }

    @Test
    void priorityScoreShouldBeCalculatedCorrectly() {

        assertEquals(
                4,
                service.calculatePriorityScore(1)
        );

        assertEquals(
                12,
                service.calculatePriorityScore(3)
        );

        assertEquals(
                20,
                service.calculatePriorityScore(5)
        );
    }

    @Test
    void priorityBelowOneShouldBeRejected() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.calculatePriorityScore(0)
        );
    }

    @Test
    void priorityAboveFiveShouldBeRejected() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.calculatePriorityScore(6)
        );
    }

    @Test
    void deadlineUrgencyShouldBeZeroWithoutDeadline() {

        assertEquals(
                0,
                service.calculateDeadlineUrgency(null)
        );

        assertEquals(
                0,
                service.calculateDeadlineUrgency("")
        );
    }

    @Test
    void deadlineMoreThanSevenDaysAwayShouldHaveLowestUrgency() {

        String date =
                LocalDate.now().plusDays(8).toString();

        assertEquals(
                1,
                service.calculateDeadlineUrgency(date)
        );
    }

    @Test
    void deadlineThreeToSevenDaysAwayShouldHaveUrgencyTwo() {

        String date =
                LocalDate.now().plusDays(5).toString();

        assertEquals(
                2,
                service.calculateDeadlineUrgency(date)
        );
    }

    @Test
    void deadlineOneToTwoDaysAwayShouldHaveUrgencyThree() {

        String date =
                LocalDate.now().plusDays(2).toString();

        assertEquals(
                3,
                service.calculateDeadlineUrgency(date)
        );
    }

    @Test
    void deadlineTodayShouldHaveMaximumUrgency() {

        String date =
                LocalDate.now().toString();

        assertEquals(
                5,
                service.calculateDeadlineUrgency(date)
        );
    }

    @Test
    void overdueDeadlineShouldHaveMaximumUrgency() {

        String date =
                LocalDate.now().minusDays(1).toString();

        assertEquals(
                5,
                service.calculateDeadlineUrgency(date)
        );
    }

    @Test
    void invalidDeadlineShouldBeRejected() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateDeadlineUrgency(
                        "not-a-date"
                )
        );
    }

    @Test
    void overdueTaskShouldBeDetected() {

        Task task = createTask(
                3,
                LocalDate.now().minusDays(1),
                60
        );

        assertTrue(
                service.isOverdue(task)
        );
    }

    @Test
    void futureTaskShouldNotBeOverdue() {

        Task task = createTask(
                3,
                LocalDate.now().plusDays(1),
                60
        );

        assertFalse(
                service.isOverdue(task)
        );
    }

    @Test
    void taskWithoutDeadlineShouldNotBeOverdue() {

        Task task = createTask(
                3,
                null,
                60
        );

        assertFalse(
                service.isOverdue(task)
        );
    }

    @Test
    void completedOverdueTaskShouldNotBeOverdue() {

        Task task = createTask(
                3,
                LocalDate.now().minusDays(1),
                60
        );

        task.setCompleted(true);

        assertFalse(
                service.isOverdue(task)
        );
    }

    @Test
    void nullTaskShouldBeRejectedWhenCheckingOverdue() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.isOverdue(null)
        );
    }

    @Test
    void remainingWorkScoreShouldBeZeroWithoutEstimate() {

        assertEquals(
                0,
                service.calculateRemainingWorkScore(null)
        );

        assertEquals(
                0,
                service.calculateRemainingWorkScore(0)
        );
    }

    @Test
    void remainingWorkUpToThirtyMinutesShouldScoreOne() {

        assertEquals(
                1,
                service.calculateRemainingWorkScore(30)
        );
    }

    @Test
    void remainingWorkUpToSixtyMinutesShouldScoreTwo() {

        assertEquals(
                2,
                service.calculateRemainingWorkScore(60)
        );
    }

    @Test
    void remainingWorkUpToOneTwentyMinutesShouldScoreThree() {

        assertEquals(
                3,
                service.calculateRemainingWorkScore(120)
        );
    }

    @Test
    void remainingWorkUpToTwoFortyMinutesShouldScoreFour() {

        assertEquals(
                4,
                service.calculateRemainingWorkScore(240)
        );
    }

    @Test
    void remainingWorkAboveTwoFortyMinutesShouldScoreFive() {

        assertEquals(
                5,
                service.calculateRemainingWorkScore(241)
        );
    }

    @Test
    void completeTaskScoreShouldUseAllComponents() {

        Task task = createTask(
                5,
                LocalDate.now().plusDays(1),
                121
        );

        int score = service.calculateScore(task);

        /*
         * Priority:
         * 5 × 4 = 20
         *
         * Deadline urgency:
         * 3 × 5 = 15
         *
         * Overdue:
         * 0 × 10 = 0
         *
         * Remaining work:
         * 4 × 2 = 8
         *
         * Total = 43
         */

        assertEquals(
                43,
                score
        );
    }

    @Test
    void overdueTaskShouldReceiveOverdueBonus() {

        Task task = createTask(
                5,
                LocalDate.now().minusDays(1),
                60
        );

        int score = service.calculateScore(task);

        /*
         * Priority:
         * 5 × 4 = 20
         *
         * Deadline urgency:
         * 5 × 5 = 25
         *
         * Overdue:
         * 10
         *
         * Remaining work:
         * 2 × 2 = 4
         *
         * Total = 59
         */

        assertEquals(
                59,
                score
        );
    }

    @Test
    void taskWithoutDeadlineOrEstimateShouldStillReceivePriorityScore() {

        Task task = createTask(
                4,
                null,
                null
        );

        int score = service.calculateScore(task);

        assertEquals(
                16,
                score
        );
    }

    private Task createTask(
            int priority,
            LocalDate dueDate,
            Integer estimatedMinutes
    ) {

        return new Task(
                "Test Task",
                "Test Description",
                null,
                priority,
                dueDate == null
                        ? null
                        : dueDate.toString(),
                null,
                estimatedMinutes,
                null
        );
    }
}

