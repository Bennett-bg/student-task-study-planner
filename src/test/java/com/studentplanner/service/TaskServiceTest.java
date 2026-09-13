
package com.studentplanner.service;

import com.studentplanner.model.Task;
import com.studentplanner.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService(new TaskRepository());
    }

    @Test
    void shouldCreateValidTask() throws SQLException {
        Task task = service.createTask(
                "Study Java",
                "Complete service layer",
                null,
                3,
                "2026-09-20",
                "18:00",
                60,
                null
        );

        assertNotNull(task);
        assertTrue(task.getId() > 0);
        assertEquals("Study Java", task.getTitle());
        assertEquals(3, task.getPriority());
        assertFalse(task.isCompleted());
        assertNull(task.getCompletedAt());
    }

    @Test
    void shouldRejectBlankTitle() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.createTask(
                        "   ",
                        null,
                        null,
                        3,
                        null,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    void shouldRejectNullTitle() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.createTask(
                        null,
                        null,
                        null,
                        3,
                        null,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    void shouldRejectInvalidPriority() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.createTask(
                        "Invalid priority",
                        null,
                        null,
                        6,
                        null,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    void shouldRejectPriorityBelowOne() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.createTask(
                        "Invalid priority",
                        null,
                        null,
                        0,
                        null,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    void shouldRejectInvalidEstimatedMinutes() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.createTask(
                        "Invalid duration",
                        null,
                        null,
                        3,
                        null,
                        null,
                        0,
                        null
                )
        );
    }

    @Test
    void shouldCreateTaskWithoutEstimatedMinutes() throws SQLException {
        Task task = service.createTask(
                "No estimate",
                null,
                null,
                3,
                null,
                null,
                null,
                null
        );

        assertNotNull(task);
        assertNull(task.getEstimatedMinutes());
    }

    @Test
    void shouldGetTask() throws SQLException {
        Task created = service.createTask(
                "Get this task",
                null,
                null,
                3,
                null,
                null,
                null,
                null
        );

        Task found = service.getTask(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Get this task", found.getTitle());
    }

    @Test
    void shouldGetAllTasks() throws SQLException {
        service.createTask(
                "Task One",
                null,
                null,
                2,
                null,
                null,
                null,
                null
        );

        service.createTask(
                "Task Two",
                null,
                null,
                4,
                null,
                null,
                null,
                null
        );

        List<Task> tasks = service.getAllTasks();

        assertTrue(tasks.size() >= 2);
    }

    @Test
    void shouldUpdateTask() throws SQLException {
        Task task = service.createTask(
                "Original title",
                "Original description",
                null,
                2,
                null,
                null,
                null,
                null
        );

        task.setTitle("Updated title");
        task.setDescription("Updated description");
        task.setPriority(5);

        service.updateTask(task);

        Task updated = service.getTask(task.getId());

        assertEquals("Updated title", updated.getTitle());
        assertEquals("Updated description", updated.getDescription());
        assertEquals(5, updated.getPriority());
    }

    @Test
    void shouldDeleteTask() throws SQLException {
        Task task = service.createTask(
                "Delete me",
                null,
                null,
                3,
                null,
                null,
                null,
                null
        );

        int id = task.getId();

        service.deleteTask(id);

        assertNull(service.getTask(id));
    }

    @Test
    void shouldCompleteTask() throws SQLException {
        Task task = service.createTask(
                "Complete me",
                null,
                null,
                3,
                null,
                null,
                null,
                null
        );

        Task completed = service.completeTask(task.getId());

        assertTrue(completed.isCompleted());
        assertNotNull(completed.getCompletedAt());

        Task stored = service.getTask(task.getId());

        assertTrue(stored.isCompleted());
        assertNotNull(stored.getCompletedAt());
    }

    @Test
    void shouldUncompleteTask() throws SQLException {
        Task task = service.createTask(
                "Uncomplete me",
                null,
                null,
                3,
                null,
                null,
                null,
                null
        );

        service.completeTask(task.getId());

        Task uncompleted = service.uncompleteTask(task.getId());

        assertFalse(uncompleted.isCompleted());
        assertNull(uncompleted.getCompletedAt());

        Task stored = service.getTask(task.getId());

        assertFalse(stored.isCompleted());
        assertNull(stored.getCompletedAt());
    }

    @Test
    void shouldRejectCompletingNonexistentTask() {
        assertThrows(
                IllegalStateException.class,
                () -> service.completeTask(999999)
        );
    }

    @Test
    void shouldRejectUncompletingNonexistentTask() {
        assertThrows(
                IllegalStateException.class,
                () -> service.uncompleteTask(999999)
        );
    }

    @Test
    void shouldRejectUpdatingNonexistentTask() {
        Task task = new Task(
                "Does not exist",
                null,
                null,
                3,
                null,
                null,
                null,
                null
        );

        task.setId(999999);

        assertThrows(
                IllegalStateException.class,
                () -> service.updateTask(task)
        );
    }

    @Test
    void shouldRejectDeletingNonexistentTask() {
        assertThrows(
                IllegalStateException.class,
                () -> service.deleteTask(999999)
        );
    }

    @Test
    void shouldRejectInvalidTaskId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.getTask(0)
        );
    }

    @Test
    void shouldRejectCompletingAlreadyCompletedTask()
            throws SQLException {

        Task task = service.createTask(
                "Already complete",
                null,
                null,
                3,
                null,
                null,
                null,
                null
        );

        service.completeTask(task.getId());

        assertThrows(
                IllegalStateException.class,
                () -> service.completeTask(task.getId())
        );
    }

    @Test
    void shouldRejectUncompletingAlreadyIncompleteTask()
            throws SQLException {

        Task task = service.createTask(
                "Already incomplete",
                null,
                null,
                3,
                null,
                null,
                null,
                null
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.uncompleteTask(task.getId())
        );
    }
}

