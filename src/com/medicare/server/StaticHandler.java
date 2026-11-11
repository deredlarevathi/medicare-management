package com.medicare.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Serves the HTML, CSS and JS files from the "web" folder. */
public class StaticHandler implements HttpHandler {
    private final Path root = Paths.get("web").toAbsolutePath().normalize();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (path.equals("/")) path = "/index.html";
        Path file = root.resolve(path.substring(1)).normalize();

        if (!file.startsWith(root) || !Files.isRegularFile(file)) {
            byte[] msg = "Not found".getBytes();
            ex.sendResponseHeaders(404, msg.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(msg); }
            return;
        }
        byte[] data = Files.readAllBytes(file);
        ex.getResponseHeaders().set("Content-Type", contentType(path));
        ex.sendResponseHeaders(200, data.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(data); }
    }

    private String contentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=utf-8";
        if (path.endsWith(".css"))  return "text/css; charset=utf-8";
        if (path.endsWith(".js"))   return "application/javascript; charset=utf-8";
        return "application/octet-stream";
    }
}
