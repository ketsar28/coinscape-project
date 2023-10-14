package com.enigma.coinscape.models.requests.user;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpdateUserRequest {
    private String id;
    private String name;
    private String password;
    private String phoneNumber;
}
