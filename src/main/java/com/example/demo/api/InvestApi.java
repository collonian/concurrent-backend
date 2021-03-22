package com.example.demo.api;

import com.example.demo.service.investment.InvalidInvestmentProblem;
import com.example.demo.service.investment.InvestmentService;
import com.example.demo.service.investment.InvestmentError;
import com.example.demo.service.investment.vo.Investment;
import com.example.demo.service.investment.vo.InvestmentParam;
import com.example.demo.service.user.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/investments")
public class InvestApi {
    private final InvestmentService investmentService;

    @Autowired
    public InvestApi(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @PostMapping
    public ResponseEntity<Investment> invest(@RequestBody InvestmentParam investmentParam,
                                                  @AuthenticationPrincipal User user)  {
        if(!user.getUserId().equals(investmentParam.getUserId())) {
            throw new InvalidInvestmentProblem(InvestmentError.UNMATCHED_USER);
        }
        investmentService.validateInvestment(investmentParam);

        boolean accepted = investmentService.tryInvestment(investmentParam);
        if(!accepted) {
            throw new InvalidInvestmentProblem(InvestmentError.EXCEED_LIMIT);
        }
        Investment result = investmentService.markInvestment(investmentParam);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.getId())
                .toUri();
        return ResponseEntity.created(location).body(result);
    }
}
