package com.enigma.coinscape.models.responses.user;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserTransactionResponse {
    private String id;
    private String name;
    private String email;
}
