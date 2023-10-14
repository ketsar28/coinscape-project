package com.enigma.coinscape.service.impl;

import com.enigma.coinscape.entities.Admin;
import com.enigma.coinscape.entities.UpgradeRequest;
import com.enigma.coinscape.entities.User;
import com.enigma.coinscape.entities.constants.EStatus;
import com.enigma.coinscape.entities.constants.ResponseMessage;
import com.enigma.coinscape.models.requests.upgrade.UpgradePremiumRequest;
import com.enigma.coinscape.models.requests.user.SearchUserRequest;
import com.enigma.coinscape.models.responses.admin.AdminResponse;
import com.enigma.coinscape.models.responses.upgrade.UpgradeUserToPremiumResponse;
import com.enigma.coinscape.models.responses.user.UserResponse;
import com.enigma.coinscape.repository.*;
import com.enigma.coinscape.service.AdminService;
import com.enigma.coinscape.service.TransactionService;
import com.enigma.coinscape.utils.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Predicate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final UpgradeRequestRepository upgradeRequestRepository;
    private final ValidationUtil validationUtil;


    private static UpgradeUserToPremiumResponse toUpgradeUserToPremium(UpgradeRequest upgradeRequest, User user) {
        return UpgradeUserToPremiumResponse.builder()
                .id(upgradeRequest.getId())
                .acceptDate(upgradeRequest.getAcceptDate())
                .status(upgradeRequest.getStatus().getName())
                .user(UserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .balance(user.getBalance())
                        .phoneNumber(user.getPhoneNumber())
                        .isPremium(user.getIsPremium())
                        .build())
                .build();
    }

    private static AdminResponse toAdminResponse(Admin admin) {
        return AdminResponse.builder()
                .id(admin.getId())
                .name(admin.getName())
                .email(admin.getEmail())
                .phoneNumber(admin.getMobilePhone())
                .profit(admin.getProfit())
                .payCode(admin.getPayCode())
                .build();
    }

    private static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .balance(user.getBalance())
                .isPremium(user.getIsPremium())
                .build();
    }

    private boolean isPremiumExpired(User user) {
        if (user.getIsPremium()) {
            LocalDateTime premiumActivationDate = user.getPremiumActivationDate();
            LocalDateTime currentDate = LocalDateTime.now();
            Duration duration = Duration.between(premiumActivationDate, currentDate);

            long premiumDurationInDays = 30;

            return duration.toDays() >= premiumDurationInDays;
        }
        return false;
    }

    @Override
    public List<AdminResponse> getAllAdmins(Pageable pageable) {
        Page<Admin> admins = adminRepository.findAll(pageable);
        List<Admin> adminList = admins.getContent();
        return adminList.stream().map(AdminServiceImpl::toAdminResponse).collect(Collectors.toList());
    }

    @Override
    public Page<UserResponse> getAllUsers(SearchUserRequest request) {
        validationUtil.validate(request);

        Specification<User> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(Objects.nonNull(request.getName())) {
                predicates.add(builder.like(builder.lower(root.get("name")), "%" + request.getName() + "%"));
            }

            if(Objects.nonNull(request.getEmail())) {
                predicates.add(builder.like(builder.lower(root.get("email")), "%" + request.getEmail() + "%"));
            }

            if(Objects.nonNull(request.getPhoneNumber())) {
                predicates.add(builder.like(builder.lower(root.get("phoneNumber")), "%" + request.getPhoneNumber() + "%"));
            }

            if(Objects.nonNull(request.getMinBalance())) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("balance"), request.getMinBalance()));
            }


            if(Objects.nonNull(request.getMaxBalance())) {
                predicates.add(builder.lessThanOrEqualTo(root.get("balance"), request.getMaxBalance()));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("updatedAt").descending());
        Page<User> users = userRepository.findAll(specification, pageable);

        List<UserResponse> userResponses = users.getContent().stream().map(AdminServiceImpl::toUserResponse).collect(Collectors.toList());
        return new PageImpl<>(userResponses, pageable, users.getTotalElements());
    }

    @Override
    public AdminResponse getAdminById(String adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.getNotFoundResourceMessage(Admin.class)));
        return toAdminResponse(admin);
    }

    @Override
    public UpgradeUserToPremiumResponse upgradeUserToPremium(UpgradePremiumRequest request) {
        validationUtil.validate(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getIsPremium()) {
            if (isPremiumExpired(user)) {
                user.setIsPremium(false);
                userRepository.save(user);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user is still in premium status.");
            }
        } else {
            user.setIsPremium(true);
            user.setPremiumActivationDate(LocalDateTime.now());
            userRepository.save(user);
        }

        UpgradeRequest upgradeRequest = upgradeRequestRepository.findById(request.getUpgradeRequestId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                ResponseMessage.getNotFoundResourceMessage(UpgradeRequest.class)));

        upgradeRequest.setStatus(EStatus.ACCEPT);
        upgradeRequest.setAcceptDate(LocalDateTime.now());
        upgradeRequestRepository.save(upgradeRequest);
        return toUpgradeUserToPremium(upgradeRequest, user);
    }

}
