package br.com.fiap.respawn.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Turma {

    private Long id;

    @NotNull(message = "A escola da turma e obrigatoria")
    private Long idEscola;

    @NotNull(message = "O professor da turma e obrigatorio")
    private Long idProfessor;

    @NotBlank(message = "O nome da turma e obrigatorio")
    @Size(max = 30, message = "O nome da turma deve ter no maximo 30 caracteres")
    private String nome;

    @NotBlank(message = "A serie e obrigatoria")
    @Size(max = 30, message = "A serie deve ter no maximo 30 caracteres")
    private String serie;

    @NotBlank(message = "O turno e obrigatorio")
    @Pattern(regexp = "MANHA|TARDE|NOITE", message = "O turno deve ser MANHA, TARDE ou NOITE")
    private String turno;

    @NotNull(message = "O ano letivo e obrigatorio")
    @Min(value = 2000, message = "Ano letivo invalido")
    @Max(value = 2100, message = "Ano letivo invalido")
    private Integer anoLetivo;

    public Turma() {
    }

    public Turma(Long id, Long idEscola, Long idProfessor, String nome, String serie, String turno, Integer anoLetivo) {
        this.id = id;
        this.idEscola = idEscola;
        this.idProfessor = idProfessor;
        this.nome = nome;
        this.serie = serie;
        this.turno = turno;
        this.anoLetivo = anoLetivo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdEscola() { return idEscola; }
    public void setIdEscola(Long idEscola) { this.idEscola = idEscola; }

    public Long getIdProfessor() { return idProfessor; }
    public void setIdProfessor(Long idProfessor) { this.idProfessor = idProfessor; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public Integer getAnoLetivo() { return anoLetivo; }
    public void setAnoLetivo(Integer anoLetivo) { this.anoLetivo = anoLetivo; }
}
