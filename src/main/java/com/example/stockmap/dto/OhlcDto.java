package com.example.stockmap.dto;

import java.math.BigDecimal;

public record OhlcDto(
        String date,
        BigDecimal open,
        BigDecimal high,
        BigDecimal low,
        BigDecimal close,
        Long volume
) {
}
