package com.studentplanner;

import com.studentplanner.model.Semester;
import com.studentplanner.model.Subject;
import com.studentplanner.model.Task;
import com.studentplanner.repository.SemesterRepository;
import com.studentplanner.repository.SubjectRepository;
import com.studentplanner.repository.TaskRepository;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskRepositoryTest {

    private SemesterRepository semesterRepository;
    private SubjectRepository subjectRepository;
    private TaskRepository taskRepository;

    private Semester semester;
    private Subject subject;

    @BeforeEach
    void setUp() throws Exception {

        DatabaseInitializer.initialize();

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM tasks");
            statement.executeUpdate("DELETE FROM subjects");
            statement.executeUpdate("DELETE FROM semesters");
        }

        semesterRepository = new SemesterRepository();
        subjectRepository = new SubjectRepository();
        taskRepository = new TaskRepository();

        semester = semesterRepository.save(
                new Semester(
                        "S3",
                        "2026-07-01",
                        "2026-12-31",
                        true
                )
        );

        subject = subjectRepository.save(
                new Subject(
                        semester.getId(),
                        "Data Structures and Algorithms",
                        "CST201",
                        "TEAL"
                )
        );
    }

    @Test
    void shouldSaveAndFindTask() throws Exception {

        Task task = new Task(
                "Complete linked list assignment",
                "Finish questions 1 to 5",
                subject.getId(),
                4,
                "2026-09-15",
                "18:00",
                90,
                "2026-09-15T16:00"
        );

        Task saved = taskRepository.save(task);

        assertTrue(saved.getId() > 0);

        Task found = taskRepository.findById(saved.getId());

        assertNotNull(found);
        assertEquals("Complete linked list assignment", found.getTitle());
        assertEquals("Finish questions 1 to 5", found.getDescription());
        assertEquals(subject.getId(), found.getSubjectId());
        assertEquals(4, found.getPriority());
        assertEquals("2026-09-15", found.getDueDate());
        assertEquals("18:00", found.getDueTime());
        assertEquals(90, found.getEstimatedMinutes());
        assertEquals("2026-09-15T16:00", found.getReminderAt());
        assertFalse(found.isCompleted());
    }

    @Test
    void shouldSaveGeneralTaskWithoutSubject() throws Exception {

        Task task = new Task(
                "Buy a new notebook",
                null,
                null,
                2,
                null,
                null,
                null,
                null
        );

        Task saved = taskRepository.save(task);

        assertTrue(saved.getId() > 0);

        Task found = taskRepository.findById(saved.getId());

        assertNotNull(found);
        assertEquals("Buy a new notebook", found.getTitle());
        assertNull(found.getSubjectId());
        assertNull(found.getDescription());
        assertNull(found.getDueDate());
        assertNull(found.getEstimatedMinutes());
    }

    @Test
    void shouldFindAllTasks() throws Exception {

        taskRepository.save(
                new Task(
                        "DSA Assignment",
                        null,
                        subject.getId(),
                        4,
                        "2026-09-15",
                        null,
                        60,
                        null
                )
        );

        taskRepository.save(
                new Task(
                        "Buy notebook",
                        null,
                        null,
                        2,
                        null,
                        null,
                        null,
                        null
                )
        );

        List<Task> tasks = taskRepository.findAll();

        assertEquals(2, tasks.size());
        assertEquals("DSA Assignment", tasks.get(0).getTitle());
        assertEquals("Buy notebook", tasks.get(1).getTitle());
    }

    @Test
    void shouldFindTasksBySubject() throws Exception {

        taskRepository.save(
                new Task(
                        "DSA Assignment",
                        null,
                        subject.getId(),
                        4,
                        "2026-09-15",
                        null,
                        60,
                        null
                )
        );

        taskRepository.save(
                new Task(
                        "Study linked lists",
                        null,
                        subject.getId(),
                        3,
                        "2026-09-16",
                        null,
                        45,
                        null
                )
        );

        taskRepository.save(
                new Task(
                        "Buy notebook",
                        null,
                        null,
                        2,
                        null,
                        null,
                        null,
                        null
                )
        );

        List<Task> tasks =
                taskRepository.findBySubjectId(subject.getId());

        assertEquals(2, tasks.size());
        assertEquals("DSA Assignment", tasks.get(0).getTitle());
        assertEquals("Study linked lists", tasks.get(1).getTitle());
    }

    @Test
    void shouldUpdateTask() throws Exception {

        Task task = taskRepository.save(
                new Task(
                        "Complete assignment",
                        "Initial description",
                        subject.getId(),
                        3,
                        "2026-09-15",
                        "18:00",
                        60,
                        null
                )
        );

        task.setTitle("Complete DSA assignment");
        task.setDescription("Finish all questions");
        task.setPriority(5);
        task.setDueDate("2026-09-16");
        task.setEstimatedMinutes(120);

        taskRepository.update(task);

        Task updated = taskRepository.findById(task.getId());

        assertNotNull(updated);
        assertEquals("Complete DSA assignment", updated.getTitle());
        assertEquals("Finish all questions", updated.getDescription());
        assertEquals(5, updated.getPriority());
        assertEquals("2026-09-16", updated.getDueDate());
        assertEquals(120, updated.getEstimatedMinutes());
    }

    @Test
    void shouldDeleteTask() throws Exception {

        Task task = taskRepository.save(
                new Task(
                        "Temporary task",
                        null,
                        subject.getId(),
                        1,
                        null,
                        null,
                        null,
                        null
                )
        );

        int id = task.getId();

        taskRepository.delete(id);

        assertNull(taskRepository.findById(id));
    }

    @Test
    void shouldMarkTaskAsCompleted() throws Exception {

        Task task = taskRepository.save(
                new Task(
                        "Finish assignment",
                        null,
                        subject.getId(),
                        4,
                        "2026-09-15",
                        null,
                        60,
                        null
                )
        );

        task.setCompleted(true);
        task.setCompletedAt("2026-09-10T20:30");

        taskRepository.update(task);

        Task completed = taskRepository.findById(task.getId());

        assertNotNull(completed);
        assertTrue(completed.isCompleted());
        assertEquals(
                "2026-09-10T20:30",
                completed.getCompletedAt()
        );
    }

    @Test
    void deletingSubjectShouldKeepTaskButRemoveSubjectReference()
            throws Exception {

        Task task = taskRepository.save(
                new Task(
                        "Study for exam",
                        null,
                        subject.getId(),
                        5,
                        "2026-09-20",
                        null,
                        120,
                        null
                )
        );

        subjectRepository.delete(subject.getId());

        Task remainingTask =
                taskRepository.findById(task.getId());

        assertNotNull(remainingTask);
        assertNull(remainingTask.getSubjectId());
    }
}