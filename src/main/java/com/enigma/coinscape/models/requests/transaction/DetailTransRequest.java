package com.enigma.coinscape.models.requests.transaction;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DetailTransRequest {
    private String noUser;
    private String payCode;
    private String paymentMethod;
    private String noUserDestination;
}
