
package com.studentplanner.service;

import com.studentplanner.model.Exam;
import com.studentplanner.model.Semester;
import com.studentplanner.model.Subject;
import com.studentplanner.repository.ExamRepository;
import com.studentplanner.repository.SemesterRepository;
import com.studentplanner.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExamServiceTest {

    private ExamService service;
    private int subjectId;
    private int secondSubjectId;

    @BeforeEach
    void setUp() throws Exception {

        service = new ExamService(new ExamRepository());

        /*
         * Exams require a valid subject because of the foreign-key
         * relationship in the database.
         *
         * Create a real semester and subjects for every test.
         */
        SemesterRepository semesterRepository = new SemesterRepository();
        SubjectRepository subjectRepository = new SubjectRepository();

        Semester semester = semesterRepository.save(
                new Semester(
                        "Exam Service Test Semester",
                        "2026-09-01",
                        "2027-01-31",
                        false
                )
        );

        Subject subject = subjectRepository.save(
                new Subject(
                        semester.getId(),
                        "Mathematics",
                        "TEST-MATH",
                        "#6C63FF"
                )
        );

        Subject secondSubject = subjectRepository.save(
                new Subject(
                        semester.getId(),
                        "Physics",
                        "TEST-PHY",
                        "#FF6B6B"
                )
        );

        subjectId = subject.getId();
        secondSubjectId = secondSubject.getId();
    }

    // --------------------------------------------------
    // Create
    // --------------------------------------------------

    @Test
    void shouldCreateValidExam() throws Exception {

        Exam exam = service.createExam(
                "First Series - Maths",
                subjectId,
                "2026-09-20",
                "10:00",
                "Modules 1 and 2",
                25,
                "Prepare formulas"
        );

        assertNotNull(exam);
        assertTrue(exam.getId() > 0);
        assertEquals("First Series - Maths", exam.getTitle());
        assertEquals(subjectId, exam.getSubjectId());
        assertEquals(25, exam.getPreparationPercent());

        assertFalse(exam.isCompleted());
        assertNull(exam.getCompletedAt());

        assertFalse(exam.hasResult());
        assertNull(exam.getMarksObtained());
        assertNull(exam.getMaximumMarks());
    }

    @Test
    void shouldRejectNullTitle() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createExam(
                        null,
                        subjectId,
                        "2026-09-20",
                        "10:00",
                        null,
                        0,
                        null
                )
        );
    }

    @Test
    void shouldRejectBlankTitle() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createExam(
                        "   ",
                        subjectId,
                        "2026-09-20",
                        "10:00",
                        null,
                        0,
                        null
                )
        );
    }

    @Test
    void shouldRejectInvalidSubjectId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createExam(
                        "Invalid Subject",
                        0,
                        "2026-09-20",
                        "10:00",
                        null,
                        0,
                        null
                )
        );
    }

    @Test
    void shouldRejectPreparationBelowZero() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createExam(
                        "Invalid Preparation",
                        subjectId,
                        "2026-09-20",
                        "10:00",
                        null,
                        -1,
                        null
                )
        );
    }

    @Test
    void shouldRejectPreparationAboveOneHundred() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createExam(
                        "Invalid Preparation",
                        subjectId,
                        "2026-09-20",
                        "10:00",
                        null,
                        101,
                        null
                )
        );
    }

    // --------------------------------------------------
    // Read
    // --------------------------------------------------

    @Test
    void shouldGetExam() throws Exception {

        Exam created = service.createExam(
                "Get Exam",
                subjectId,
                "2026-09-21",
                "09:00",
                null,
                0,
                null
        );

        Exam found = service.getExam(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Get Exam", found.getTitle());
    }

    @Test
    void shouldGetAllExams() throws Exception {

        service.createExam(
                "Exam One",
                subjectId,
                "2026-09-22",
                "09:00",
                null,
                0,
                null
        );

        service.createExam(
                "Exam Two",
                subjectId,
                "2026-09-23",
                "09:00",
                null,
                0,
                null
        );

        List<Exam> exams = service.getAllExams();

        assertTrue(exams.size() >= 2);
    }

    @Test
    void shouldGetExamsForSubject() throws Exception {

        Exam first = service.createExam(
                "Maths Exam",
                subjectId,
                "2026-09-24",
                "09:00",
                null,
                0,
                null
        );

        Exam second = service.createExam(
                "Physics Exam",
                secondSubjectId,
                "2026-09-25",
                "09:00",
                null,
                0,
                null
        );

        List<Exam> exams = service.getExamsForSubject(subjectId);

        assertTrue(
                exams.stream()
                        .anyMatch(exam -> exam.getId() == first.getId())
        );

        assertFalse(
                exams.stream()
                        .anyMatch(exam -> exam.getId() == second.getId())
        );
    }

    // --------------------------------------------------
    // Update
    // --------------------------------------------------

    @Test
    void shouldUpdateExam() throws Exception {

        Exam exam = service.createExam(
                "Original Exam",
                subjectId,
                "2026-09-26",
                "09:00",
                "Original syllabus",
                20,
                "Original notes"
        );

        exam.setTitle("Updated Exam");
        exam.setSyllabus("Updated syllabus");
        exam.setPreparationPercent(75);
        exam.setNotes("Updated notes");

        service.updateExam(exam);

        Exam updated = service.getExam(exam.getId());

        assertEquals("Updated Exam", updated.getTitle());
        assertEquals("Updated syllabus", updated.getSyllabus());
        assertEquals(75, updated.getPreparationPercent());
        assertEquals("Updated notes", updated.getNotes());
    }

    @Test
    void shouldDeleteExam() throws Exception {

        Exam exam = service.createExam(
                "Delete Me",
                subjectId,
                "2026-09-27",
                "09:00",
                null,
                0,
                null
        );

        int id = exam.getId();

        service.deleteExam(id);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getExam(id)
        );
    }

    // --------------------------------------------------
    // Completion
    // --------------------------------------------------

    @Test
    void shouldCompleteExamWithoutResult() throws Exception {

        Exam exam = service.createExam(
                "Complete Without Result",
                subjectId,
                "2026-09-28",
                "09:00",
                null,
                0,
                null
        );

        Exam completed = service.completeExam(exam.getId());

        assertTrue(completed.isCompleted());
        assertNotNull(completed.getCompletedAt());

        assertFalse(completed.hasResult());

        Exam stored = service.getExam(exam.getId());

        assertTrue(stored.isCompleted());
        assertNotNull(stored.getCompletedAt());
        assertFalse(stored.hasResult());
    }

    @Test
    void shouldUncompleteExamWithoutResult() throws Exception {

        Exam exam = service.createExam(
                "Uncomplete Me",
                subjectId,
                "2026-09-29",
                "09:00",
                null,
                0,
                null
        );

        service.completeExam(exam.getId());

        Exam uncompleted = service.uncompleteExam(exam.getId());

        assertFalse(uncompleted.isCompleted());
        assertNull(uncompleted.getCompletedAt());
        assertFalse(uncompleted.hasResult());
    }

    @Test
    void shouldRejectUncompleteExamWithResult() throws Exception {

        Exam exam = service.createExam(
                "Result Exam",
                subjectId,
                "2026-09-30",
                "09:00",
                null,
                0,
                null
        );

        service.recordResult(
                exam.getId(),
                45,
                50
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.uncompleteExam(exam.getId())
        );

        Exam stored = service.getExam(exam.getId());

        assertTrue(stored.isCompleted());
        assertTrue(stored.hasResult());
    }

    // --------------------------------------------------
    // Results
    // --------------------------------------------------

    @Test
    void shouldRecordResultAndAutomaticallyCompleteExam()
            throws Exception {

        Exam exam = service.createExam(
                "Result Auto Complete",
                subjectId,
                "2026-10-01",
                "09:00",
                null,
                0,
                null
        );

        assertFalse(exam.isCompleted());
        assertFalse(exam.hasResult());

        Exam updated = service.recordResult(
                exam.getId(),
                42,
                50
        );

        assertTrue(updated.isCompleted());
        assertNotNull(updated.getCompletedAt());

        assertTrue(updated.hasResult());
        assertEquals(42.0, updated.getMarksObtained());
        assertEquals(50.0, updated.getMaximumMarks());
    }

    @Test
    void shouldRecordResultForAlreadyCompletedExam()
            throws Exception {

        Exam exam = service.createExam(
                "Completed Then Result",
                subjectId,
                "2026-10-02",
                "09:00",
                null,
                0,
                null
        );

        service.completeExam(exam.getId());

        Exam updated = service.recordResult(
                exam.getId(),
                38,
                40
        );

        assertTrue(updated.isCompleted());
        assertNotNull(updated.getCompletedAt());

        assertTrue(updated.hasResult());
        assertEquals(38.0, updated.getMarksObtained());
        assertEquals(40.0, updated.getMaximumMarks());
    }

    @Test
    void shouldClearResultWithoutUncompletingExam()
            throws Exception {

        Exam exam = service.createExam(
                "Clear Result",
                subjectId,
                "2026-10-03",
                "09:00",
                null,
                0,
                null
        );

        service.recordResult(
                exam.getId(),
                40,
                50
        );

        Exam cleared = service.clearResult(exam.getId());

        assertTrue(cleared.isCompleted());
        assertNotNull(cleared.getCompletedAt());

        assertFalse(cleared.hasResult());
        assertNull(cleared.getMarksObtained());
        assertNull(cleared.getMaximumMarks());
    }

    // --------------------------------------------------
    // Result validation
    // --------------------------------------------------

    @Test
    void shouldRejectNegativeMarks() throws Exception {

        Exam exam = service.createExam(
                "Negative Marks",
                subjectId,
                "2026-10-04",
                "09:00",
                null,
                0,
                null
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.recordResult(
                        exam.getId(),
                        -1,
                        50
                )
        );
    }

    @Test
    void shouldRejectZeroMaximumMarks() throws Exception {

        Exam exam = service.createExam(
                "Zero Maximum",
                subjectId,
                "2026-10-05",
                "09:00",
                null,
                0,
                null
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.recordResult(
                        exam.getId(),
                        0,
                        0
                )
        );
    }

    @Test
    void shouldRejectMarksAboveMaximum() throws Exception {

        Exam exam = service.createExam(
                "Too Many Marks",
                subjectId,
                "2026-10-06",
                "09:00",
                null,
                0,
                null
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.recordResult(
                        exam.getId(),
                        51,
                        50
                )
        );
    }

    @Test
    void shouldRejectIncompleteExamWithResult()
            throws Exception {

        Exam exam = service.createExam(
                "Invalid State",
                subjectId,
                "2026-10-07",
                "09:00",
                null,
                0,
                null
        );

        /*
         * The normal recordResult() method would automatically
         * complete the exam, so create the invalid state directly
         * on the model to verify updateExam() protects the rule.
         */
        exam.setMarksObtained(40.0);
        exam.setMaximumMarks(50.0);
        exam.setCompleted(false);
        exam.setCompletedAt(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateExam(exam)
        );
    }

    // --------------------------------------------------
    // Persistence
    // --------------------------------------------------

    @Test
    void shouldPersistResultAndCompletionState()
            throws Exception {

        Exam exam = service.createExam(
                "Persistence Test",
                subjectId,
                "2026-10-08",
                "09:00",
                null,
                0,
                null
        );

        service.recordResult(
                exam.getId(),
                47,
                50
        );

        Exam stored = service.getExam(exam.getId());

        assertTrue(stored.isCompleted());
        assertNotNull(stored.getCompletedAt());

        assertTrue(stored.hasResult());
        assertEquals(47.0, stored.getMarksObtained());
        assertEquals(50.0, stored.getMaximumMarks());
    }
}

