# Railway + Supabase Deployment

Use Supabase for PostgreSQL and Railway for the Spring Boot API.

## 1. Supabase

Project dashboard:

```text
https://supabase.com/dashboard/project/lhohtjjxcrguyrweytkj
```

Project ref:

```text
lhohtjjxcrguyrweytkj
```

Direct database host:

```text
db.lhohtjjxcrguyrweytkj.supabase.co
```

1. Open your Supabase project.
2. Open `database/schema.sql`.
3. In Supabase SQL Editor, run the SQL after removing this line:

```sql
CREATE DATABASE bootcamp;
```

4. Copy your Supabase JDBC connection string.

Recommended JDBC format:

```text
jdbc:postgresql://db.lhohtjjxcrguyrweytkj.supabase.co:5432/postgres?sslmode=require
```

Use `postgres` as the username for the direct connection. If Supabase gives you a pooler connection instead, use the exact pooler host and username shown in the Supabase `Connect` panel.

## 2. Railway Backend

Railway project dashboard:

```text
https://railway.com/project/f662d9c2-4b16-43d9-ab80-a2d6076a83ee
```

Project ID:

```text
f662d9c2-4b16-43d9-ab80-a2d6076a83ee
```

Connect this Railway project to the GitHub repository:

```text
https://github.com/andrew6005/bootcamp-final-project-stock
```

Railway will use `railway.json` and the Dockerfile automatically.

Set these Railway variables:

```text
DB_URL=jdbc:postgresql://db.lhohtjjxcrguyrweytkj.supabase.co:5432/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=YOUR_SUPABASE_DATABASE_PASSWORD
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

Use the public Railway domain from the deployed service. It is different from the Railway dashboard URL.

## 3. Vercel Frontend

In Vercel project settings, add this environment variable:

```text
STOCKMAP_API_BASE_URL=https://YOUR_RAILWAY_APP.up.railway.app
```

Then redeploy Vercel. The static frontend will call the Railway API.

## 4. Data Loading

Run the Python loaders against your Supabase database:

```bash
DB_URL="postgresql+psycopg2://postgres:YOUR_SUPABASE_DATABASE_PASSWORD@db.lhohtjjxcrguyrweytkj.supabase.co:5432/postgres" \
python python/prepare_1_load_snp500_symbol.py
```

```bash
DB_URL="postgresql+psycopg2://postgres:YOUR_SUPABASE_DATABASE_PASSWORD@db.lhohtjjxcrguyrweytkj.supabase.co:5432/postgres" \
python python/prepare_2_load_ohlcv_data.py
```

If your Supabase password contains symbols like `@`, `:` or `/`, URL-encode it in the Python `DB_URL`.
