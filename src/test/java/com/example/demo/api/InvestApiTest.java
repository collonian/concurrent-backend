package com.example.demo.api;

import com.example.demo.api.config.JacksonConfig;
import com.example.demo.api.config.exception.DemoExceptionHandler;
import com.example.demo.service.investment.InvalidInvestmentProblem;
import com.example.demo.service.investment.InvestmentError;
import com.example.demo.service.investment.InvestmentService;
import com.example.demo.service.investment.vo.Investment;
import com.example.demo.service.investment.vo.InvestmentParam;
import com.example.demo.service.product.ProductService;
import com.example.demo.service.user.UserService;
import com.example.demo.service.user.vo.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvestApi.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import(JacksonConfig.class)
class InvestApiTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private InvestmentService investmentService;
    @MockBean
    private ProductService productService;
    @MockBean
    private UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeAll() {
        when(userService.findByUserId(BigDecimal.ONE))
                .thenReturn(Optional.of(new User(BigDecimal.ONE)));
    }

    @Test
    public void shouldThrowUnmatchedUser_whenInvest_givenUnmatchedUser() throws Exception {
        // when
        String content = objectMapper.writeValueAsString(
                InvestmentParam.create(BigDecimal.ONE, BigDecimal.TEN, new BigDecimal("123"))
        );
        ResultActions result = mvc
                .perform(
                        post("/api/investments")
                                .header("X-USER-ID", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        // then
        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("error_code").value("UNMATCHED_USER"));
    }

    @Test
    public void shouldThrowExceedLimit_whenInvest_givenExeedLimitInvest() throws Exception {
        // given
        when(investmentService.requestInvestment(any()))
                .thenReturn(false);

        // when
        String content = objectMapper.writeValueAsString(
                InvestmentParam.create(BigDecimal.TEN, BigDecimal.ONE, new BigDecimal("123"))
        );
        ResultActions result = mvc
                .perform(
                        post("/api/investments")
                                .header("X-USER-ID", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        // then
        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("error_code").value("EXCEED_LIMIT"));
    }

    @Test
    public void shouldSucceed_whenInvest_givenNormalInvestment() throws Exception {
        // given
        when(investmentService.requestInvestment(any()))
                .thenReturn(true);
        when(investmentService.markInvestment(any()))
                .thenReturn(Investment.create(
                                BigDecimal.TEN, BigDecimal.ONE, new BigDecimal("123"),
                                "some product", new BigDecimal("5432")
                        ));

        // when
        String content = objectMapper.writeValueAsString(
                InvestmentParam.create(BigDecimal.TEN, BigDecimal.ONE, new BigDecimal("123"))
        );
        ResultActions result = mvc
                .perform(
                        post("/api/investments")
                                .header("X-USER-ID", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );


        // then
        result
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("productId").value(10))
                .andExpect(jsonPath("investingAmount").value(123))
                .andExpect(jsonPath("totalInvestingAmount").value(5432))
                ;
    }
}