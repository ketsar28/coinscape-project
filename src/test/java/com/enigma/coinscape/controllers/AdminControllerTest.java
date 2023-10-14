package com.enigma.coinscape.controllers;

import com.enigma.coinscape.models.requests.upgrade.UpgradePremiumRequest;
import com.enigma.coinscape.models.requests.user.SearchUserRequest;
import com.enigma.coinscape.models.responses.admin.AdminResponse;
import com.enigma.coinscape.models.responses.upgrade.UpgradeUserToPremiumResponse;
import com.enigma.coinscape.models.responses.user.UserResponse;
import com.enigma.coinscape.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@WithMockUser
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @Test
    public void testUpgradeUserToPremium() throws Exception {
        UpgradePremiumRequest request = new UpgradePremiumRequest();
        request.setUserId("user1");
        request.setUpgradeRequestId("request1");

        UpgradeUserToPremiumResponse upgradeResponse = new UpgradeUserToPremiumResponse();
        upgradeResponse.setId("1");
        upgradeResponse.setAcceptDate(LocalDateTime.now());
        upgradeResponse.setStatus("Premium");

        UserResponse userResponse = new UserResponse();
        userResponse.setId("user1");
        userResponse.setName("haris");
        userResponse.setEmail("haris@gmail.com");
        userResponse.setBalance(new BigDecimal(1000));
        userResponse.setPhoneNumber("1234567890");
        userResponse.setIsPremium(true);

        upgradeResponse.setUser(userResponse);

        when(adminService.upgradeUserToPremium(any(UpgradePremiumRequest.class))).thenReturn(upgradeResponse);

        mockMvc.perform(post("/api/admin/upgrade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("LEVEL UPGRADED"));

        verify(adminService, times(1)).upgradeUserToPremium(any(UpgradePremiumRequest.class));
    }


    @Test
    public void testGetAllUsers() throws Exception {
        List<UserResponse> userResponses = Arrays.asList(
                UserResponse.builder()
                        .id("user1")
                        .name("haris")
                        .email("haris@gmail.com")
                        .phoneNumber("1234567890")
                        .balance(new BigDecimal(1000))
                        .isPremium(false)
                        .build()
        );

        Page<UserResponse> userPage = new PageImpl<>(userResponses);

        when(adminService.getAllUsers(any(SearchUserRequest.class))).thenReturn(userPage);

        mockMvc.perform(get("/api/admin/users")
                        .param("size", "10")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"));

    }


    @Test
    public void testGetAdminById() throws Exception {
        String adminId = "admin1";
        AdminResponse adminResponse = AdminResponse.builder()
                .id(adminId)
                .name("Admin Haris")
                .email("adminharis@example.com")
                .phoneNumber("9876543210")
                .build();

        when(adminService.getAdminById(adminId)).thenReturn(adminResponse);

        mockMvc.perform(get("/api/admin/{admin_id}", adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"));

        verify(adminService, times(1)).getAdminById(adminId);
    }


    @Test
    public void testGetAllAdmins() throws Exception {
        List<AdminResponse> adminResponses = Arrays.asList(
                new AdminResponse("adminharis", "Admin haris", "haris@gmail.com", "1234567890", BigDecimal.valueOf(2000), "123"),
                new AdminResponse("admindwi", "Admin dwi", "dwi@gmail.com", "9876543210", BigDecimal.valueOf(20000), "1234")
        );

        when(adminService.getAllAdmins(any(Pageable.class))).thenReturn(adminResponses);

        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value("adminharis"))
                .andExpect(jsonPath("$.data[0].name").value("Admin haris"))
                .andExpect(jsonPath("$.data[0].email").value("haris@gmail.com"))
                .andExpect(jsonPath("$.data[0].phoneNumber").value("1234567890"))
                .andExpect(jsonPath("$.data[1].id").value("admindwi"))
                .andExpect(jsonPath("$.data[1].name").value("Admin dwi"))
                .andExpect(jsonPath("$.data[1].email").value("dwi@gmail.com"))
                .andExpect(jsonPath("$.data[1].phoneNumber").value("9876543210"));
    }

}


