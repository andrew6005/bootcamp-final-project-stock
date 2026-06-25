import os

import pandas as pd
from sqlalchemy import create_engine, text


CSV_URL = "https://raw.githubusercontent.com/datasets/s-and-p-500-companies/master/data/constituents.csv"
DB_USER = os.getenv("DB_USERNAME", os.getenv("USER", "postgres"))
DB_PASSWORD = os.getenv("DB_PASSWORD", "")
DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = os.getenv("DB_PORT", "5432")
DB_NAME = os.getenv("DB_NAME", "bootcamp")
DB_URL = os.getenv("DB_URL", f"postgresql+psycopg2://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}")


def normalize_columns(df):
    column_map = {
        "Symbol": "symbol",
        "symbol": "symbol",
        "Name": "company_name",
        "Security": "company_name",
        "company_name": "company_name",
        "Sector": "industry",
        "GICS Sector": "industry",
        "industry": "industry",
    }
    normalized = df.rename(columns={column: column_map[column] for column in df.columns if column in column_map})
    required = ["symbol", "company_name", "industry"]
    missing = [column for column in required if column not in normalized.columns]
    if missing:
        raise ValueError(f"CSV is missing columns {missing}. Available columns: {list(df.columns)}")
    return normalized[required].dropna()


def main():
    df = pd.read_csv(CSV_URL)
    df = normalize_columns(df)

    engine = create_engine(DB_URL)
    with engine.begin() as conn:
        for row in df.to_dict("records"):
            conn.execute(
                text(
                    """
                    INSERT INTO stocks(symbol, company_name, industry)
                    VALUES (:symbol, :company_name, :industry)
                    ON CONFLICT(symbol) DO UPDATE SET
                        company_name = EXCLUDED.company_name,
                        industry = EXCLUDED.industry
                    """
                ),
                row,
            )

    print(f"Loaded {len(df)} stock symbols.")


if __name__ == "__main__":
    main()
