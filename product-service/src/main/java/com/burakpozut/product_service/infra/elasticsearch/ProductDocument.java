package com.burakpozut.product_service.infra.elasticsearch;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.product_service.domain.Product;
import com.burakpozut.product_service.domain.ProductStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(indexName = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Keyword)
    private String currency;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Integer)
    private Integer inventory; // Add this - it's in your domain!

    public static ProductDocument from(Product product) {
        return new ProductDocument(
                product.id().toString(),
                product.name(),
                product.price().doubleValue(),
                product.currency().name(),
                product.status().name(),
                product.inventory()); // Include inventory
    }

    public Product toDomain() {
        return Product.rehydrate(
                UUID.fromString(id),
                name,
                BigDecimal.valueOf(price),
                Currency.valueOf(currency),
                ProductStatus.valueOf(status),
                0L, // version - not stored in ES
                inventory); // Use inventory from ES
    }
}