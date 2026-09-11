package com.studentplanner.service;

import com.studentplanner.model.FocusSession;
import com.studentplanner.repository.FocusSessionRepository;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FocusSessionService {

    private final FocusSessionRepository repository;

    public FocusSessionService() {
        this.repository = new FocusSessionRepository();
    }

    public FocusSessionService(FocusSessionRepository repository) {
        this.repository = repository;
    }

    public FocusSession startSession(
            Integer taskId,
            int plannedMinutes
    ) throws SQLException {

        if (plannedMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Planned duration must be greater than zero."
            );
        }

        String startedAt = LocalDateTime.now().toString();

        FocusSession session = new FocusSession(
                taskId,
                startedAt,
                null,
                plannedMinutes,
                null,
                false
        );

        return repository.save(session);
    }

    public FocusSession completeSession(
            FocusSession session,
            LocalDateTime endedAt
    ) throws SQLException {

        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null.");
        }

        if (session.isCompleted()) {
            throw new IllegalStateException("Session is already completed.");
        }

        if (endedAt == null) {
            throw new IllegalArgumentException("End time cannot be null.");
        }

        LocalDateTime startedAt =
                LocalDateTime.parse(session.getStartedAt());

        if (endedAt.isBefore(startedAt)) {
            throw new IllegalArgumentException(
                    "End time cannot be before start time."
            );
        }

        long seconds = Duration.between(startedAt, endedAt).getSeconds();

        int actualMinutes = (int) Math.max(1, Math.round(seconds / 60.0));

        session.setEndedAt(endedAt.toString());
        session.setActualMinutes(actualMinutes);
        session.setCompleted(true);

        repository.update(session);

        return session;
    }

    public FocusSession cancelSession(
            FocusSession session,
            LocalDateTime endedAt
    ) throws SQLException {

        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null.");
        }

        if (session.isCompleted()) {
            throw new IllegalStateException(
                    "Completed sessions cannot be cancelled."
            );
        }

        if (endedAt == null) {
            throw new IllegalArgumentException("End time cannot be null.");
        }

        LocalDateTime startedAt =
                LocalDateTime.parse(session.getStartedAt());

        if (endedAt.isBefore(startedAt)) {
            throw new IllegalArgumentException(
                    "End time cannot be before start time."
            );
        }

        long seconds = Duration.between(startedAt, endedAt).getSeconds();

        int actualMinutes = (int) Math.max(1, Math.round(seconds / 60.0));

        session.setEndedAt(endedAt.toString());
        session.setActualMinutes(actualMinutes);
        session.setCompleted(false);

        repository.update(session);

        return session;
    }

    public FocusSession getSession(int id) throws SQLException {
        return repository.findById(id);
    }

    public List<FocusSession> getAllSessions() throws SQLException {
        return repository.findAll();
    }

    public List<FocusSession> getSessionsForTask(int taskId)
            throws SQLException {
        return repository.findByTaskId(taskId);
    }

    public List<FocusSession> getCompletedSessions()
            throws SQLException {
        return repository.findCompleted();
    }
}