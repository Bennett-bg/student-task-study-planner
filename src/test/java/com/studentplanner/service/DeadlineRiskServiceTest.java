
package com.studentplanner.service;

import com.studentplanner.model.Task;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DeadlineRiskServiceTest {

    private final DeadlineRiskService service =
            new DeadlineRiskService();

    @Test
    void nullTaskShouldBeRejected() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateRisk(null)
        );
    }

    @Test
    void completedTaskShouldHaveNoRisk() {

        Task task = createTask(
                5,
                LocalDate.now(),
                120
        );

        task.setCompleted(true);

        assertEquals(
                DeadlineRiskService.RiskLevel.NONE,
                service.calculateRisk(task)
        );
    }

    @Test
    void completedTaskShouldHaveNoneRiskLabel() {

        Task task = createTask(
                5,
                LocalDate.now(),
                120
        );

        task.setCompleted(true);

        assertEquals(
                "NONE",
                service.getRiskLabel(task)
        );
    }

    @Test
    void completedTaskShouldHaveCompletedDescription() {

        Task task = createTask(
                5,
                LocalDate.now(),
                120
        );

        task.setCompleted(true);

        assertEquals(
                "This task has been completed.",
                service.getRiskDescription(task)
        );
    }

    @Test
    void overdueTaskShouldBeCritical() {

        Task task = createTask(
                3,
                LocalDate.now().minusDays(1),
                60
        );

        assertEquals(
                DeadlineRiskService.RiskLevel.CRITICAL,
                service.calculateRisk(task)
        );
    }

    @Test
    void overdueTaskShouldHaveCriticalLabel() {

        Task task = createTask(
                3,
                LocalDate.now().minusDays(1),
                60
        );

        assertEquals(
                "CRITICAL",
                service.getRiskLabel(task)
        );
    }

    @Test
    void overdueTaskShouldHaveOverdueDescription() {

        Task task = createTask(
                3,
                LocalDate.now().minusDays(2),
                60
        );

        assertEquals(
                "This task is overdue.",
                service.getRiskDescription(task)
        );
    }

    @Test
    void taskDueTodayShouldBeHighRisk() {

        Task task = createTask(
                3,
                LocalDate.now(),
                60
        );

        assertEquals(
                DeadlineRiskService.RiskLevel.HIGH,
                service.calculateRisk(task)
        );
    }

    @Test
    void taskDueTomorrowShouldBeHighRisk() {

        Task task = createTask(
                3,
                LocalDate.now().plusDays(1),
                60
        );

        assertEquals(
                DeadlineRiskService.RiskLevel.HIGH,
                service.calculateRisk(task)
        );
    }

    @Test
    void taskDueInTwoDaysShouldBeMediumRisk() {

        Task task = createTask(
                3,
                LocalDate.now().plusDays(2),
                60
        );

        assertEquals(
                DeadlineRiskService.RiskLevel.MEDIUM,
                service.calculateRisk(task)
        );
    }

    @Test
    void taskDueInThreeDaysShouldBeMediumRisk() {

        Task task = createTask(
                3,
                LocalDate.now().plusDays(3),
                60
        );

        assertEquals(
                DeadlineRiskService.RiskLevel.MEDIUM,
                service.calculateRisk(task)
        );
    }

    @Test
    void taskDueInFourDaysShouldBeLowRisk() {

        Task task = createTask(
                3,
                LocalDate.now().plusDays(4),
                60
        );

        assertEquals(
                DeadlineRiskService.RiskLevel.LOW,
                service.calculateRisk(task)
        );
    }

    @Test
    void taskDueInSevenDaysShouldBeLowRisk() {

        Task task = createTask(
                3,
                LocalDate.now().plusDays(7),
                60
        );

        assertEquals(
                DeadlineRiskService.RiskLevel.LOW,
                service.calculateRisk(task)
        );
    }

    @Test
    void taskWithoutDeadlineShouldBeLowRisk() {

        Task task = createTask(
                3,
                null,
                60
        );

        assertEquals(
                DeadlineRiskService.RiskLevel.LOW,
                service.calculateRisk(task)
        );
    }

    @Test
    void highPriorityTaskWithoutDeadlineShouldBeMediumRisk() {

        Task task = createTask(
                5,
                null,
                60
        );

        assertEquals(
                DeadlineRiskService.RiskLevel.MEDIUM,
                service.calculateRisk(task)
        );
    }

    @Test
    void invalidDeadlineShouldBeRejected() {

        Task task = createTask(
                3,
                null,
                60
        );

        task.setDueDate("not-a-date");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateRisk(task)
        );
    }

    @Test
    void taskScoreShouldBeAvailableThroughRiskService() {

        Task task = createTask(
                5,
                LocalDate.now().minusDays(1),
                60
        );

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
                service.getTaskScore(task)
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