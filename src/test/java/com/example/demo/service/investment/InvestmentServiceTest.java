package com.example.demo.service.investment;

import com.example.demo.service.Page;
import com.example.demo.service.investment.vo.Investment;
import com.example.demo.service.investment.vo.InvestmentList;
import com.example.demo.service.investment.vo.InvestmentParam;
import com.example.demo.service.product.ProductRepository;
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
    private InvestmentRepository investmentRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private InvestmentService investmentService;
    private User user = new User(BigDecimal.ONE);

    @Test
    public void shouldNotFindInvestment_whenFindByUser_givenEmptyInvestment() {
        // given
        when(investmentRepository.countByUser(any())).thenReturn(0);

        // when
        InvestmentList result = investmentService.findByUser(user, new Page(0, 10));

        // then
        assertEquals(0, result.getCount());
        assertTrue(result.getInvestments().isEmpty());
        verify(investmentRepository).countByUser(eq(user));
        verify(investmentRepository, times(0)).findByUser(any(), any());
    }

    @Test
    public void shouldFindInvestment_whenFindByUser_thereIsInvestments() {
        // given
        when(investmentRepository.countByUser(any())).thenReturn(1);
        when(investmentRepository.findByUser(any(), any()))
                .thenReturn(Collections.singletonList(new Investment()));

        // when
        InvestmentList result = investmentService.findByUser(user, new Page(0, 10));

        // then
        assertEquals(1, result.getCount());
        assertEquals(1, result.getInvestments().size());
        verify(investmentRepository).countByUser(eq(user));
        verify(investmentRepository).findByUser(eq(user), any());
    }

    @Test
    public void shouldSaveInvestment_whenInvest_givenAcceptableInvestment() {
        // given
        when(productRepository.findByProductId(any()))
                .thenReturn(Product.builder()
                        .startedAt(LocalDateTime.MIN)
                        .finishedAt(LocalDateTime.MAX)
                        .totalInvestingAmount(new BigDecimal("1000"))
                        .collectedInvestingAmount(new BigDecimal("500"))
                        .build()
                );
        when(investmentRepository.isAcceptable(any()))
                .thenReturn(true);

        // when
        InvestmentParam param = InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("255"));
        investmentService.invest(param);

        // then
        verify(investmentRepository).isAcceptable(eq(param));
        ArgumentCaptor<Investment> captor = ArgumentCaptor.forClass(Investment.class);
        verify(investmentRepository).save(captor.capture());

        assertEquals(BigDecimal.ONE, captor.getValue().getProductId());
        assertEquals(BigDecimal.TEN, captor.getValue().getUserId());
        assertEquals(new BigDecimal("255"), captor.getValue().getInvestingAmount());
    }

    @Test
    public void shouldThrowException_whenInvest_givenUnacceptableInvestment() {
        // given
        when(productRepository.findByProductId(any()))
                .thenReturn(Product.builder()
                        .startedAt(LocalDateTime.MIN)
                        .finishedAt(LocalDateTime.MAX)
                        .totalInvestingAmount(new BigDecimal("1000"))
                        .collectedInvestingAmount(new BigDecimal("500"))
                        .build()
                );
        when(investmentRepository.isAcceptable(any()))
                .thenReturn(false);

        // when
        InvestmentParam param = InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("255"));
        InvalidInvestmentProblem problem = assertThrows(InvalidInvestmentProblem.class, () -> investmentService.invest(param));

        // then
        verify(investmentRepository).isAcceptable(eq(param));
        verify(investmentRepository, times(0)).save(any());
        assertEquals(InvestmentError.EXCEED_LIMIT, problem.getParameters().get("error_code"));

    }

    @Test
    public void shouldPass_whenValidate_givenNormalProduct() {
        when(productRepository.findByProductId(any()))
                .thenReturn(Product.builder()
                        .startedAt(LocalDateTime.MIN)
                        .finishedAt(LocalDateTime.MAX)
                        .totalInvestingAmount(new BigDecimal("1000"))
                        .collectedInvestingAmount(new BigDecimal("500"))
                        .build()
                );

        InvestmentParam param = InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("255"));
        investmentService.validate(param);

    }
    @Test
    public void shouldFaildWithInvalidProduct_whenValidate_givenInvalidProduct() {
        when(productRepository.findByProductId(any()))
                .thenReturn(null);

        InvestmentParam param = InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("255"));
        InvalidInvestmentProblem problem = assertThrows(InvalidInvestmentProblem.class, () -> investmentService.validate(param));

        assertEquals(InvestmentError.INVALID_PRODUCT, problem.getParameters().get("error_code"));
        verify(productRepository).findByProductId(eq(BigDecimal.ONE));
    }
    @Test
    public void shouldFailedWithNotStarted_whenValidate_givenNotStartedProduct() {
        when(productRepository.findByProductId(any()))
                .thenReturn(Product.builder()
                        .startedAt(LocalDateTime.now().plusDays(2))
                        .finishedAt(LocalDateTime.MAX)
                        .totalInvestingAmount(new BigDecimal("1000"))
                        .collectedInvestingAmount(new BigDecimal("500"))
                        .build()
                );

        InvestmentParam param = InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("255"));
        InvalidInvestmentProblem problem = assertThrows(InvalidInvestmentProblem.class, () -> investmentService.validate(param));

        assertEquals(InvestmentError.NOT_STARTED, problem.getParameters().get("error_code"));
        verify(productRepository).findByProductId(eq(BigDecimal.ONE));
    }
    @Test
    public void shouldFailedWithFinished_whenValidate_givenFinishedProduct() {
        when(productRepository.findByProductId(any()))
                .thenReturn(Product.builder()
                        .startedAt(LocalDateTime.MIN)
                        .finishedAt(LocalDateTime.now().minusDays(2))
                        .totalInvestingAmount(new BigDecimal("1000"))
                        .collectedInvestingAmount(new BigDecimal("500"))
                        .build()
                );

        InvestmentParam param = InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("255"));
        InvalidInvestmentProblem problem = assertThrows(InvalidInvestmentProblem.class, () -> investmentService.validate(param));

        assertEquals(InvestmentError.FINISHED, problem.getParameters().get("error_code"));
        verify(productRepository).findByProductId(eq(BigDecimal.ONE));
    }

    @Test
    public void shouldFailedWithSoldout_whenValidate_givenSoldoutProduct() {
        when(productRepository.findByProductId(any()))
                .thenReturn(Product.builder()
                        .startedAt(LocalDateTime.MIN)
                        .finishedAt(LocalDateTime.MAX)
                        .totalInvestingAmount(new BigDecimal("1000"))
                        .collectedInvestingAmount(new BigDecimal("1000"))
                        .build()
                );

        InvestmentParam param = InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("255"));
        InvalidInvestmentProblem problem = assertThrows(InvalidInvestmentProblem.class, () -> investmentService.validate(param));

        assertEquals(InvestmentError.SOLDOUT, problem.getParameters().get("error_code"));
        verify(productRepository).findByProductId(eq(BigDecimal.ONE));
    }
}