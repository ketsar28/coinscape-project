package com.enigma.coinscape.controllers;

import com.enigma.coinscape.models.requests.upgrade.UpgradePremiumRequest;
import com.enigma.coinscape.models.requests.user.SearchUserRequest;
import com.enigma.coinscape.models.responses.admin.AdminResponse;
import com.enigma.coinscape.models.responses.general.CommonResponse;
import com.enigma.coinscape.models.responses.general.PagingResponse;
import com.enigma.coinscape.models.responses.upgrade.UpgradeUserToPremiumResponse;
import com.enigma.coinscape.models.responses.user.UserResponse;
import com.enigma.coinscape.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgradeUserToPremium(@RequestBody UpgradePremiumRequest request) {
        UpgradeUserToPremiumResponse upgradeResponse = adminService.upgradeUserToPremium(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(upgradeResponse)
                .message("LEVEL UPGRADED")
                .statusCode(HttpStatus.CREATED.value())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping(
            path = "/users"
    )
    public ResponseEntity<?> getAllUsers(@RequestParam(value= "size", required = false, defaultValue = "10") Integer size,
                                    @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                    @RequestParam(value = "name", required = false) String name,
                                    @RequestParam(value = "email", required = false) String email,
                                    @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                                    @RequestParam(value = "minBalance", required = false) Integer minBalance,
                                    @RequestParam(value = "maxBalance", required = false) Integer maxBalance) {

        SearchUserRequest request = SearchUserRequest.builder()
                .name(name)
                .size(size)
                .page(page)
                .email(email)
                .minBalance(minBalance)
                .maxBalance(maxBalance)
                .phoneNumber(phoneNumber)
                .build();

        Page<UserResponse> users = adminService.getAllUsers(request);
        PagingResponse paging = PagingResponse.builder()
                .currentPage(users.getNumber())
                .size(users.getSize())
                .totalPage(users.getTotalPages())
                .build();

        CommonResponse<?> response = CommonResponse.builder()
                .data(users.getContent())
                .message("OK")
                .paging(paging)
                .statusCode(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{admin_id}")
    public ResponseEntity<?> getAdminById(@PathVariable("admin_id") String adminId) {
        AdminResponse admin = adminService.getAdminById(adminId);
        CommonResponse<?> response = CommonResponse.builder()
                .message("OK")
                .statusCode(HttpStatus.OK.value())
                .data(admin)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllAdmins(@PageableDefault(page = 0, size = 5, sort = "updatedAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        List<AdminResponse> responses = adminService.getAllAdmins(pageable);
        CommonResponse<?> response = CommonResponse.builder()
                .message("OK")
                .statusCode(HttpStatus.OK.value())
                .data(responses)
                .build();

        return ResponseEntity.ok(response);
    }


}
