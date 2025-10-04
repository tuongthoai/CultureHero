package com.newing.culture_hero.service;

import com.newing.culture_hero.entity.Task;
import com.newing.culture_hero.entity.TaskDifficulty;
import com.newing.culture_hero.entity.TaskStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskService {

  Task createTask(
      String title, String description, int rewardXp, int rewardCoins, TaskDifficulty difficulty);

  List<Task> findAll();

  Optional<Task> findById(UUID id);

  List<Task> findByStatus(TaskStatus status);

  Task updateTask(
      UUID id,
      String title,
      String description,
      int rewardXp,
      int rewardCoins,
      TaskDifficulty difficulty,
      TaskStatus status);

  void deleteTask(UUID id);
}
