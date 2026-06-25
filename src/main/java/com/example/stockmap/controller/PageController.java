package com.example.stockmap.controller;

import com.example.stockmap.service.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {
    private final StockService stockService;

    public PageController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping({"/", "/heatmap"})
    public String heatmap(Model model) {
        model.addAttribute("companies", stockService.getCompanies());
        return "heatmap";
    }

    @GetMapping("/stocks/{symbol}")
    public String detail(@PathVariable String symbol, Model model) {
        model.addAttribute("symbol", symbol.toUpperCase());
        return "stock-detail";
    }
}
