package com.studentplanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

public class DatabaseInitializerTest {

    @Test
    void databaseShouldInitializeCorrectly() throws Exception {

        DatabaseInitializer.initialize();

        Path databaseFile = Path.of("monolith.db");

        assertTrue(Files.exists(databaseFile));

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT name FROM sqlite_master " +
                     "WHERE type = 'table'")) {

            int tableCount = 0;

            while (resultSet.next()) {
                tableCount++;
            }

            assertTrue(tableCount >= 10);
        }
    }
}