package com.enigma.coinscape.entities.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum EStatus {
    PENDING("Pending"),
    ACCEPT("Accept"),
    REJECT("Reject");

    private final String name;

    public static EStatus getType(String value) {
        return Arrays.stream(values()).filter(eStatus -> eStatus.name.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ResponseMessage.getNotFoundResourceMessage(EStatus.class)));
    }
}
