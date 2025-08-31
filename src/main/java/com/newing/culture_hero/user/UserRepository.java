
package com.newing.culture_hero.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    Optional<User> findByIdAndCompanyId(UUID id, UUID companyId);

    List<User> findByCompanyId(UUID companyId);
}