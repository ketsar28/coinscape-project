package com.enigma.coinscape.models.requests.transaction;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SearchTransactionUserRequest {
    private String idUser;
    private String description;
    private Integer charge;
    private String paymentMethod;
    private String transactionType;
}
