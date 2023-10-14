package com.enigma.coinscape.repository;

import com.enigma.coinscape.entities.Admin;
import com.enigma.coinscape.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {
    Optional<Admin> findByPayCode(String noUserDestination);
    boolean existsByPayCode(String payCode);

    Optional<Admin> findByEmail(String email);

    Optional<Admin> findFirstByEmail(String email);
}
