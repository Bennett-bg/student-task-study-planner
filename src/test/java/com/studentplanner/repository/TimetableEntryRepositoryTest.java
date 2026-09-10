package com.studentplanner.repository;

import com.studentplanner.DatabaseInitializer;
import com.studentplanner.model.Semester;
import com.studentplanner.model.Subject;
import com.studentplanner.model.TimetableEntry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TimetableEntryRepositoryTest {

    private TimetableEntryRepository timetableRepository;
    private SubjectRepository subjectRepository;
    private int subjectId;

    @BeforeEach
    void setUp() throws Exception {

        deleteTestDatabase();

        DatabaseInitializer.initialize();

        timetableRepository = new TimetableEntryRepository();
        subjectRepository = new SubjectRepository();

        SemesterRepository semesterRepository = new SemesterRepository();

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

        subjectId = subject.getId();
    }

    @Test
    void shouldSaveAndFindTimetableEntry() throws Exception {

        TimetableEntry entry = new TimetableEntry(
                subjectId,
                1,
                "09:00",
                "10:00",
                "Room 101",
                "First period"
        );

        TimetableEntry saved = timetableRepository.save(entry);

        assertTrue(saved.getId() > 0);

        TimetableEntry found =
                timetableRepository.findById(saved.getId());

        assertNotNull(found);
        assertEquals(subjectId, found.getSubjectId());
        assertEquals(1, found.getDayOfWeek());
        assertEquals("09:00", found.getStartTime());
        assertEquals("10:00", found.getEndTime());
        assertEquals("Room 101", found.getRoom());
        assertEquals("First period", found.getNotes());
    }

    @Test
    void shouldFindAllEntriesInDayAndTimeOrder() throws Exception {

        timetableRepository.save(
                new TimetableEntry(
                        subjectId,
                        2,
                        "11:00",
                        "12:00",
                        "Room 102",
                        null
                )
        );

        timetableRepository.save(
                new TimetableEntry(
                        subjectId,
                        1,
                        "10:00",
                        "11:00",
                        "Room 101",
                        null
                )
        );

        timetableRepository.save(
                new TimetableEntry(
                        subjectId,
                        1,
                        "08:00",
                        "09:00",
                        "Room 101",
                        null
                )
        );

        List<TimetableEntry> entries = timetableRepository.findAll();

        assertEquals(3, entries.size());

        assertEquals(1, entries.get(0).getDayOfWeek());
        assertEquals("08:00", entries.get(0).getStartTime());

        assertEquals(1, entries.get(1).getDayOfWeek());
        assertEquals("10:00", entries.get(1).getStartTime());

        assertEquals(2, entries.get(2).getDayOfWeek());
    }

    @Test
    void shouldFindEntriesByDay() throws Exception {

        timetableRepository.save(
                new TimetableEntry(
                        subjectId,
                        3,
                        "09:00",
                        "10:00",
                        "Room 103",
                        null
                )
        );

        timetableRepository.save(
                new TimetableEntry(
                        subjectId,
                        3,
                        "11:00",
                        "12:00",
                        "Room 103",
                        null
                )
        );

        timetableRepository.save(
                new TimetableEntry(
                        subjectId,
                        4,
                        "09:00",
                        "10:00",
                        "Room 104",
                        null
                )
        );

        List<TimetableEntry> entries =
                timetableRepository.findByDayOfWeek(3);

        assertEquals(2, entries.size());

        assertEquals("09:00", entries.get(0).getStartTime());
        assertEquals("11:00", entries.get(1).getStartTime());
    }

    @Test
    void shouldFindEntriesBySubject() throws Exception {

        SemesterRepository semesterRepository = new SemesterRepository();

        Semester secondSemester = semesterRepository.save(
                new Semester(
                        "Second Semester",
                        "2027-01-01",
                        "2027-05-31",
                        false
                )
        );

        Subject secondSubject = subjectRepository.save(
                new Subject(
                        secondSemester.getId(),
                        "Operating Systems",
                        "CST202",
                        "PURPLE"
                )
        );

        timetableRepository.save(
                new TimetableEntry(
                        subjectId,
                        1,
                        "09:00",
                        "10:00",
                        "Room 101",
                        null
                )
        );

        timetableRepository.save(
                new TimetableEntry(
                        secondSubject.getId(),
                        2,
                        "10:00",
                        "11:00",
                        "Room 202",
                        null
                )
        );

        List<TimetableEntry> entries =
                timetableRepository.findBySubjectId(subjectId);

        assertEquals(1, entries.size());
        assertEquals(subjectId, entries.get(0).getSubjectId());
    }

    @Test
    void shouldUpdateTimetableEntry() throws Exception {

        TimetableEntry entry = timetableRepository.save(
                new TimetableEntry(
                        subjectId,
                        1,
                        "09:00",
                        "10:00",
                        "Room 101",
                        "Original"
                )
        );

        entry.setDayOfWeek(5);
        entry.setStartTime("14:00");
        entry.setEndTime("15:00");
        entry.setRoom("Lab 2");
        entry.setNotes("Updated");

        timetableRepository.update(entry);

        TimetableEntry updated =
                timetableRepository.findById(entry.getId());

        assertNotNull(updated);
        assertEquals(5, updated.getDayOfWeek());
        assertEquals("14:00", updated.getStartTime());
        assertEquals("15:00", updated.getEndTime());
        assertEquals("Lab 2", updated.getRoom());
        assertEquals("Updated", updated.getNotes());
    }

    @Test
    void shouldDeleteTimetableEntry() throws Exception {

        TimetableEntry entry = timetableRepository.save(
                new TimetableEntry(
                        subjectId,
                        1,
                        "09:00",
                        "10:00",
                        "Room 101",
                        null
                )
        );

        timetableRepository.delete(entry.getId());

        TimetableEntry deleted =
                timetableRepository.findById(entry.getId());

        assertNull(deleted);
    }

    @Test
    void deletingSubjectShouldDeleteTimetableEntries() throws Exception {

        TimetableEntry entry = timetableRepository.save(
                new TimetableEntry(
                        subjectId,
                        1,
                        "09:00",
                        "10:00",
                        "Room 101",
                        null
                )
        );

        subjectRepository.delete(subjectId);

        TimetableEntry deleted =
                timetableRepository.findById(entry.getId());

        assertNull(deleted);
    }

    @Test
    void shouldAllowNullRoomAndNotes() throws Exception {

        TimetableEntry entry = timetableRepository.save(
                new TimetableEntry(
                        subjectId,
                        6,
                        "15:00",
                        "16:00",
                        null,
                        null
                )
        );

        TimetableEntry found =
                timetableRepository.findById(entry.getId());

        assertNotNull(found);
        assertNull(found.getRoom());
        assertNull(found.getNotes());
    }

    private void deleteTestDatabase() throws Exception {

        Path databasePath = Path.of("monolith.db");

        if (Files.exists(databasePath)) {
            Files.delete(databasePath);
        }
    }
}