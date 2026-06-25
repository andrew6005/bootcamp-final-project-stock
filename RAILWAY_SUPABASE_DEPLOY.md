# Railway + Supabase Deployment

Use Supabase for PostgreSQL and Railway for the Spring Boot API.

## 1. Supabase

1. Create a Supabase project.
2. Open `database/schema.sql`.
3. In Supabase SQL Editor, run the SQL after removing this line:

```sql
CREATE DATABASE bootcamp;
```

4. Copy your Supabase JDBC connection string.

Recommended JDBC format:

```text
jdbc:postgresql://YOUR_SUPABASE_HOST:5432/postgres?sslmode=require
```

If you use Supabase pooler port `6543`, keep the same `?sslmode=require`.

## 2. Railway Backend

Create a Railway project from this GitHub repository:

```text
https://github.com/andrew6005/bootcamp-final-project-stock
```

Railway will use `railway.json` and the Dockerfile automatically.

Set these Railway variables:

```text
DB_URL=jdbc:postgresql://YOUR_SUPABASE_HOST:5432/postgres?sslmode=require
DB_USERNAME=YOUR_SUPABASE_USERNAME
DB_PASSWORD=YOUR_SUPABASE_PASSWORD
FINNHUB_API_KEY=YOUR_FINNHUB_API_KEY
```

Optional Redis variables if you add Redis on Railway:

```text
REDIS_HOST=YOUR_REDIS_HOST
REDIS_PORT=YOUR_REDIS_PORT
```

Do not set `PORT` manually. Railway supplies it.

After deploy, test:

```text
https://YOUR_RAILWAY_APP.up.railway.app/data/heatmap
https://YOUR_RAILWAY_APP.up.railway.app/data/ohlc?symbol=TSLA
```

## 3. Vercel Frontend

In Vercel project settings, add this environment variable:

```text
STOCKMAP_API_BASE_URL=https://YOUR_RAILWAY_APP.up.railway.app
```

Then redeploy Vercel. The static frontend will call the Railway API.

## 4. Data Loading

Run the Python loaders against Supabase:

```bash
DB_URL="postgresql+psycopg2://YOUR_SUPABASE_USERNAME:YOUR_SUPABASE_PASSWORD@YOUR_SUPABASE_HOST:5432/postgres" \
python python/prepare_1_load_snp500_symbol.py
```

```bash
DB_URL="postgresql+psycopg2://YOUR_SUPABASE_USERNAME:YOUR_SUPABASE_PASSWORD@YOUR_SUPABASE_HOST:5432/postgres" \
python python/prepare_2_load_ohlcv_data.py
```

If your Supabase password contains symbols like `@`, `:` or `/`, URL-encode it in the Python `DB_URL`.
