package com.newing.culture_hero.repository;

import com.newing.culture_hero.entity.Company;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findByCode(String code);

    // Optional<Company> findByName(String name);

    boolean existsByCode(String code);
}
