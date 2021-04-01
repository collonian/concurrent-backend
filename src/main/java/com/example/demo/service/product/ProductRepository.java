package com.example.demo.service.product;

import com.example.demo.service.Page;
import com.example.demo.service.product.vo.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository {
    List<Product> findInvestable(LocalDateTime now, Page page);

    int countInvestable(LocalDateTime now);

    Product findByProductId(BigDecimal productId);
}
