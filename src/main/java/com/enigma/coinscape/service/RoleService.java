package com.enigma.coinscape.service;

import com.enigma.coinscape.entities.Role;
import com.enigma.coinscape.entities.constants.ERole;

public interface RoleService {
    Role getOrSave(ERole role);
}
