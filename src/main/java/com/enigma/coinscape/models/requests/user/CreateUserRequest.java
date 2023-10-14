package com.enigma.coinscape.models.requests.user;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateUserRequest {
    private String name;
    private String email;
    private String phoneNumber;
}
