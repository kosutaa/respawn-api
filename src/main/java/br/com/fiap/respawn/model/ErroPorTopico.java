package br.com.fiap.respawn.model;

/** Linha do painel do professor: desempenho da turma em um topico. */
public class ErroPorTopico {

    private Long idTopico;
    private String topico;
    private String disciplina;
    private Integer totalRespostas;
    private Integer totalErros;
    private Double percentualErro;

    public ErroPorTopico() {
    }

    public ErroPorTopico(Long idTopico, String topico, String disciplina,
                         Integer totalRespostas, Integer totalErros) {
        this.idTopico = idTopico;
        this.topico = topico;
        this.disciplina = disciplina;
        this.totalRespostas = totalRespostas;
        this.totalErros = totalErros;
        this.percentualErro = totalRespostas == 0 ? 0.0
                : Math.round(totalErros * 1000.0 / totalRespostas) / 10.0;
    }

    public Long getIdTopico() { return idTopico; }
    public String getTopico() { return topico; }
    public String getDisciplina() { return disciplina; }
    public Integer getTotalRespostas() { return totalRespostas; }
    public Integer getTotalErros() { return totalErros; }
    public Double getPercentualErro() { return percentualErro; }
}
