package com.example.demo.service.product;

import com.example.demo.repository.mybatis.mapper.ProductMapper;
import com.example.demo.service.Page;
import com.example.demo.service.product.vo.Product;
import com.example.demo.service.product.vo.ProductList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class ProductService {
    private final ProductMapper productMapper;

    @Autowired
    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public ProductList queryInvestableProducts(Page page) {
        LocalDateTime now = LocalDateTime.now();
        int count = productMapper.countInvestable(now);
        List<Product> products = count == 0 ?
                Collections.emptyList() :
                productMapper.findInvestable(now, page);
        return new ProductList(count, products);
    }
}
