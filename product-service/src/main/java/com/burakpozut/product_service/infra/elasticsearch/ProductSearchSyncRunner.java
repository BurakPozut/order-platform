package com.burakpozut.product_service.infra.elasticsearch;

import com.burakpozut.product_service.app.service.elasticsearch.SyncProductSearchService;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class ProductSearchSyncRunner implements ApplicationRunner {

    private final SyncProductSearchService syncProductSearchService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("product.search.sync.startup.start");
        try {
            syncProductSearchService.sync();
            log.info("product.search.sync.startup.completed");
        } catch (Exception e) {
            log.warn("product.search.sync.startup.failed message={} action=continuing",
                    e.getMessage(), e);
        }
    }
}