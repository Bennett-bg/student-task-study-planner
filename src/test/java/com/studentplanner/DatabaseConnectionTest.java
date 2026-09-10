package com.studentplanner;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

public class DatabaseConnectionTest {

    @Test
    void databaseConnectionShouldWork() throws Exception {
        Connection connection = DatabaseConnection.connect();

        assertNotNull(connection);
        assertNotNull(connection.getMetaData());

        connection.close();
    }
}