package com.enigma.coinscape.service.impl;

import com.enigma.coinscape.entities.Role;
import com.enigma.coinscape.entities.constants.ERole;
import com.enigma.coinscape.repository.RoleRepository;
import com.enigma.coinscape.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    @Override
    public Role getOrSave(ERole role) {
        return roleRepository.findByRole(role).orElseGet(() -> roleRepository.save(Role.builder()
                        .role(role)
                .build()));
    }
}
