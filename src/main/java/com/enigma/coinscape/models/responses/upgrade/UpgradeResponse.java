package com.enigma.coinscape.models.responses.upgrade;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpgradeResponse {
    private String id;
    private String status;
    private String userId;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime requestDate;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime acceptDate;

    @JsonProperty("acceptDate")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public LocalDateTime getAcceptDate() {
        return acceptDate;
    }
}
