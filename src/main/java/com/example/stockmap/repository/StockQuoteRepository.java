package com.example.stockmap.repository;

import com.example.stockmap.entity.StockQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StockQuoteRepository extends JpaRepository<StockQuote, Long> {
    Optional<StockQuote> findTopBySymbolIgnoreCaseOrderByUpdateTimeDesc(String symbol);
    List<StockQuote> findAllByOrderByChangePercentDesc();
}
