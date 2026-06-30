package com.gabriel.testesistemarecebiveis.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cedente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cedente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cod_empresa", nullable = false, length = 50)
    private String codEmpresa;

    @Column(nullable = false, length = 100)
    private String nome;
}
