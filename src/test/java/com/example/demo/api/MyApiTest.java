package com.example.demo.api;

import com.example.demo.service.investment.InvestmentService;
import com.example.demo.service.investment.vo.Investment;
import com.example.demo.service.investment.vo.InvestmentList;
import com.example.demo.service.user.UserService;
import com.example.demo.service.user.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MyApi.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class MyApiTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private InvestmentService investmentService;
    @MockBean
    private UserService userService;

    @BeforeEach
    public void beforeAll() {
        when(userService.findByUserId(BigDecimal.ONE))
                .thenReturn(Optional.of(new User(BigDecimal.ONE)));
    }
    @Test
    public void shouldSucceed_whenFindMyInvestment_givenXUserId() throws Exception {
        // given
        when(investmentService.findByUser(any(), any()))
                .thenReturn(new InvestmentList(
                        1,
                        Collections.singletonList(Investment.create(
                                BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("123"),
                                "product title", new BigDecimal("5678")
                        ))
                ));

        // when
        final ResultActions result =
                mvc.perform(
                        get("/api/my/investments")
                                .header("X-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("count").value(1))
                .andExpect(jsonPath("investments[0].productId").value(1))
                .andExpect(jsonPath("investments[0].userId").value(10))
                .andExpect(jsonPath("investments[0].investingAmount").value(123))
                .andExpect(jsonPath("investments[0].title").value("product title"))
                .andExpect(jsonPath("investments[0].totalInvestingAmount").value(5678))
        ;
        verify(investmentService).findByUser(any(), any());
    }
}