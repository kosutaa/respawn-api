package br.com.fiap.respawn.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Exercicio {

    private Long id;

    @NotNull(message = "O topico do exercicio e obrigatorio")
    private Long idTopico;

    @NotBlank(message = "O titulo e obrigatorio")
    @Size(max = 100, message = "O titulo deve ter no maximo 100 caracteres")
    private String titulo;

    @NotBlank(message = "O enunciado e obrigatorio")
    @Size(max = 400, message = "O enunciado deve ter no maximo 400 caracteres")
    private String enunciado;

    @NotNull(message = "O tempo estimado e obrigatorio")
    @Min(value = 1, message = "O tempo estimado deve ser maior que zero")
    @Max(value = 99, message = "O tempo estimado deve ser no maximo 99 minutos")
    private Integer tempoEstimadoMin;

    @NotNull(message = "O XP de recompensa e obrigatorio")
    @Min(value = 1, message = "O XP de recompensa deve ser maior que zero")
    @Max(value = 9999, message = "O XP de recompensa deve ser no maximo 9999")
    private Integer xpRecompensa;

    @NotBlank(message = "A dificuldade e obrigatoria")
    @Pattern(regexp = "BASICO|INTERMEDIARIO|AVANCADO",
             message = "A dificuldade deve ser BASICO, INTERMEDIARIO ou AVANCADO")
    private String dificuldade;

    public Exercicio() {
    }

    public Exercicio(Long id, Long idTopico, String titulo, String enunciado,
                     Integer tempoEstimadoMin, Integer xpRecompensa, String dificuldade) {
        this.id = id;
        this.idTopico = idTopico;
        this.titulo = titulo;
        this.enunciado = enunciado;
        this.tempoEstimadoMin = tempoEstimadoMin;
        this.xpRecompensa = xpRecompensa;
        this.dificuldade = dificuldade;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdTopico() { return idTopico; }
    public void setIdTopico(Long idTopico) { this.idTopico = idTopico; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }

    public Integer getTempoEstimadoMin() { return tempoEstimadoMin; }
    public void setTempoEstimadoMin(Integer tempoEstimadoMin) { this.tempoEstimadoMin = tempoEstimadoMin; }

    public Integer getXpRecompensa() { return xpRecompensa; }
    public void setXpRecompensa(Integer xpRecompensa) { this.xpRecompensa = xpRecompensa; }

    public String getDificuldade() { return dificuldade; }
    public void setDificuldade(String dificuldade) { this.dificuldade = dificuldade; }
}
