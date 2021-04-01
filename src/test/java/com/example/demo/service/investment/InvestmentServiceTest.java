package com.example.demo.service.investment;

import com.example.demo.service.Page;
import com.example.demo.service.investment.vo.Investment;
import com.example.demo.service.investment.vo.InvestmentList;
import com.example.demo.service.investment.vo.InvestmentParam;
import com.example.demo.service.user.vo.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
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
        when(investmentRepository.isAcceptable(any()))
                .thenReturn(true);

        // when
        InvestmentParam param = InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("255"));
        investmentService.invest(param);

        // then
        verify(investmentRepository).validateInvestment(eq(param));
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
        when(investmentRepository.isAcceptable(any()))
                .thenReturn(false);

        // when
        InvestmentParam param = InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("255"));
        InvalidInvestmentProblem problem = assertThrows(InvalidInvestmentProblem.class, () -> investmentService.invest(param));

        // then
        verify(investmentRepository).validateInvestment(eq(param));
        verify(investmentRepository).isAcceptable(eq(param));
        verify(investmentRepository, times(0)).save(any());
        assertEquals(InvestmentError.EXCEED_LIMIT, problem.getParameters().get("error_code"));

    }

}