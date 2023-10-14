package com.enigma.coinscape.controllers;

import com.enigma.coinscape.models.requests.user.UpdateUserRequest;
import com.enigma.coinscape.models.responses.general.CommonResponse;
import com.enigma.coinscape.models.responses.transaction.TransactionResponse;
import com.enigma.coinscape.models.responses.user.UserResponse;
import com.enigma.coinscape.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{user_id}")
    public ResponseEntity<?> getUserById(@PathVariable("user_id") String userId) {
        UserResponse user = userService.getUserById(userId);
        CommonResponse<?> response = CommonResponse.builder()
                .data(user)
                .message("OK")
                .statusCode(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(
            path = "/history/{user_id}"
    )
    public ResponseEntity<?> getAllTransaction(@PathVariable(name = "user_id") String userId) {

        List<TransactionResponse> transaction = userService.getAllTransaction(userId);
        CommonResponse<?> response = CommonResponse.builder()
                .message("OK")
                .data(transaction)
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest request) {
        UserResponse user = userService.updateUser(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(user)
                .message("OK")
                .statusCode(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(response);
    }
}
