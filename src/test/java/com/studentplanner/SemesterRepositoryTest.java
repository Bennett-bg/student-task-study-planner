package com.studentplanner;

import com.studentplanner.model.Semester;
import com.studentplanner.repository.SemesterRepository;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SemesterRepositoryTest {

    private SemesterRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseInitializer.initialize();

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM semesters");
        }

        repository = new SemesterRepository();
    }

    @Test
    void shouldSaveAndFindSemester() throws Exception {

        Semester semester = new Semester(
                "S3",
                "2026-07-01",
                "2026-12-31",
                true
        );

        Semester saved = repository.save(semester);

        assertTrue(saved.getId() > 0);

        Semester found = repository.findById(saved.getId());

        assertNotNull(found);
        assertEquals("S3", found.getName());
        assertEquals("2026-07-01", found.getStartDate());
        assertEquals("2026-12-31", found.getEndDate());
        assertTrue(found.isCurrent());
    }

    @Test
    void shouldFindAllSemesters() throws Exception {

        repository.save(new Semester(
                "S3",
                "2026-07-01",
                "2026-12-31",
                true
        ));

        repository.save(new Semester(
                "S4",
                "2027-01-01",
                "2027-06-30",
                false
        ));

        List<Semester> semesters = repository.findAll();

        assertEquals(2, semesters.size());
        assertEquals("S3", semesters.get(0).getName());
        assertEquals("S4", semesters.get(1).getName());
    }

    @Test
    void shouldUpdateSemester() throws Exception {

        Semester semester = repository.save(new Semester(
                "S3",
                "2026-07-01",
                "2026-12-31",
                true
        ));

        semester.setName("Third Semester");
        semester.setCurrent(false);

        repository.update(semester);

        Semester updated = repository.findById(semester.getId());

        assertNotNull(updated);
        assertEquals("Third Semester", updated.getName());
        assertFalse(updated.isCurrent());
    }

    @Test
    void shouldDeleteSemester() throws Exception {

        Semester semester = repository.save(new Semester(
                "S3",
                "2026-07-01",
                "2026-12-31",
                true
        ));

        int id = semester.getId();

        repository.delete(id);

        Semester deleted = repository.findById(id);

        assertNull(deleted);
    }
}