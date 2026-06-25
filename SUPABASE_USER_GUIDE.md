# Supabase User Guide for Stock Heatmap Project

This project is a Spring Boot app with PostgreSQL. Supabase should be used for the PostgreSQL database. Supabase does not directly run the whole Java project folder.

## Do You Need to Pay?

For this school project, you normally do not need to pay. Supabase has a `Free` plan.

Use the Free plan if you only need:

- A small PostgreSQL database for project data
- SQL Editor
- Connection from your Java app
- A small amount of file storage for optional zip upload

You may need a paid plan only if:

- Your database becomes larger than the Free plan limit
- You need more active projects
- You need production-level backups, larger storage, or more bandwidth
- Your teacher or team specifically requires a paid project

Important: Supabase pricing can change. Always check the official pricing page before entering payment details:

```text
https://supabase.com/pricing
```

For this final project, start with the Free plan.

## Step 0: Create a Supabase Account

1. Open this website:

```text
https://supabase.com
```

2. Click `Start your project` or `Sign in`.
3. Choose one sign-up method:

- `Continue with GitHub`
- `Continue with Google`
- Email and password

The easiest method is usually GitHub or Google.

4. If Supabase asks you to verify your email, open your email inbox and click the verification link.
5. After login, you should see the Supabase Dashboard:

```text
https://supabase.com/dashboard
```

6. If Supabase asks for billing/payment during sign-up, choose the Free plan if available. Do not choose Pro unless you really want to pay.

Account safety:

- Save your Supabase password somewhere safe.
- Save your database password somewhere safe.
- Do not post your database password on GitHub.
- Do not paste your database password in screenshots.

## Step 0.1: Create an Organization

When you first enter the dashboard, Supabase may ask you to create an organization.

1. Organization name can be anything, for example:

```text
Andrew School Projects
```

2. Organization type can be `Personal` or `Other`.
3. Select the Free plan.
4. Continue to the dashboard.

## What Goes to Supabase

- Database tables: `stocks`, `stock_profiles`, `stock_quote`, `stock_ohlc_data`
- Database rows loaded by the Python scripts

## What Does Not Go to Supabase

- The whole Spring Boot folder as a running app
- Java files, HTML, CSS, and JS as an executable server
- Redis cache

The Java app still runs from VS Code, Maven, Docker, Render, Railway, or another Java hosting service. Supabase only replaces your local PostgreSQL database.

## Step 1: Create a Supabase Project

Your Supabase project is:

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

Keep your database password private. Do not commit it to GitHub.

## Step 2: Create Tables in Supabase

1. Open your Supabase project.
2. Go to `SQL Editor`.
3. Click `New query`.
4. Open this local file:

```text
database/schema.sql
```

5. Copy the SQL into the Supabase SQL Editor.
6. Do not run this line in Supabase:

```sql
CREATE DATABASE bootcamp;
```

Supabase already created the database for you.

7. Run the rest of the SQL.

## Step 3: Get the Supabase Connection String

1. In Supabase, click `Connect`.
2. Choose a Postgres connection string.
3. For this Spring Boot project, use the Session pooler or direct connection string.
4. Copy the connection details.

Your JDBC URL should look similar to this:

```text
jdbc:postgresql://db.lhohtjjxcrguyrweytkj.supabase.co:5432/postgres?sslmode=require
```

For the direct connection, the username is usually:

```text
postgres
```

For pooler connections, the username may look like:

```text
postgres.YOUR_PROJECT_REF
```

## Step 4: Run Java with Supabase Database

In VS Code terminal, run:

```bash
cd "/Users/andrew/Documents/andrew的MacBook Air/finalproject/stock/2026-06-15/final-project-stock"
```

Then start Spring Boot with Supabase settings:

```bash
DB_URL="jdbc:postgresql://db.lhohtjjxcrguyrweytkj.supabase.co:5432/postgres?sslmode=require" \
DB_USERNAME="postgres" \
DB_PASSWORD="YOUR_SUPABASE_DATABASE_PASSWORD" \
mvn spring-boot:run
```

If your Supabase connection uses port `6543`, put `6543` in the JDBC URL.

## Step 5: Load Stock Symbols into Supabase

Open a new terminal tab and activate Python:

```bash
cd "/Users/andrew/Documents/andrew的MacBook Air/finalproject/stock/2026-06-15/final-project-stock"
source bootcamp-env/bin/activate
```

Run the first Python file with the Supabase database URL:

```bash
DB_URL="postgresql+psycopg2://postgres:YOUR_SUPABASE_DATABASE_PASSWORD@db.lhohtjjxcrguyrweytkj.supabase.co:5432/postgres" \
python python/prepare_1_load_snp500_symbol.py
```

## Step 6: Load OHLCV Data into Supabase

Run:

```bash
DB_URL="postgresql+psycopg2://postgres:YOUR_SUPABASE_DATABASE_PASSWORD@db.lhohtjjxcrguyrweytkj.supabase.co:5432/postgres" \
python python/prepare_2_load_ohlcv_data.py
```

If Yahoo Finance returns `429 Too Many Requests`, the script will load fallback OHLCV data so the project can still display candlestick charts.

## Step 7: Verify Data in Supabase

In Supabase SQL Editor, run:

```sql
SELECT COUNT(*) FROM stocks;
SELECT COUNT(*) FROM stock_ohlc_data;
SELECT symbol, COUNT(*)
FROM stock_ohlc_data
GROUP BY symbol
ORDER BY symbol;
```

You should see stock symbols and OHLCV rows.

## Step 8: Run the App

Start Java again with Supabase settings:

```bash
DB_URL="jdbc:postgresql://db.lhohtjjxcrguyrweytkj.supabase.co:5432/postgres?sslmode=require" \
DB_USERNAME="postgres" \
DB_PASSWORD="YOUR_SUPABASE_DATABASE_PASSWORD" \
mvn spring-boot:run
```

Open:

```text
http://localhost:8080/heatmap
```

Click a stock tile, for example `TSLA`, to see the candlestick chart and OHLCV table.

## Optional: Upload the Project Folder as a File

If your teacher wants the folder uploaded, Supabase Storage can store a zip file, but it will only be a file upload. It will not run the Java app.

Create a zip:

```bash
cd "/Users/andrew/Documents/andrew的MacBook Air/finalproject/stock/2026-06-15"
zip -r final-project-stock.zip final-project-stock
```

Then in Supabase:

1. Go to `Storage`.
2. Create a bucket, for example `submission`.
3. Upload `final-project-stock.zip`.

## Quick Checklist

- Supabase project created
- Tables created in SQL Editor
- Java app can connect to Supabase
- `prepare_1_load_snp500_symbol.py` loaded stock symbols
- `prepare_2_load_ohlcv_data.py` loaded OHLCV data
- `/heatmap` works
- `/stocks/TSLA` shows candlestick and OHLCV table
