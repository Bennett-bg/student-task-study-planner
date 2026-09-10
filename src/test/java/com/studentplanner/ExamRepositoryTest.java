package com.studentplanner;

import com.studentplanner.model.Exam;
import com.studentplanner.model.Semester;
import com.studentplanner.model.Subject;
import com.studentplanner.repository.ExamRepository;
import com.studentplanner.repository.SemesterRepository;
import com.studentplanner.repository.SubjectRepository;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExamRepositoryTest {

    private SemesterRepository semesterRepository;
    private SubjectRepository subjectRepository;
    private ExamRepository examRepository;

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

        semesterRepository = new SemesterRepository();
        subjectRepository = new SubjectRepository();
        examRepository = new ExamRepository();

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
    void shouldSaveAndFindExamWithoutResult() throws Exception {

        Exam exam = new Exam(
                "First Series Examination",
                subject.getId(),
                "2026-09-20",
                "10:00",
                "Unit 1, Unit 2 and Unit 3",
                65,
                "Revise linked lists and trees"
        );

        Exam saved = examRepository.save(exam);

        assertTrue(saved.getId() > 0);

        Exam found = examRepository.findById(saved.getId());

        assertNotNull(found);
        assertEquals("First Series Examination", found.getTitle());
        assertEquals(subject.getId(), found.getSubjectId());
        assertEquals("2026-09-20", found.getExamDate());
        assertEquals("10:00", found.getExamTime());
        assertEquals(
                "Unit 1, Unit 2 and Unit 3",
                found.getSyllabus()
        );
        assertEquals(65, found.getPreparationPercent());
        assertEquals(
                "Revise linked lists and trees",
                found.getNotes()
        );

        assertNull(found.getMarksObtained());
        assertNull(found.getMaximumMarks());
        assertFalse(found.hasResult());
    }

    @Test
    void shouldSaveExamWithResult() throws Exception {

        Exam exam = new Exam(
                "Class Test",
                subject.getId(),
                "2026-09-25",
                "14:00",
                "Unit 1",
                100,
                null
        );

        exam.setMarksObtained(18.0);
        exam.setMaximumMarks(20.0);

        Exam saved = examRepository.save(exam);

        Exam found = examRepository.findById(saved.getId());

        assertNotNull(found);
        assertEquals(18.0, found.getMarksObtained());
        assertEquals(20.0, found.getMaximumMarks());
        assertTrue(found.hasResult());
    }

    @Test
    void shouldFindAllExams() throws Exception {

        examRepository.save(
                new Exam(
                        "First Series Examination",
                        subject.getId(),
                        "2026-09-20",
                        null,
                        null,
                        50,
                        null
                )
        );

        examRepository.save(
                new Exam(
                        "Second Series Examination",
                        subject.getId(),
                        "2026-10-20",
                        null,
                        null,
                        20,
                        null
                )
        );

        List<Exam> exams = examRepository.findAll();

        assertEquals(2, exams.size());
        assertEquals(
                "First Series Examination",
                exams.get(0).getTitle()
        );
        assertEquals(
                "Second Series Examination",
                exams.get(1).getTitle()
        );
    }

    @Test
    void shouldFindExamsBySubject() throws Exception {

        examRepository.save(
                new Exam(
                        "First Series Examination",
                        subject.getId(),
                        "2026-09-20",
                        null,
                        null,
                        50,
                        null
                )
        );

        examRepository.save(
                new Exam(
                        "Semester Examination",
                        subject.getId(),
                        "2026-12-10",
                        null,
                        null,
                        10,
                        null
                )
        );

        List<Exam> exams =
                examRepository.findBySubjectId(subject.getId());

        assertEquals(2, exams.size());
        assertEquals(
                "First Series Examination",
                exams.get(0).getTitle()
        );
        assertEquals(
                "Semester Examination",
                exams.get(1).getTitle()
        );
    }

    @Test
    void shouldUpdateExam() throws Exception {

        Exam exam = examRepository.save(
                new Exam(
                        "Class Test",
                        subject.getId(),
                        "2026-09-20",
                        "10:00",
                        "Unit 1",
                        40,
                        "Initial notes"
                )
        );

        exam.setTitle("First Series Examination");
        exam.setExamDate("2026-09-22");
        exam.setExamTime("09:30");
        exam.setSyllabus("Unit 1, Unit 2 and Unit 3");
        exam.setPreparationPercent(80);
        exam.setNotes("Almost ready");
        exam.setMarksObtained(17.5);
        exam.setMaximumMarks(20.0);

        examRepository.update(exam);

        Exam updated =
                examRepository.findById(exam.getId());

        assertNotNull(updated);
        assertEquals(
                "First Series Examination",
                updated.getTitle()
        );
        assertEquals("2026-09-22", updated.getExamDate());
        assertEquals("09:30", updated.getExamTime());
        assertEquals(
                "Unit 1, Unit 2 and Unit 3",
                updated.getSyllabus()
        );
        assertEquals(80, updated.getPreparationPercent());
        assertEquals("Almost ready", updated.getNotes());
        assertEquals(17.5, updated.getMarksObtained());
        assertEquals(20.0, updated.getMaximumMarks());
        assertTrue(updated.hasResult());
    }

    @Test
    void shouldDeleteExam() throws Exception {

        Exam exam = examRepository.save(
                new Exam(
                        "Temporary Test",
                        subject.getId(),
                        "2026-09-30",
                        null,
                        null,
                        0,
                        null
                )
        );

        int id = exam.getId();

        examRepository.delete(id);

        assertNull(examRepository.findById(id));
    }

    @Test
    void deletingSubjectShouldDeleteItsExams() throws Exception {

        Exam exam = examRepository.save(
                new Exam(
                        "First Series Examination",
                        subject.getId(),
                        "2026-09-20",
                        null,
                        null,
                        50,
                        null
                )
        );

        subjectRepository.delete(subject.getId());

        assertNull(examRepository.findById(exam.getId()));
    }
}