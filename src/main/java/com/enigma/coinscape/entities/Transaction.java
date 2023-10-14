package com.enigma.coinscape.entities;

import com.enigma.coinscape.entities.constants.BaseEntity;
import com.enigma.coinscape.entities.constants.ETransactionType;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "m_transaction")
public class Transaction extends BaseEntity {
    @Column(name = "trans_date")
    private LocalDateTime transDate;

    @Enumerated(EnumType.STRING)
    private ETransactionType eTransactionType;

    private BigDecimal amount;

    private String description;

    @OneToOne
    @JoinColumn(name = "detail_transaction_id")
    private DetailTrans detailTrans;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    private UpgradeRequest upgradeRequest;
}
