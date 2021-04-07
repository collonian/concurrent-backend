package com.example.demo.repository.mybatis.mapper;

import com.example.demo.service.user.vo.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@MybatisTest
class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void find() {
        User user = userMapper.findByUserId(new BigDecimal("3"));
        assertEquals(new BigDecimal("3"), user.getUserId());
    }
}