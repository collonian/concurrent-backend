package com.example.demo.service.product.vo;

import com.example.demo.service.product.ProductStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"productId"})
@Builder
public class Product {
    private BigDecimal productId;
    private String title;
    private BigDecimal totalInvestingAmount;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    private BigDecimal collectedCount;
    private BigDecimal collectedInvestingAmount;

    public ProductStatus getStatus() {
        if( LocalDateTime.now().isBefore(startedAt)) {
            return ProductStatus.PLANNED;
        }
        if(null != collectedInvestingAmount && collectedInvestingAmount.compareTo(totalInvestingAmount) >= 0) {
            return ProductStatus.SOLDOUT;
        }
        if( LocalDateTime.now().isAfter(finishedAt)) {
            return ProductStatus.CLOSED;
        }
        return ProductStatus.OPEN;
    }
}
