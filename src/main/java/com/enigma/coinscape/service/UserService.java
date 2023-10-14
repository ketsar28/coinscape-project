package com.enigma.coinscape.service;


import com.enigma.coinscape.models.requests.user.UpdateUserRequest;
import com.enigma.coinscape.models.responses.transaction.TransactionResponse;
import com.enigma.coinscape.models.responses.user.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse getUserById(String userId);
    List<TransactionResponse> getAllTransaction(String userId);
    UserResponse updateUser(UpdateUserRequest request);
}
