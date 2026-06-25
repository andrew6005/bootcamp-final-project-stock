const FIVE_MINUTES = 5 * 60 * 1000;
const API_BASE_URL = "https://stockmap-api.onrender.com";

const sectorNames = {
    Automotive: "Consumer Cyclical",
    "Financial Services": "Financial",
    Semiconductors: "Technology"
};

const fallbackRows = [
    { stockId: 1, symbol: "AAPL", name: "Apple Inc", marketCap: 3200000000000, industry: "Technology", price: 0, marketPriceChgPct: 1.18 },
    { stockId: 2, symbol: "MSFT", name: "Microsoft Corporation", marketCap: 3100000000000, industry: "Technology", price: 0, marketPriceChgPct: 0.72 },
    { stockId: 3, symbol: "NVDA", name: "NVIDIA Corporation", marketCap: 2800000000000, industry: "Semiconductors", price: 0, marketPriceChgPct: 2.14 },
    { stockId: 4, symbol: "GOOGL", name: "Alphabet Inc", marketCap: 2100000000000, industry: "Communication Services", price: 0, marketPriceChgPct: -0.46 },
    { stockId: 5, symbol: "AMZN", name: "Amazon.com Inc", marketCap: 1900000000000, industry: "Consumer Cyclical", price: 0, marketPriceChgPct: 0.31 },
    { stockId: 6, symbol: "META", name: "Meta Platforms Inc", marketCap: 1300000000000, industry: "Communication Services", price: 0, marketPriceChgPct: -1.22 },
    { stockId: 7, symbol: "TSLA", name: "Tesla, Inc", marketCap: 850000000000, industry: "Automotive", price: 0, marketPriceChgPct: 1.95 },
    { stockId: 8, symbol: "JPM", name: "JPMorgan Chase", marketCap: 590000000000, industry: "Financial Services", price: 0, marketPriceChgPct: -0.84 },
    { stockId: 9, symbol: "V", name: "Visa Inc", marketCap: 560000000000, industry: "Financial Services", price: 0, marketPriceChgPct: 0.56 },
    { stockId: 10, symbol: "WMT", name: "Walmart Inc", marketCap: 520000000000, industry: "Consumer Defensive", price: 0, marketPriceChgPct: -0.18 },
    { stockId: 11, symbol: "LLY", name: "Eli Lilly", marketCap: 760000000000, industry: "Healthcare", price: 0, marketPriceChgPct: 1.03 },
    { stockId: 12, symbol: "XOM", name: "Exxon Mobil", marketCap: 470000000000, industry: "Energy", price: 0, marketPriceChgPct: -1.41 }
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
    if (!board || !rows.length) return;

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
        const res = await fetch(`${API_BASE_URL}/data/heatmap`);
        if (!res.ok) throw new Error(`Heatmap request failed: ${res.status}`);
        drawHeatmap(await res.json());
    } catch (error) {
        drawHeatmap(initialRows().length ? initialRows() : fallbackRows);
    }
}

window.addEventListener("resize", () => loadHeatmap());
loadHeatmap();
setInterval(loadHeatmap, FIVE_MINUTES);
