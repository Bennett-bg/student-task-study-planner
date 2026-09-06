package com.studentplanner;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskStorage {

    private static final String FILE_NAME = "tasks.dat";

    public static void saveTasks(List<Task> tasks) {

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {

            for (Task task : tasks) {

                writer.println(
                        task.getTitle() + "|" +
                        task.getSubject() + "|" +
                        task.getDueDate() + "|" +
                        task.getPriority() + "|" +
                        task.isCompleted()
                );
            }

        } catch (IOException e) {

            System.out.println("Could not save tasks: " + e.getMessage());
        }
    }

    public static ArrayList<Task> loadTasks() {

        ArrayList<Task> tasks = new ArrayList<>();

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return tasks;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\\|", -1);

                if (parts.length != 5) {
                    continue;
                }

                String title = parts[0];
                String subject = parts[1];
                LocalDate dueDate = LocalDate.parse(parts[2]);
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

                task.setCompleted(completed);

                tasks.add(task);
            }

        } catch (IOException | IllegalArgumentException e) {

            System.out.println("Could not load tasks: " + e.getMessage());
        }

        return tasks;
    }
}