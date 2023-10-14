package com.enigma.coinscape.entities;

import com.enigma.coinscape.entities.constants.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "m_admin")
public class Admin extends BaseEntity {

    private String name;

    private String email;

    private String payCode;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @OneToOne
    @JoinColumn(name = "user_credential_id")
    private UserCredential userCredential;

    @Column(columnDefinition = "decimal(10) default 0")
    private BigDecimal profit;

    @OneToMany(mappedBy = "admin")
    private List<Transaction> transactions;

}
