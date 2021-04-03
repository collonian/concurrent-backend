package com.example.demo.repository.investment;

import com.example.demo.repository.mybatis.mapper.InvestmentMapper;
import com.example.demo.repository.mybatis.mapper.ProductMapper;
import com.example.demo.service.Page;
import com.example.demo.service.investment.InvalidInvestmentProblem;
import com.example.demo.service.investment.InvestmentError;
import com.example.demo.service.investment.vo.Investment;
import com.example.demo.service.investment.vo.InvestmentEvent;
import com.example.demo.service.investment.vo.InvestmentParam;
import com.example.demo.service.product.vo.Product;
import com.example.demo.service.user.vo.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class InvestmentRepositoryImplTest {
    @Mock
    private InvestmentMapper investmentMapper;
    @InjectMocks
    InvestmentRepositoryImpl investmentRepository;

    @Test
    public void shouldMarkInvertEvent_whenIsAcceptable_givenUnacceptableInvestment() {
        when(investmentMapper.isInvestmentAccepted(any()))
                .thenReturn(false);

        investmentRepository.isAcceptable(InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("123")));

        ArgumentCaptor<InvestmentEvent> captor = ArgumentCaptor.forClass(InvestmentEvent.class);
        verify(investmentMapper, times(2)).markInvestmentEvent(captor.capture());
        InvestmentEvent investmentEvent = captor.getAllValues().get(0);
        InvestmentEvent invertEvent = captor.getAllValues().get(1);
        verify(investmentMapper).isInvestmentAccepted(eq(investmentEvent));
        assertEquals(new BigDecimal("123"), investmentEvent.getInvestingAmount());
        assertEquals(new BigDecimal("-123"), invertEvent.getInvestingAmount());
    }
    @Test
    public void shouldMarkInvestmentEventOnlyOnce_whenIsAcceptable_givenAcceptableInvestment() {
        when(investmentMapper.isInvestmentAccepted(any()))
                .thenReturn(true);

        investmentRepository.isAcceptable(InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("123")));

        ArgumentCaptor<InvestmentEvent> captor = ArgumentCaptor.forClass(InvestmentEvent.class);
        verify(investmentMapper).markInvestmentEvent(captor.capture());
        InvestmentEvent investmentEvent = captor.getValue();
        verify(investmentMapper).isInvestmentAccepted(eq(investmentEvent));
        assertEquals(new BigDecimal("123"), investmentEvent.getInvestingAmount());
    }

    @Test
    public void shouldSaveAndFind_whenSave() {
        // when
        Investment param = Investment.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("255"));
        investmentRepository.save(param);

        // then
        ArgumentCaptor<Investment> captor = ArgumentCaptor.forClass(Investment.class);
        verify(investmentMapper).markInvestment(captor.capture());
        assertEquals(BigDecimal.ONE, captor.getValue().getProductId());
        assertEquals(BigDecimal.TEN, captor.getValue().getUserId());
        assertEquals(new BigDecimal("255"), captor.getValue().getInvestingAmount());

        verify(investmentMapper).findById(eq(captor.getValue().getId()));
    }

    @Test
    public void shouldPassUserId_whenCountByUser() {
        when(investmentMapper.countByUserId(any())).thenReturn(123);

        int result = investmentRepository.countByUser(new User(BigDecimal.ONE));

        verify(investmentMapper).countByUserId(eq(BigDecimal.ONE));
        assertEquals(123, result);
    }

    @Test
    public void shouldPassUserId_whenFindByUser() {
        when(investmentMapper.findByUserId(any(), any()))
                .thenReturn(Collections.singletonList(Investment.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("123"))));


        List<Investment> result = investmentRepository.findByUser(new User(BigDecimal.ONE), new Page(0, 10));

        verify(investmentMapper).findByUserId(eq(BigDecimal.ONE), any());
        assertEquals(1, result.size());
    }
}