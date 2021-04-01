package com.example.demo.repository.investment;

import com.example.demo.repository.mybatis.mapper.InvestmentMapper;
import com.example.demo.repository.mybatis.mapper.ProductMapper;
import com.example.demo.service.Page;
import com.example.demo.service.investment.InvalidInvestmentProblem;
import com.example.demo.service.investment.InvestmentError;
import com.example.demo.service.investment.InvestmentRepository;
import com.example.demo.service.investment.vo.Investment;
import com.example.demo.service.investment.vo.InvestmentEvent;
import com.example.demo.service.investment.vo.InvestmentParam;
import com.example.demo.service.product.vo.Product;
import com.example.demo.service.user.vo.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class InvestmentRepositoryImpl implements InvestmentRepository {
    private final InvestmentMapper investmentMapper;
    private final ProductMapper productMapper;

    public InvestmentRepositoryImpl(InvestmentMapper investmentMapper, ProductMapper productMapper) {
        this.investmentMapper = investmentMapper;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
    public boolean isAcceptable(InvestmentParam investment) {
        InvestmentEvent event = InvestmentEvent.from(investment);
        investmentMapper.markInvestmentEvent(event);
        boolean accepted = investmentMapper.isInvestmentAccepted(event);
        if(!accepted) {
            InvestmentEvent invertEvent = InvestmentEvent.invert(event);
            investmentMapper.markInvestmentEvent(invertEvent);
        }
        return accepted;
    }

    @Override
    public Investment save(Investment investment) {
        investmentMapper.markInvestment(investment);
        return investmentMapper.findById(investment.getId());
    }

    @Override
    public void validateInvestment(InvestmentParam investmentParam) throws InvalidInvestmentProblem {
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

    @Override
    public int countByUser(User user) {
        return investmentMapper.countByUserId(user.getUserId());
    }

    @Override
    public List<Investment> findByUser(User user, Page page) {
        return investmentMapper.findByUserId(user.getUserId(), page);
    }

}
