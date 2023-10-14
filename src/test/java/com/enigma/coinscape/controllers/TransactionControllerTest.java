package com.enigma.coinscape.controllers;

import com.enigma.coinscape.entities.Transaction;
import com.enigma.coinscape.entities.constants.ETransactionType;
import com.enigma.coinscape.models.requests.transaction.SearchTransactionAllUserRequest;
import com.enigma.coinscape.models.requests.transaction.TransactionRequest;
import com.enigma.coinscape.models.requests.transaction.TransactionUpgradeRequest;
import com.enigma.coinscape.models.responses.transaction.TransactionResponse;
import com.enigma.coinscape.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WithMockUser
@AutoConfigureMockMvc
class TransactionControllerTest {

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void createTransactionForUpgradeUserToPremium() throws Exception {
        TransactionUpgradeRequest request = new TransactionUpgradeRequest();
        request.setAmount(BigDecimal.valueOf(100000));
        request.setTransactionType(ETransactionType.UPGRADE.getTransactionTypeEnum());
        request.setDescription("upgrade");

        TransactionResponse response = TransactionResponse.builder()
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .description(request.getDescription())
                .build();
        when(transactionService.createTransactionUpgrade(any(TransactionUpgradeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/transactions/upgrade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("CREATED"))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.amount").value(BigDecimal.valueOf(100000)))
                .andExpect(jsonPath("$.data.transactionType").value("Upgrade"))
                .andExpect(jsonPath("$.data.description").value("upgrade"));

        verify(transactionService, times(1)).createTransactionUpgrade(any(TransactionUpgradeRequest.class));


    }

    @Test
    void createTransaction() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(BigDecimal.valueOf(200000));
        request.setTransactionType(ETransactionType.TRANSFER.getTransactionTypeEnum());
        request.setDescription("transfer");

        TransactionResponse response = new TransactionResponse();
        response.setAmount(request.getAmount());
        response.setDescription(request.getDescription());
        response.setTransactionType(request.getTransactionType());

        when(transactionService.createTransaction(any(TransactionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("CREATED"))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.amount").value(BigDecimal.valueOf(200000)))
                .andExpect(jsonPath("$.data.transactionType").value("Transfer"))
                .andExpect(jsonPath("$.data.description").value("transfer"));

        verify(transactionService, times(1)).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void getTransactionById() throws Exception {
        String transactionId = "1";
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setAmount(BigDecimal.valueOf(200000));
        transaction.setDescription("top up");

        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setIdTransaction(transactionId);
        transactionResponse.setAmount(transaction.getAmount());
        transactionResponse.setDescription(transaction.getDescription());

        when(transactionService.getTransById(transactionId)).thenReturn(transactionResponse);

        mockMvc.perform(get("/api/transactions/{transaction_id}", transactionId)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.idTransaction").value(transactionId))
                .andExpect(jsonPath("$.data.amount").value(BigDecimal.valueOf(200000)))
                .andExpect(jsonPath("$.data.description").value("top up"));

        verify(transactionService, times(1)).getTransById(eq(transactionId));

    }

    @Test
    void getAllTransaction() throws Exception {
        TransactionResponse trans1 = new TransactionResponse();
        trans1.setIdTransaction("1");
        trans1.setAmount(BigDecimal.valueOf(300000));
        trans1.setDescription("top up");

        TransactionResponse trans2 = new TransactionResponse();
        trans2.setIdTransaction("2");
        trans2.setAmount(BigDecimal.valueOf(200000));
        trans2.setDescription("transfer");

        List<TransactionResponse> transactions = Arrays.asList(trans1, trans2);

        when(transactionService.getAllTransactionUser(any(SearchTransactionAllUserRequest.class))).thenReturn(new PageImpl<>(transactions));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].idTransaction").value("1"))
                .andExpect(jsonPath("$.data[0].amount").value(300000))
                .andExpect(jsonPath("$.data[0].description").value("top up"))
                .andExpect(jsonPath("$.data[1].idTransaction").value("2"))
                .andExpect(jsonPath("$.data[1].amount").value(200000))
                .andExpect(jsonPath("$.data[1].description").value("transfer"));

        verify(transactionService, times(1)).getAllTransactionUser(any(SearchTransactionAllUserRequest.class));
    }
}
