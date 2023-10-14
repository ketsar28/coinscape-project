package com.enigma.coinscape.service.impl;

import com.enigma.coinscape.entities.*;
import com.enigma.coinscape.entities.constants.EPayment;
import com.enigma.coinscape.entities.constants.ResponseMessage;
import com.enigma.coinscape.models.requests.user.UpdateUserRequest;
import com.enigma.coinscape.models.responses.transaction.DetailTransResponse;
import com.enigma.coinscape.models.responses.transaction.TransactionResponse;
import com.enigma.coinscape.models.responses.upgrade.UpgradeResponse;
import com.enigma.coinscape.models.responses.user.UserResponse;
import com.enigma.coinscape.repository.*;
import com.enigma.coinscape.security.BCriptUtils;
import com.enigma.coinscape.service.UserService;
import com.enigma.coinscape.utils.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidationUtil validationUtil;
    private final UserCredentialRepository userCredentialRepository;
    private final TransactionRepository transactionRepository;
    private final BCriptUtils bCriptUtils;


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
    private static TransactionResponse toTransactionResponse(Transaction transaction, DetailTrans detailTrans, EPayment paymentMethod, User user) {
        TransactionResponse response = TransactionResponse.builder()
                .idTransaction(transaction.getId())
                .transDate(new Date())
                .amount(transaction.getAmount())
                .transactionType(transaction.getETransactionType().getTransactionTypeEnum())
                .description(transaction.getDescription())
                .transDetail(DetailTransResponse.builder()
                        .idDetailTrans(detailTrans.getId())
                        .charge(detailTrans.getCharge())
                        .paymentMethod(String.valueOf(paymentMethod))
                        .user(UserResponse.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .isPremium(user.getIsPremium())
                                .build())
                        .build())
                .build();

        if (transaction.getUpgradeRequest() != null) {
            UpgradeRequest upgradeRequest = transaction.getUpgradeRequest();
            response.setUpgradeDetail(UpgradeResponse.builder()
                    .userId(user.getId())
                    .id(upgradeRequest.getId())
                    .status(upgradeRequest.getStatus().getName())
                    .requestDate(upgradeRequest.getRequestDate())
                    .acceptDate(upgradeRequest.getAcceptDate())
                    .build());
        }

        return response;
    }

    @Override
    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId).get();
        return toUserResponse(user);
    }
    @Override
    public List<TransactionResponse> getAllTransaction(String userId) {
        List<Transaction> transactions = transactionRepository.findAllByDetailTrans_User_IdOrderByTransDateDesc(userId);

        return transactions.stream()
                .map(transaction -> toTransactionResponse(
                        transaction,
                        transaction.getDetailTrans(),
                        transaction.getDetailTrans().getPaymentMethod(),
                        transaction.getDetailTrans().getUser()))
                .collect(Collectors.toList());
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponse updateUser(UpdateUserRequest request) {
        validationUtil.validate(request);
        User existingUser = userRepository.findFirstById(request.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if(Objects.nonNull(existingUser)) {
             existingUser.setName(request.getName());
             existingUser.setPhoneNumber(request.getPhoneNumber());
             existingUser.setIsPremium(existingUser.getIsPremium());

             if(Objects.nonNull(request.getPassword())) {
                 UserCredential userCredential = userCredentialRepository.findByEmail(existingUser.getEmail())
                         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                 ResponseMessage.getNotFoundResourceMessage(UserCredential.class)));
                userCredential.setPassword(bCriptUtils.hashPassword(request.getPassword()));
                userCredentialRepository.save(userCredential);
             }

            userRepository.save(existingUser);

            return toUserResponse(existingUser);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }
}
