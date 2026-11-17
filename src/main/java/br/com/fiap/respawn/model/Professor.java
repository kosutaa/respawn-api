package br.com.fiap.respawn.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Professor vinculado a uma escola (tabela T_SC_PROFESSOR). */
public class Professor {

    private Long id;

    @NotNull(message = "A escola do professor e obrigatoria")
    private Long idEscola;

    @NotBlank(message = "O nome do professor e obrigatorio")
    @Size(max = 100, message = "O nome deve ter no maximo 100 caracteres")
    private String nome;

    @NotBlank(message = "O e-mail e obrigatorio")
    @Email(message = "E-mail invalido")
    @Size(max = 100, message = "O e-mail deve ter no maximo 100 caracteres")
    private String email;

    @NotBlank(message = "A disciplina e obrigatoria")
    @Pattern(regexp = "MATEMATICA|PORTUGUES", message = "A disciplina deve ser MATEMATICA ou PORTUGUES")
    private String disciplina;

    public Professor() {
    }

    public Professor(Long id, Long idEscola, String nome, String email, String disciplina) {
        this.id = id;
        this.idEscola = idEscola;
        this.nome = nome;
        this.email = email;
        this.disciplina = disciplina;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdEscola() { return idEscola; }
    public void setIdEscola(Long idEscola) { this.idEscola = idEscola; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisciplina() { return disciplina; }
    public void setDisciplina(String disciplina) { this.disciplina = disciplina; }
}
