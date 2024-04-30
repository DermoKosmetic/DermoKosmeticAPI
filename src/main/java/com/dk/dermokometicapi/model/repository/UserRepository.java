package com.dk.dermokometicapi.model.repository;

import com.dk.dermokometicapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    void deleteByUsername(String username);

    Optional<User> findByUsernameAndPassword(String username, String password);
    Optional<User> findByEmailAndPassword(String email, String password);
}
