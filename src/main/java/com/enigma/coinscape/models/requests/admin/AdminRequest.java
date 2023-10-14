package com.enigma.coinscape.models.requests.admin;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AdminRequest {
    private String name;
    private String email;
    private String phoneNumber;
}
