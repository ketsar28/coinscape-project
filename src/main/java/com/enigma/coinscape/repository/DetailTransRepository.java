package com.enigma.coinscape.repository;

import com.enigma.coinscape.entities.DetailTrans;
import com.enigma.coinscape.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DetailTransRepository extends JpaRepository<DetailTrans, String> {
}
