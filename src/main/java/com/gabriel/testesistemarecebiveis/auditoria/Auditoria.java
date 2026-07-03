package com.gabriel.testesistemarecebiveis.auditoria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Registro de auditoria persistido para cada entidade criada, atualizada ou
 * removida. Guarda uma fotografia (em JSON) do estado da entidade no momento
 * da operacao, alem de quem executou e quando.
 */
@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome da entidade auditada (ex.: {@code Cedente}). */
    @Column(nullable = false, length = 150)
    private String entidade;

    /** Identificador da entidade auditada, serializado como texto. */
    @Column(name = "entidade_id", length = 100)
    private String entidadeId;

    /** Operacao que gerou o registro. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoOperacao operacao;

    /** Usuario autenticado que executou a operacao, ou {@code sistema}. */
    @Column(length = 100)
    private String usuario;

    /** Fotografia do estado da entidade (JSON) no momento da operacao. */
    @Column(columnDefinition = "text")
    private String dados;

    /** Momento em que a operacao foi registrada. */
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
}
