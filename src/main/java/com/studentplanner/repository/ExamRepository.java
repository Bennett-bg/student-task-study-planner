package com.studentplanner.repository;

import com.studentplanner.DatabaseConnection;
import com.studentplanner.model.Exam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExamRepository {

    public Exam save(Exam exam) throws SQLException {

        String sql = """
                INSERT INTO exams (
                    title,
                    subject_id,
                    exam_date,
                    exam_time,
                    syllabus,
                    preparation_percent,
                    notes,
                    marks_obtained,
                    maximum_marks
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, exam.getTitle());
            statement.setInt(2, exam.getSubjectId());
            statement.setString(3, exam.getExamDate());
            statement.setString(4, exam.getExamTime());
            statement.setString(5, exam.getSyllabus());
            statement.setInt(6, exam.getPreparationPercent());
            statement.setString(7, exam.getNotes());

            if (exam.getMarksObtained() == null) {
                statement.setNull(8, java.sql.Types.REAL);
            } else {
                statement.setDouble(8, exam.getMarksObtained());
            }

            if (exam.getMaximumMarks() == null) {
                statement.setNull(9, java.sql.Types.REAL);
            } else {
                statement.setDouble(9, exam.getMaximumMarks());
            }

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    exam.setId(keys.getInt(1));
                }
            }
        }

        return exam;
    }

    public Exam findById(int id) throws SQLException {

        String sql = """
                SELECT id, title, subject_id, exam_date, exam_time,
                       syllabus, preparation_percent, notes,
                       marks_obtained, maximum_marks
                FROM exams
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        }

        return null;
    }

    public List<Exam> findAll() throws SQLException {

        String sql = """
                SELECT id, title, subject_id, exam_date, exam_time,
                       syllabus, preparation_percent, notes,
                       marks_obtained, maximum_marks
                FROM exams
                ORDER BY exam_date, id
                """;

        List<Exam> exams = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                exams.add(mapRow(resultSet));
            }
        }

        return exams;
    }

    public List<Exam> findBySubjectId(int subjectId) throws SQLException {

        String sql = """
                SELECT id, title, subject_id, exam_date, exam_time,
                       syllabus, preparation_percent, notes,
                       marks_obtained, maximum_marks
                FROM exams
                WHERE subject_id = ?
                ORDER BY exam_date, id
                """;

        List<Exam> exams = new ArrayList<>();

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    exams.add(mapRow(resultSet));
                }
            }
        }

        return exams;
    }

    public void update(Exam exam) throws SQLException {

        String sql = """
                UPDATE exams
                SET title = ?,
                    subject_id = ?,
                    exam_date = ?,
                    exam_time = ?,
                    syllabus = ?,
                    preparation_percent = ?,
                    notes = ?,
                    marks_obtained = ?,
                    maximum_marks = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, exam.getTitle());
            statement.setInt(2, exam.getSubjectId());
            statement.setString(3, exam.getExamDate());
            statement.setString(4, exam.getExamTime());
            statement.setString(5, exam.getSyllabus());
            statement.setInt(6, exam.getPreparationPercent());
            statement.setString(7, exam.getNotes());

            if (exam.getMarksObtained() == null) {
                statement.setNull(8, java.sql.Types.REAL);
            } else {
                statement.setDouble(8, exam.getMarksObtained());
            }

            if (exam.getMaximumMarks() == null) {
                statement.setNull(9, java.sql.Types.REAL);
            } else {
                statement.setDouble(9, exam.getMaximumMarks());
            }

            statement.setInt(10, exam.getId());

            statement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {

        String sql = "DELETE FROM exams WHERE id = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private Exam mapRow(ResultSet resultSet) throws SQLException {

        double marksObtained =
                resultSet.getDouble("marks_obtained");

        Double nullableMarksObtained =
                resultSet.wasNull() ? null : marksObtained;

        double maximumMarks =
                resultSet.getDouble("maximum_marks");

        Double nullableMaximumMarks =
                resultSet.wasNull() ? null : maximumMarks;

        return new Exam(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getInt("subject_id"),
                resultSet.getString("exam_date"),
                resultSet.getString("exam_time"),
                resultSet.getString("syllabus"),
                resultSet.getInt("preparation_percent"),
                resultSet.getString("notes"),
                nullableMarksObtained,
                nullableMaximumMarks
        );
    }
}