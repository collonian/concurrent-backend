package com.example.demo.api;

import com.example.demo.service.product.ProductService;
import com.example.demo.service.product.vo.Product;
import com.example.demo.service.product.vo.ProductList;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductApi.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class ProductApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService productService;
    @MockBean
    private UserService userService;

    @BeforeEach
    public void beforeAll() {
        when(userService.findByUserId(BigDecimal.ONE))
                .thenReturn(Optional.of(new User(BigDecimal.ONE)));
    }

    @Test
    public void shouldSucceed_whenFindInvestableProducts_givenXUserId() throws Exception {
        // given
        when(productService.findInvestable(any()))
                .thenReturn(new ProductList(
                        1,
                        Collections.singletonList(
                                Product.builder()
                                        .productId(new BigDecimal("123"))
                                        .title("Some product")
                                        .totalInvestingAmount(new BigDecimal("2345"))
                                        .startedAt(LocalDateTime.now().minusDays(10))
                                        .finishedAt(LocalDateTime.now().plusDays(10))
                                        .collectedInvestingAmount(new BigDecimal("345"))
                                        .build()
                        )
                ));

        // when
        final ResultActions result =
                mvc.perform(
                    get("/api/products")
                            .header("X-USER-ID", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                );

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("count").value(1))
                .andExpect(jsonPath("products[0].productId").value(123))
                .andExpect(jsonPath("products[0].totalInvestingAmount").value(2345))
                .andExpect(jsonPath("products[0].collectedInvestingAmount").value(345))
        ;
        verify(productService).findInvestable(any());
    }
}