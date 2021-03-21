package com.example.demo.service.investment.vo;

import lombok.Getter;

import java.util.List;

@Getter
public class InvestmentList {
    private int count;
    private List<Investment> investments;

    public InvestmentList(int count, List<Investment> investments) {
        this.count = count;
        this.investments = investments;
    }
}
