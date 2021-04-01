package com.example.demo.repository.mybatis.mapper;

import com.example.demo.service.Page;
import com.example.demo.service.investment.vo.Investment;
import com.example.demo.service.investment.vo.InvestmentEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@MybatisTest
class InvestmentMapperTest {
    @Autowired
    private InvestmentMapper mapper;

    @Test
    void findByUserId() {
        List<Investment> result = mapper.findByUserId(new BigDecimal("3"), new Page(0, 10));

        assertEquals(5, result.size());
    }

    @Test
    void countByUserId() {
        int count = mapper.countByUserId(new BigDecimal("3"));

        assertEquals(5, count);
    }

    @Test
    void markInvestment() {
        mapper.markInvestment(Investment.create(
                new BigDecimal("5"),
                new BigDecimal("3"),
                new BigDecimal("500")
        ));

        int count = mapper.countByUserId(new BigDecimal("3"));
        assertEquals(6, count);
    }

    @Test
    void isInvestmentAccepted() {
        InvestmentEvent acceptable = InvestmentEvent.create(new BigDecimal("6"), new BigDecimal("3"), new BigDecimal("150"));
        mapper.markInvestmentEvent(acceptable);
        assertTrue(mapper.isInvestmentAccepted(acceptable));


        InvestmentEvent unacceptable = InvestmentEvent.create(new BigDecimal("6"), new BigDecimal("3"), new BigDecimal("150"));
        mapper.markInvestmentEvent(unacceptable);
        assertFalse(mapper.isInvestmentAccepted(unacceptable));
    }

    @Test
    void findById() {
        Investment investment = mapper.findById("uuid-5");
        assertEquals(new BigDecimal("3"), investment.getProductId());
        assertEquals(new BigDecimal("2"), investment.getUserId());
        assertEquals(new BigDecimal("100"), investment.getInvestingAmount());
    }
}