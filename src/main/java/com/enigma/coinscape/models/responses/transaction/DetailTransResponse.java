package com.enigma.coinscape.models.responses.transaction;


import com.enigma.coinscape.models.responses.user.UserResponse;
import com.enigma.coinscape.models.responses.user.UserTransactionResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DetailTransResponse {
    private String idDetailTrans;
    private BigDecimal charge;
    private String paymentMethod;
    private UserResponse user;

    @JsonProperty("charge")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public BigDecimal getCharge() {
        return charge;
    }
}
