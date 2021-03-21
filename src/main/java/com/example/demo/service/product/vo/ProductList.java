package com.example.demo.service.product.vo;

import lombok.Getter;

import java.util.List;

@Getter
public class ProductList {
    private int count;
    private List<Product> products;

    public ProductList(int count, List<Product> products) {
        this.count = count;
        this.products = products;
    }
}
