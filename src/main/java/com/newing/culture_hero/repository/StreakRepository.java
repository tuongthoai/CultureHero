package com.newing.culture_hero.repository;

import com.newing.culture_hero.entity.Streak;
import com.newing.culture_hero.user.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StreakRepository extends JpaRepository<Streak, UUID> {
    Optional<Streak> findByUser(User userId);
}
