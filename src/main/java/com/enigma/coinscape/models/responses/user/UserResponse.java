package com.enigma.coinscape.models.responses.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private BigDecimal balance;
    private Boolean isPremium;

    @JsonProperty("phoneNumber")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getPhoneNumber(){
        return phoneNumber;
    }

    @JsonProperty("balance")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public BigDecimal getBalance(){
        return balance;
    }
}
