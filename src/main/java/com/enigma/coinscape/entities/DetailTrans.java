package com.enigma.coinscape.entities;

import com.enigma.coinscape.entities.constants.BaseEntity;
import com.enigma.coinscape.entities.constants.EPayment;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "m_detail_transaction")
public class DetailTrans extends BaseEntity {
    private BigDecimal charge;

    @Enumerated(EnumType.STRING)
    private EPayment paymentMethod;

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction trans;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
