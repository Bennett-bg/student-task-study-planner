package com.studentplanner.repository;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.DatabaseInitializer;
import com.studentplanner.model.Exam;
import com.studentplanner.model.Semester;
import com.studentplanner.model.Subject;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExamRepositoryTest {

    private ExamRepository examRepository;
    private SemesterRepository semesterRepository;
    private SubjectRepository subjectRepository;

    private Semester semester;
    private Subject subject;

    @BeforeEach
    void setUp() throws Exception {

        DatabaseInitializer.initialize();

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM exams");
            statement.executeUpdate("DELETE FROM subjects");
            statement.executeUpdate("DELETE FROM semesters");
        }

        examRepository = new ExamRepository();
        semesterRepository = new SemesterRepository();
        subjectRepository = new SubjectRepository();

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
    void shouldSaveAndFindExam() throws Exception {

        Exam exam = new Exam(
                "First Series",
                subject.getId(),
                "2026-09-20",
                "10:00",
                "Units 1 and 2",
                0,
                "Prepare thoroughly",
                null,
                null
        );

        Exam saved = examRepository.save(exam);

        assertTrue(saved.getId() > 0);

        Exam found = examRepository.findById(saved.getId());

        assertNotNull(found);
        assertEquals("First Series", found.getTitle());
        assertEquals(subject.getId(), found.getSubjectId());
        assertEquals("2026-09-20", found.getExamDate());
        assertEquals("10:00", found.getExamTime());
        assertEquals("Units 1 and 2", found.getSyllabus());
        assertEquals(0, found.getPreparationPercent());
        assertEquals("Prepare thoroughly", found.getNotes());

        assertFalse(found.isCompleted());
        assertNull(found.getCompletedAt());
        assertFalse(found.hasResult());
    }

    @Test
    void shouldFindAllExams() throws Exception {

        examRepository.save(new Exam(
                "First Series",
                subject.getId(),
                "2026-09-20",
                "10:00",
                "Units 1 and 2",
                0,
                null,
                null,
                null
        ));

        examRepository.save(new Exam(
                "Second Series",
                subject.getId(),
                "2026-10-20",
                "10:00",
                "Units 3 and 4",
                0,
                null,
                null,
                null
        ));

        List<Exam> exams = examRepository.findAll();

        assertEquals(2, exams.size());
        assertEquals("First Series", exams.get(0).getTitle());
        assertEquals("Second Series", exams.get(1).getTitle());
    }

    @Test
    void shouldFindExamsBySubject() throws Exception {

        examRepository.save(new Exam(
                "First Series",
                subject.getId(),
                "2026-09-20",
                "10:00",
                null,
                0,
                null,
                null,
                null
        ));

        examRepository.save(new Exam(
                "Semester Exam",
                subject.getId(),
                "2026-12-10",
                "10:00",
                null,
                0,
                null,
                null,
                null
        ));

        List<Exam> exams =
                examRepository.findBySubjectId(subject.getId());

        assertEquals(2, exams.size());
        assertEquals("First Series", exams.get(0).getTitle());
        assertEquals("Semester Exam", exams.get(1).getTitle());
    }

    @Test
    void shouldUpdateExam() throws Exception {

        Exam exam = examRepository.save(
                new Exam(
                        "First Series",
                        subject.getId(),
                        "2026-09-20",
                        "10:00",
                        "Units 1 and 2",
                        0,
                        null,
                        null,
                        null
                )
        );

        exam.setTitle("First Series - Updated");
        exam.setExamDate("2026-09-22");
        exam.setPreparationPercent(75);
        exam.setNotes("Almost prepared");

        examRepository.update(exam);

        Exam updated = examRepository.findById(exam.getId());

        assertNotNull(updated);
        assertEquals("First Series - Updated", updated.getTitle());
        assertEquals("2026-09-22", updated.getExamDate());
        assertEquals(75, updated.getPreparationPercent());
        assertEquals("Almost prepared", updated.getNotes());
    }

    @Test
    void shouldDeleteExam() throws Exception {

        Exam exam = examRepository.save(
                new Exam(
                        "First Series",
                        subject.getId(),
                        "2026-09-20",
                        "10:00",
                        null,
                        0,
                        null,
                        null,
                        null
                )
        );

        int id = exam.getId();

        examRepository.delete(id);

        assertNull(examRepository.findById(id));
    }

    @Test
    void shouldSaveExamCompletionStatus() throws Exception {

        Exam exam = new Exam(
                "First Series",
                subject.getId(),
                "2026-09-20",
                "10:00",
                null,
                100,
                null,
                null,
                null
        );

        exam.setCompleted(true);
        exam.setCompletedAt("2026-09-20T12:00:00");

        Exam saved = examRepository.save(exam);

        Exam found = examRepository.findById(saved.getId());

        assertNotNull(found);
        assertTrue(found.isCompleted());
        assertEquals(
                "2026-09-20T12:00:00",
                found.getCompletedAt()
        );
    }

    @Test
    void shouldUpdateExamCompletionStatus() throws Exception {

        Exam exam = examRepository.save(
                new Exam(
                        "First Series",
                        subject.getId(),
                        "2026-09-20",
                        "10:00",
                        null,
                        50,
                        null,
                        null,
                        null
                )
        );

        assertFalse(exam.isCompleted());

        exam.setCompleted(true);
        exam.setCompletedAt("2026-09-20T12:00:00");

        examRepository.update(exam);

        Exam completed = examRepository.findById(exam.getId());

        assertNotNull(completed);
        assertTrue(completed.isCompleted());
        assertEquals(
                "2026-09-20T12:00:00",
                completed.getCompletedAt()
        );
    }

    @Test
    void completionAndResultShouldBeIndependent() throws Exception {

        Exam exam = new Exam(
                "First Series",
                subject.getId(),
                "2026-09-20",
                "10:00",
                null,
                100,
                null,
                42.0,
                50.0
        );

        exam.setCompleted(true);
        exam.setCompletedAt("2026-09-20T12:00:00");

        Exam saved = examRepository.save(exam);

        Exam found = examRepository.findById(saved.getId());

        assertNotNull(found);

        // Completion is stored.
        assertTrue(found.isCompleted());
        assertEquals(
                "2026-09-20T12:00:00",
                found.getCompletedAt()
        );

        // Result is also stored independently.
        assertTrue(found.hasResult());
        assertEquals(42.0, found.getMarksObtained());
        assertEquals(50.0, found.getMaximumMarks());
    }

    @Test
    void completedExamCanHaveNoResult() throws Exception {

        Exam exam = new Exam(
                "First Series",
                subject.getId(),
                "2026-09-20",
                "10:00",
                null,
                100,
                null,
                null,
                null
        );

        exam.setCompleted(true);
        exam.setCompletedAt("2026-09-20T12:00:00");

        Exam saved = examRepository.save(exam);

        Exam found = examRepository.findById(saved.getId());

        assertNotNull(found);

        assertTrue(found.isCompleted());
        assertFalse(found.hasResult());
        assertNull(found.getMarksObtained());
        assertNull(found.getMaximumMarks());
    }
}