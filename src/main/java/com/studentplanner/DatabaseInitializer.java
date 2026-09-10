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
            }
        }
    }

    private static String readSchema(InputStream inputStream) throws IOException {
        StringBuilder schema = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
                schema.append(line).append('\n');
            }
        }

        return schema.toString();
    }

    private static void executeStatements(Statement statement, String schema)
            throws SQLException {

        String[] statements = schema.split(";");

        for (String sql : statements) {
            String trimmedSql = sql.trim();

            if (!trimmedSql.isEmpty()) {
                statement.executeUpdate(trimmedSql);
            }
        }
    }
}