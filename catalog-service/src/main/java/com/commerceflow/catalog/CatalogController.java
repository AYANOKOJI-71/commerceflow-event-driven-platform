package com.commerceflow.catalog;

import java.util.List;
import java.util.Map;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {
  @GetMapping
  @Cacheable("catalog-products")
  public List<Map<String, Object>> products() {
    return List.of(
        Map.of("sku", "SKU-URBAN-MUG", "name", "Urban Mug", "priceCents", 1999, "stock", 24),
        Map.of("sku", "SKU-DESK-LAMP", "name", "Desk Lamp", "priceCents", 3499, "stock", 12));
  }
}
