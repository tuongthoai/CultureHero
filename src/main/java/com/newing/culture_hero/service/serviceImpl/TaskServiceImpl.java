package com.newing.culture_hero.service.serviceImpl;

import com.newing.culture_hero.entity.Task;
import com.newing.culture_hero.entity.TaskDifficulty;
import com.newing.culture_hero.entity.TaskStatus;
import com.newing.culture_hero.repository.TaskRepository;
import com.newing.culture_hero.service.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    @Override
    public Task createTask(
            String title,
            String description,
            int rewardXp,
            int rewardCoins,
            TaskDifficulty difficulty) {
        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setTitle(title);
        task.setDescription(description);
        task.setRewardXp(rewardXp);
        task.setRewardCoins(rewardCoins);
        task.setDifficulty(difficulty);
        task.setStatus(TaskStatus.ACTIVE);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return taskRepository.findById(id);
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    @Override
    public Task updateTask(
            UUID id,
            String title,
            String description,
            int rewardXp,
            int rewardCoins,
            TaskDifficulty difficulty,
            TaskStatus status) {
        Task task =
                taskRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("Task not found: " + id));

        task.setTitle(title);
        task.setDescription(description);
        task.setRewardXp(rewardXp);
        task.setRewardCoins(rewardCoins);
        task.setDifficulty(difficulty);
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(UUID id) {
        taskRepository.deleteById(id);
    }
}
