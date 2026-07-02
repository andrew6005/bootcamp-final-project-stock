const FIVE_MINUTES = 5 * 60 * 1000;
const API_BASE_URL = window.STOCKMAP_API_BASE_URL || "";
let currentRows = [];

const sectorNames = {
    Automotive: "Consumer Cyclical",
    "Financial Services": "Financial",
    Semiconductors: "Technology"
};

const fallbackRows = [
    { stockId: 1, symbol: "AAPL", name: "Apple Inc", marketCap: 3000000000000, industry: "Technology", price: 0, marketPriceChgPct: 3.14 },
    { stockId: 2, symbol: "MSFT", name: "Microsoft Corporation", marketCap: 3100000000000, industry: "Technology", price: 0, marketPriceChgPct: 3.02 },
    { stockId: 3, symbol: "NVDA", name: "NVIDIA Corporation", marketCap: 2900000000000, industry: "Semiconductors", price: 0, marketPriceChgPct: -1.25 },
    { stockId: 4, symbol: "AVGO", name: "Broadcom Inc", marketCap: 760000000000, industry: "Semiconductors", price: 0, marketPriceChgPct: -2.23 },
    { stockId: 5, symbol: "ORCL", name: "Oracle Corporation", marketCap: 480000000000, industry: "Technology", price: 0, marketPriceChgPct: -2.76 },
    { stockId: 6, symbol: "PLTR", name: "Palantir Technologies", marketCap: 160000000000, industry: "Technology", price: 0, marketPriceChgPct: 7.76 },
    { stockId: 7, symbol: "AMD", name: "Advanced Micro Devices", marketCap: 250000000000, industry: "Semiconductors", price: 0, marketPriceChgPct: -6.89 },
    { stockId: 8, symbol: "QCOM", name: "Qualcomm", marketCap: 230000000000, industry: "Semiconductors", price: 0, marketPriceChgPct: -1.55 },
    { stockId: 9, symbol: "CRM", name: "Salesforce", marketCap: 260000000000, industry: "Technology", price: 0, marketPriceChgPct: 0.68 },
    { stockId: 10, symbol: "NOW", name: "ServiceNow", marketCap: 170000000000, industry: "Technology", price: 0, marketPriceChgPct: 9.85 },
    { stockId: 11, symbol: "ADBE", name: "Adobe", marketCap: 210000000000, industry: "Technology", price: 0, marketPriceChgPct: -1.20 },
    { stockId: 12, symbol: "INTU", name: "Intuit", marketCap: 180000000000, industry: "Technology", price: 0, marketPriceChgPct: -0.40 },
    { stockId: 13, symbol: "IBM", name: "IBM", marketCap: 170000000000, industry: "Technology", price: 0, marketPriceChgPct: 0.34 },
    { stockId: 14, symbol: "CSCO", name: "Cisco", marketCap: 190000000000, industry: "Technology", price: 0, marketPriceChgPct: -4.37 },
    { stockId: 15, symbol: "GOOGL", name: "Alphabet Inc", marketCap: 2100000000000, industry: "Communication Services", price: 0, marketPriceChgPct: 1.07 },
    { stockId: 16, symbol: "META", name: "Meta Platforms Inc", marketCap: 1200000000000, industry: "Communication Services", price: 0, marketPriceChgPct: 8.81 },
    { stockId: 17, symbol: "NFLX", name: "Netflix Inc", marketCap: 290000000000, industry: "Communication Services", price: 0, marketPriceChgPct: 3.91 },
    { stockId: 18, symbol: "DIS", name: "Disney", marketCap: 190000000000, industry: "Communication Services", price: 0, marketPriceChgPct: 0.15 },
    { stockId: 19, symbol: "TMUS", name: "T-Mobile US", marketCap: 210000000000, industry: "Communication Services", price: 0, marketPriceChgPct: 0.25 },
    { stockId: 20, symbol: "VZ", name: "Verizon", marketCap: 170000000000, industry: "Communication Services", price: 0, marketPriceChgPct: 0.42 },
    { stockId: 21, symbol: "T", name: "AT&T", marketCap: 130000000000, industry: "Communication Services", price: 0, marketPriceChgPct: 0.66 },
    { stockId: 22, symbol: "AMZN", name: "Amazon.com Inc", marketCap: 1900000000000, industry: "Consumer Cyclical", price: 0, marketPriceChgPct: 1.41 },
    { stockId: 23, symbol: "TSLA", name: "Tesla Inc", marketCap: 600000000000, industry: "Automotive", price: 0, marketPriceChgPct: 1.12 },
    { stockId: 24, symbol: "HD", name: "Home Depot", marketCap: 360000000000, industry: "Consumer Cyclical", price: 0, marketPriceChgPct: 0.44 },
    { stockId: 25, symbol: "MCD", name: "McDonald's", marketCap: 220000000000, industry: "Consumer Cyclical", price: 0, marketPriceChgPct: -0.33 },
    { stockId: 26, symbol: "BKNG", name: "Booking Holdings", marketCap: 180000000000, industry: "Consumer Cyclical", price: 0, marketPriceChgPct: 2.47 },
    { stockId: 27, symbol: "SBUX", name: "Starbucks", marketCap: 90000000000, industry: "Consumer Cyclical", price: 0, marketPriceChgPct: 0.52 },
    { stockId: 28, symbol: "TJX", name: "TJX Companies", marketCap: 130000000000, industry: "Consumer Cyclical", price: 0, marketPriceChgPct: -0.58 },
    { stockId: 29, symbol: "NKE", name: "Nike", marketCap: 110000000000, industry: "Consumer Cyclical", price: 0, marketPriceChgPct: -0.75 },
    { stockId: 30, symbol: "JPM", name: "JPMorgan Chase & Co", marketCap: 550000000000, industry: "Financial Services", price: 0, marketPriceChgPct: 2.06 },
    { stockId: 31, symbol: "V", name: "Visa Inc", marketCap: 520000000000, industry: "Financial Services", price: 0, marketPriceChgPct: 2.33 },
    { stockId: 32, symbol: "MA", name: "Mastercard", marketCap: 430000000000, industry: "Financial Services", price: 0, marketPriceChgPct: 1.72 },
    { stockId: 33, symbol: "BAC", name: "Bank of America", marketCap: 310000000000, industry: "Financial Services", price: 0, marketPriceChgPct: 2.42 },
    { stockId: 34, symbol: "WFC", name: "Wells Fargo", marketCap: 210000000000, industry: "Financial Services", price: 0, marketPriceChgPct: 3.99 },
    { stockId: 35, symbol: "GS", name: "Goldman Sachs", marketCap: 150000000000, industry: "Financial Services", price: 0, marketPriceChgPct: -0.22 },
    { stockId: 36, symbol: "MS", name: "Morgan Stanley", marketCap: 160000000000, industry: "Financial Services", price: 0, marketPriceChgPct: -0.35 },
    { stockId: 37, symbol: "AXP", name: "American Express", marketCap: 210000000000, industry: "Financial Services", price: 0, marketPriceChgPct: -0.61 },
    { stockId: 38, symbol: "C", name: "Citigroup", marketCap: 120000000000, industry: "Financial Services", price: 0, marketPriceChgPct: -1.04 },
    { stockId: 39, symbol: "BX", name: "Blackstone", marketCap: 160000000000, industry: "Financial Services", price: 0, marketPriceChgPct: 1.60 },
    { stockId: 40, symbol: "WMT", name: "Walmart", marketCap: 530000000000, industry: "Consumer Defensive", price: 0, marketPriceChgPct: -3.92 },
    { stockId: 41, symbol: "COST", name: "Costco", marketCap: 410000000000, industry: "Consumer Defensive", price: 0, marketPriceChgPct: -1.15 },
    { stockId: 42, symbol: "PG", name: "Procter & Gamble", marketCap: 390000000000, industry: "Consumer Defensive", price: 0, marketPriceChgPct: 0.35 },
    { stockId: 43, symbol: "KO", name: "Coca-Cola", marketCap: 300000000000, industry: "Consumer Defensive", price: 0, marketPriceChgPct: 0.03 },
    { stockId: 44, symbol: "PEP", name: "PepsiCo", marketCap: 240000000000, industry: "Consumer Defensive", price: 0, marketPriceChgPct: 0.47 },
    { stockId: 45, symbol: "PM", name: "Philip Morris", marketCap: 200000000000, industry: "Consumer Defensive", price: 0, marketPriceChgPct: 0.86 },
    { stockId: 46, symbol: "MO", name: "Altria", marketCap: 90000000000, industry: "Consumer Defensive", price: 0, marketPriceChgPct: 0.41 },
    { stockId: 47, symbol: "LLY", name: "Eli Lilly and Company", marketCap: 850000000000, industry: "Healthcare", price: 0, marketPriceChgPct: -0.64 },
    { stockId: 48, symbol: "JNJ", name: "Johnson & Johnson", marketCap: 370000000000, industry: "Healthcare", price: 0, marketPriceChgPct: 0.00 },
    { stockId: 49, symbol: "UNH", name: "UnitedHealth", marketCap: 460000000000, industry: "Healthcare", price: 0, marketPriceChgPct: 2.63 },
    { stockId: 50, symbol: "ABBV", name: "AbbVie", marketCap: 330000000000, industry: "Healthcare", price: 0, marketPriceChgPct: -0.23 },
    { stockId: 51, symbol: "MRK", name: "Merck", marketCap: 320000000000, industry: "Healthcare", price: 0, marketPriceChgPct: -2.44 },
    { stockId: 52, symbol: "ABT", name: "Abbott Laboratories", marketCap: 240000000000, industry: "Healthcare", price: 0, marketPriceChgPct: 1.56 },
    { stockId: 53, symbol: "TMO", name: "Thermo Fisher", marketCap: 210000000000, industry: "Healthcare", price: 0, marketPriceChgPct: 1.18 },
    { stockId: 54, symbol: "DHR", name: "Danaher", marketCap: 180000000000, industry: "Healthcare", price: 0, marketPriceChgPct: 0.44 },
    { stockId: 55, symbol: "PFE", name: "Pfizer", marketCap: 150000000000, industry: "Healthcare", price: 0, marketPriceChgPct: -0.83 },
    { stockId: 56, symbol: "XOM", name: "Exxon Mobil Corporation", marketCap: 470000000000, industry: "Energy", price: 0, marketPriceChgPct: -0.32 },
    { stockId: 57, symbol: "CVX", name: "Chevron", marketCap: 280000000000, industry: "Energy", price: 0, marketPriceChgPct: -0.04 },
    { stockId: 58, symbol: "COP", name: "ConocoPhillips", marketCap: 130000000000, industry: "Energy", price: 0, marketPriceChgPct: -0.85 },
    { stockId: 59, symbol: "GE", name: "GE Aerospace", marketCap: 180000000000, industry: "Industrials", price: 0, marketPriceChgPct: 0.32 },
    { stockId: 60, symbol: "CAT", name: "Caterpillar", marketCap: 180000000000, industry: "Industrials", price: 0, marketPriceChgPct: -6.90 },
    { stockId: 61, symbol: "RTX", name: "RTX", marketCap: 160000000000, industry: "Industrials", price: 0, marketPriceChgPct: 1.08 },
    { stockId: 62, symbol: "BA", name: "Boeing", marketCap: 110000000000, industry: "Industrials", price: 0, marketPriceChgPct: 0.97 },
    { stockId: 63, symbol: "UNP", name: "Union Pacific", marketCap: 140000000000, industry: "Industrials", price: 0, marketPriceChgPct: 0.20 },
    { stockId: 64, symbol: "LIN", name: "Linde", marketCap: 220000000000, industry: "Basic Materials", price: 0, marketPriceChgPct: -0.51 },
    { stockId: 65, symbol: "SHW", name: "Sherwin-Williams", marketCap: 90000000000, industry: "Basic Materials", price: 0, marketPriceChgPct: 0.38 },
    { stockId: 66, symbol: "NEE", name: "NextEra Energy", marketCap: 150000000000, industry: "Utilities", price: 0, marketPriceChgPct: 0.42 },
    { stockId: 67, symbol: "SO", name: "Southern Company", marketCap: 90000000000, industry: "Utilities", price: 0, marketPriceChgPct: 0.36 },
    { stockId: 68, symbol: "AMT", name: "American Tower", marketCap: 90000000000, industry: "Real Estate", price: 0, marketPriceChgPct: 0.62 },
    { stockId: 69, symbol: "PLD", name: "Prologis", marketCap: 100000000000, industry: "Real Estate", price: 0, marketPriceChgPct: -0.40 }
];

function normalizedSector(industry) {
    return sectorNames[industry] || industry || "Other";
}

function initialRows() {
    const element = document.getElementById("initialCompanies");
    if (!element) return [];
    try {
        return JSON.parse(element.textContent).map(company => ({
            stockId: company.id,
            symbol: company.symbol,
            name: company.companyName,
            marketCap: company.marketCap,
            industry: company.industry,
            price: 0,
            marketPriceChgPct: 0
        }));
    } catch (error) {
        return [];
    }
}

function fallbackPercent(symbol) {
    let hash = 0;
    for (const char of symbol) hash = (hash * 31 + char.charCodeAt(0)) % 997;
    return ((hash % 700) - 350) / 100;
}

function colorForChange(percent) {
    const value = Math.max(-3, Math.min(3, Number(percent) || 0));
    if (value > 0.05) return d3.interpolateRgb("#1f7a3a", "#63d46e")(value / 3);
    if (value < -0.05) return d3.interpolateRgb("#8f1418", "#f04b49")(Math.abs(value) / 3);
    return "#2847b8";
}

function textColor(percent) {
    return Math.abs(Number(percent) || 0) > 1.7 ? "#ffffff" : "#f5f7fb";
}

function buildHierarchy(rows) {
    const groups = d3.group(rows, row => normalizedSector(row.industry));
    return {
        name: "stocks",
        children: Array.from(groups, ([name, children]) => ({
            name,
            children: children.map(row => ({
                ...row,
                value: Math.max(Number(row.marketCap) || 1, 1),
                marketPriceChgPct: Number(row.marketPriceChgPct) || fallbackPercent(row.symbol)
            }))
        }))
    };
}

function drawHeatmap(rows) {
    const board = document.getElementById("heatmap");
    const emptyState = document.getElementById("emptyState");
    const hasRows = Array.isArray(rows) && rows.length > 0;

    if (emptyState) emptyState.hidden = hasRows;
    if (board) board.hidden = !hasRows;
    if (!board || !hasRows) return;

    const width = board.clientWidth || 1200;
    const height = Math.max(board.clientHeight || 650, 560);
    const root = d3.hierarchy(buildHierarchy(rows))
        .sum(node => node.value || 0)
        .sort((a, b) => b.value - a.value);

    d3.treemap()
        .size([width, height])
        .paddingOuter(4)
        .paddingTop(24)
        .paddingInner(3)
        .round(true)(root);

    board.replaceChildren();

    const svg = d3.select(board)
        .append("svg")
        .attr("class", "heatmap-svg")
        .attr("viewBox", `0 0 ${width} ${height}`)
        .attr("role", "img");

    const sectors = svg.selectAll("g.sector-node")
        .data(root.children || [])
        .join("g")
        .attr("class", "sector-node");

    sectors.append("rect")
        .attr("x", d => d.x0)
        .attr("y", d => d.y0)
        .attr("width", d => d.x1 - d.x0)
        .attr("height", d => d.y1 - d.y0)
        .attr("fill", "#30333d")
        .attr("stroke", "#4b505d");

    sectors.append("text")
        .attr("class", "sector-label")
        .attr("x", d => d.x0 + 8)
        .attr("y", d => d.y0 + 17)
        .text(d => d.data.name);

    const leaves = svg.selectAll("a.stock-node")
        .data(root.leaves())
        .join("a")
        .attr("class", "stock-node")
        .attr("href", d => `/stocks/${d.data.symbol}`);

    leaves.append("title")
        .text(d => `${d.data.name || d.data.symbol} | ${d.data.industry || "Other"} | ${formatSigned(d.data.marketPriceChgPct)}%`);

    leaves.append("rect")
        .attr("x", d => d.x0)
        .attr("y", d => d.y0)
        .attr("width", d => Math.max(0, d.x1 - d.x0))
        .attr("height", d => Math.max(0, d.y1 - d.y0))
        .attr("fill", d => colorForChange(d.data.marketPriceChgPct))
        .attr("stroke", "rgba(21, 24, 30, .8)");

    leaves.each(function (d) {
        const width = d.x1 - d.x0;
        const height = d.y1 - d.y0;
       if (width < 20 || height < 18) return;

        const fontSize = Math.max(7, Math.min(34, Math.sqrt(width * height) / 7));
        const group = d3.select(this);
       const label = group.append("text")
        .attr("class", "ticker-label")
        .attr("x", (d.x0 + d.x1) / 2)
        .attr("y", (d.y0 + d.y1) / 2 - 2)
        .attr("fill", textColor(d.data.marketPriceChgPct))
        .attr("font-size", fontSize)
        .text(d.data.symbol);

if (width < 35) {
    label.attr(
        "transform",
        `rotate(-90 ${(d.x0 + d.x1) / 2} ${(d.y0 + d.y1) / 2})`
    );
}

        if (height >= 52) {
            group.append("text")
                .attr("class", "change-label")
                .attr("x", (d.x0 + d.x1) / 2)
                .attr("y", (d.y0 + d.y1) / 2 + fontSize * .82)
                .attr("fill", textColor(d.data.marketPriceChgPct))
                .attr("font-size", Math.max(10, fontSize * .52))
                .text(`${formatSigned(d.data.marketPriceChgPct)}%`);
        }
    });
}

function formatSigned(value) {
    const number = Number(value) || 0;
    return `${number >= 0 ? "+" : ""}${number.toFixed(2)}`;
}

async function loadHeatmap() {
    try {
        const res = await fetch(`${API_BASE_URL}/data/heatmap?t=${Date.now()}`, { cache: "no-store" });
        if (!res.ok) throw new Error(`Heatmap request failed: ${res.status}`);
        const rows = await res.json();
        currentRows = Array.isArray(rows) && rows.length ? rows : fallbackRows;
        drawHeatmap(currentRows);
    } catch (error) {
        currentRows = initialRows().length ? initialRows() : fallbackRows;
        drawHeatmap(currentRows);
    }
}

window.addEventListener("resize", () => {
    if (currentRows.length) drawHeatmap(currentRows);
});
loadHeatmap();
setInterval(loadHeatmap, FIVE_MINUTES);
