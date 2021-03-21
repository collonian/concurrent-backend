package com.example.demo.service.investment;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.util.Map;

public class InvalidInvestmentProblem extends AbstractThrowableProblem {
    public InvalidInvestmentProblem(InvestmentError status) {
        super(null,
                Status.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                Status.UNPROCESSABLE_ENTITY,
                "", // TODO: use i18n message
                null,
                null,
                Map.of("message_code", status.getMessageCode(), "error_code", status)
        );
    }
}
