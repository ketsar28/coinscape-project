package com.enigma.coinscape.models.responses.upgrade;

import com.enigma.coinscape.models.responses.user.UserResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpgradeUserToPremiumResponse {
    private String id;
    private String status;
    private UserResponse user;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime acceptDate;
}
