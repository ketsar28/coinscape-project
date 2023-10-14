package com.enigma.coinscape.controllers;

import com.enigma.coinscape.models.requests.user.UpdateUserRequest;
import com.enigma.coinscape.models.responses.transaction.TransactionResponse;
import com.enigma.coinscape.models.responses.user.UserResponse;
import com.enigma.coinscape.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WithMockUser
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUserById() throws Exception {
        String userId = "123";

        UserResponse response = UserResponse.builder()
                .id(userId)
                .name("user")
                .email("user@gmail.com")
                .phoneNumber("0850xxx")
                .build();

        when(userService.getUserById(eq(userId))).thenReturn(response);

        mockMvc.perform(
                        get("/api/users/{userId}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.name").value("user"))
                .andExpect(jsonPath("$.data.phoneNumber").value("0850xxx"));

        verify(userService, times(1)).getUserById(eq(userId));
    }

    @Test
    void getAllTransaction() throws Exception {
        String userId = "123";
        List<TransactionResponse> transactions = new ArrayList<>();

        TransactionResponse trans1 = new TransactionResponse();
        trans1.setIdTransaction("1");
        trans1.setAmount(BigDecimal.valueOf(300000));
        trans1.setDescription("top up");

        TransactionResponse trans2 = new TransactionResponse();
        trans2.setIdTransaction("2");
        trans2.setAmount(BigDecimal.valueOf(200000));
        trans2.setDescription("transfer");

        transactions.add(trans1);
        transactions.add(trans2);

        when(userService.getAllTransaction(userId)).thenReturn(transactions);

        mockMvc.perform(get("/api/users/history/{userId}", userId)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].idTransaction").value(trans1.getIdTransaction()))
                .andExpect(jsonPath("$.data[0].amount").value(trans1.getAmount()))
                .andExpect(jsonPath("$.data[0].description").value(trans1.getDescription()))
                .andExpect(jsonPath("$.data[1].idTransaction").value(trans2.getIdTransaction()))
                .andExpect(jsonPath("$.data[1].amount").value(trans2.getAmount()))
                .andExpect(jsonPath("$.data[1].description").value(trans2.getDescription()));
        verify(userService, times(1)).getAllTransaction(userId);
    }

    @Test
    void updateUser() throws Exception{
        String userId = "123";
        UpdateUserRequest updateUser = new UpdateUserRequest();
        updateUser.setId(userId);
        updateUser.setName("user updated");
        updateUser.setPhoneNumber("0850xxx");

        UserResponse updatedUserResponse = new UserResponse();
        updatedUserResponse.setId(userId);
        updatedUserResponse.setName(updateUser.getName());
        updatedUserResponse.setPhoneNumber(updateUser.getPhoneNumber());

        when(userService.updateUser(any(UpdateUserRequest.class))).thenReturn(updatedUserResponse);

        mockMvc.perform(
                        put("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateUser))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.name").value(updateUser.getName()))
                .andExpect(jsonPath("$.data.phoneNumber").value(updateUser.getPhoneNumber()));

        verify(userService, times(1)).updateUser(any(UpdateUserRequest.class));
    }
}
