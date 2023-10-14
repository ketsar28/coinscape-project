package com.enigma.coinscape.models.responses.admin;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AdminResponse {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private BigDecimal profit;
    private String payCode;
}
