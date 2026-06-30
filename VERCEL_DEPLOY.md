# Vercel Deploy

Use these Vercel project settings:

```text
Framework Preset: Other
Root Directory: ./
Build Command: npm run build:vercel
Output Directory: static
```

The app entry file is:

```text
static/index.html
```

Set this Vercel environment variable to the public Spring Boot API URL:

```text
STOCKMAP_API_BASE_URL=https://YOUR_BACKEND_HOST
```

The browser calls Vercel's same-origin `/api/...` proxy, and the proxy forwards requests to `STOCKMAP_API_BASE_URL`. This avoids browser CORS failures when loading `/data/heatmap` and `/data/ohlc`.
