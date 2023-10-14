package com.enigma.coinscape.models.requests.user;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SearchUserRequest {
    private Integer page;
    private Integer size;
    private String name;
    private String email;
    private String phoneNumber;
    private Integer minBalance;
    private Integer maxBalance;
}
