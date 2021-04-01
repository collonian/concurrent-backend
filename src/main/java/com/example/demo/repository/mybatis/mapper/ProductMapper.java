package com.example.demo.repository.mybatis.mapper;

import com.example.demo.service.Page;
import com.example.demo.service.product.ProductRepository;
import com.example.demo.service.product.vo.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
@Repository
public interface ProductMapper extends ProductRepository {
    List<Product> findInvestable(@Param("now") LocalDateTime now, @Param("page") Page page);

    int countInvestable(@Param("now") LocalDateTime now);

    Product findByProductId(@Param("productId") BigDecimal productId);
}
