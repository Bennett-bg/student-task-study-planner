package com.studentplanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() throws SQLException, IOException {
        try (
            Connection connection = DatabaseConnection.connect();
            InputStream inputStream = DatabaseInitializer.class
                    .getResourceAsStream("/schema.sql")
        ) {
            if (inputStream == null) {
                throw new IOException("schema.sql not found.");
            }

            String schema = readSchema(inputStream);

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("PRAGMA foreign_keys = ON;");

                executeStatements(statement, schema);

                migrateAchievementsTable(statement);
                migrateExamsTable(statement);
            }
        }
    }

    private static String readSchema(InputStream inputStream)
            throws IOException {

        StringBuilder schema = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        inputStream,
                        StandardCharsets.UTF_8
                )
        )) {

            String line;

            while ((line = reader.readLine()) != null) {
                schema.append(line).append('\n');
            }
        }

        return schema.toString();
    }

    private static void executeStatements(
            Statement statement,
            String schema
    ) throws SQLException {

        String[] statements = schema.split(";");

        for (String sql : statements) {
            String trimmedSql = sql.trim();

            if (!trimmedSql.isEmpty()) {
                statement.executeUpdate(trimmedSql);
            }
        }
    }

    private static void migrateAchievementsTable(
            Statement statement
    ) throws SQLException {

        try {
            statement.executeUpdate("""
                    ALTER TABLE achievements
                    ADD COLUMN icon TEXT NOT NULL DEFAULT 'DEFAULT'
                    """);

        } catch (SQLException exception) {

            if (!isDuplicateColumnError(exception)) {
                throw exception;
            }
        }
    }

    private static void migrateExamsTable(
            Statement statement
    ) throws SQLException {

        try {
            statement.executeUpdate("""
                    ALTER TABLE exams
                    ADD COLUMN completed INTEGER NOT NULL DEFAULT 0
                    """);

        } catch (SQLException exception) {

            if (!isDuplicateColumnError(exception)) {
                throw exception;
            }
        }

        try {
            statement.executeUpdate("""
                    ALTER TABLE exams
                    ADD COLUMN completed_at TEXT
                    """);

        } catch (SQLException exception) {

            if (!isDuplicateColumnError(exception)) {
                throw exception;
            }
        }
    }

    private static boolean isDuplicateColumnError(
            SQLException exception
    ) {

        String message = exception.getMessage();

        return message != null
                && message.toLowerCase()
                        .contains("duplicate column name");
    }
}