package com.enigma.coinscape.service;


import com.enigma.coinscape.models.requests.transaction.SearchTransactionAllUserRequest;
import com.enigma.coinscape.models.requests.transaction.TransactionRequest;
import com.enigma.coinscape.models.requests.transaction.TransactionUpgradeRequest;
import com.enigma.coinscape.models.responses.transaction.TransactionResponse;
import org.springframework.data.domain.Page;

public interface TransactionService {
    TransactionResponse createTransactionUpgrade(TransactionUpgradeRequest request);
    TransactionResponse createTransaction(TransactionRequest request);
    TransactionResponse getTransById(String transId);
    Page<TransactionResponse> getAllTransactionUser(SearchTransactionAllUserRequest request);
}
