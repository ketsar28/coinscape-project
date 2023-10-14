package com.enigma.coinscape.repository;

import com.enigma.coinscape.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    Optional<User> findFirstByEmail(String email);
    Optional<User> findFirstById(String id);
    Optional<User> findByPhoneNumber(String noUser);
    Optional<User> findByEmail(String email);
}
