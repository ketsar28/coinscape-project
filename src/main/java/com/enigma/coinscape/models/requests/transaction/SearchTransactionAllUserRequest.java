package com.enigma.coinscape.models.requests.transaction;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SearchTransactionAllUserRequest {
    private Integer page;
    private Integer size;
    private String description;
    private Integer charge;
    private String paymentMethod;
    private String transactionType;
}
