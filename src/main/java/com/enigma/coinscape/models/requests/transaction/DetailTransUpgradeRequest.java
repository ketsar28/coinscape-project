package com.enigma.coinscape.models.requests.transaction;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DetailTransUpgradeRequest {
    private String noUser;
    private String payCode;
    private String paymentMethod;
}
