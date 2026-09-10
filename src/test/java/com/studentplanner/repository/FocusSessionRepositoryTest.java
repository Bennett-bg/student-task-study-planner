package com.studentplanner.repository;

import com.studentplanner.DatabaseInitializer;
import com.studentplanner.model.FocusSession;
import com.studentplanner.model.Semester;
import com.studentplanner.model.Subject;
import com.studentplanner.model.Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FocusSessionRepositoryTest {

    private FocusSessionRepository focusRepository;
    private TaskRepository taskRepository;
    private int taskId;

    @BeforeEach
    void setUp() throws Exception {

        deleteTestDatabase();

        DatabaseInitializer.initialize();

        focusRepository = new FocusSessionRepository();
        taskRepository = new TaskRepository();

        SemesterRepository semesterRepository =
                new SemesterRepository();

        SubjectRepository subjectRepository =
                new SubjectRepository();

        Semester semester = semesterRepository.save(
                new Semester(
                        "Test Semester",
                        "2026-06-01",
                        "2026-10-31",
                        true
                )
        );

        Subject subject = subjectRepository.save(
                new Subject(
                        semester.getId(),
                        "Data Structures",
                        "CST201",
                        "TEAL"
                )
        );

        Task task = taskRepository.save(
                new Task(
                        "Study linked list",
                        "Complete linked list exercises",
                        subject.getId(),
                        4,
                        "2026-09-15",
                        "18:00",
                        60,
                        null
                )
        );

        taskId = task.getId();
    }

    @Test
    void shouldSaveAndFindFocusSession() throws Exception {

        FocusSession session = new FocusSession(
                taskId,
                "2026-09-11T18:00:00",
                "2026-09-11T18:25:00",
                25,
                25,
                true
        );

        FocusSession saved =
                focusRepository.save(session);

        assertTrue(saved.getId() > 0);

        FocusSession found =
                focusRepository.findById(saved.getId());

        assertNotNull(found);
        assertEquals(taskId, found.getTaskId());
        assertEquals(
                "2026-09-11T18:00:00",
                found.getStartedAt()
        );
        assertEquals(
                "2026-09-11T18:25:00",
                found.getEndedAt()
        );
        assertEquals(25, found.getPlannedMinutes());
        assertEquals(25, found.getActualMinutes());
        assertTrue(found.isCompleted());
    }

    @Test
    void shouldAllowSessionWithoutTask() throws Exception {

        FocusSession session = new FocusSession(
                null,
                "2026-09-11T19:00:00",
                null,
                25,
                null,
                false
        );

        FocusSession saved =
                focusRepository.save(session);

        FocusSession found =
                focusRepository.findById(saved.getId());

        assertNotNull(found);
        assertNull(found.getTaskId());
        assertNull(found.getEndedAt());
        assertNull(found.getActualMinutes());
        assertFalse(found.isCompleted());
    }

    @Test
    void shouldFindAllSessionsInReverseStartOrder()
            throws Exception {

        focusRepository.save(
                new FocusSession(
                        taskId,
                        "2026-09-11T10:00:00",
                        "2026-09-11T10:25:00",
                        25,
                        25,
                        true
                )
        );

        focusRepository.save(
                new FocusSession(
                        taskId,
                        "2026-09-11T14:00:00",
                        "2026-09-11T14:30:00",
                        30,
                        30,
                        true
                )
        );

        focusRepository.save(
                new FocusSession(
                        taskId,
                        "2026-09-11T12:00:00",
                        "2026-09-11T12:20:00",
                        20,
                        20,
                        true
                )
        );

        List<FocusSession> sessions =
                focusRepository.findAll();

        assertEquals(3, sessions.size());

        assertEquals(
                "2026-09-11T14:00:00",
                sessions.get(0).getStartedAt()
        );

        assertEquals(
                "2026-09-11T12:00:00",
                sessions.get(1).getStartedAt()
        );

        assertEquals(
                "2026-09-11T10:00:00",
                sessions.get(2).getStartedAt()
        );
    }

    @Test
    void shouldFindSessionsByTask() throws Exception {

        Task secondTask = taskRepository.save(
                new Task(
                        "Study trees",
                        "Practice tree problems",
                        null,
                        3,
                        "2026-09-16",
                        "18:00",
                        45,
                        null
                )
        );

        focusRepository.save(
                new FocusSession(
                        taskId,
                        "2026-09-11T10:00:00",
                        "2026-09-11T10:25:00",
                        25,
                        25,
                        true
                )
        );

        focusRepository.save(
                new FocusSession(
                        secondTask.getId(),
                        "2026-09-11T11:00:00",
                        "2026-09-11T11:25:00",
                        25,
                        25,
                        true
                )
        );

        List<FocusSession> sessions =
                focusRepository.findByTaskId(taskId);

        assertEquals(1, sessions.size());
        assertEquals(taskId, sessions.get(0).getTaskId());
    }

    @Test
    void shouldFindOnlyCompletedSessions() throws Exception {

        focusRepository.save(
                new FocusSession(
                        taskId,
                        "2026-09-11T10:00:00",
                        "2026-09-11T10:25:00",
                        25,
                        25,
                        true
                )
        );

        focusRepository.save(
                new FocusSession(
                        taskId,
                        "2026-09-11T11:00:00",
                        null,
                        25,
                        null,
                        false
                )
        );

        List<FocusSession> completed =
                focusRepository.findCompleted();

        assertEquals(1, completed.size());
        assertTrue(completed.get(0).isCompleted());
    }

    @Test
    void shouldUpdateFocusSession() throws Exception {

        FocusSession session = focusRepository.save(
                new FocusSession(
                        taskId,
                        "2026-09-11T10:00:00",
                        null,
                        25,
                        null,
                        false
                )
        );

        session.setEndedAt("2026-09-11T10:30:00");
        session.setActualMinutes(30);
        session.setCompleted(true);

        focusRepository.update(session);

        FocusSession updated =
                focusRepository.findById(session.getId());

        assertNotNull(updated);
        assertEquals(
                "2026-09-11T10:30:00",
                updated.getEndedAt()
        );
        assertEquals(30, updated.getActualMinutes());
        assertTrue(updated.isCompleted());
    }

    @Test
    void shouldDeleteFocusSession() throws Exception {

        FocusSession session = focusRepository.save(
                new FocusSession(
                        taskId,
                        "2026-09-11T10:00:00",
                        "2026-09-11T10:25:00",
                        25,
                        25,
                        true
                )
        );

        focusRepository.delete(session.getId());

        FocusSession deleted =
                focusRepository.findById(session.getId());

        assertNull(deleted);
    }

    @Test
    void deletingTaskShouldKeepSessionButRemoveTaskLink()
            throws Exception {

        FocusSession session = focusRepository.save(
                new FocusSession(
                        taskId,
                        "2026-09-11T10:00:00",
                        "2026-09-11T10:25:00",
                        25,
                        25,
                        true
                )
        );

        taskRepository.delete(taskId);

        FocusSession remaining =
                focusRepository.findById(session.getId());

        assertNotNull(remaining);
        assertNull(remaining.getTaskId());
    }

    private void deleteTestDatabase() throws Exception {

        Path databasePath = Path.of("monolith.db");

        if (Files.exists(databasePath)) {
            Files.delete(databasePath);
        }
    }
}