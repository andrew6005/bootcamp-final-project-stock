package com.example.stockmap.repository;

import com.example.stockmap.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findBySymbolIgnoreCase(String symbol);

    @Transactional
    @Modifying
    @Query(value = """
            INSERT INTO stock_profiles(stock_id)
            VALUES (:stockId)
            ON CONFLICT(stock_id) DO NOTHING
            """, nativeQuery = true)
    void ensureProfileRow(@Param("stockId") Long stockId);
}
