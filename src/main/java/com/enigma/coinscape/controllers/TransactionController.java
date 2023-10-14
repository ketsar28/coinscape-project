package com.enigma.coinscape.controllers;

import com.enigma.coinscape.models.requests.transaction.SearchTransactionAllUserRequest;
import com.enigma.coinscape.models.requests.transaction.TransactionRequest;
import com.enigma.coinscape.models.requests.transaction.TransactionUpgradeRequest;
import com.enigma.coinscape.models.responses.general.CommonResponse;
import com.enigma.coinscape.models.responses.general.PagingResponse;
import com.enigma.coinscape.models.responses.transaction.TransactionResponse;
import com.enigma.coinscape.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/upgrade")
    public ResponseEntity<?> createTransactionForUpgradeUserToPremium(@RequestBody TransactionUpgradeRequest request) {
        TransactionResponse transactionUpgrade = transactionService.createTransactionUpgrade(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(transactionUpgrade)
                .message("CREATED")
                .statusCode(HttpStatus.CREATED.value())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequest request) {
        TransactionResponse transaction = transactionService.createTransaction(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(transaction)
                .message("CREATED")
                .statusCode(HttpStatus.CREATED.value())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{transaction_id}")
    public ResponseEntity<?> getTransactionById(@PathVariable("transaction_id") String transactionId) {
        TransactionResponse transaction = transactionService.getTransById(transactionId);
        CommonResponse<?> response = CommonResponse.builder()
                .message("OK")
                .data(transaction)
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllTransaction(@RequestParam(value= "size", required = false, defaultValue = "10") Integer size,
                                               @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                               @RequestParam(value = "description", required = false) String description,
                                               @RequestParam(value = "charge", required = false) Integer charge,
                                               @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
                                               @RequestParam(value = "transactionType", required = false) String transactionType) {

        SearchTransactionAllUserRequest request = SearchTransactionAllUserRequest.builder()
                .page(page)
                .size(size)
                .charge(charge)
                .description(description)
                .paymentMethod(paymentMethod)
                .transactionType(transactionType)
                .build();

        Page<TransactionResponse> transaction = transactionService.getAllTransactionUser(request);

        PagingResponse paging = PagingResponse.builder()
                .size(transaction.getSize())
                .totalPage(transaction.getTotalPages())
                .currentPage(transaction.getNumber())
                .build();

        CommonResponse<?> response = CommonResponse.builder()
                .message("OK")
                .data(transaction.getContent())
                .paging(paging)
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

}
