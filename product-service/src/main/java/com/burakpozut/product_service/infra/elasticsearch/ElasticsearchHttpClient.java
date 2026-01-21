package com.burakpozut.product_service.infra.elasticsearch;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ElasticsearchHttpClient {

    @Value("${spring.elasticsearch.uris:http://localhost:9200}")
    private String esUris;

    @Value("${spring.elasticsearch.username:}")
    private String esUsername;

    @Value("${spring.elasticsearch.password:}")
    private String esPassword;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5)).build();

    public record EsResponse(int statusCode, String body) {
    }

    public EsResponse put(String path, String body) {
        return execute("PUT", path, body, "application/json");
    }

    public EsResponse post(String path, String body, String contentType) {
        return execute("POST", path, body, contentType);
    }

    public EsResponse post(String path) {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(uri(path))
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody());
        addAuth(b);
        return send(b.build());
    }

    private EsResponse execute(String method, String path, String body, String contentType) {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(uri(path))
                .header("Content-Type", contentType)
                .header("Accept", "application/json");
        addAuth(b);
        if ("PUT".equals(method)) {
            b.PUT(HttpRequest.BodyPublishers.ofString(body));
        } else {
            b.POST(HttpRequest.BodyPublishers.ofString(body));
        }
        return send(b.build());
    }

    private URI uri(String path) {
        String base = esUris.split(",")[0].trim();
        return URI.create(base + "/" + path);
    }

    private void addAuth(HttpRequest.Builder b) {
        if (esUsername != null && !esUsername.isBlank()) {
            String cred = esUsername + ":" + (esPassword != null ? esPassword : "");
            b.header("Authorization",
                    "Basic " + Base64.getEncoder().encodeToString(cred.getBytes(StandardCharsets.UTF_8)));
        }
    }

    private EsResponse send(HttpRequest req) {
        try {
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            return new EsResponse(res.statusCode(), res.body());
        } catch (Exception e) {
            throw new RuntimeException("Elasticsearch request failed: " + e.getMessage(), e);
        }
    }
}
