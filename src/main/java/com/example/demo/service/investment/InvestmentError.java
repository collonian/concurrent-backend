package com.example.demo.service.investment;

public enum InvestmentError {
    INVALID_PRODUCT("error.investment.invalid_product"),
    UNMATCHED_USER("error.investment.unmatched_user"),
    NOT_STARTED("error.investment.not_started"),
    FINISHED("error.investment.finished"),
    SOLDOUT("error.investment.sold_out"),
    EXCEED_LIMIT("error.investment.exceed_limit")
    ;

    private final String messageCode;
    InvestmentError(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }
}
