package br.com.fiap.respawn.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Resposta do aluno a um exercicio dentro de uma missao (tabela T_SC_RESPOSTA_ALUNO).
 * A missao vem da URL (POST /missoes/{id}/respostas) e a data e a do registro.
 */
public class RespostaAluno {

    private Long id;

    private Long idMissao;

    @NotNull(message = "O exercicio respondido e obrigatorio")
    private Long idExercicio;

    @NotNull(message = "Informe se a resposta esta correta")
    private Boolean correta;

    @NotNull(message = "O tempo gasto e obrigatorio")
    @Min(value = 1, message = "O tempo gasto deve ser maior que zero")
    @Max(value = 99, message = "O tempo gasto deve ser no maximo 99 minutos")
    private Integer tempoGastoMin;

    private LocalDate data;

    public RespostaAluno() {
    }

    public RespostaAluno(Long id, Long idMissao, Long idExercicio, Boolean correta,
                         Integer tempoGastoMin, LocalDate data) {
        this.id = id;
        this.idMissao = idMissao;
        this.idExercicio = idExercicio;
        this.correta = correta;
        this.tempoGastoMin = tempoGastoMin;
        this.data = data;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdMissao() { return idMissao; }
    public void setIdMissao(Long idMissao) { this.idMissao = idMissao; }

    public Long getIdExercicio() { return idExercicio; }
    public void setIdExercicio(Long idExercicio) { this.idExercicio = idExercicio; }

    public Boolean getCorreta() { return correta; }
    public void setCorreta(Boolean correta) { this.correta = correta; }

    public Integer getTempoGastoMin() { return tempoGastoMin; }
    public void setTempoGastoMin(Integer tempoGastoMin) { this.tempoGastoMin = tempoGastoMin; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
}
