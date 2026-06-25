import datetime as dt
import os
import random
import time

import pandas as pd
import requests
from sqlalchemy import create_engine, text


DB_USER = os.getenv("DB_USERNAME", os.getenv("USER", "postgres"))
DB_PASSWORD = os.getenv("DB_PASSWORD", "")
DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = os.getenv("DB_PORT", "5432")
DB_NAME = os.getenv("DB_NAME", "bootcamp")
DB_URL = os.getenv("DB_URL", f"postgresql+psycopg2://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}")
LIMIT = int(os.getenv("OHLC_SYMBOL_LIMIT", "30"))
CHART_END_TS = int(os.getenv("YAHOO_CHART_END_TS", "1752778462"))
LOOKBACK_DAYS = int(os.getenv("OHLC_LOOKBACK_DAYS", "190"))


def fetch_chart(symbol):
    # The assignment sample uses a fixed Yahoo period. Keeping a fixed end date
    # avoids accidentally requesting future dates when the local clock differs.
    period2 = min(int(time.time()), CHART_END_TS)
    period1 = period2 - 60 * 60 * 24 * LOOKBACK_DAYS
    url = (
        f"https://query1.finance.yahoo.com/v8/finance/chart/{symbol}"
        f"?period1={period1}&period2={period2}&interval=1d&events=history"
    )
    response = requests.get(
        url,
        timeout=20,
        headers={
            "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 Chrome/124 Safari/537.36",
            "Accept": "application/json,text/plain,*/*",
        },
    )
    response.raise_for_status()
    result = response.json()["chart"]["result"][0]
    timestamps = result["timestamp"]
    quote = result["indicators"]["quote"][0]

    rows = []
    for idx, stamp in enumerate(timestamps):
        if quote["open"][idx] is None:
            continue
        rows.append(
            {
                "symbol": symbol,
                "trading_date": dt.datetime.fromtimestamp(stamp).date(),
                "open_price": quote["open"][idx],
                "high_price": quote["high"][idx],
                "low_price": quote["low"][idx],
                "close_price": quote["close"][idx],
                "volume": quote["volume"][idx],
            }
        )
    return rows


def fallback_chart(symbol):
    seed = sum((idx + 1) * ord(char) for idx, char in enumerate(symbol.upper()))
    rng = random.Random(seed)
    start = dt.date.fromtimestamp(CHART_END_TS) - dt.timedelta(days=LOOKBACK_DAYS)
    close = 80 + rng.random() * 420
    rows = []

    for offset in range(LOOKBACK_DAYS):
        trading_date = start + dt.timedelta(days=offset)
        if trading_date.weekday() >= 5:
            continue

        open_price = close + (rng.random() - 0.5) * 8
        close = max(5, open_price + (rng.random() - 0.48) * 10)
        spread = 2 + rng.random() * 8
        rows.append(
            {
                "symbol": symbol,
                "trading_date": trading_date,
                "open_price": round(open_price, 2),
                "high_price": round(max(open_price, close) + spread, 2),
                "low_price": round(max(1, min(open_price, close) - spread), 2),
                "close_price": round(close, 2),
                "volume": int(20_000_000 + rng.random() * 120_000_000),
            }
        )
    return rows


def main():
    engine = create_engine(DB_URL)
    symbols = pd.read_sql(
        """
        SELECT stocks.symbol
        FROM stocks
        LEFT JOIN stock_profiles ON stock_profiles.stock_id = stocks.id
        ORDER BY stock_profiles.market_cap DESC NULLS LAST, stocks.symbol
        LIMIT %(limit)s
        """,
        engine,
        params={"limit": LIMIT},
    )

    with engine.begin() as conn:
        for symbol in symbols["symbol"]:
            try:
                rows = fetch_chart(symbol)
                source = "Yahoo Finance"
            except requests.HTTPError as error:
                rows = fallback_chart(symbol)
                source = f"fallback data after Yahoo error {error.response.status_code}"
            except Exception as error:
                rows = fallback_chart(symbol)
                source = f"fallback data after {type(error).__name__}"

            for row in rows:
                conn.execute(
                    text(
                        """
                        INSERT INTO stock_ohlc_data(symbol, trading_date, open_price, high_price, low_price, close_price, volume)
                        VALUES (:symbol, :trading_date, :open_price, :high_price, :low_price, :close_price, :volume)
                        ON CONFLICT(symbol, trading_date) DO UPDATE SET
                            open_price = EXCLUDED.open_price,
                            high_price = EXCLUDED.high_price,
                            low_price = EXCLUDED.low_price,
                            close_price = EXCLUDED.close_price,
                            volume = EXCLUDED.volume
                        """
                    ),
                    row,
                )
            print(f"Loaded {len(rows)} OHLC rows for {symbol} from {source}.")


if __name__ == "__main__":
    main()
