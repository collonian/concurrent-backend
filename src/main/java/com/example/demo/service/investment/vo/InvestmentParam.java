package com.example.demo.service.investment.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class InvestmentParam {
    private BigDecimal productId;
    private BigDecimal userId;
    private BigDecimal investingAmount;

    public static InvestmentParam create(BigDecimal productId, BigDecimal userId, BigDecimal investingAmount) {
        InvestmentParam investment = new InvestmentParam();
        investment.productId = productId;
        investment.userId = userId;
        investment.investingAmount = investingAmount;
        return investment;
    }
}
