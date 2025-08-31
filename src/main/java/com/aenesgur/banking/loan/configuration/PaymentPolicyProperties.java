package com.aenesgur.banking.loan.configuration;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "payment.policy")
@Getter
@Setter
public class PaymentPolicyProperties {
    private BigDecimal earlyRate;
    private BigDecimal lateRate;
}
