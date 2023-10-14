package com.enigma.coinscape.entities.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum EPayment{
    BANK("Bank"),
    AGENT("Agent");


    private final String name;

    public static EPayment getType(String value) {
        return Arrays.stream(values()).filter(eTransactionType -> eTransactionType.name.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.getNotFoundResourceMessage(EPayment.class)));
    }

    public static EPayment getTypeNumber(String number) {
        try {
            return Arrays.stream(values()).filter(eTransactionType -> eTransactionType.ordinal() == Integer.parseInt(number) - 1)
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.getNotFoundResourceMessage(EPayment.class)));
        } catch (NumberFormatException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid Transaction Type");
        }
    }
}
