package com.newing.culture_hero.controller;

import com.newing.culture_hero.entity.Task;
import com.newing.culture_hero.service.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PreAuthorize("hasAnyRole('CLIENT_ADMIN')")
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task taskRequest) {
        Task task =
                taskService.createTask(
                        taskRequest.getTitle(),
                        taskRequest.getDescription(),
                        taskRequest.getRewardXp(),
                        taskRequest.getRewardCoins(),
                        taskRequest.getDifficulty());
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PreAuthorize("hasAnyRole('CONSULTANT_ADMIN','CLIENT_ADMIN','PARTICIPANT')")
    @GetMapping
    public ResponseEntity<List<Task>> getTasks() {
        List<Task> tasks = taskService.findAll();
        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("hasAnyRole('CONSULTANT_ADMIN','CLIENT_ADMIN','PARTICIPANT')")
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable UUID id) {
        Optional<Task> task = taskService.findById(id);
        return task.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('CONSULTANT_ADMIN','CLIENT_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable UUID id, @RequestBody Task taskRequest) {
        Task updated =
                taskService.updateTask(
                        id,
                        taskRequest.getTitle(),
                        taskRequest.getDescription(),
                        taskRequest.getRewardXp(),
                        taskRequest.getRewardCoins(),
                        taskRequest.getDifficulty(),
                        taskRequest.getStatus());
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAnyRole('CONSULTANT_ADMIN','CLIENT_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('PARTICIPANT')")
    @PostMapping("/{id}/complete")
    public ResponseEntity<String> completeTask(@PathVariable UUID id) {
        return ResponseEntity.ok("Task " + id + " marked as completed.");
    }
}
