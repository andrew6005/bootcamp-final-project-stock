const fs = require("fs");
const path = require("path");

const root = path.resolve(__dirname, "..");
const staticConfigPath = path.join(root, "src/main/resources/static/js/config.js");
const apiBaseUrl = process.env.STOCKMAP_API_BASE_URL || "https://stockmap-api.onrender.com";

fs.writeFileSync(
    staticConfigPath,
    `window.STOCKMAP_API_BASE_URL = ${JSON.stringify(apiBaseUrl)};\n`
);

console.log(`Configured Vercel API base URL: ${apiBaseUrl}`);
