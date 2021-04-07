package com.example.demo.repository.mybatis.mapper;

import com.example.demo.service.Page;
import com.example.demo.service.product.vo.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@MybatisTest
class ProductMapperTest {
    @Autowired
    private ProductMapper productMapper;

    @Test
    public void findInvestable() {
        List<Product> products = productMapper.findInvestable(LocalDateTime.now(), new Page(0, 10));
        assertEquals(2, products.size());

        Product one = products.get(0);
        assertEquals(BigDecimal.ONE, one.getProductId());
        assertEquals("first", one.getTitle());
        assertEquals(new BigDecimal("2"), one.getCollectedCount());
        assertEquals(new BigDecimal("200"), one.getCollectedInvestingAmount());
        assertTrue(LocalDateTime.now().isAfter(one.getStartedAt()));
        assertTrue(LocalDateTime.now().isBefore(one.getFinishedAt()));

        Product two = products.get(1);
        assertEquals(new BigDecimal("2"), two.getProductId());
        assertEquals("second", two.getTitle());
        assertEquals(new BigDecimal("2"), two.getCollectedCount());
        assertEquals(new BigDecimal("300"), two.getCollectedInvestingAmount());
        assertTrue(LocalDateTime.now().isAfter(two.getStartedAt()));
        assertTrue(LocalDateTime.now().isBefore(two.getFinishedAt()));
    }

    @Test
    public void countInvestable() {
        int count = productMapper.countInvestable(LocalDateTime.now());
        assertEquals(2, count);
    }

    @Test
    public void findByProductId() {
        Product product = productMapper.findByProductId(new BigDecimal("4"));
        assertEquals(new BigDecimal("4"), product.getProductId());
        assertEquals("4 title", product.getTitle());
        assertEquals(new BigDecimal("40000"), product.getTotalInvestingAmount());
        assertEquals(new BigDecimal("3"), product.getCollectedCount());
        assertEquals(new BigDecimal("2800"), product.getCollectedInvestingAmount());
    }
}