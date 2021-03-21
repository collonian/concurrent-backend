package com.example.demo.repository.mybatis.mapper;

import com.example.demo.service.Page;
import com.example.demo.service.investment.vo.InvestmentEvent;
import com.example.demo.service.investment.vo.Investment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Mapper
@Repository
public interface InvestmentMapper {
    List<Investment> findByUserId(@Param("userId") BigDecimal userId, @Param("page") Page page);

    int countByUserId(@Param("userId") BigDecimal userId);

    void markInvestment(@Param("investment") Investment investment);

    void markInvestmentEvent(@Param("investment") InvestmentEvent investmentEvent);

    boolean isInvestmentAccepted(@Param("investment") InvestmentEvent investmentEvent);

    Investment findById(@Param("investmentId") String investmentId);
}
