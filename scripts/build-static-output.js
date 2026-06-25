const fs = require("fs");
const path = require("path");

const root = path.resolve(__dirname, "..");
const sourceDir = path.join(root, "src/main/resources/static");
const outputDir = path.join(root, "static");
const apiBaseUrl = process.env.STOCKMAP_API_BASE_URL || "https://stockmap-api.onrender.com";

fs.rmSync(outputDir, { recursive: true, force: true });
fs.cpSync(sourceDir, outputDir, { recursive: true });
fs.writeFileSync(
    path.join(outputDir, "js/config.js"),
    `window.STOCKMAP_API_BASE_URL = ${JSON.stringify(apiBaseUrl)};\n`
);

console.log(`Built Vercel static output at ${path.relative(root, outputDir)}`);
