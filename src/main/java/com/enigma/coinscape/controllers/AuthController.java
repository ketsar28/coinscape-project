package com.enigma.coinscape.controllers;

import com.enigma.coinscape.models.requests.auth.AuthRequest;
import com.enigma.coinscape.models.responses.auth.LoginResponse;
import com.enigma.coinscape.models.responses.auth.RegisterResponse;
import com.enigma.coinscape.models.responses.general.CommonResponse;
import com.enigma.coinscape.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(
            path = "/register/user"
    )
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest request) {
        RegisterResponse register = authService.registerUser(request);
        CommonResponse<Object> commonResponse = CommonResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("successfully registered")
                .data(register)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
    }

    @PostMapping(
            path = "/login"
    )
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest request){
        LoginResponse login = authService.loginUser(request);
        CommonResponse<Object> commonResponse = CommonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully login")
                .data(login)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
    @PostMapping(
            path = "/register/admin"
    )
    public ResponseEntity<?> registerAdmin(@RequestBody AuthRequest request) {
        RegisterResponse register = authService.registerAdmin(request);
        CommonResponse<Object> commonResponse = CommonResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("successfully registered")
                .data(register)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
    }

}
