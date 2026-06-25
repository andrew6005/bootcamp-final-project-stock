package com.example.stockmap.repository;

import com.example.stockmap.entity.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
    List<StockHistory> findBySymbolIgnoreCaseOrderByTradingDateAsc(String symbol);
    boolean existsBySymbolIgnoreCase(String symbol);
}
