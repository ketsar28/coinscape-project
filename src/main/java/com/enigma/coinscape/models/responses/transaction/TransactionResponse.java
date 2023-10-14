package com.enigma.coinscape.models.responses.transaction;

import com.enigma.coinscape.models.responses.upgrade.UpgradeResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransactionResponse {
    private String idTransaction;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date transDate;
    private BigDecimal amount;
    private String description;
    private String transactionType;
    private DetailTransResponse transDetail;
    private UpgradeResponse upgradeDetail;

    @JsonProperty("upgradeDetail")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public UpgradeResponse getUpgradeDetail(){
        return upgradeDetail;
    }

}
