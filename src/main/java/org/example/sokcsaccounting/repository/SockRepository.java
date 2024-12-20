package org.example.sokcsaccounting.repository;

import org.example.sokcsaccounting.data.Socks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SockRepository extends JpaRepository<Socks, UUID> {

    Optional<Socks> findByColorAndCottonPart(String color, Double cottonPart);

    long countByColorAndCottonPartGreaterThan(String color, Double cottonPart);

    long countByColorAndCottonPartLessThan(String color, Double cottonPart);

    long countByColorAndCottonPart(String color, Double cottonPart);

    Page<Socks> findAllByColorAndCottonPartGreaterThan(String color, Double cottonPart, Pageable pageable);

    Page<Socks> findAllByColorAndCottonPartLessThan(String color, Double cottonPart, Pageable pageable);

    Page<Socks> findAllByColorAndCottonPartBetween(String color, Double cottonPart1, Double cottonPart2, Pageable pageable);

    Page<Socks> findAllByColorAndCottonPart(String color, Double cottonPart, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Socks s SET s.quantity = :quantity WHERE s.id = :id")
    void updateSocksById(UUID id, Integer quantity);

    @Modifying
    @Transactional
    @Query("UPDATE Socks s SET s.color = :color, s.cottonPart = :cottonPart, s.quantity = :quantity WHERE s.id = :id")
    void updateSocksById(UUID id, String color, Double cottonPart, Integer quantity);

}
