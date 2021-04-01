package com.example.demo.service.investment;

import com.example.demo.service.Page;
import com.example.demo.service.investment.vo.Investment;
import com.example.demo.service.investment.vo.InvestmentParam;
import com.example.demo.service.user.vo.User;

import java.util.List;

public interface InvestmentRepository {
    boolean isAcceptable(InvestmentParam investment);
    Investment save(Investment investmentParam);
    void validateInvestment(InvestmentParam investmentParam) throws InvalidInvestmentProblem;
    int countByUser(User user);
    List<Investment> findByUser(User user, Page page);
}
