package com.enigma.coinscape.models.requests.transaction;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TransactionUpgradeRequest {
    private BigDecimal amount;
    private String description;
    private String transactionType;
    private DetailTransUpgradeRequest detailTrans;
}
