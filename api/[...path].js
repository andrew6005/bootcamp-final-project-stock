const DEFAULT_BACKEND_URL = "https://stockmap-api.onrender.com";

module.exports = async function handler(req, res) {
    const backendUrl = (process.env.STOCKMAP_API_BASE_URL || DEFAULT_BACKEND_URL).replace(/\/+$/, "");
    const path = Array.isArray(req.query.path) ? req.query.path.join("/") : req.query.path || "";
    const query = new URLSearchParams(req.query);
    query.delete("path");

    const target = new URL(`${backendUrl}/${path}`);
    query.forEach((value, key) => target.searchParams.append(key, value));

    try {
        const upstream = await fetch(target, {
            method: req.method,
            headers: {
                accept: req.headers.accept || "application/json"
            }
        });

        const contentType = upstream.headers.get("content-type");
        if (contentType) res.setHeader("content-type", contentType);
        res.status(upstream.status).send(await upstream.text());
    } catch (error) {
        res.status(502).json({
            error: "Backend request failed",
            message: error.message
        });
    }
};
