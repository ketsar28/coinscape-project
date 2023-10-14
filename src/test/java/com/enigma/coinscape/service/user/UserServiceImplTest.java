package com.enigma.coinscape.service.user;

import com.enigma.coinscape.entities.DetailTrans;
import com.enigma.coinscape.entities.Transaction;
import com.enigma.coinscape.entities.User;
import com.enigma.coinscape.entities.constants.EPayment;
import com.enigma.coinscape.entities.constants.ETransactionType;
import com.enigma.coinscape.models.requests.user.UpdateUserRequest;
import com.enigma.coinscape.models.responses.transaction.TransactionResponse;
import com.enigma.coinscape.models.responses.user.UserResponse;
import com.enigma.coinscape.repository.*;
import com.enigma.coinscape.security.BCriptUtils;
import com.enigma.coinscape.service.UserService;
import com.enigma.coinscape.service.impl.UserServiceImpl;
import com.enigma.coinscape.utils.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImplTest {

    private final BCriptUtils bCriptUtils = mock(BCriptUtils.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final ValidationUtil validationUtil = mock(ValidationUtil.class);
    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final DetailTransRepository detailTransRepository = mock(DetailTransRepository.class);
    private final UpgradeRequestRepository upgradeRequestRepository = mock(UpgradeRequestRepository.class);
    private final UserCredentialRepository userCredentialRepository = mock(UserCredentialRepository.class);
    private final UserService userService = new UserServiceImpl(userRepository, validationUtil,
            userCredentialRepository,transactionRepository,bCriptUtils);

    @BeforeEach
    void setUp() {
        reset(userRepository, validationUtil, roleRepository,
                transactionRepository,upgradeRequestRepository,
                userCredentialRepository, bCriptUtils, transactionRepository, detailTransRepository);
    }

    @Test
    void getUserById() {
        String userId = "1";
        User dummyUser = new User();
        dummyUser.setId(userId);
        dummyUser.setName("user1");
        dummyUser.setEmail("email1");
        dummyUser.setPhoneNumber("0851xxx");

        when(userRepository.findById(userId)).thenReturn(Optional.of(dummyUser));

        UserResponse retriviedUser = userService.getUserById(userId);

        verify(userRepository, times(1)).findById(userId);

        assertEquals(userId, retriviedUser.getId());
        assertEquals(dummyUser.getName(), retriviedUser.getName());
    }

    @Test
    void getAllTransactions() {
        String userId = "user123";

        List<Transaction> mockTransactions = new ArrayList<>();

        mockTransactions.add(buildTransaction("1", userId, ETransactionType.UPGRADE));
        mockTransactions.add(buildTransaction("2", userId, ETransactionType.TRANSFER));

        when(transactionRepository.findAllByDetailTrans_User_IdOrderByTransDateDesc(userId))
                .thenReturn(mockTransactions);

        List<TransactionResponse> transactionResponses = userService.getAllTransaction(userId);

        assertThat(transactionResponses).isNotEmpty();
        assertThat(transactionResponses).hasSize(mockTransactions.size());

        verify(transactionRepository, times(1)).findAllByDetailTrans_User_IdOrderByTransDateDesc(userId);
    }

    @Test
    void updateUser() {
        UpdateUserRequest dummyRequest = new UpdateUserRequest();
        dummyRequest.setId("1");
        dummyRequest.setName("new user");

        User oldUser = new User();
        oldUser.setId(dummyRequest.getId());
        oldUser.setName("old user");

        User newUser = new User();
        newUser.setId(oldUser.getId());
        newUser.setName(dummyRequest.getName());

        when(userRepository.findFirstById(oldUser.getId())).thenReturn(Optional.of(oldUser));
        UserResponse updateUser = userService.updateUser(dummyRequest);
        verify(userRepository, times(1)).save(newUser);

        assertEquals(dummyRequest.getName(),updateUser.getName());
        System.out.println(dummyRequest.getName() + " : " + updateUser.getName());
    }


    private Transaction buildTransaction(String id, String userId, ETransactionType transactionType) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setETransactionType(transactionType);
        transaction.setAmount(BigDecimal.valueOf(100000.00));
        transaction.setDescription("transaksi");

        DetailTrans detailTrans = new DetailTrans();
        detailTrans.setId("detail_" + id);
        detailTrans.setPaymentMethod(EPayment.AGENT);
        User user = new User();
        user.setId(userId);
        detailTrans.setUser(user);
        transaction.setDetailTrans(detailTrans);

        return transaction;
    }


}
