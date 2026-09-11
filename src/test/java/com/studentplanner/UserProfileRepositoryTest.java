package com.studentplanner;

import static org.junit.jupiter.api.Assertions.*;

import com.studentplanner.model.UserProfile;
import com.studentplanner.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

public class UserProfileRepositoryTest {

    private UserProfileRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        repository = new UserProfileRepository();

        try (
            Connection connection = DatabaseConnection.connect();
            Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate("DELETE FROM user_profile");
        }
    }

    @Test
    void getShouldReturnNullWhenProfileDoesNotExist()
            throws Exception {

        UserProfile profile = repository.get();

        assertNull(profile);
    }

    @Test
    void saveShouldStoreProfile()
            throws Exception {

        UserProfile profile =
                new UserProfile("Bennett", "DEFAULT");

        UserProfile saved = repository.save(profile);

        assertEquals(1, saved.getId());
        assertEquals("Bennett", saved.getDisplayName());
        assertEquals("DEFAULT", saved.getAvatar());

        UserProfile retrieved = repository.get();

        assertNotNull(retrieved);
        assertEquals(1, retrieved.getId());
        assertEquals("Bennett", retrieved.getDisplayName());
        assertEquals("DEFAULT", retrieved.getAvatar());
    }

    @Test
    void updateShouldModifyProfile()
            throws Exception {

        UserProfile profile =
                new UserProfile("Bennett", "DEFAULT");

        repository.save(profile);

        profile.setDisplayName("Monolith User");
        profile.setAvatar("HEXAGON");

        repository.update(profile);

        UserProfile updated = repository.get();

        assertNotNull(updated);
        assertEquals(1, updated.getId());
        assertEquals("Monolith User", updated.getDisplayName());
        assertEquals("HEXAGON", updated.getAvatar());
    }

    @Test
    void saveShouldSetIdToOne()
            throws Exception {

        UserProfile profile =
                new UserProfile("Bennett", "DEFAULT");

        assertEquals(0, profile.getId());

        repository.save(profile);

        assertEquals(1, profile.getId());
    }
}