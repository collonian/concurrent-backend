package com.example.demo.service.investment;

import com.example.demo.repository.mybatis.mapper.InvestmentMapper;
import com.example.demo.repository.mybatis.mapper.ProductMapper;
import com.example.demo.service.Page;
import com.example.demo.service.investment.vo.InvestmentEvent;
import com.example.demo.service.investment.vo.InvestmentParam;
import com.example.demo.service.investment.vo.InvestmentList;
import com.example.demo.service.investment.vo.Investment;
import com.example.demo.service.product.vo.Product;
import com.example.demo.service.user.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class InvestmentService {
    private final InvestmentMapper investmentMapper;
    private final ProductMapper productMapper;

    @Autowired
    public InvestmentService(InvestmentMapper investmentMapper, ProductMapper productMapper) {
        this.investmentMapper = investmentMapper;
        this.productMapper = productMapper;
    }

    public InvestmentList queryByUser(User user, Page page) {
        int count = investmentMapper.countByUserId(user.getUserId());
        List<Investment> investments = count == 0 ?
                Collections.emptyList() :
                investmentMapper.findByUserId(user.getUserId(), page)
                ;
        return new InvestmentList(count, investments);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
    public boolean requestInvestment(InvestmentParam investment) {
        InvestmentEvent event = InvestmentEvent.from(investment);
        investmentMapper.markInvestmentEvent(event);
        boolean accepted = investmentMapper.isInvestmentAccepted(event);
        if(!accepted) {
            InvestmentEvent invertEvent = InvestmentEvent.invert(event);
            investmentMapper.markInvestmentEvent(invertEvent);
        }
        return accepted;
    }

    public Investment markInvestment(InvestmentParam investmentParam) {
        Investment investment = Investment.from(investmentParam);
        investmentMapper.markInvestment(investment);
        return investmentMapper.findById(investment.getId());
    }

    public void validateInvestment(InvestmentParam investmentParam) {
        Product product = productMapper.findByProductId(investmentParam.getProductId());
        if(null == product) {
            throw new InvalidInvestmentProblem(InvestmentError.INVALID_PRODUCT);
        }

        switch(product.getStatus()) {
            case PLANNED:
                throw new InvalidInvestmentProblem(InvestmentError.NOT_STARTED);
            case CLOSED:
                throw new InvalidInvestmentProblem(InvestmentError.FINISHED);
            case SOLDOUT:
                throw new InvalidInvestmentProblem(InvestmentError.SOLDOUT);
            case OPEN:
                break;
        }
    }
}
