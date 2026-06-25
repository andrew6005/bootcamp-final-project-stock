package com.example.stockmap.controller;

import com.example.stockmap.dto.HeatmapStockDto;
import com.example.stockmap.dto.StockOhlcResponse;
import com.example.stockmap.entity.Company;
import com.example.stockmap.service.StockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/data")
@CrossOrigin(origins = "*")
public class DataController {
    private final StockService stockService;

    public DataController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/heatmap")
    public List<HeatmapStockDto> heatmap() {
        return stockService.getHeatmapData();
    }

    @GetMapping("/ohlc")
    public StockOhlcResponse ohlc(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String symbol
    ) {
        return stockService.getOhlcData(id, symbol);
    }

    @PostMapping("/profile/refresh")
    public Company refreshCompanyProfile(@RequestParam String symbol) {
        return stockService.refreshCompanyProfile(symbol);
    }
}
