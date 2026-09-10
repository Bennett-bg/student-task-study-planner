package com.studentplanner;

import com.studentplanner.model.Semester;
import com.studentplanner.model.Subject;
import com.studentplanner.repository.SemesterRepository;
import com.studentplanner.repository.SubjectRepository;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SubjectRepositoryTest {

    private SemesterRepository semesterRepository;
    private SubjectRepository subjectRepository;
    private Semester semester;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseInitializer.initialize();

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM subjects");
            statement.executeUpdate("DELETE FROM semesters");
        }

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
    }

    @Test
    void shouldSaveAndFindSubject() throws Exception {

        Subject subject = new Subject(
                semester.getId(),
                "Data Structures and Algorithms",
                "CST201",
                "TEAL"
        );

        Subject saved = subjectRepository.save(subject);

        assertTrue(saved.getId() > 0);

        Subject found = subjectRepository.findById(saved.getId());

        assertNotNull(found);
        assertEquals("Data Structures and Algorithms", found.getName());
        assertEquals("CST201", found.getCode());
        assertEquals("TEAL", found.getColor());
        assertEquals(semester.getId(), found.getSemesterId());
    }

    @Test
    void shouldFindSubjectsBySemester() throws Exception {

        subjectRepository.save(new Subject(
                semester.getId(),
                "Data Structures and Algorithms",
                "CST201",
                "TEAL"
        ));

        subjectRepository.save(new Subject(
                semester.getId(),
                "Object Oriented Programming",
                "CST202",
                "AMBER"
        ));

        List<Subject> subjects =
                subjectRepository.findBySemesterId(semester.getId());

        assertEquals(2, subjects.size());
        assertEquals(
                "Data Structures and Algorithms",
                subjects.get(0).getName()
        );
        assertEquals(
                "Object Oriented Programming",
                subjects.get(1).getName()
        );
    }

    @Test
    void shouldFindAllSubjects() throws Exception {

        subjectRepository.save(new Subject(
                semester.getId(),
                "Data Structures and Algorithms",
                "CST201",
                "TEAL"
        ));

        subjectRepository.save(new Subject(
                semester.getId(),
                "Object Oriented Programming",
                "CST202",
                "AMBER"
        ));

        List<Subject> subjects = subjectRepository.findAll();

        assertEquals(2, subjects.size());
    }

    @Test
    void shouldUpdateSubject() throws Exception {

        Subject subject = subjectRepository.save(
                new Subject(
                        semester.getId(),
                        "Data Structures and Algorithms",
                        "CST201",
                        "TEAL"
                )
        );

        subject.setName("Advanced Data Structures");
        subject.setColor("VIOLET");

        subjectRepository.update(subject);

        Subject updated =
                subjectRepository.findById(subject.getId());

        assertNotNull(updated);
        assertEquals("Advanced Data Structures", updated.getName());
        assertEquals("VIOLET", updated.getColor());
    }

    @Test
    void shouldDeleteSubject() throws Exception {

        Subject subject = subjectRepository.save(
                new Subject(
                        semester.getId(),
                        "Data Structures and Algorithms",
                        "CST201",
                        "TEAL"
                )
        );

        int id = subject.getId();

        subjectRepository.delete(id);

        assertNull(subjectRepository.findById(id));
    }

    @Test
    void deletingSemesterShouldDeleteItsSubjects() throws Exception {

        subjectRepository.save(
                new Subject(
                        semester.getId(),
                        "Data Structures and Algorithms",
                        "CST201",
                        "TEAL"
                )
        );

        semesterRepository.delete(semester.getId());

        assertTrue(
                subjectRepository.findBySemesterId(semester.getId()).isEmpty()
        );
    }
}