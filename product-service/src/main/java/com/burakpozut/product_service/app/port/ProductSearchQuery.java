package com.burakpozut.product_service.app.port;

import java.util.List;

public interface ProductSearchQuery {
    List<ProductSearchResult> search(String query);

}
