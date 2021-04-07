package com.example.demo.service.user;

import com.example.demo.service.user.vo.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Transactional
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByUserId(BigDecimal userId) {
        return Optional.ofNullable(userRepository.findByUserId(userId));
    }
}
