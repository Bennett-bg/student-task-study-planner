
package com.studentplanner.service;

import com.studentplanner.model.Exam;
import com.studentplanner.repository.ExamRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ExamService {

    private final ExamRepository examRepository;

    public ExamService() {
        this.examRepository = new ExamRepository();
    }

    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    // -------------------------
    // Create
    // -------------------------

    public Exam createExam(
            String title,
            int subjectId,
            String examDate,
            String examTime,
            String syllabus,
            int preparationPercent,
            String notes
    ) throws Exception {

        validateTitle(title);
        validateSubjectId(subjectId);
        validatePreparationPercent(preparationPercent);

        Exam exam = new Exam(
                title.trim(),
                subjectId,
                examDate,
                examTime,
                syllabus,
                preparationPercent,
                notes,
                null,
                null
        );

        return examRepository.save(exam);
    }

    // -------------------------
    // Read
    // -------------------------

    public Exam getExam(int id) throws Exception {

        validateId(id);

        Exam exam = examRepository.findById(id);

        if (exam == null) {
            throw new IllegalArgumentException(
                    "Exam not found with id: " + id
            );
        }

        return exam;
    }

    public List<Exam> getAllExams() throws Exception {
        return examRepository.findAll();
    }

    public List<Exam> getExamsForSubject(int subjectId) throws Exception {

        validateSubjectId(subjectId);

        return examRepository.findBySubjectId(subjectId);
    }

    // -------------------------
    // Update
    // -------------------------

    public Exam updateExam(Exam exam) throws Exception {

        validateExam(exam);

        Exam existingExam = examRepository.findById(exam.getId());

        if (existingExam == null) {
            throw new IllegalArgumentException(
                    "Exam not found with id: " + exam.getId()
            );
        }

        /*
         * Result and completion remain logically consistent.
         *
         * If an exam has a result, it must be completed.
         */
        if (exam.hasResult() && !exam.isCompleted()) {
            exam.setCompleted(true);

            if (exam.getCompletedAt() == null) {
                exam.setCompletedAt(
                        LocalDateTime.now().toString()
                );
            }
        }

        validateCompletionState(exam);

        examRepository.update(exam);

        return exam;
    }

    // -------------------------
    // Delete
    // -------------------------

    public void deleteExam(int id) throws Exception {

        validateId(id);

        Exam existingExam = examRepository.findById(id);

        if (existingExam == null) {
            throw new IllegalArgumentException(
                    "Exam not found with id: " + id
            );
        }

        examRepository.delete(id);
    }

    // -------------------------
    // Completion
    // -------------------------

    public Exam completeExam(int id) throws Exception {

        Exam exam = getExam(id);

        if (exam.isCompleted()) {
            throw new IllegalStateException(
                    "Exam is already completed."
            );
        }

        exam.setCompleted(true);
        exam.setCompletedAt(
                LocalDateTime.now().toString()
        );

        validateCompletionState(exam);

        examRepository.update(exam);

        return exam;
    }

    public Exam uncompleteExam(int id) throws Exception {

        Exam exam = getExam(id);

        /*
         * An exam with a result cannot be marked incomplete.
         * The result proves that the exam has already happened.
         */
        if (exam.hasResult()) {
            throw new IllegalStateException(
                    "An exam with a recorded result cannot be marked incomplete."
            );
        }

        if (!exam.isCompleted()) {
            throw new IllegalStateException(
                    "Exam is already incomplete."
            );
        }

        exam.setCompleted(false);
        exam.setCompletedAt(null);

        validateCompletionState(exam);

        examRepository.update(exam);

        return exam;
    }

    // -------------------------
    // Results
    // -------------------------

    public Exam recordResult(
            int id,
            double marksObtained,
            double maximumMarks
    ) throws Exception {

        Exam exam = getExam(id);

        validateResult(marksObtained, maximumMarks);

        /*
         * Recording a result means the exam has definitely happened.
         * Therefore result entry automatically completes the exam.
         */
        if (!exam.isCompleted()) {
            exam.setCompleted(true);
            exam.setCompletedAt(
                    LocalDateTime.now().toString()
            );
        }

        exam.setMarksObtained(marksObtained);
        exam.setMaximumMarks(maximumMarks);

        validateCompletionState(exam);

        examRepository.update(exam);

        return exam;
    }

    public Exam clearResult(int id) throws Exception {

        Exam exam = getExam(id);

        if (!exam.hasResult()) {
            throw new IllegalStateException(
                    "Exam does not have a recorded result."
            );
        }

        /*
         * Clearing the result does NOT undo completion.
         *
         * The exam has already happened; only the stored marks
         * are being removed.
         */
        exam.setMarksObtained(null);
        exam.setMaximumMarks(null);

        validateCompletionState(exam);

        examRepository.update(exam);

        return exam;
    }

    // -------------------------
    // Validation
    // -------------------------

    private void validateExam(Exam exam) {

        if (exam == null) {
            throw new IllegalArgumentException(
                    "Exam cannot be null."
            );
        }

        validateId(exam.getId());
        validateTitle(exam.getTitle());
        validateSubjectId(exam.getSubjectId());
        validatePreparationPercent(
                exam.getPreparationPercent()
        );
        validateCompletionState(exam);
    }

    private void validateCompletionState(Exam exam) {

        /*
         * A result without completion is logically impossible.
         */
        if (exam.hasResult() && !exam.isCompleted()) {
            throw new IllegalArgumentException(
                    "An exam with a result must be completed."
            );
        }

        /*
         * A completed exam must have a completion timestamp.
         */
        if (exam.isCompleted()
                && exam.getCompletedAt() == null) {

            throw new IllegalArgumentException(
                    "Completed exam must have a completion timestamp."
            );
        }

        /*
         * An incomplete exam must not have a completion timestamp.
         */
        if (!exam.isCompleted()
                && exam.getCompletedAt() != null) {

            throw new IllegalArgumentException(
                    "Incomplete exam cannot have a completion timestamp."
            );
        }
    }

    private void validateTitle(String title) {

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Exam title cannot be blank."
            );
        }
    }

    private void validateSubjectId(int subjectId) {

        if (subjectId <= 0) {
            throw new IllegalArgumentException(
                    "Subject ID must be greater than 0."
            );
        }
    }

    private void validatePreparationPercent(
            int preparationPercent) {

        if (preparationPercent < 0
                || preparationPercent > 100) {

            throw new IllegalArgumentException(
                    "Preparation percent must be between 0 and 100."
            );
        }
    }

    private void validateResult(
            double marksObtained,
            double maximumMarks) {

        if (Double.isNaN(marksObtained)
                || Double.isInfinite(marksObtained)) {

            throw new IllegalArgumentException(
                    "Marks obtained must be a valid number."
            );
        }

        if (Double.isNaN(maximumMarks)
                || Double.isInfinite(maximumMarks)) {

            throw new IllegalArgumentException(
                    "Maximum marks must be a valid number."
            );
        }

        if (maximumMarks <= 0) {
            throw new IllegalArgumentException(
                    "Maximum marks must be greater than 0."
            );
        }

        if (marksObtained < 0) {
            throw new IllegalArgumentException(
                    "Marks obtained cannot be negative."
            );
        }

        if (marksObtained > maximumMarks) {
            throw new IllegalArgumentException(
                    "Marks obtained cannot exceed maximum marks."
            );
        }
    }

    private void validateId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "ID must be greater than 0."
            );
        }
    }
}
