package com.enigma.coinscape.service;


import com.enigma.coinscape.models.requests.auth.AuthRequest;
import com.enigma.coinscape.models.responses.auth.LoginResponse;
import com.enigma.coinscape.models.responses.auth.RegisterResponse;

public interface AuthService {
    RegisterResponse registerUser(AuthRequest request);
    RegisterResponse registerAdmin(AuthRequest request);
    LoginResponse loginUser(AuthRequest request);
}
