package com.example.demo.api;

import com.example.demo.service.Page;
import com.example.demo.service.investment.InvestmentService;
import com.example.demo.service.investment.vo.InvestmentList;
import com.example.demo.service.user.vo.DemoUserDetails;
import com.example.demo.service.user.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/my")
public class MyApi {
    private final InvestmentService investmentService;

    @Autowired
    public MyApi(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @GetMapping(path="/investments")
    public InvestmentList findMyInvestment(
            @AuthenticationPrincipal DemoUserDetails userDetails,
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) int offset,
            @RequestParam(value = "limit", defaultValue = "20") @Min(1) @Max(100) int limit
    ) {
        return investmentService.findByUser(userDetails.getUser(), new Page(offset, limit));
    }
}
