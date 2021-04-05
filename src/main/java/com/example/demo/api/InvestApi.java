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
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
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
    public ResponseEntity<Investment> invest(@RequestBody @Valid() InvestmentParam investmentParam,
                                             Errors errors,
                                             @ApiIgnore @AuthenticationPrincipal(expression="user") User user)  {
        if(!user.getUserId().equals(investmentParam.getUserId())) {
            throw new InvalidInvestmentProblem(InvestmentError.UNMATCHED_USER);
        }
        if(errors.hasErrors()) {
            throw new InvalidInvestmentProblem(InvestmentError.INVALID_INVESTMENT_AMOUNT);
        }
        Investment result = investmentService.invest(investmentParam);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.getId())
                .toUri();
        return ResponseEntity.created(location).body(result);
    }
}
