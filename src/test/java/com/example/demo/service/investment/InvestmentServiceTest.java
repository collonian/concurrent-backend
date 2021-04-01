package com.example.demo.service.investment;

import com.example.demo.repository.mybatis.mapper.InvestmentMapper;
import com.example.demo.repository.mybatis.mapper.ProductMapper;
import com.example.demo.service.Page;
import com.example.demo.service.investment.vo.Investment;
import com.example.demo.service.investment.vo.InvestmentEvent;
import com.example.demo.service.investment.vo.InvestmentList;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class InvestmentServiceTest {
    @Mock
    private InvestmentMapper investmentMapper;
    @Mock
    private ProductMapper productMapper;
    @InjectMocks
    private InvestmentService investmentService;

    @Test
    public void shouldNotCallInvestment_whenFindByUser_givenEmptyInvestment() {
        // given
        when(investmentMapper.countByUserId(any())).thenReturn(0);

        // when
        InvestmentList result = investmentService.findByUser(new User(BigDecimal.ZERO), new Page(0, 10));

        // then
        assertEquals(0, result.getCount());
        assertTrue(result.getInvestments().isEmpty());
        verify(investmentMapper).countByUserId(eq(BigDecimal.ZERO));
        verify(investmentMapper, times(0)).findByUserId(any(), any());
    }

    @Test
    public void shouldPassUserId_whenFindByUser() {
        // given
        when(investmentMapper.countByUserId(any()))
                .thenReturn(1);
        when(investmentMapper.findByUserId(any(), any()))
                .thenReturn(Collections.singletonList(new Investment()));

        // when
        InvestmentList result = investmentService.findByUser(new User(new BigDecimal("13")), new Page(0, 10));

        // then
        assertEquals(1, result.getCount());
        assertEquals(1, result.getInvestments().size());
        verify(investmentMapper).countByUserId(eq(new BigDecimal("13")));
        verify(investmentMapper).findByUserId(eq(new BigDecimal("13")), any());
    }

    @Test
    public void shouldNotMarkInvertEvent_whenTryInvestment_givenAcceptableRequest() {
        // given
        when(investmentMapper.isInvestmentAccepted(any()))
                .thenReturn(true);

        // when
        InvestmentParam param = InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("255"));
        investmentService.tryInvestment(param);

        // then
        verify(investmentMapper).isInvestmentAccepted(any());
        verify(investmentMapper).markInvestmentEvent(any());
    }
    @Test
    public void shouldMarkInvertEvent_whenTryInvestment_givenUnacceptableRequest() {
        // given
        when(investmentMapper.isInvestmentAccepted(any()))
                .thenReturn(false);

        // when
        InvestmentParam param = InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("255"));
        investmentService.tryInvestment(param);

        // then
        verify(investmentMapper).isInvestmentAccepted(any());
        ArgumentCaptor<InvestmentEvent> captor = ArgumentCaptor.forClass(InvestmentEvent.class);
        verify(investmentMapper, times(2)).markInvestmentEvent(captor.capture());

        assertEquals(new BigDecimal("255"), captor.getAllValues().get(0).getInvestingAmount());
        assertEquals(new BigDecimal("-255"), captor.getAllValues().get(1).getInvestingAmount());
    }

    @Test
    public void shouldMarkAndFind_whenMarkInvestment() {
        // when
        InvestmentParam param = InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("255"));
        investmentService.markInvestment(param);

        // then
        ArgumentCaptor<Investment> captor = ArgumentCaptor.forClass(Investment.class);
        verify(investmentMapper).markInvestment(captor.capture());
        assertEquals(BigDecimal.ONE, captor.getValue().getProductId());
        assertEquals(BigDecimal.TEN, captor.getValue().getUserId());
        assertEquals(new BigDecimal("255"), captor.getValue().getInvestingAmount());

        verify(investmentMapper).findById(captor.getValue().getId());
    }


    @Test
    public void shouldPass_whenValidate_givenNormalProduct() {
        // given
        when(productMapper.findByProductId(BigDecimal.ONE))
                .thenReturn(Product.builder()
                        .startedAt(LocalDateTime.now().minusDays(10))
                        .finishedAt(LocalDateTime.now().plusDays(5))
                        .totalInvestingAmount(new BigDecimal("10000"))
                        .collectedInvestingAmount(new BigDecimal("100"))
                        .build()
                );

        // when
        investmentService.validateInvestment(InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("123")));

        // then
        verify(productMapper).findByProductId(eq(BigDecimal.ONE));
    }
    @Test
    public void shouldThrowInvalidProduct_whenValidate_givenNotExistsProduct() {
        // when
        InvalidInvestmentProblem problem = assertThrows(InvalidInvestmentProblem.class, () -> {
            investmentService.validateInvestment(InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("123")));
        });

        // then
        assertEquals(InvestmentError.INVALID_PRODUCT, problem.getParameters().get("error_code"));
    }

    @Test
    public void shouldThrowNotStarted_whenValidate_givenNotStartedProduct() {
        // given
        when(productMapper.findByProductId(BigDecimal.ONE))
                .thenReturn(Product.builder()
                        .startedAt(LocalDateTime.now().plusDays(5))
                        .finishedAt(LocalDateTime.now().plusDays(10))
                        .totalInvestingAmount(new BigDecimal("10000"))
                        .collectedInvestingAmount(BigDecimal.ZERO)
                        .build()
                );

        // when
        InvalidInvestmentProblem problem = assertThrows(InvalidInvestmentProblem.class, () -> {
            investmentService.validateInvestment(InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("123")));
        });

        // then
        assertEquals(InvestmentError.NOT_STARTED, problem.getParameters().get("error_code"));
    }

    @Test
    public void shouldThrowFinished_whenValidate_givenClosedProduct() {
        // given
        when(productMapper.findByProductId(BigDecimal.ONE))
                .thenReturn(Product.builder()
                        .startedAt(LocalDateTime.now().minusDays(10))
                        .finishedAt(LocalDateTime.now().minusDays(5))
                        .totalInvestingAmount(new BigDecimal("10000"))
                        .collectedInvestingAmount(BigDecimal.ZERO)
                        .build()
                );

        // when
        InvalidInvestmentProblem problem = assertThrows(InvalidInvestmentProblem.class, () -> {
            investmentService.validateInvestment(InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("123")));
        });

        // then
        assertEquals(InvestmentError.FINISHED, problem.getParameters().get("error_code"));
    }

    @Test
    public void shouldThrowSoldout_whenValidate_givenSoldoutProduct() {
        // given
        when(productMapper.findByProductId(BigDecimal.ONE))
                .thenReturn(Product.builder()
                        .startedAt(LocalDateTime.now().minusDays(10))
                        .finishedAt(LocalDateTime.now().plusDays(5))
                        .totalInvestingAmount(new BigDecimal("10000"))
                        .collectedInvestingAmount(new BigDecimal("10000"))
                        .build()
                );

        // when
        InvalidInvestmentProblem problem = assertThrows(InvalidInvestmentProblem.class, () -> {
            investmentService.validateInvestment(InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("123")));
        });

        // then
        assertEquals(InvestmentError.SOLDOUT, problem.getParameters().get("error_code"));
    }
}