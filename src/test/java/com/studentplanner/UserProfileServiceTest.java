package com.studentplanner;

import static org.junit.jupiter.api.Assertions.*;

import com.studentplanner.model.UserProfile;
import com.studentplanner.repository.UserProfileRepository;
import com.studentplanner.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

public class UserProfileServiceTest {

    private UserProfileService service;

    @BeforeEach
    void setUp() throws Exception {
        try (
            Connection connection = DatabaseConnection.connect();
            Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate("DELETE FROM user_profile");
        }

        service = new UserProfileService(
                new UserProfileRepository()
        );
    }

    @Test
    void getProfileShouldReturnNullWhenProfileDoesNotExist()
            throws Exception {

        UserProfile profile = service.getProfile();

        assertNull(profile);
    }

    @Test
    void getOrCreateProfileShouldCreateDefaultProfile()
            throws Exception {

        UserProfile profile = service.getOrCreateProfile();

        assertNotNull(profile);
        assertEquals(1, profile.getId());
        assertEquals("Monolith User", profile.getDisplayName());
        assertEquals("DEFAULT", profile.getAvatar());
    }

    @Test
    void getOrCreateProfileShouldReturnExistingProfile()
            throws Exception {

        UserProfile created = service.createProfile(
                "Bennett",
                "HEXAGON"
        );

        UserProfile retrieved = service.getOrCreateProfile();

        assertEquals(created.getId(), retrieved.getId());
        assertEquals("Bennett", retrieved.getDisplayName());
        assertEquals("HEXAGON", retrieved.getAvatar());
    }

    @Test
    void createProfileShouldStoreCustomProfile()
            throws Exception {

        UserProfile profile = service.createProfile(
                "Bennett",
                "HEXAGON"
        );

        assertNotNull(profile);
        assertEquals(1, profile.getId());
        assertEquals("Bennett", profile.getDisplayName());
        assertEquals("HEXAGON", profile.getAvatar());
    }

    @Test
    void createProfileShouldTrimValues()
            throws Exception {

        UserProfile profile = service.createProfile(
                "  Bennett  ",
                "  HEXAGON  "
        );

        assertEquals("Bennett", profile.getDisplayName());
        assertEquals("HEXAGON", profile.getAvatar());
    }

    @Test
    void createProfileShouldRejectEmptyDisplayName()
            throws Exception {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createProfile("", "DEFAULT")
        );
    }

    @Test
    void createProfileShouldRejectBlankDisplayName()
            throws Exception {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createProfile("   ", "DEFAULT")
        );
    }

    @Test
    void createProfileShouldRejectNullDisplayName()
            throws Exception {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createProfile(null, "DEFAULT")
        );
    }

    @Test
    void createProfileShouldRejectEmptyAvatar()
            throws Exception {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createProfile("Bennett", "")
        );
    }

    @Test
    void createProfileShouldRejectBlankAvatar()
            throws Exception {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createProfile("Bennett", "   ")
        );
    }

    @Test
    void createProfileShouldRejectNullAvatar()
            throws Exception {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createProfile("Bennett", null)
        );
    }

    @Test
    void createProfileShouldRejectDuplicateProfile()
            throws Exception {

        service.createProfile("Bennett", "DEFAULT");

        assertThrows(
                IllegalStateException.class,
                () -> service.createProfile("Joshua", "HEXAGON")
        );
    }

    @Test
    void updateProfileShouldUpdateNameAndAvatar()
            throws Exception {

        service.createProfile("Bennett", "DEFAULT");

        service.updateProfile("Monolith User", "HEXAGON");

        UserProfile updated = service.getProfile();

        assertNotNull(updated);
        assertEquals("Monolith User", updated.getDisplayName());
        assertEquals("HEXAGON", updated.getAvatar());
    }

    @Test
    void updateDisplayNameShouldUpdateOnlyName()
            throws Exception {

        service.createProfile("Bennett", "DEFAULT");

        service.updateDisplayName("Monolith User");

        UserProfile updated = service.getProfile();

        assertNotNull(updated);
        assertEquals("Monolith User", updated.getDisplayName());
        assertEquals("DEFAULT", updated.getAvatar());
    }

    @Test
    void updateAvatarShouldUpdateOnlyAvatar()
            throws Exception {

        service.createProfile("Bennett", "DEFAULT");

        service.updateAvatar("HEXAGON");

        UserProfile updated = service.getProfile();

        assertNotNull(updated);
        assertEquals("Bennett", updated.getDisplayName());
        assertEquals("HEXAGON", updated.getAvatar());
    }

    @Test
    void updateProfileShouldRejectEmptyDisplayName()
            throws Exception {

        service.createProfile("Bennett", "DEFAULT");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateProfile("", "HEXAGON")
        );
    }

    @Test
    void updateProfileShouldRejectEmptyAvatar()
            throws Exception {

        service.createProfile("Bennett", "DEFAULT");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateProfile("Bennett", "")
        );
    }

    @Test
    void updateDisplayNameShouldRejectEmptyName()
            throws Exception {

        service.createProfile("Bennett", "DEFAULT");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateDisplayName("")
        );
    }

    @Test
    void updateAvatarShouldRejectEmptyAvatar()
            throws Exception {

        service.createProfile("Bennett", "DEFAULT");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateAvatar("")
        );
    }

    @Test
    void updateProfileShouldFailWhenProfileDoesNotExist()
            throws Exception {

        assertThrows(
                IllegalStateException.class,
                () -> service.updateProfile("Bennett", "DEFAULT")
        );
    }

    @Test
    void updateDisplayNameShouldFailWhenProfileDoesNotExist()
            throws Exception {

        assertThrows(
                IllegalStateException.class,
                () -> service.updateDisplayName("Bennett")
        );
    }

    @Test
    void updateAvatarShouldFailWhenProfileDoesNotExist()
            throws Exception {

        assertThrows(
                IllegalStateException.class,
                () -> service.updateAvatar("DEFAULT")
        );
    }
}