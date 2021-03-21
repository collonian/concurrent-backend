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
public class InvestmentEvent {
    private String id;
    private BigDecimal productId;
    private BigDecimal userId;
    private BigDecimal investingAmount;
    private LocalDateTime createdAt;

    public static InvestmentEvent from(InvestmentParam param) {
        return create(param.getProductId(), param.getUserId(), param.getInvestingAmount());
    }
    public static InvestmentEvent create(BigDecimal productId, BigDecimal userId, BigDecimal investingAmount) {
        InvestmentEvent event = new InvestmentEvent();
        event.id = UUID.randomUUID().toString();
        event.productId = productId;
        event.userId = userId;
        event.investingAmount = investingAmount;
        return event;
    }

    public static InvestmentEvent invert(InvestmentEvent event) {
        return create(event.getProductId(), event.getUserId(), event.getInvestingAmount().negate());
    }
}
