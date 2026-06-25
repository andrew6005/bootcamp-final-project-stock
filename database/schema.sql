CREATE DATABASE bootcamp;

CREATE TABLE IF NOT EXISTS stocks (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(20) UNIQUE NOT NULL,
    company_name VARCHAR(255),
    industry VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS stock_profiles (
    stock_id BIGINT PRIMARY KEY,
    logo TEXT,
    market_cap NUMERIC(20,2),
    share_outstanding NUMERIC(20,4),
    ipo_date DATE,
    web_url TEXT,
    CONSTRAINT fk_stock_profiles_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS stock_quote (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    current_price NUMERIC(12,2),
    change_amount NUMERIC(12,2),
    change_percent NUMERIC(12,2),
    high_price NUMERIC(12,2),
    low_price NUMERIC(12,2),
    open_price NUMERIC(12,2),
    previous_close NUMERIC(12,2),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock_quote_stock FOREIGN KEY (symbol) REFERENCES stocks(symbol)
);

CREATE TABLE IF NOT EXISTS stock_ohlc_data (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    open_price NUMERIC(12,2),
    high_price NUMERIC(12,2),
    low_price NUMERIC(12,2),
    close_price NUMERIC(12,2),
    volume BIGINT,
    trading_date DATE,
    CONSTRAINT uq_stock_ohlc_symbol_date UNIQUE (symbol, trading_date),
    CONSTRAINT fk_stock_ohlc_stock FOREIGN KEY (symbol) REFERENCES stocks(symbol)
);

INSERT INTO stocks(symbol, company_name, industry) VALUES
('AAPL','Apple Inc.','Technology'),
('MSFT','Microsoft Corporation','Technology'),
('NVDA','NVIDIA Corporation','Technology'),
('TSLA','Tesla Inc.','Automobiles'),
('AMZN','Amazon.com Inc.','Consumer Cyclical'),
('META','Meta Platforms Inc.','Communication Services'),
('GOOGL','Alphabet Inc.','Communication Services'),
('JPM','JPMorgan Chase & Co.','Financial Services'),
('LLY','Eli Lilly and Company','Healthcare'),
('XOM','Exxon Mobil Corporation','Energy')
ON CONFLICT(symbol) DO NOTHING;

INSERT INTO stock_profiles(stock_id, market_cap, share_outstanding, ipo_date, web_url, logo)
SELECT id, market_cap, share_outstanding, ipo_date, web_url, logo
FROM (
    VALUES
    ('AAPL',3500000000000::NUMERIC,4375.48::NUMERIC,'1980-12-12'::DATE,'https://www.apple.com/','https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/AAPL.png'),
    ('MSFT',3400000000000::NUMERIC,7430.00::NUMERIC,'1986-03-13'::DATE,'https://www.microsoft.com/','https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/MSFT.png'),
    ('NVDA',3200000000000::NUMERIC,24490.00::NUMERIC,'1999-01-22'::DATE,'https://www.nvidia.com/','https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/NVDA.png'),
    ('TSLA',900000000000::NUMERIC,3220.96::NUMERIC,'2010-06-29'::DATE,'https://www.tesla.com/','https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/TSLA.png'),
    ('AMZN',2100000000000::NUMERIC,10620.00::NUMERIC,'1997-05-15'::DATE,'https://www.amazon.com/','https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/AMZN.png'),
    ('META',1500000000000::NUMERIC,2520.00::NUMERIC,'2012-05-18'::DATE,'https://about.meta.com/','https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/META.png'),
    ('GOOGL',2200000000000::NUMERIC,12160.00::NUMERIC,'2004-08-19'::DATE,'https://abc.xyz/','https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/GOOGL.png'),
    ('JPM',700000000000::NUMERIC,2820.00::NUMERIC,'1969-03-05'::DATE,'https://www.jpmorganchase.com/','https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/JPM.png'),
    ('LLY',800000000000::NUMERIC,950.00::NUMERIC,'1970-07-09'::DATE,'https://www.lilly.com/','https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/LLY.png'),
    ('XOM',500000000000::NUMERIC,3900.00::NUMERIC,'1970-01-13'::DATE,'https://corporate.exxonmobil.com/','https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/XOM.png')
) AS seed(symbol, market_cap, share_outstanding, ipo_date, web_url, logo)
JOIN stocks ON stocks.symbol = seed.symbol
ON CONFLICT(stock_id) DO NOTHING;
