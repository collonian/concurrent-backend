package com.example.demo.repository.mybatis.mapper;

import com.example.demo.service.user.vo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Mapper
@Repository
public interface UserMapper {
    User findByUserId(@Param("userId") BigDecimal userId);
}
