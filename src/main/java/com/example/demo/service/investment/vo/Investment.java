package com.example.demo.service.investment.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Investment {
    private String id;
    private BigDecimal productId;
    private BigDecimal userId;
    private String title;
    private BigDecimal totalInvestingAmount;
    private BigDecimal investingAmount;
    private LocalDateTime createdAt;

    public static Investment from(InvestmentParam param) {
        return create(param.getProductId(), param.getUserId(), param.getInvestingAmount());
    }
    public static Investment create(BigDecimal productId, BigDecimal userId, BigDecimal investingAmount) {
        return create(productId, userId, investingAmount, null, null);
    }
    public static Investment create(BigDecimal productId, BigDecimal userId, BigDecimal investingAmount, String title, BigDecimal totalInvestingAmount) {
        Investment investment = new Investment();
        investment.id = UUID.randomUUID().toString();
        investment.productId = productId;
        investment.userId = userId;
        investment.investingAmount = investingAmount;
        investment.title = title;
        investment.totalInvestingAmount = totalInvestingAmount;
        return investment;
    }
}