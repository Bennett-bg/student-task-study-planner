package com.studentplanner;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskStorage {

    private static final String FILE_NAME = "tasks.dat";

    public static void saveTasks(List<Task> tasks) {

        try (PrintWriter writer =
                     new PrintWriter(new FileWriter(FILE_NAME))) {

            for (Task task : tasks) {

                String completedDate = "";

                if (task.getCompletedDate() != null) {
                    completedDate =
                            task.getCompletedDate().toString();
                }

                writer.println(
                        task.getTitle() + "|" +
                        task.getSubject() + "|" +
                        task.getDueDate() + "|" +
                        task.getPriority() + "|" +
                        task.isCompleted() + "|" +
                        completedDate
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Could not save tasks: "
                    + e.getMessage()
            );
        }
    }

    public static ArrayList<Task> loadTasks() {

        ArrayList<Task> tasks = new ArrayList<>();

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return tasks;
        }

        try (BufferedReader reader =
                     new BufferedReader(
                             new FileReader(file)
                     )) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts =
                        line.split("\\|", -1);

                // Support both old and new formats
                if (parts.length != 5 &&
                    parts.length != 6) {

                    continue;
                }

                String title = parts[0];
                String subject = parts[1];

                LocalDate dueDate =
                        LocalDate.parse(parts[2]);

                Task.Priority priority =
                        Task.Priority.valueOf(parts[3]);

                boolean completed =
                        Boolean.parseBoolean(parts[4]);

                Task task = new Task(
                        title,
                        subject,
                        dueDate,
                        priority
                );

                if (completed) {

                    task.setCompleted(true);

                    // New format contains completion date
                    if (parts.length == 6
                            && !parts[5].isBlank()) {

                        task.setCompletedDate(
                                LocalDate.parse(parts[5])
                        );
                    }
                }

                tasks.add(task);
            }

        } catch (
                IOException |
                IllegalArgumentException e
        ) {

            System.out.println(
                    "Could not load tasks: "
                    + e.getMessage()
            );
        }

        return tasks;
    }
}