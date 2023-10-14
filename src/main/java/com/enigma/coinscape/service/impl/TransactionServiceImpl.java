package com.enigma.coinscape.service.impl;

import com.enigma.coinscape.entities.*;
import com.enigma.coinscape.entities.constants.EPayment;
import com.enigma.coinscape.entities.constants.EStatus;
import com.enigma.coinscape.entities.constants.ETransactionType;
import com.enigma.coinscape.entities.constants.ResponseMessage;
import com.enigma.coinscape.models.requests.transaction.SearchTransactionAllUserRequest;
import com.enigma.coinscape.models.requests.transaction.TransactionRequest;
import com.enigma.coinscape.models.requests.transaction.TransactionUpgradeRequest;
import com.enigma.coinscape.models.responses.transaction.DetailTransResponse;
import com.enigma.coinscape.models.responses.transaction.TransactionResponse;
import com.enigma.coinscape.models.responses.upgrade.UpgradeResponse;
import com.enigma.coinscape.models.responses.user.UserResponse;
import com.enigma.coinscape.repository.*;
import com.enigma.coinscape.service.TransactionService;
import com.enigma.coinscape.service.UserService;
import com.enigma.coinscape.utils.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
        private final TransactionRepository transactionRepository;
        private final DetailTransRepository detailTransRepository;
        private final UpgradeRequestRepository upgradeRequestRepository;
        private final AdminRepository adminRepository;
        private final ValidationUtil validationUtil;
        private final UserRepository userRepository;

        private static UserResponse toUserResponse(User user, Boolean isIncludePhoneNumberAndBalance) {
            UserResponse.UserResponseBuilder response = UserResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .isPremium(user.getIsPremium());

            if(isIncludePhoneNumberAndBalance) {
                response.phoneNumber(user.getPhoneNumber());
                response.balance(user.getBalance());
            }

            return response.build();
        }

        private static DetailTransResponse toResponseDetailTrans(DetailTrans detailTrans, EPayment paymentMethod, User user, Boolean isIncludePhoneNumberAndBalance) {
            return DetailTransResponse.builder()
                    .idDetailTrans(detailTrans.getId())
                    .charge(detailTrans.getCharge())
                    .paymentMethod(String.valueOf(paymentMethod))
                    .user(toUserResponse(user, isIncludePhoneNumberAndBalance))
                    .build();
        }

    private static TransactionResponse toTransactionResponse(
            Transaction transaction, DetailTrans detailTrans, EPayment paymentMethod,
            User user, UpgradeRequest upgradeRequest, Boolean isIncludePhoneNumberAndBalance) {

        TransactionResponse response = TransactionResponse.builder()
                .idTransaction(transaction.getId())
                .transDate(new Date())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .transactionType(transaction.getETransactionType().getTransactionTypeEnum())
                .transDetail(toResponseDetailTrans(detailTrans, paymentMethod, user, isIncludePhoneNumberAndBalance))
                .build();

        if (Objects.nonNull(transaction.getUpgradeRequest())) {
                response.setUpgradeDetail(UpgradeResponse.builder()
                        .userId(user.getId())
                        .id(upgradeRequest.getId())
                        .status(upgradeRequest.getStatus().getName())
                        .requestDate(upgradeRequest.getRequestDate())
                        .build());
            }

        return response;
    }

    @Transactional(rollbackOn = Exception.class)
    @Override

    public TransactionResponse createTransactionUpgrade(TransactionUpgradeRequest request) {
        validationUtil.validate(request);

        User user = userRepository.findByPhoneNumber(request.getDetailTrans().getNoUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Phone number not found"));

        DetailTrans detailTrans = new DetailTrans();
        EPayment paymentMethod = EPayment.getType(request.getDetailTrans().getPaymentMethod());
        ETransactionType transactionType = ETransactionType.fromValue(request.getTransactionType());
        BigDecimal balance = user.getBalance();


        if(transactionType.equals(ETransactionType.UPGRADE)) {

            if(request.getAmount().compareTo(BigDecimal.valueOf(100000)) != 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have to pay 100,000");
            }
            if (balance.compareTo(request.getAmount()) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry, your balance is insufficient for the upgrade. You need at least 100,000 in balance");
            }

            Admin admin = adminRepository.findByPayCode(request.getDetailTrans().getPayCode())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin with the paycode was not found"));

            // Lakukan update profit admin
            BigDecimal profit = admin.getProfit();
            admin.setProfit(profit.add(request.getAmount()));
            adminRepository.save(admin);

            user.setBalance(balance.subtract(request.getAmount()));
            userRepository.save(user);
        }
        detailTrans.setPaymentMethod(paymentMethod);
        detailTrans.setUser(user);

        Transaction transaction = Transaction.builder()
                .description(request.getDescription())
                .amount(request.getAmount())
                .transDate(LocalDateTime.now())
                .detailTrans(detailTrans)
                .eTransactionType(transactionType)
                .build();

        detailTrans.setTrans(transaction);
        detailTransRepository.save(detailTrans);
        transactionRepository.save(transaction);

        UpgradeRequest upgradeRequest = UpgradeRequest.builder()
                .status(EStatus.PENDING)
                .user(user)
                .requestDate(LocalDateTime.now())
                .build();
        upgradeRequestRepository.save(upgradeRequest);

        transaction.setUpgradeRequest(upgradeRequest);
        transactionRepository.save(transaction);

        return toTransactionResponse(transaction, detailTrans, paymentMethod, user, upgradeRequest, false);
    }



    @Transactional(rollbackOn = Exception.class)
        @Override
        public TransactionResponse createTransaction(TransactionRequest request) {
            validationUtil.validate(request);

            User user = userRepository.findByPhoneNumber(request.getDetailTrans().getNoUser())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Phone number not found"));

            DetailTrans detailTrans = new DetailTrans();
            EPayment paymentMethod = EPayment.getType(request.getDetailTrans().getPaymentMethod());
            ETransactionType transactionType = ETransactionType.fromValue(request.getTransactionType());
            BigDecimal balance = user.getBalance();

            if (transactionType.equals(ETransactionType.TOP_UP)) {
                BigDecimal maxTopUpAmount = user.getIsPremium() ? new BigDecimal("20000000") : new BigDecimal("10000000");
                if(request.getAmount().compareTo(maxTopUpAmount) > 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum top up for non-premium is " + maxTopUpAmount);
                }
                BigDecimal newBalance = balance.add(request.getAmount());
                if (!user.getIsPremium()){
                    newBalance.max(BigDecimal.valueOf(10000000));
                } else {
                    newBalance.max(BigDecimal.valueOf(20000000));
                }
                if (newBalance.compareTo(maxTopUpAmount) > 0){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Balance amount exceeds the maximum limit (" + maxTopUpAmount + ")");
                }
                user.setBalance(newBalance);
            } else if (transactionType.equals(ETransactionType.TRANSFER)) {
                if (!user.getIsPremium()) {
                    if (request.getAmount().compareTo(new BigDecimal("5000000")) > 0) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum transfer for non-premium is 5.000.000");
                    }
                    detailTrans.setCharge(BigDecimal.valueOf(2000));
                } else {
                    detailTrans.setCharge(BigDecimal.valueOf(1000));
                }

                Admin admin = adminRepository.findByPayCode(request.getDetailTrans().getPayCode())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sorry the transfer failed, contact admin immediately!"));
                BigDecimal currentProfit = admin.getProfit();
                admin.setProfit(currentProfit.add(detailTrans.getCharge()));
                adminRepository.save(admin);

                BigDecimal transferAmount = request.getAmount().add(detailTrans.getCharge());

                if (balance.compareTo(transferAmount) < 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance for transfer");
                } else if(request.getAmount().compareTo(BigDecimal.valueOf(10000)) < 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum transfer is 10.000");
                }

                BigDecimal newBalance = balance.subtract(transferAmount);
                log.info("old balance {}",  balance);
                log.info("new balance {}", newBalance);
                user.setBalance(newBalance);


                User userDestination = userRepository.findByPhoneNumber(request.getDetailTrans().getNoUserDestination())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination user not found"));

                if (Objects.nonNull(userDestination)) {
                    userDestination.setBalance(userDestination.getBalance().add(request.getAmount()));
                    userRepository.save(userDestination);
                }

            }

            userRepository.save(user);
            detailTrans.setPaymentMethod(paymentMethod);
            detailTrans.setUser(user);

            Transaction transaction = Transaction.builder()
                    .description(request.getDescription())
                    .amount(request.getAmount())
                    .transDate(LocalDateTime.now())
                    .detailTrans(detailTrans)
                    .eTransactionType(transactionType)
                    .build();

            detailTrans.setTrans(transaction);
            detailTransRepository.save(detailTrans);
            transactionRepository.save(transaction);
            log.info("upgrade requset : " + transaction.getUpgradeRequest());

            return toTransactionResponse(transaction, detailTrans, paymentMethod, user, transaction.getUpgradeRequest(), false);
        }


        @Override
        public TransactionResponse getTransById(String transId) {
            Transaction transaction = transactionRepository.findById(transId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.getNotFoundResourceMessage(Transaction.class)));
            return toTransactionResponse(transaction, transaction.getDetailTrans(),
                    transaction.getDetailTrans().getPaymentMethod(),
                    transaction.getDetailTrans().getUser(),
                    transaction.getUpgradeRequest(), false);
        }

        @Override
        public Page<TransactionResponse> getAllTransactionUser(SearchTransactionAllUserRequest request) {
            Specification<Transaction> specification = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if(Objects.nonNull(request.getDescription())) {
                    predicates.add(builder.like(builder.lower(root.get("description")), "%" + request.getDescription().toLowerCase() + "%"));
                }

                if(Objects.nonNull(request.getCharge()))  {
                    Join<Transaction, DetailTrans> detailTransJoin = root.join("detailTrans", JoinType.INNER);
                    predicates.add(builder.equal(detailTransJoin.get("charge"), request.getCharge()));
                }

                if(Objects.nonNull(request.getPaymentMethod())) {
                    Join<Transaction, DetailTrans> detailTransJoin = root.join("detailTrans", JoinType.INNER);
                    predicates.add(builder.equal(detailTransJoin.get("paymentMethod"), EPayment.getType(request.getPaymentMethod())));
                }

                if(Objects.nonNull(request.getTransactionType())) {
                    predicates.add(builder.equal(root.get("eTransactionType"), ETransactionType.fromValue(request.getTransactionType()).getTransactionTypeEnum()));
                }

                return query.where(predicates.toArray(predicates.toArray(new Predicate[]{}))).getRestriction();
            };
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("transDate").descending());
            Page<Transaction> transactions = transactionRepository.findAll(specification, pageable);

            List<TransactionResponse> transactionResponses = transactions.getContent().stream().map(transaction ->
                    toTransactionResponse(transaction, transaction.getDetailTrans(),
                            transaction.getDetailTrans().getPaymentMethod(),
                            transaction.getDetailTrans().getUser(), transaction.getUpgradeRequest(), false)).collect(Collectors.toList());

            return new PageImpl<>(transactionResponses, pageable, transactions.getTotalElements());
        }

//    @Override
//    public void deleteTransaction(String transId) {
//        Transaction transaction = transactionRepository.findById(transId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.getNotFoundResourceMessage(Transaction.class)));
//        DetailTrans detailTrans = detailTransRepository.findByTrans_CreatedAt(transaction.getCreatedAt()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.getNotFoundResourceMessage(DetailTrans.class)));
//
//        detailTransRepository.delete(detailTrans);
//        transactionRepository.delete(transaction);
//    }
}

