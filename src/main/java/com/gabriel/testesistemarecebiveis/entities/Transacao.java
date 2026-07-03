package com.gabriel.testesistemarecebiveis.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recebivel", nullable = false)
    private Recebivel recebivel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_moeda", nullable = false)
    private Moeda moeda;

    @Column(name = "data_operacao", nullable = false)
    private LocalDate dataOperacao;

    @Column(name = "data_liquidacao")
    private LocalDate dataLiquidacao;

    @Column(name = "qtde_operacao", nullable = false, precision = 18, scale = 8)
    private BigDecimal qtdeOperacao;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 8)
    private BigDecimal precoUnitario;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /**
     * Deságio calculado (valor presente / preço unitário - 1). Não é persistido:
     * é preenchido pela listagem a partir da precificação do recebível.
     */
    @Transient
    private BigDecimal desagio;
}
