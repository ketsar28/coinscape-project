package com.enigma.coinscape.entities;

import com.enigma.coinscape.entities.constants.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "m_user")
public class User extends BaseEntity {
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(columnDefinition = "decimal(10) default 0")
    private BigDecimal balance;

    @Column(name = "is_premium")
    private Boolean isPremium;

    @Column(name = "premium_activation_date")
    private LocalDateTime premiumActivationDate;

    @OneToOne
    @JoinColumn(name = "user_credential_id")
    private UserCredential userCredential;

}

