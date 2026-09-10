package com.studentplanner.repository;

import com.studentplanner.DatabaseInitializer;
import com.studentplanner.model.FocusSession;
import com.studentplanner.model.Semester;
import com.studentplanner.model.SessionNote;
import com.studentplanner.model.Subject;
import com.studentplanner.model.Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SessionNoteRepositoryTest {

    private SessionNoteRepository noteRepository;
    private FocusSessionRepository focusRepository;
    private int focusSessionId;

    @BeforeEach
    void setUp() throws Exception {

        deleteTestDatabase();

        DatabaseInitializer.initialize();

        noteRepository = new SessionNoteRepository();
        focusRepository = new FocusSessionRepository();

        SemesterRepository semesterRepository =
                new SemesterRepository();

        SubjectRepository subjectRepository =
                new SubjectRepository();

        TaskRepository taskRepository =
                new TaskRepository();

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

        FocusSession session = focusRepository.save(
                new FocusSession(
                        task.getId(),
                        "2026-09-11T18:00:00",
                        "2026-09-11T18:25:00",
                        25,
                        25,
                        true
                )
        );

        focusSessionId = session.getId();
    }

    @Test
    void shouldSaveAndFindNote() throws Exception {

        SessionNote note = noteRepository.save(
                new SessionNote(
                        focusSessionId,
                        "Reviewed linked list insertion and deletion."
                )
        );

        assertTrue(note.getId() > 0);

        SessionNote found =
                noteRepository.findById(note.getId());

        assertNotNull(found);
        assertEquals(focusSessionId, found.getFocusSessionId());
        assertEquals(
                "Reviewed linked list insertion and deletion.",
                found.getNote()
        );
        assertNotNull(found.getCreatedAt());
    }

    @Test
    void shouldFindAllNotes() throws Exception {

        noteRepository.save(
                new SessionNote(
                        focusSessionId,
                        "First note"
                )
        );

        noteRepository.save(
                new SessionNote(
                        focusSessionId,
                        "Second note"
                )
        );

        List<SessionNote> notes =
                noteRepository.findAll();

        assertEquals(2, notes.size());
    }

    @Test
    void shouldFindNotesByFocusSession() throws Exception {

        SessionNote first = noteRepository.save(
                new SessionNote(
                        focusSessionId,
                        "First note"
                )
        );

        noteRepository.save(
                new SessionNote(
                        focusSessionId,
                        "Second note"
                )
        );

        List<SessionNote> notes =
                noteRepository.findByFocusSessionId(
                        focusSessionId
                );

        assertEquals(2, notes.size());
        assertEquals(
                focusSessionId,
                notes.get(0).getFocusSessionId()
        );
        assertEquals(
                first.getFocusSessionId(),
                notes.get(1).getFocusSessionId()
        );
    }

    @Test
    void shouldUpdateNote() throws Exception {

        SessionNote note = noteRepository.save(
                new SessionNote(
                        focusSessionId,
                        "Original note"
                )
        );

        note.setNote("Updated note");

        noteRepository.update(note);

        SessionNote updated =
                noteRepository.findById(note.getId());

        assertNotNull(updated);
        assertEquals("Updated note", updated.getNote());
    }

    @Test
    void shouldDeleteNote() throws Exception {

        SessionNote note = noteRepository.save(
                new SessionNote(
                        focusSessionId,
                        "Temporary note"
                )
        );

        noteRepository.delete(note.getId());

        SessionNote deleted =
                noteRepository.findById(note.getId());

        assertNull(deleted);
    }

    @Test
    void deletingFocusSessionShouldDeleteNotes()
            throws Exception {

        SessionNote note = noteRepository.save(
                new SessionNote(
                        focusSessionId,
                        "This should be deleted with the session."
                )
        );

        focusRepository.delete(focusSessionId);

        SessionNote deleted =
                noteRepository.findById(note.getId());

        assertNull(deleted);
    }

    private void deleteTestDatabase() throws Exception {

        Path databasePath = Path.of("monolith.db");

        if (Files.exists(databasePath)) {
            Files.delete(databasePath);
        }
    }
}