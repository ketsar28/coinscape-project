package com.enigma.coinscape.repository;

import com.enigma.coinscape.entities.Role;
import com.enigma.coinscape.entities.constants.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByRole(ERole role);
}
