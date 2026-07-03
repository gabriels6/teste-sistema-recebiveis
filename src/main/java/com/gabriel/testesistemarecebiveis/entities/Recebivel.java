package com.gabriel.testesistemarecebiveis.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "recebivel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recebivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_moeda", nullable = false)
    private Moeda moeda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cedente", nullable = false)
    private Cedente cedente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_recebivel", nullable = false)
    private TipoRecebivel tipoRecebivel;

    @Column(name = "cod_ativo", nullable = false, length = 50)
    private String codAtivo;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "taxa_base", nullable = false, precision = 18, scale = 8)
    private BigDecimal taxaBase;
}
