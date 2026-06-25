package com.example.stockmap.service;

import com.example.stockmap.entity.StockQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class FinnhubService {
    private static final Logger log = LoggerFactory.getLogger(FinnhubService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${finnhub.api.key}")
    private String apiKey;

    public StockQuote getQuote(String symbol) {
        String url = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + apiKey;
        Map body = getForMap(url, symbol, "quote");
        if (body == null || body.get("c") == null) throw new RuntimeException("No quote API response for " + symbol);

        StockQuote q = new StockQuote();
        q.setSymbol(symbol.toUpperCase());
        q.setCurrentPrice(toBigDecimal(body.get("c")));
        q.setChangeAmount(toBigDecimal(body.get("d")));
        q.setChangePercent(toBigDecimal(body.get("dp")));
        q.setHighPrice(toBigDecimal(body.get("h")));
        q.setLowPrice(toBigDecimal(body.get("l")));
        q.setOpenPrice(toBigDecimal(body.get("o")));
        q.setPreviousClose(toBigDecimal(body.get("pc")));
        q.setUpdateTime(LocalDateTime.now());
        return q;
    }

    public Map getCompanyProfile(String symbol) {
        String url = "https://finnhub.io/api/v1/stock/profile2?symbol=" + symbol + "&token=" + apiKey;
        Map body = getForMap(url, symbol, "company profile");
        if (body == null || body.isEmpty()) throw new RuntimeException("No company profile response for " + symbol);
        return body;
    }

    private Map getForMap(String url, String symbol, String requestName) {
        try {
            Map body = restTemplate.getForObject(url, Map.class);
            if (body != null && body.get("error") != null) {
                throw new RuntimeException("Finnhub " + requestName + " error for " + symbol + ": " + body.get("error"));
            }
            return body;
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                log.warn("Finnhub rate limit hit while fetching {} for {}", requestName, symbol);
            }
            throw new RuntimeException(
                    "Finnhub " + requestName + " request failed for " + symbol
                            + " (" + e.getStatusCode() + "): " + e.getResponseBodyAsString(),
                    e
            );
        }
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        return BigDecimal.valueOf(Double.parseDouble(value.toString()));
    }
}
