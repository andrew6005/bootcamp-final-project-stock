package com.example.stockmap.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stocks")
@SecondaryTable(
        name = "stock_profiles",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "stock_id")
)
public class Company {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String symbol;
    private String companyName;
    private String industry;
    @Column(table = "stock_profiles", columnDefinition = "TEXT")
    private String logo;
    @Column(table = "stock_profiles")
    private BigDecimal marketCap;
    @Column(table = "stock_profiles")
    private BigDecimal shareOutstanding;
    @Column(table = "stock_profiles")
    private LocalDate ipoDate;
    @Column(table = "stock_profiles", columnDefinition = "TEXT")
    private String webUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }

    public BigDecimal getShareOutstanding() {
        return shareOutstanding;
    }

    public void setShareOutstanding(BigDecimal shareOutstanding) {
        this.shareOutstanding = shareOutstanding;
    }

    public LocalDate getIpoDate() {
        return ipoDate;
    }

    public void setIpoDate(LocalDate ipoDate) {
        this.ipoDate = ipoDate;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
}
