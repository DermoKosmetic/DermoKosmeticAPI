package com.dk.dermokometicapi.repositories;

import com.dk.dermokometicapi.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    void deleteByUsername(String username);

    Optional<User> findByUsernameAndPassword(String username, String password);
    Optional<User> findByEmailAndPassword(String email, String password);

    boolean existsByEmail(String email);

    Optional<User> findByemail(String email);

    void deleteByEmail(String email);
}
