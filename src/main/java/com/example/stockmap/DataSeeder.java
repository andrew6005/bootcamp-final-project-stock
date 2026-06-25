package com.example.stockmap;

import com.example.stockmap.entity.Company;
import com.example.stockmap.repository.CompanyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedCompanies(CompanyRepository companyRepository) {
        return args -> {
            List<Company> companies = List.of(
                    company("AAPL", "Apple Inc.", "Technology", "3000000000000", "1980-12-12", "https://www.apple.com/", "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/AAPL.png"),
                    company("MSFT", "Microsoft Corporation", "Technology", "3100000000000", "1986-03-13", "https://www.microsoft.com/", "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/MSFT.png"),
                    company("NVDA", "NVIDIA Corporation", "Semiconductors", "2900000000000", "1999-01-22", "https://www.nvidia.com/", "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/NVDA.png"),
                    company("AVGO", "Broadcom Inc.", "Semiconductors", "760000000000", "2009-08-06", "https://www.broadcom.com/", "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/AVGO.png"),
                    company("ORCL", "Oracle Corporation", "Technology", "480000000000", "1986-03-12", "https://www.oracle.com/", "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/ORCL.png"),
                    company("PLTR", "Palantir Technologies", "Technology", "160000000000", "2020-09-30", "https://www.palantir.com/", "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/PLTR.png"),
                    company("AMD", "Advanced Micro Devices", "Semiconductors", "250000000000"),
                    company("QCOM", "Qualcomm", "Semiconductors", "230000000000"),
                    company("CRM", "Salesforce", "Technology", "260000000000"),
                    company("NOW", "ServiceNow", "Technology", "170000000000"),
                    company("ADBE", "Adobe", "Technology", "210000000000"),
                    company("INTU", "Intuit", "Technology", "180000000000"),
                    company("IBM", "IBM", "Technology", "170000000000"),
                    company("CSCO", "Cisco", "Technology", "190000000000"),
                    company("GOOGL", "Alphabet Inc.", "Communication Services", "2100000000000", "2004-08-19", "https://abc.xyz/", "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/GOOGL.png"),
                    company("META", "Meta Platforms Inc.", "Communication Services", "1200000000000", "2012-05-18", "https://about.meta.com/", "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/META.png"),
                    company("NFLX", "Netflix Inc.", "Communication Services", "290000000000", "2002-05-23", "https://www.netflix.com/", "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/NFLX.png"),
                    company("DIS", "Disney", "Communication Services", "190000000000"),
                    company("TMUS", "T-Mobile US", "Communication Services", "210000000000"),
                    company("VZ", "Verizon", "Communication Services", "170000000000"),
                    company("T", "AT&T", "Communication Services", "130000000000"),
                    company("AMZN", "Amazon.com Inc.", "Consumer Cyclical", "1900000000000", "1997-05-15", "https://www.amazon.com/", "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/AMZN.png"),
                    company("TSLA", "Tesla Inc.", "Automotive", "600000000000", "2010-06-29", "https://www.tesla.com/", "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/TSLA.png"),
                    company("HD", "Home Depot", "Consumer Cyclical", "360000000000"),
                    company("MCD", "McDonald's", "Consumer Cyclical", "220000000000"),
                    company("BKNG", "Booking Holdings", "Consumer Cyclical", "180000000000"),
                    company("SBUX", "Starbucks", "Consumer Cyclical", "90000000000"),
                    company("TJX", "TJX Companies", "Consumer Cyclical", "130000000000"),
                    company("NKE", "Nike", "Consumer Cyclical", "110000000000"),
                    company("JPM", "JPMorgan Chase & Co.", "Financial Services", "550000000000", "1969-03-05", "https://www.jpmorganchase.com/", "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/JPM.png"),
                    company("V", "Visa Inc.", "Financial Services", "520000000000", "2008-03-19", "https://usa.visa.com/", "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/V.png"),
                    company("MA", "Mastercard", "Financial Services", "430000000000"),
                    company("BAC", "Bank of America", "Financial Services", "310000000000"),
                    company("WFC", "Wells Fargo", "Financial Services", "210000000000"),
                    company("GS", "Goldman Sachs", "Financial Services", "150000000000"),
                    company("MS", "Morgan Stanley", "Financial Services", "160000000000"),
                    company("AXP", "American Express", "Financial Services", "210000000000"),
                    company("C", "Citigroup", "Financial Services", "120000000000"),
                    company("BX", "Blackstone", "Financial Services", "160000000000"),
                    company("WMT", "Walmart", "Consumer Defensive", "530000000000"),
                    company("COST", "Costco", "Consumer Defensive", "410000000000"),
                    company("PG", "Procter & Gamble", "Consumer Defensive", "390000000000"),
                    company("KO", "Coca-Cola", "Consumer Defensive", "300000000000"),
                    company("PEP", "PepsiCo", "Consumer Defensive", "240000000000"),
                    company("PM", "Philip Morris", "Consumer Defensive", "200000000000"),
                    company("MO", "Altria", "Consumer Defensive", "90000000000"),
                    company("LLY", "Eli Lilly and Company", "Healthcare", "850000000000", "1970-07-09", "https://www.lilly.com/", "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/LLY.png"),
                    company("JNJ", "Johnson & Johnson", "Healthcare", "370000000000", "1944-09-24", "https://www.jnj.com/", "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/JNJ.png"),
                    company("UNH", "UnitedHealth", "Healthcare", "460000000000"),
                    company("ABBV", "AbbVie", "Healthcare", "330000000000"),
                    company("MRK", "Merck", "Healthcare", "320000000000"),
                    company("ABT", "Abbott Laboratories", "Healthcare", "240000000000"),
                    company("TMO", "Thermo Fisher", "Healthcare", "210000000000"),
                    company("DHR", "Danaher", "Healthcare", "180000000000"),
                    company("PFE", "Pfizer", "Healthcare", "150000000000"),
                    company("XOM", "Exxon Mobil Corporation", "Energy", "470000000000", "1970-01-13", "https://corporate.exxonmobil.com/", "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/XOM.png"),
                    company("CVX", "Chevron", "Energy", "280000000000"),
                    company("COP", "ConocoPhillips", "Energy", "130000000000"),
                    company("GE", "GE Aerospace", "Industrials", "180000000000"),
                    company("CAT", "Caterpillar", "Industrials", "180000000000"),
                    company("RTX", "RTX", "Industrials", "160000000000"),
                    company("BA", "Boeing", "Industrials", "110000000000"),
                    company("UNP", "Union Pacific", "Industrials", "140000000000"),
                    company("LIN", "Linde", "Basic Materials", "220000000000"),
                    company("SHW", "Sherwin-Williams", "Basic Materials", "90000000000"),
                    company("NEE", "NextEra Energy", "Utilities", "150000000000"),
                    company("SO", "Southern Company", "Utilities", "90000000000"),
                    company("AMT", "American Tower", "Real Estate", "90000000000"),
                    company("PLD", "Prologis", "Real Estate", "100000000000")
            );

            for (Company company : companies) {
                companyRepository.findBySymbolIgnoreCase(company.getSymbol())
                        .ifPresentOrElse(existing -> {
                            companyRepository.ensureProfileRow(existing.getId());
                            existing.setCompanyName(company.getCompanyName());
                            existing.setIndustry(company.getIndustry());
                            existing.setMarketCap(company.getMarketCap());
                            existing.setIpoDate(company.getIpoDate());
                            existing.setWebUrl(company.getWebUrl());
                            existing.setLogo(company.getLogo());
                            existing.setShareOutstanding(company.getShareOutstanding());
                            companyRepository.save(existing);
                        }, () -> companyRepository.save(company));
            }
        };
    }

    private Company company(String symbol, String companyName, String industry, String marketCap) {
        return company(symbol, companyName, industry, marketCap, null, null, null);
    }

    private Company company(String symbol, String companyName, String industry, String marketCap, String ipoDate, String webUrl, String logo) {
        Company company = new Company();
        company.setSymbol(symbol);
        company.setCompanyName(companyName);
        company.setIndustry(industry);
        company.setMarketCap(new BigDecimal(marketCap));
        company.setShareOutstanding(new BigDecimal(marketCap).divide(new BigDecimal("1000000000")));
        if (ipoDate != null) company.setIpoDate(LocalDate.parse(ipoDate));
        company.setWebUrl(webUrl);
        company.setLogo(logo);
        return company;
    }
}
