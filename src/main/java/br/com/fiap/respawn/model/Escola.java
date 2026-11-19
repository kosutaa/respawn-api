package br.com.fiap.respawn.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Escola {

    private Long id;

    @NotBlank(message = "O nome da escola e obrigatorio")
    @Size(max = 100, message = "O nome da escola deve ter no maximo 100 caracteres")
    private String nome;

    @NotBlank(message = "O bairro e obrigatorio")
    @Size(max = 60, message = "O bairro deve ter no maximo 60 caracteres")
    private String bairro;

    @NotBlank(message = "A zona e obrigatoria")
    @Size(max = 30, message = "A zona deve ter no maximo 30 caracteres")
    private String zona;

    @NotBlank(message = "O codigo INEP e obrigatorio")
    @Pattern(regexp = "\\d{8}", message = "O codigo INEP deve ter 8 digitos")
    private String inep;

    public Escola() {
    }

    public Escola(Long id, String nome, String bairro, String zona, String inep) {
        this.id = id;
        this.nome = nome;
        this.bairro = bairro;
        this.zona = zona;
        this.inep = inep;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public String getInep() { return inep; }
    public void setInep(String inep) { this.inep = inep; }
}
