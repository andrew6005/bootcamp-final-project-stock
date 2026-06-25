package com.example.stockmap.controller;

import com.example.stockmap.dto.HeatmapStockDto;
import com.example.stockmap.dto.StockOhlcResponse;
import com.example.stockmap.entity.Company;
import com.example.stockmap.entity.StockQuote;
import com.example.stockmap.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockApiController {
    private final StockService stockService;

    public StockApiController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/quote/{symbol}")
    public StockQuote quote(@PathVariable String symbol) {
        return stockService.getLatestQuote(symbol);
    }

    @GetMapping("/symbols")
    public List<String> symbols() {
        return stockService.getCompanies().stream()
                .map(Company::getSymbol)
                .toList();
    }

    @GetMapping("/companies")
    public List<Company> companies() {
        return stockService.getCompanies();
    }

    @GetMapping("/quotes")
    public List<HeatmapStockDto> quotes() {
        return stockService.getHeatmapData();
    }

    @PostMapping("/refresh/{symbol}")
    public StockQuote refresh(@PathVariable String symbol) {
        return stockService.refreshQuote(symbol);
    }

    @GetMapping("/ohlc/{symbol}")
    public StockOhlcResponse ohlc(@PathVariable String symbol) {
        return stockService.getOhlcData(null, symbol);
    }
}
