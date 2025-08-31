
package com.newing.culture_hero.user;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(UUID companyId, String username, String rawPassword, Role role) {
        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException("Username '" + username + "' already exists");
        }

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setCompanyId(companyId);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User findById(UUID userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public List<User> findByCompanyId(UUID companyId) {
        return userRepository.findByCompanyId(companyId);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}