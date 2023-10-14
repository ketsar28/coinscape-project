package com.enigma.coinscape.entities.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ETransactionType {
    TOP_UP("Top Up"),
    TRANSFER("Transfer"),
    UPGRADE("Upgrade");


    private final String transactionTypeEnum;

    public String getValue() {
        return transactionTypeEnum;
    }

    public static ETransactionType fromValue(String value) {
        for (ETransactionType type : values()) {
            if (type.transactionTypeEnum.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid transaction type: " + value);
    }
}