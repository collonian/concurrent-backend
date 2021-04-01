package com.example.demo.service.user;

import com.example.demo.service.user.vo.User;

import java.math.BigDecimal;

public interface UserRepository {
    User findByUserId(BigDecimal userId);
}
