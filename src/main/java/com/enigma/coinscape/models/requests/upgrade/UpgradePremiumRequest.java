package com.enigma.coinscape.models.requests.upgrade;

import com.enigma.coinscape.entities.constants.EStatus;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpgradePremiumRequest {
   private String userId;
   private String upgradeRequestId;
}
