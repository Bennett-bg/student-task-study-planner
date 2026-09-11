package com.studentplanner.service;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.model.FocusSession;
import com.studentplanner.model.Task;
import com.studentplanner.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FocusSessionServiceTest {

    private FocusSessionService service;
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() throws Exception {
        service = new FocusSessionService();
        taskRepository = new TaskRepository();

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM focus_sessions");
            statement.executeUpdate("DELETE FROM tasks");
        }
    }

    private Task createTask(String title) throws Exception {
        Task task = new Task(
                title,
                null,
                null,
                3,
                null,
                null,
                30,
                null
        );

        return taskRepository.save(task);
    }

    @Test
    void startSessionShouldCreateSession() throws Exception {

        Task task = createTask("Test Task");

        FocusSession session = service.startSession(task.getId(), 25);

        assertNotNull(session);
        assertTrue(session.getId() > 0);
        assertEquals(task.getId(), session.getTaskId());
        assertEquals(25, session.getPlannedMinutes());
        assertFalse(session.isCompleted());
        assertNull(session.getEndedAt());
        assertNull(session.getActualMinutes());
    }

    @Test
    void startSessionWithoutTaskShouldWork() throws Exception {

        FocusSession session = service.startSession(null, 25);

        assertNotNull(session);
        assertNull(session.getTaskId());
        assertEquals(25, session.getPlannedMinutes());
        assertFalse(session.isCompleted());
    }

    @Test
    void startSessionShouldRejectZeroDuration() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.startSession(null, 0)
        );
    }

    @Test
    void startSessionShouldRejectNegativeDuration() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.startSession(null, -10)
        );
    }

    @Test
    void completeSessionShouldCalculateActualMinutes() throws Exception {

        FocusSession session = service.startSession(null, 25);

        LocalDateTime startedAt =
                LocalDateTime.parse(session.getStartedAt());

        LocalDateTime endedAt = startedAt.plusMinutes(25);

        service.completeSession(session, endedAt);

        assertTrue(session.isCompleted());
        assertEquals(25, session.getActualMinutes());
        assertEquals(endedAt.toString(), session.getEndedAt());

        FocusSession saved = service.getSession(session.getId());

        assertNotNull(saved);
        assertTrue(saved.isCompleted());
        assertEquals(25, saved.getActualMinutes());
    }

    @Test
    void completeSessionShouldRoundActualMinutes() throws Exception {

        FocusSession session = service.startSession(null, 30);

        LocalDateTime startedAt =
                LocalDateTime.parse(session.getStartedAt());

        LocalDateTime endedAt =
                startedAt.plusSeconds(90);

        service.completeSession(session, endedAt);

        assertEquals(2, session.getActualMinutes());
    }

    @Test
    void completeSessionShouldRejectNullSession() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.completeSession(
                        null,
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void completeSessionShouldRejectNullEndTime() throws Exception {

        FocusSession session = service.startSession(null, 25);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.completeSession(session, null)
        );
    }

    @Test
    void completeSessionShouldRejectEndTimeBeforeStart() throws Exception {

        FocusSession session = service.startSession(null, 25);

        LocalDateTime startedAt =
                LocalDateTime.parse(session.getStartedAt());

        LocalDateTime endedAt = startedAt.minusMinutes(1);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.completeSession(session, endedAt)
        );
    }

    @Test
    void completedSessionShouldNotBeCompletedAgain() throws Exception {

        FocusSession session = service.startSession(null, 25);

        LocalDateTime startedAt =
                LocalDateTime.parse(session.getStartedAt());

        service.completeSession(
                session,
                startedAt.plusMinutes(25)
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.completeSession(
                        session,
                        startedAt.plusMinutes(30)
                )
        );
    }

    @Test
    void cancelSessionShouldRecordActualTimeButRemainIncomplete()
            throws Exception {

        FocusSession session = service.startSession(null, 25);

        LocalDateTime startedAt =
                LocalDateTime.parse(session.getStartedAt());

        LocalDateTime endedAt =
                startedAt.plusMinutes(10);

        service.cancelSession(session, endedAt);

        assertFalse(session.isCompleted());
        assertEquals(10, session.getActualMinutes());
        assertEquals(endedAt.toString(), session.getEndedAt());

        FocusSession saved = service.getSession(session.getId());

        assertNotNull(saved);
        assertFalse(saved.isCompleted());
        assertEquals(10, saved.getActualMinutes());
    }

    @Test
    void cancelSessionShouldRejectNullSession() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.cancelSession(
                        null,
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void cancelSessionShouldRejectNullEndTime() throws Exception {

        FocusSession session = service.startSession(null, 25);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.cancelSession(session, null)
        );
    }

    @Test
    void cancelSessionShouldRejectEndTimeBeforeStart() throws Exception {

        FocusSession session = service.startSession(null, 25);

        LocalDateTime startedAt =
                LocalDateTime.parse(session.getStartedAt());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.cancelSession(
                        session,
                        startedAt.minusMinutes(1)
                )
        );
    }

    @Test
    void completedSessionShouldNotBeCancelled() throws Exception {

        FocusSession session = service.startSession(null, 25);

        LocalDateTime startedAt =
                LocalDateTime.parse(session.getStartedAt());

        service.completeSession(
                session,
                startedAt.plusMinutes(25)
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.cancelSession(
                        session,
                        startedAt.plusMinutes(30)
                )
        );
    }

    @Test
    void getSessionsForTaskShouldReturnMatchingSessions()
            throws Exception {

        Task firstTask = createTask("First Task");
        Task secondTask = createTask("Second Task");

        FocusSession first = service.startSession(
                firstTask.getId(),
                25
        );

        service.startSession(
                secondTask.getId(),
                30
        );

        service.startSession(
                firstTask.getId(),
                45
        );

        List<FocusSession> sessions =
                service.getSessionsForTask(firstTask.getId());

        assertEquals(2, sessions.size());

        assertTrue(
                sessions.stream()
                        .allMatch(session ->
                                session.getTaskId() == firstTask.getId())
        );

        assertTrue(
                sessions.stream()
                        .anyMatch(session ->
                                session.getId() == first.getId())
        );
    }

    @Test
    void getCompletedSessionsShouldReturnOnlyCompletedSessions()
            throws Exception {

        FocusSession completed = service.startSession(null, 25);
        FocusSession incomplete = service.startSession(null, 30);

        LocalDateTime startedAt =
                LocalDateTime.parse(completed.getStartedAt());

        service.completeSession(
                completed,
                startedAt.plusMinutes(25)
        );

        List<FocusSession> sessions =
                service.getCompletedSessions();

        assertEquals(1, sessions.size());
        assertEquals(completed.getId(), sessions.get(0).getId());
        assertTrue(sessions.get(0).isCompleted());

        assertNotEquals(
                incomplete.getId(),
                sessions.get(0).getId()
        );
    }
}