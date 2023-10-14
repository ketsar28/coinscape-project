package com.enigma.coinscape.service.impl;

import com.enigma.coinscape.entities.*;
import com.enigma.coinscape.entities.constants.ERole;
import com.enigma.coinscape.entities.constants.ETokenType;
import com.enigma.coinscape.entities.constants.ResponseMessage;
import com.enigma.coinscape.models.requests.auth.AuthRequest;
import com.enigma.coinscape.models.responses.auth.LoginResponse;
import com.enigma.coinscape.models.responses.auth.RegisterResponse;
import com.enigma.coinscape.repository.AdminRepository;
import com.enigma.coinscape.repository.UserCredentialRepository;
import com.enigma.coinscape.repository.UserRepository;
import com.enigma.coinscape.security.BCriptUtils;
import com.enigma.coinscape.security.JwtUtils;
import com.enigma.coinscape.service.AuthService;
import com.enigma.coinscape.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtils jwtUtils;
    private final BCriptUtils bCriptUtils;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final AuthenticationManager authenticationManager;
    private final UserCredentialRepository userCredentialRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public RegisterResponse registerUser(AuthRequest request) {
        try {
            Role role =roleService.getOrSave(ERole.ROLE_REGULAR_USER);
            UserCredential credential = UserCredential.builder()
                    .email(request.getEmail())
                    .password(bCriptUtils.hashPassword(request.getPassword()))
                    .roles(List.of(role))
                    .build();
            userCredentialRepository.saveAndFlush(credential);

            User user = User.builder()
                    .email(request.getEmail())
                    .name(request.getName())
                    .phoneNumber(request.getMobilePhone())
                    .userCredential(credential)
                    .build();
            user.setBalance(BigDecimal.ZERO);
            user.setIsPremium(false);

            userRepository.saveAndFlush(user);

            return RegisterResponse.builder()
                    .email(credential.getEmail())
                    .build();
        } catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exist");
        }
    }

    @Override
    public RegisterResponse registerAdmin(AuthRequest request) {
        try {

            if(adminRepository.existsByPayCode(request.getPayCode())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        ResponseMessage.getDuplicateResourceMessage(Admin.class));
            }

            Role role =roleService.getOrSave(ERole.ROLE_ADMIN);
            UserCredential credential = UserCredential.builder()
                    .email(request.getEmail())
                    .password(bCriptUtils.hashPassword(request.getPassword()))
                    .roles(List.of(role))
                    .build();
            userCredentialRepository.saveAndFlush(credential);

            Admin admin = Admin.builder()
                    .email(request.getEmail())
                    .name(request.getName())
                    .mobilePhone(request.getMobilePhone())
                    .payCode(request.getPayCode())
                    .userCredential(credential)
                    .build();
            admin.setProfit(BigDecimal.ZERO);

            adminRepository.save(admin);

            return RegisterResponse.builder()
                    .email(credential.getEmail())
                    .build();
        } catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin already exist");
        }
    }

    @Override
    public LoginResponse loginUser(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailImpl userDetail =(UserDetailImpl)authentication.getPrincipal();
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email user tidak ditemukan"));

            if (user.getIsPremium()) {
                List<GrantedAuthority> updatedAuthorities = new ArrayList<>();
                updatedAuthorities.add(new SimpleGrantedAuthority(ERole.ROLE_PREMIUM_USER.name()));

                userDetail.setAuthorities(updatedAuthorities);
            } else {
                List<GrantedAuthority> updatedAuthorities = new ArrayList<>();
                updatedAuthorities.add(new SimpleGrantedAuthority(ERole.ROLE_REGULAR_USER.name()));

                userDetail.setAuthorities(updatedAuthorities);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        List<String> roles = userDetail.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        String token = jwtUtils.generateToken(userDetail.getEmail());

        return LoginResponse.builder()
                .email(userDetail.getEmail())
                .roles(roles)
                .token(token)
                .build();
    }

}

