package com.enigma.coinscape.service;

import com.enigma.coinscape.models.requests.admin.AdminRequest;
import com.enigma.coinscape.models.requests.upgrade.UpgradePremiumRequest;
import com.enigma.coinscape.models.requests.user.SearchUserRequest;
import com.enigma.coinscape.models.responses.admin.AdminResponse;
import com.enigma.coinscape.models.responses.upgrade.UpgradeUserToPremiumResponse;
import com.enigma.coinscape.models.responses.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    List<AdminResponse> getAllAdmins(Pageable pageable);
    Page<UserResponse> getAllUsers(SearchUserRequest request);
    AdminResponse getAdminById(String adminId);
    UpgradeUserToPremiumResponse upgradeUserToPremium(UpgradePremiumRequest request);
}
