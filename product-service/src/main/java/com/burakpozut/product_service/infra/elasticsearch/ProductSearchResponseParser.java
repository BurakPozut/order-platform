package com.burakpozut.product_service.infra.elasticsearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.product_service.app.port.ProductSearchResult;
import com.burakpozut.product_service.domain.ProductStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSearchResponseParser {

    private final ObjectMapper objectMapper;

    public List<ProductSearchResult> parse(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        JsonNode hits = root.path("hits").path("hits");
        List<ProductSearchResult> list = new ArrayList<>();
        for (JsonNode hit : hits) {
            try {
                String id = hit.path("_id").asText();
                JsonNode src = hit.path("_source");
                list.add(new ProductSearchResult(
                        UUID.fromString(id),
                        src.path("name").asText(),
                        BigDecimal.valueOf(src.path("price").asDouble()),
                        Currency.valueOf(src.path("currency").asText()),
                        ProductStatus.valueOf(src.path("status").asText())));
            } catch (IllegalArgumentException e) {
                log.debug("Skipping hit with invalid UUID _id: {}", hit.path("_id").asText());
            }
        }
        return list;
    }
}