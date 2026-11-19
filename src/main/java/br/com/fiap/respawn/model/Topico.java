package br.com.fiap.respawn.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Topico {

    private Long id;

    // null = topico de base, sem pre-requisito
    private Long idTopicoPrereq;

    @NotBlank(message = "O nome do topico e obrigatorio")
    @Size(max = 80, message = "O nome do topico deve ter no maximo 80 caracteres")
    private String nome;

    @NotBlank(message = "A disciplina e obrigatoria")
    @Pattern(regexp = "MATEMATICA|PORTUGUES", message = "A disciplina deve ser MATEMATICA ou PORTUGUES")
    private String disciplina;

    @NotNull(message = "O peso base e obrigatorio")
    @Min(value = 1, message = "O peso base deve estar entre 1 e 10")
    @Max(value = 10, message = "O peso base deve estar entre 1 e 10")
    private Integer pesoBase;

    public Topico() {
    }

    public Topico(Long id, Long idTopicoPrereq, String nome, String disciplina, Integer pesoBase) {
        this.id = id;
        this.idTopicoPrereq = idTopicoPrereq;
        this.nome = nome;
        this.disciplina = disciplina;
        this.pesoBase = pesoBase;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdTopicoPrereq() { return idTopicoPrereq; }
    public void setIdTopicoPrereq(Long idTopicoPrereq) { this.idTopicoPrereq = idTopicoPrereq; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDisciplina() { return disciplina; }
    public void setDisciplina(String disciplina) { this.disciplina = disciplina; }

    public Integer getPesoBase() { return pesoBase; }
    public void setPesoBase(Integer pesoBase) { this.pesoBase = pesoBase; }
}
