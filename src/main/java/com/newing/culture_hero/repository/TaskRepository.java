package com.newing.culture_hero.repository;

import com.newing.culture_hero.entity.Task;
import com.newing.culture_hero.entity.TaskStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByStatus(TaskStatus status);
}
