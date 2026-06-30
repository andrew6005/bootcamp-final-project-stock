const fs = require("fs");
const path = require("path");

const root = path.resolve(__dirname, "..");
const publicDir = path.join(root, "public");
const staticDir = path.join(root, "src/main/resources/static");
const templateDir = path.join(root, "src/main/resources/templates");
const apiBaseUrl = process.env.STOCKMAP_CLIENT_API_BASE_URL || "/api";

fs.rmSync(publicDir, { recursive: true, force: true });
fs.mkdirSync(publicDir, { recursive: true });
fs.cpSync(staticDir, publicDir, { recursive: true });

fs.copyFileSync(path.join(templateDir, "heatmap.html"), path.join(publicDir, "index.html"));
fs.copyFileSync(path.join(templateDir, "heatmap.html"), path.join(publicDir, "heatmap.html"));
fs.copyFileSync(path.join(templateDir, "stock-detail.html"), path.join(publicDir, "stock-detail.html"));

fs.mkdirSync(path.join(publicDir, "js"), { recursive: true });
fs.writeFileSync(
    path.join(publicDir, "js/config.js"),
    `window.STOCKMAP_API_BASE_URL = ${JSON.stringify(apiBaseUrl)};\n`
);
