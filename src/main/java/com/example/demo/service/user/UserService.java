package com.example.demo.service.user;

import com.example.demo.repository.mybatis.mapper.UserMapper;
import com.example.demo.service.user.vo.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Transactional
@Service
public class UserService {
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Optional<User> findByUserId(BigDecimal userId) {
        return Optional.ofNullable(userMapper.findByUserId(userId));
    }
}
