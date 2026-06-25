const FIVE_MINUTES = 5 * 60 * 1000;
const API_BASE_URL = window.STOCKMAP_API_BASE_URL || "";
const ACTIVE_SYMBOL = resolveSymbol();

function resolveSymbol() {
    if (typeof SYMBOL !== "undefined" && SYMBOL) return SYMBOL;

    const match = window.location.pathname.match(/\/stocks\/([^/]+)/);
    if (match) return decodeURIComponent(match[1]).toUpperCase();

    const params = new URLSearchParams(window.location.search);
    return (params.get("symbol") || "TSLA").toUpperCase();
}

async function loadQuote() {
    let payload;
    try {
        const res = await fetch(`${API_BASE_URL}/data/ohlc?symbol=${encodeURIComponent(ACTIVE_SYMBOL)}`);
        if (!res.ok) throw new Error(`OHLC request failed: ${res.status}`);
        payload = await res.json();
    } catch (error) {
        payload = {
            symbol: ACTIVE_SYMBOL,
            companyName: companyName(ACTIVE_SYMBOL),
            ohlcs: buildFallbackSeries(ACTIVE_SYMBOL)
        };
    }

    const series = normalizeOhlcs(payload.ohlcs);
    const last = series[series.length - 1];
    const previous = series[series.length - 2] || last;
    const current = numberOr(last.close, 0);
    const change = current - numberOr(previous.close, current);
    const percent = previous.close ? change / previous.close * 100 : 0;

    document.getElementById("lastPrice").textContent = current.toFixed(3);
    const changeValue = document.getElementById("changeValue");
    changeValue.textContent = `${formatSigned(change)} (${formatSigned(percent)}%)`;
    changeValue.className = percent >= 0 ? "positive" : "negative";
    document.getElementById("updatedAt").textContent = `Updated: ${formatUpdatedDate(new Date())}`;
    document.getElementById("nextRefresh").textContent = `Next refresh: ${formatClock(new Date(Date.now() + FIVE_MINUTES))}`;
    document.getElementById("stockName").textContent = `${payload.companyName || companyName(ACTIVE_SYMBOL)} (${ACTIVE_SYMBOL}.US)`;

    drawCandlestickChart(series);
    renderOhlcData(series);
}

function normalizeOhlcs(ohlcs) {
    return (ohlcs || [])
        .map(row => ({
            date: row.date,
            open: numberOr(row.open, 0),
            high: numberOr(row.high, 0),
            low: numberOr(row.low, 0),
            close: numberOr(row.close, 0),
            volume: numberOr(row.volume, 0)
        }))
        .filter(row => row.date && row.open && row.high && row.low && row.close);
}

function drawCandlestickChart(series) {
    const dates = series.map(row => row.date);
    const priceTrace = {
        type: "candlestick",
        x: dates,
        open: series.map(row => row.open),
        high: series.map(row => row.high),
        low: series.map(row => row.low),
        close: series.map(row => row.close),
        increasing: { line: { color: "#6ee85f" }, fillcolor: "#6ee85f" },
        decreasing: { line: { color: "#ef4444" }, fillcolor: "#ef4444" },
        name: ACTIVE_SYMBOL,
        xaxis: "x",
        yaxis: "y"
    };

    const volumeTrace = {
        type: "bar",
        x: dates,
        y: series.map(row => row.volume),
        marker: { color: "rgba(120, 125, 150, .55)" },
        name: "Volume",
        xaxis: "x",
        yaxis: "y2"
    };

    const layout = {
        paper_bgcolor: "#000000",
        plot_bgcolor: "#05074a",
        margin: { l: 52, r: 74, t: 26, b: 42 },
        showlegend: false,
        dragmode: "pan",
        xaxis: {
            rangeslider: { visible: false },
            gridcolor: "rgba(160, 168, 190, .24)",
            color: "#d7d7d7",
            tickfont: { size: 13 }
        },
        yaxis: {
            domain: [0.24, 1],
            side: "right",
            gridcolor: "rgba(160, 168, 190, .28)",
            color: "#d7d7d7",
            tickfont: { size: 13 }
        },
        yaxis2: {
            domain: [0, 0.18],
            side: "right",
            showgrid: false,
            color: "#777",
            tickfont: { size: 11 }
        },
        font: {
            family: "Arial, Helvetica, sans-serif",
            color: "#d7d7d7"
        }
    };

    Plotly.react("candleChart", [priceTrace, volumeTrace], layout, {
        displayModeBar: false,
        responsive: true
    });
}

function renderOhlcData(series) {
    const last = series[series.length - 1];
    if (!last) return;

    document.getElementById("ohlcDate").textContent = last.date;
    document.getElementById("ohlcOpen").textContent = formatPrice(last.open);
    document.getElementById("ohlcHigh").textContent = formatPrice(last.high);
    document.getElementById("ohlcLow").textContent = formatPrice(last.low);
    document.getElementById("ohlcClose").textContent = formatPrice(last.close);
    document.getElementById("ohlcVolume").textContent = formatVolume(last.volume);

    const rows = series.slice(-10).reverse();
    document.getElementById("ohlcRows").replaceChildren(...rows.map(row => {
        const tr = document.createElement("tr");
        [
            row.date,
            formatPrice(row.open),
            formatPrice(row.high),
            formatPrice(row.low),
            formatPrice(row.close),
            formatVolume(row.volume)
        ].forEach(value => {
            const td = document.createElement("td");
            td.textContent = value;
            tr.appendChild(td);
        });
        return tr;
    }));
}

function numberOr(value, fallback) {
    const number = Number(value);
    return Number.isFinite(number) ? number : fallback;
}

function formatSigned(value) {
    return `${value >= 0 ? "+" : ""}${Number(value).toFixed(3)}`;
}

function formatPrice(value) {
    return Number(value).toFixed(2);
}

function formatVolume(value) {
    return Math.round(Number(value) || 0).toLocaleString("en-US");
}

function formatUpdatedDate(date) {
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    const hh = String(date.getHours()).padStart(2, "0");
    const min = String(date.getMinutes()).padStart(2, "0");
    return `${yyyy}/${mm}/${dd} ${hh}:${min} HKT`;
}

function formatClock(date) {
    const hh = String(date.getHours()).padStart(2, "0");
    const min = String(date.getMinutes()).padStart(2, "0");
    return `${hh}:${min} HKT`;
}

function companyName(symbol) {
    const names = {
        AAPL: "Apple Inc",
        MSFT: "Microsoft Corporation",
        NVDA: "NVIDIA Corporation",
        GOOGL: "Alphabet Inc",
        AMZN: "Amazon.com Inc",
        META: "Meta Platforms Inc",
        TSLA: "Tesla, Inc",
        JPM: "JPMorgan Chase",
        V: "Visa Inc",
        WMT: "Walmart Inc",
        LLY: "Eli Lilly",
        XOM: "Exxon Mobil"
    };
    return names[symbol] || `${symbol}, Inc`;
}

function buildFallbackSeries(symbol) {
    let seed = 0;
    for (const char of symbol) seed = (seed * 31 + char.charCodeAt(0)) >>> 0;
    const random = () => {
        seed = (seed * 1664525 + 1013904223) >>> 0;
        return seed / 4294967296;
    };

    const rows = [];
    const date = new Date();
    date.setDate(date.getDate() - 126);
    let close = 120 + random() * 260;

    for (let i = 0; i < 126; i++) {
        const open = close + (random() - .5) * 8;
        close = Math.max(5, open + (random() - .48) * 10);
        const spread = 2 + random() * 9;
        rows.push({
            date: date.toISOString().slice(0, 10),
            open,
            high: Math.max(open, close) + spread,
            low: Math.min(open, close) - spread,
            close,
            volume: Math.round(20_000_000 + random() * 120_000_000)
        });
        date.setDate(date.getDate() + 1);
    }
    return rows;
}

loadQuote();
setInterval(loadQuote, FIVE_MINUTES);
