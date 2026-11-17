package br.com.fiap.respawn.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * "Missao do dia": sessao de estudo do aluno limitada pelo tempo que ele
 * tem disponivel (tabela T_SC_MISSAO_DIARIA). O XP ganho e calculado
 * pelas respostas corretas e nao pode ser editado diretamente.
 */
public class MissaoDiaria {

    private Long id;

    @NotNull(message = "O aluno da missao e obrigatorio")
    private Long idAluno;

    /** Se nao for informada no cadastro, a missao e criada com a data de hoje. */
    private LocalDate data;

    @NotNull(message = "O tempo disponivel e obrigatorio")
    @Min(value = 1, message = "O tempo disponivel deve ser maior que zero")
    @Max(value = 999, message = "O tempo disponivel deve ser no maximo 999 minutos")
    private Integer tempoDisponivelMin;

    private Integer xpGanho;

    private Boolean concluida;

    public MissaoDiaria() {
    }

    public MissaoDiaria(Long id, Long idAluno, LocalDate data, Integer tempoDisponivelMin,
                        Integer xpGanho, Boolean concluida) {
        this.id = id;
        this.idAluno = idAluno;
        this.data = data;
        this.tempoDisponivelMin = tempoDisponivelMin;
        this.xpGanho = xpGanho;
        this.concluida = concluida;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdAluno() { return idAluno; }
    public void setIdAluno(Long idAluno) { this.idAluno = idAluno; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public Integer getTempoDisponivelMin() { return tempoDisponivelMin; }
    public void setTempoDisponivelMin(Integer tempoDisponivelMin) { this.tempoDisponivelMin = tempoDisponivelMin; }

    public Integer getXpGanho() { return xpGanho; }
    public void setXpGanho(Integer xpGanho) { this.xpGanho = xpGanho; }

    public Boolean getConcluida() { return concluida; }
    public void setConcluida(Boolean concluida) { this.concluida = concluida; }
}
