package com.studentplanner.model;

public class Exam {

    private int id;
    private String title;
    private int subjectId;
    private String examDate;
    private String examTime;
    private String syllabus;
    private int preparationPercent;
    private String notes;
    private Double marksObtained;
    private Double maximumMarks;

    public Exam(
            String title,
            int subjectId,
            String examDate,
            String examTime,
            String syllabus,
            int preparationPercent,
            String notes
    ) {
        this.title = title;
        this.subjectId = subjectId;
        this.examDate = examDate;
        this.examTime = examTime;
        this.syllabus = syllabus;
        this.preparationPercent = preparationPercent;
        this.notes = notes;
    }

    public Exam(
            int id,
            String title,
            int subjectId,
            String examDate,
            String examTime,
            String syllabus,
            int preparationPercent,
            String notes,
            Double marksObtained,
            Double maximumMarks
    ) {
        this.id = id;
        this.title = title;
        this.subjectId = subjectId;
        this.examDate = examDate;
        this.examTime = examTime;
        this.syllabus = syllabus;
        this.preparationPercent = preparationPercent;
        this.notes = notes;
        this.marksObtained = marksObtained;
        this.maximumMarks = maximumMarks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    public String getSyllabus() {
        return syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }

    public int getPreparationPercent() {
        return preparationPercent;
    }

    public void setPreparationPercent(int preparationPercent) {
        this.preparationPercent = preparationPercent;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Double getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(Double marksObtained) {
        this.marksObtained = marksObtained;
    }

    public Double getMaximumMarks() {
        return maximumMarks;
    }

    public void setMaximumMarks(Double maximumMarks) {
        this.maximumMarks = maximumMarks;
    }

    public boolean hasResult() {
        return marksObtained != null && maximumMarks != null;
    }

    @Override
    public String toString() {
        return title;
    }
}