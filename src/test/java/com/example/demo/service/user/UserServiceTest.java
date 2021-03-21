package com.example.demo.service.user;

import com.example.demo.DemoApplication;
import com.example.demo.repository.mybatis.mapper.UserMapper;
import com.example.demo.service.user.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class UserServiceTest {
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void beforeEach() {
        when(userMapper.findByUserId(eq(BigDecimal.ONE)))
                .thenReturn(new User(BigDecimal.ONE));
        when(userMapper.findByUserId(eq(BigDecimal.TEN)))
                .thenReturn(new User(BigDecimal.TEN));
    }

    @Test
    public void shouldPassUserId_whenFindByUserId() {
        // when
        User user = userService.findByUserId(BigDecimal.ONE).get();

        // then
        verify(userMapper).findByUserId(eq(BigDecimal.ONE));
        assertEquals(user.getUserId(), BigDecimal.ONE);
    }
}