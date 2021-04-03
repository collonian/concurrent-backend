package com.example.demo.service.investment;

import com.example.demo.service.Page;
import com.example.demo.service.investment.vo.InvestmentParam;
import com.example.demo.service.investment.vo.InvestmentList;
import com.example.demo.service.investment.vo.Investment;
import com.example.demo.service.product.ProductRepository;
import com.example.demo.service.product.vo.Product;
import com.example.demo.service.user.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class InvestmentService {
    private final InvestmentRepository investmentRepository;
    private final ProductRepository productRepository;

    @Autowired
    public InvestmentService(InvestmentRepository investmentRepository, ProductRepository productRepository) {
        this.investmentRepository = investmentRepository;
        this.productRepository = productRepository;
    }

    public InvestmentList findByUser(User user, Page page) {
        int count = investmentRepository.countByUser(user);
        List<Investment> investments = count == 0 ?
                Collections.emptyList() :
                investmentRepository.findByUser(user, page)
                ;
        return new InvestmentList(count, investments);
    }

    public Investment invest(InvestmentParam investmentParam) throws InvalidInvestmentProblem {
        validate(investmentParam);
        if(investmentRepository.isAcceptable(investmentParam)) {
            Investment investment = Investment.from(investmentParam);
            return investmentRepository.save(investment);
        } else {
            throw new InvalidInvestmentProblem(InvestmentError.EXCEED_LIMIT);
        }
    }

    public void validate(InvestmentParam investmentParam) throws InvalidInvestmentProblem {
        Product product = productRepository.findByProductId(investmentParam.getProductId());
        if(null == product) {
            throw new InvalidInvestmentProblem(InvestmentError.INVALID_PRODUCT);
        }

        switch(product.getStatus()) {
            case OPEN:
                break;
            case PLANNED:
                throw new InvalidInvestmentProblem(InvestmentError.NOT_STARTED);
            case CLOSED:
                throw new InvalidInvestmentProblem(InvestmentError.FINISHED);
            case SOLDOUT:
                throw new InvalidInvestmentProblem(InvestmentError.SOLDOUT);
        }
    }

}
