package com.studentplanner.service;

import com.studentplanner.model.UserProfile;
import com.studentplanner.repository.UserProfileRepository;

import java.sql.SQLException;

public class UserProfileService {

    private static final String DEFAULT_NAME = "Monolith User";
    private static final String DEFAULT_AVATAR = "DEFAULT";

    private final UserProfileRepository repository;

    public UserProfileService() {
        this.repository = new UserProfileRepository();
    }

    public UserProfileService(UserProfileRepository repository) {
        this.repository = repository;
    }

    public UserProfile getProfile() throws SQLException {
        return repository.get();
    }

    public UserProfile getOrCreateProfile() throws SQLException {
        UserProfile profile = repository.get();

        if (profile != null) {
            return profile;
        }

        UserProfile newProfile = new UserProfile(
                DEFAULT_NAME,
                DEFAULT_AVATAR
        );

        return repository.save(newProfile);
    }

    public UserProfile createProfile(
            String displayName,
            String avatar
    ) throws SQLException {

        validateDisplayName(displayName);
        validateAvatar(avatar);

        if (repository.get() != null) {
            throw new IllegalStateException(
                    "A user profile already exists."
            );
        }

        UserProfile profile = new UserProfile(
                displayName.trim(),
                avatar.trim()
        );

        return repository.save(profile);
    }

    public void updateProfile(
            String displayName,
            String avatar
    ) throws SQLException {

        validateDisplayName(displayName);
        validateAvatar(avatar);

        UserProfile profile = repository.get();

        if (profile == null) {
            throw new IllegalStateException(
                    "User profile does not exist."
            );
        }

        profile.setDisplayName(displayName.trim());
        profile.setAvatar(avatar.trim());

        repository.update(profile);
    }

    public void updateDisplayName(String displayName)
            throws SQLException {

        validateDisplayName(displayName);

        UserProfile profile = repository.get();

        if (profile == null) {
            throw new IllegalStateException(
                    "User profile does not exist."
            );
        }

        profile.setDisplayName(displayName.trim());

        repository.update(profile);
    }

    public void updateAvatar(String avatar)
            throws SQLException {

        validateAvatar(avatar);

        UserProfile profile = repository.get();

        if (profile == null) {
            throw new IllegalStateException(
                    "User profile does not exist."
            );
        }

        profile.setAvatar(avatar.trim());

        repository.update(profile);
    }

    private void validateDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException(
                    "Display name cannot be empty."
            );
        }
    }

    private void validateAvatar(String avatar) {
        if (avatar == null || avatar.isBlank()) {
            throw new IllegalArgumentException(
                    "Avatar cannot be empty."
            );
        }
    }
}
