package com.example.stockmap.service;

import com.example.stockmap.dto.HeatmapStockDto;
import com.example.stockmap.dto.OhlcDto;
import com.example.stockmap.dto.StockOhlcResponse;
import com.example.stockmap.entity.Company;
import com.example.stockmap.entity.StockHistory;
import com.example.stockmap.entity.StockQuote;
import com.example.stockmap.repository.CompanyRepository;
import com.example.stockmap.repository.StockHistoryRepository;
import com.example.stockmap.repository.StockQuoteRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

@Service
public class StockService {
    private static final Logger log = LoggerFactory.getLogger(StockService.class);
    private static final long FINNHUB_FREE_TIER_DELAY_MS = 1_100L;
    private static final Duration QUOTE_CACHE_TTL = Duration.ofMinutes(5);
    private static final Duration HEATMAP_CACHE_TTL = Duration.ofMinutes(5);
    private static final Duration COMPANY_CACHE_TTL = Duration.ofHours(6);
    private static final Duration OHLC_CACHE_TTL = Duration.ofHours(12);
    private static final Map<String, BigDecimal> SAMPLE_CHANGE_PERCENT = Map.ofEntries(
            Map.entry("AAPL", BigDecimal.valueOf(3.14)),
            Map.entry("MSFT", BigDecimal.valueOf(3.02)),
            Map.entry("NVDA", BigDecimal.valueOf(-1.25)),
            Map.entry("AVGO", BigDecimal.valueOf(-2.23)),
            Map.entry("ORCL", BigDecimal.valueOf(-2.76)),
            Map.entry("PLTR", BigDecimal.valueOf(7.76)),
            Map.entry("AMD", BigDecimal.valueOf(-6.89)),
            Map.entry("QCOM", BigDecimal.valueOf(-1.55)),
            Map.entry("GOOGL", BigDecimal.valueOf(1.07)),
            Map.entry("META", BigDecimal.valueOf(8.81)),
            Map.entry("NFLX", BigDecimal.valueOf(3.91)),
            Map.entry("AMZN", BigDecimal.valueOf(1.41)),
            Map.entry("TSLA", BigDecimal.valueOf(1.12)),
            Map.entry("MCD", BigDecimal.valueOf(-0.33)),
            Map.entry("BKNG", BigDecimal.valueOf(2.47)),
            Map.entry("JPM", BigDecimal.valueOf(2.06)),
            Map.entry("V", BigDecimal.valueOf(2.33)),
            Map.entry("MA", BigDecimal.valueOf(1.72)),
            Map.entry("BAC", BigDecimal.valueOf(2.42)),
            Map.entry("WFC", BigDecimal.valueOf(3.99)),
            Map.entry("BX", BigDecimal.valueOf(1.60)),
            Map.entry("WMT", BigDecimal.valueOf(-3.92)),
            Map.entry("COST", BigDecimal.valueOf(-1.15)),
            Map.entry("KO", BigDecimal.valueOf(0.03)),
            Map.entry("LLY", BigDecimal.valueOf(-0.64)),
            Map.entry("JNJ", BigDecimal.valueOf(0.00)),
            Map.entry("UNH", BigDecimal.valueOf(2.63)),
            Map.entry("ABBV", BigDecimal.valueOf(-0.23)),
            Map.entry("MRK", BigDecimal.valueOf(-2.44)),
            Map.entry("PFE", BigDecimal.valueOf(-0.83)),
            Map.entry("XOM", BigDecimal.valueOf(-0.32)),
            Map.entry("CVX", BigDecimal.valueOf(-0.04)),
            Map.entry("GE", BigDecimal.valueOf(0.32)),
            Map.entry("CAT", BigDecimal.valueOf(-6.90)),
            Map.entry("RTX", BigDecimal.valueOf(1.08)),
            Map.entry("BA", BigDecimal.valueOf(0.97))
    );

    private final CompanyRepository companyRepository;
    private final StockQuoteRepository quoteRepository;
    private final StockHistoryRepository historyRepository;
    private final FinnhubService finnhubService;
    private final RedisCacheService redisCacheService;

    public StockService(
            CompanyRepository companyRepository,
            StockQuoteRepository quoteRepository,
            StockHistoryRepository historyRepository,
            FinnhubService finnhubService,
            RedisCacheService redisCacheService
    ) {
        this.companyRepository = companyRepository;
        this.quoteRepository = quoteRepository;
        this.historyRepository = historyRepository;
        this.finnhubService = finnhubService;
        this.redisCacheService = redisCacheService;
    }

    public List<Company> getCompanies() {
        String cacheKey = "stocks:companies:all";
        return redisCacheService.get(cacheKey, new TypeReference<List<Company>>() {})
                .orElseGet(() -> {
                    List<Company> companies = companyRepository.findAll(Sort.by(Sort.Direction.DESC, "marketCap"));
                    redisCacheService.put(cacheKey, companies, COMPANY_CACHE_TTL);
                    return companies;
                });
    }

    public StockQuote refreshQuote(String symbol) {
        return refreshQuote(symbol, true);
    }

    private StockQuote refreshQuote(String symbol, boolean evictHeatmap) {
        StockQuote quote = finnhubService.getQuote(normalizeSymbol(symbol));
        StockQuote saved = quoteRepository.save(quote);
        redisCacheService.put(quoteCacheKey(symbol), saved, QUOTE_CACHE_TTL);
        if (evictHeatmap) redisCacheService.evict("stocks:heatmap");
        return saved;
    }

    public StockQuote getLatestQuote(String symbol) {
        String cacheKey = quoteCacheKey(symbol);
        return redisCacheService.get(cacheKey, new TypeReference<StockQuote>() {})
                .orElseGet(() -> {
                    StockQuote quote = quoteRepository.findTopBySymbolIgnoreCaseOrderByUpdateTimeDesc(symbol)
                            .orElseGet(() -> fallbackQuote(symbol));
                    redisCacheService.put(cacheKey, quote, QUOTE_CACHE_TTL);
                    return quote;
                });
    }

    public List<HeatmapStockDto> getHeatmapData() {
        String cacheKey = "stocks:heatmap";
        return redisCacheService.get(cacheKey, new TypeReference<List<HeatmapStockDto>>() {})
                .orElseGet(() -> {
                    List<HeatmapStockDto> heatmap = getCompanies().stream()
                            .map(company -> {
                                StockQuote quote = getLatestQuote(company.getSymbol());
                                return new HeatmapStockDto(
                                        company.getId(),
                                        company.getSymbol(),
                                        company.getCompanyName(),
                                        quote.getCurrentPrice(),
                                        quote.getChangeAmount(),
                                        quote.getChangePercent(),
                                        company.getMarketCap(),
                                        company.getIndustry(),
                                        company.getIpoDate() == null ? null : company.getIpoDate().toString(),
                                        company.getWebUrl(),
                                        company.getShareOutstanding(),
                                        company.getLogo()
                                );
                            })
                            .toList();
                    redisCacheService.put(cacheKey, heatmap, HEATMAP_CACHE_TTL);
                    return heatmap;
                });
    }

    public StockOhlcResponse getOhlcData(Long id, String symbol) {
        Company company = resolveCompany(id, symbol);
        String cacheKey = ohlcCacheKey(company.getSymbol());
        return redisCacheService.get(cacheKey, new TypeReference<StockOhlcResponse>() {})
                .orElseGet(() -> {
                    StockOhlcResponse response = buildOhlcResponse(company);
                    redisCacheService.put(cacheKey, response, OHLC_CACHE_TTL);
                    return response;
                });
    }

    private StockOhlcResponse buildOhlcResponse(Company company) {
        ensureHistory(company);

        List<OhlcDto> ohlcs = historyRepository.findBySymbolIgnoreCaseOrderByTradingDateAsc(company.getSymbol()).stream()
                .map(history -> new OhlcDto(
                        history.getTradingDate().toString(),
                        history.getOpenPrice(),
                        history.getHighPrice(),
                        history.getLowPrice(),
                        history.getClosePrice(),
                        history.getVolume()
                ))
                .toList();
        List<OhlcDto> datedToToday = makeContinuousOhlcDatesEndingToday(ohlcs);

        return new StockOhlcResponse(
                company.getId(),
                company.getSymbol(),
                company.getCompanyName(),
                company.getMarketCap(),
                company.getIndustry(),
                company.getShareOutstanding(),
                company.getLogo(),
                datedToToday
        );
    }

    private List<OhlcDto> makeContinuousOhlcDatesEndingToday(List<OhlcDto> ohlcs) {
        if (ohlcs.isEmpty()) return ohlcs;

        LocalDate date = LocalDate.now();
        LocalDate[] dates = new LocalDate[ohlcs.size()];
        for (int i = ohlcs.size() - 1; i >= 0; i--) {
            dates[i] = date;
            date = previousTradingDate(date);
        }

        return java.util.stream.IntStream.range(0, ohlcs.size())
                .mapToObj(i -> {
                    OhlcDto row = ohlcs.get(i);
                    return new OhlcDto(
                            dates[i].toString(),
                            row.open(),
                            row.high(),
                            row.low(),
                            row.close(),
                            row.volume()
                    );
                })
                .toList();
    }

    private LocalDate previousTradingDate(LocalDate date) {
        LocalDate previous = date.minusDays(1);
        while (previous.getDayOfWeek().getValue() >= 6) {
            previous = previous.minusDays(1);
        }
        return previous;
    }

    @Transactional
    public Company refreshCompanyProfile(String symbol) {
        Company company = companyRepository.findBySymbolIgnoreCase(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Unknown stock symbol: " + symbol));

        Map profile = finnhubService.getCompanyProfile(company.getSymbol());
        companyRepository.ensureProfileRow(company.getId());
        company.setCompanyName(text(profile.get("name"), company.getCompanyName()));
        company.setIndustry(text(profile.get("finnhubIndustry"), company.getIndustry()));
        company.setLogo(text(profile.get("logo"), company.getLogo()));
        company.setWebUrl(text(profile.get("weburl"), company.getWebUrl()));
        company.setMarketCap(number(profile.get("marketCapitalization"), company.getMarketCap()));
        company.setShareOutstanding(number(profile.get("shareOutstanding"), company.getShareOutstanding()));
        company.setIpoDate(date(profile.get("ipo"), company.getIpoDate()));
        Company saved = companyRepository.save(company);
        redisCacheService.evict("stocks:companies:all");
        redisCacheService.evict("stocks:heatmap");
        redisCacheService.evict(ohlcCacheKey(saved.getSymbol()));
        return saved;
    }

    @Scheduled(fixedDelay = 300000, initialDelay = 15000)
    public void refreshAllQuotes() {
        boolean refreshedAnyQuote = false;
        for (Company company : companyRepository.findAll()) {
            try {
                refreshQuote(company.getSymbol(), false);
                refreshedAnyQuote = true;
                Thread.sleep(FINNHUB_FREE_TIER_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Quote refresh interrupted after {}", company.getSymbol());
                return;
            } catch (Exception e) {
                log.warn("API fail: {} - {}", company.getSymbol(), e.getMessage());
            }
        }
        if (refreshedAnyQuote) redisCacheService.evict("stocks:heatmap");
    }

    private Company resolveCompany(Long id, String symbol) {
        if (id != null) {
            return companyRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown stock id: " + id));
        }
        if (symbol != null && !symbol.isBlank()) {
            return companyRepository.findBySymbolIgnoreCase(symbol)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown stock symbol: " + symbol));
        }
        return companyRepository.findBySymbolIgnoreCase("TSLA")
                .orElseGet(() -> companyRepository.findAll().stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No stock symbols found")));
    }

    private void ensureHistory(Company company) {
        if (historyRepository.existsBySymbolIgnoreCase(company.getSymbol())) return;

        StockQuote quote = getLatestQuote(company.getSymbol());
        BigDecimal current = quote.getCurrentPrice() == null || quote.getCurrentPrice().compareTo(BigDecimal.ZERO) <= 0
                ? BigDecimal.valueOf(100)
                : quote.getCurrentPrice();
        Random random = new Random(company.getSymbol().hashCode());
        LocalDate start = LocalDate.now().minusMonths(6);
        double close = current.doubleValue() * (1.12 + random.nextDouble() * 0.35);

        for (int i = 0; i < 126; i++) {
            double drift = (current.doubleValue() - close) / Math.max(1, 126 - i);
            double open = close + (random.nextDouble() - 0.5) * current.doubleValue() * 0.025;
            close = Math.max(current.doubleValue() * 0.45, open + drift + (random.nextDouble() - 0.47) * current.doubleValue() * 0.045);
            double spread = current.doubleValue() * (0.018 + random.nextDouble() * 0.038);

            StockHistory history = new StockHistory();
            history.setSymbol(company.getSymbol());
            history.setTradingDate(start.plusDays(i));
            history.setOpenPrice(scale(open));
            history.setClosePrice(scale(close));
            history.setHighPrice(scale(Math.max(open, close) + spread * random.nextDouble()));
            history.setLowPrice(scale(Math.min(open, close) - spread * random.nextDouble()));
            history.setVolume(20_000_000L + Math.round(random.nextDouble() * 90_000_000L));
            historyRepository.save(history);
        }
    }

    private StockQuote fallbackQuote(String symbol) {
        String normalizedSymbol = normalizeSymbol(symbol);
        int hash = Math.abs(normalizedSymbol.hashCode());
        BigDecimal currentPrice = BigDecimal.valueOf(60 + hash % 420).setScale(2, RoundingMode.HALF_UP);
        BigDecimal changePercent = SAMPLE_CHANGE_PERCENT
                .getOrDefault(normalizedSymbol, BigDecimal.valueOf(((hash % 700) - 350) / 100.0))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal changeAmount = currentPrice.multiply(changePercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        StockQuote quote = new StockQuote();
        quote.setSymbol(normalizedSymbol);
        quote.setCurrentPrice(currentPrice);
        quote.setChangePercent(changePercent);
        quote.setChangeAmount(changeAmount);
        quote.setOpenPrice(currentPrice.subtract(changeAmount.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP)));
        quote.setHighPrice(currentPrice.multiply(BigDecimal.valueOf(1.025)).setScale(2, RoundingMode.HALF_UP));
        quote.setLowPrice(currentPrice.multiply(BigDecimal.valueOf(0.975)).setScale(2, RoundingMode.HALF_UP));
        quote.setPreviousClose(currentPrice.subtract(changeAmount));
        quote.setUpdateTime(LocalDateTime.now());
        return quote;
    }

    private String quoteCacheKey(String symbol) {
        return "stocks:quote:" + normalizeSymbol(symbol);
    }

    private String ohlcCacheKey(String symbol) {
        return "stocks:ohlc:v2:" + normalizeSymbol(symbol) + ":" + LocalDate.now();
    }

    private String normalizeSymbol(String symbol) {
        return symbol == null ? "" : symbol.toUpperCase(Locale.ROOT);
    }

    private BigDecimal scale(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private String text(Object value, String fallback) {
        return value == null || value.toString().isBlank() ? fallback : value.toString();
    }

    private BigDecimal number(Object value, BigDecimal fallback) {
        if (value == null || value.toString().isBlank()) return fallback;
        return BigDecimal.valueOf(Double.parseDouble(value.toString()));
    }

    private LocalDate date(Object value, LocalDate fallback) {
        if (value == null || value.toString().isBlank()) return fallback;
        return LocalDate.parse(value.toString());
    }
}
