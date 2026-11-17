package br.com.fiap.respawn.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Estudante que usa o Respawn (tabela T_SC_ALUNO).
 * XP, nivel, sequencia de dias e ultimo acesso sao controlados pela
 * gamificacao (RespostaAlunoBO) e nao podem ser editados diretamente.
 */
public class Aluno {

    /** A cada XP_POR_NIVEL pontos o aluno sobe um nivel. */
    public static final int XP_POR_NIVEL = 100;

    private Long id;

    @NotNull(message = "A turma do aluno e obrigatoria")
    private Long idTurma;

    @NotBlank(message = "O nome do aluno e obrigatorio")
    @Size(max = 100, message = "O nome deve ter no maximo 100 caracteres")
    private String nome;

    @NotNull(message = "A idade e obrigatoria")
    @Min(value = 10, message = "A idade minima e 10 anos")
    @Max(value = 99, message = "Idade invalida")
    private Integer idade;

    @NotNull(message = "O tempo diario disponivel e obrigatorio")
    @Min(value = 1, message = "O tempo diario deve ser maior que zero")
    @Max(value = 999, message = "O tempo diario deve ser no maximo 999 minutos")
    private Integer tempoDiarioMin;

    private Integer xpTotal;
    private Integer nivel;
    private Integer streakDias;
    private LocalDate ultimoAcesso;

    public Aluno() {
    }

    public Aluno(Long id, Long idTurma, String nome, Integer idade, Integer tempoDiarioMin,
                 Integer xpTotal, Integer nivel, Integer streakDias, LocalDate ultimoAcesso) {
        this.id = id;
        this.idTurma = idTurma;
        this.nome = nome;
        this.idade = idade;
        this.tempoDiarioMin = tempoDiarioMin;
        this.xpTotal = xpTotal;
        this.nivel = nivel;
        this.streakDias = streakDias;
        this.ultimoAcesso = ultimoAcesso;
    }

    /** Soma (ou subtrai, se negativo) XP e recalcula o nivel. Nunca fica abaixo de zero. */
    public void adicionarXp(int xp) {
        this.xpTotal = Math.max(0, this.xpTotal + xp);
        this.nivel = calcularNivel(this.xpTotal);
    }

    /** Nivel 1 ate 199 XP; depois, um nivel a cada 100 XP (ex.: 450 XP = nivel 4). */
    public static int calcularNivel(int xpTotal) {
        return Math.max(1, xpTotal / XP_POR_NIVEL);
    }

    /**
     * Atualiza a sequencia de dias (streak) ao estudar em uma data:
     * mesmo dia mantem, dia seguinte soma 1, depois de um intervalo recomeca em 1.
     */
    public void registrarEstudo(LocalDate data) {
        if (ultimoAcesso != null && ultimoAcesso.equals(data)) {
            return;
        }
        if (ultimoAcesso != null && ultimoAcesso.plusDays(1).equals(data)) {
            streakDias = streakDias + 1;
        } else {
            streakDias = 1;
        }
        ultimoAcesso = data;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdTurma() { return idTurma; }
    public void setIdTurma(Long idTurma) { this.idTurma = idTurma; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }

    public Integer getTempoDiarioMin() { return tempoDiarioMin; }
    public void setTempoDiarioMin(Integer tempoDiarioMin) { this.tempoDiarioMin = tempoDiarioMin; }

    public Integer getXpTotal() { return xpTotal; }
    public void setXpTotal(Integer xpTotal) { this.xpTotal = xpTotal; }

    public Integer getNivel() { return nivel; }
    public void setNivel(Integer nivel) { this.nivel = nivel; }

    public Integer getStreakDias() { return streakDias; }
    public void setStreakDias(Integer streakDias) { this.streakDias = streakDias; }

    public LocalDate getUltimoAcesso() { return ultimoAcesso; }
    public void setUltimoAcesso(LocalDate ultimoAcesso) { this.ultimoAcesso = ultimoAcesso; }
}
