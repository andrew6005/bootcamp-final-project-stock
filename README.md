# Stock Map Final Project

This project follows the final-project system design guideline in one Spring Boot app:

- `project-data-provider` responsibility: Finnhub quote/profile calls live in `FinnhubService`.
- `project-stock-data` responsibility: persisted `stocks`, `stock_profiles`, `stock_ohlc_data`, quotes, and `/data/...` JSON APIs.
- `project-heatmap-ui` responsibility: Thymeleaf pages plus separated CSS and JavaScript for the heatmap and candlestick chart.

## Create database

```bash
createdb bootcamp
```

If `createdb` says database already exists, ignore it.

## Run project

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080/heatmap
```

Useful APIs:

```text
GET http://localhost:8080/data/heatmap
GET http://localhost:8080/data/ohlc?symbol=TSLA
GET http://localhost:8080/data/ohlc?id=1
POST http://localhost:8080/data/profile/refresh?symbol=AAPL

GET http://localhost:8080/api/stocks/symbols
GET http://localhost:8080/api/stocks/companies
GET http://localhost:8080/api/stocks/quotes
GET http://localhost:8080/api/stocks/ohlc/TSLA
```

Redis is used with a read-through cache pattern. Company lists are cached for 6 hours, real-time quote/heatmap data for 5 minutes, and OHLCV responses for 12 hours. On a cache miss, the app reads from Postgres or the external provider, then stores the result in Redis.

## Python setup

```bash
./python_env_setup.sh
source bootcamp-env/bin/activate
python python/prepare_1_load_snp500_symbol.py
python python/prepare_2_load_ohlcv_data.py
```

`prepare_1_load_snp500_symbol.py` loads symbols from the GitHub datasets CSV into `stocks`.
`prepare_2_load_ohlcv_data.py` loads OHLCV history from Yahoo Finance into `stock_ohlc_data`. The same workflow is also provided as `python/prepare_2_load_ohlcv_data.ipynb` for the required notebook submission.
Company profile and real-time quote data are fetched from Finnhub by the Spring Boot app.

## Docker setup

```bash
mvn clean package
docker compose up -d --build
```

Or rebuild and restart the app container:

```bash
./docker_env_setup.sh
```

## If your PostgreSQL username is different

For local `mvn spring-boot:run`, the app defaults to your macOS username. If your database uses another PostgreSQL user, run with environment variables:

```bash
DB_USERNAME=your_username DB_PASSWORD=your_password mvn spring-boot:run
```

Example using old postgres user:

```bash
DB_USERNAME=postgres DB_PASSWORD=admin1234 mvn spring-boot:run
```
