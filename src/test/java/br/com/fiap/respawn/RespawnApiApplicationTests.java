package br.com.fiap.respawn;

import br.com.fiap.respawn.model.Aluno;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testes das regras de gamificacao do Aluno (nao precisam de banco de dados).
 */
class RespawnApiApplicationTests {

    private Aluno novoAluno(int xp, int streak, LocalDate ultimoAcesso) {
        return new Aluno(1L, 1L, "Ana", 15, 15, xp, Aluno.calcularNivel(xp), streak, ultimoAcesso);
    }

    @Test
    void nivelSegueOsDadosDoBanco() {
        // Valores do DML: 90 XP = nivel 1, 120 = 1, 250 = 2, 320 = 3, 450 = 4, 580 = 5
        assertEquals(1, Aluno.calcularNivel(90));
        assertEquals(1, Aluno.calcularNivel(120));
        assertEquals(2, Aluno.calcularNivel(250));
        assertEquals(3, Aluno.calcularNivel(320));
        assertEquals(4, Aluno.calcularNivel(450));
        assertEquals(5, Aluno.calcularNivel(580));
    }

    @Test
    void ganharXpSobeDeNivel() {
        Aluno ana = novoAluno(450, 7, null);
        ana.adicionarXp(60);
        assertEquals(510, ana.getXpTotal());
        assertEquals(5, ana.getNivel());
    }

    @Test
    void perderXpNuncaFicaNegativo() {
        Aluno aluno = novoAluno(30, 1, null);
        aluno.adicionarXp(-50);
        assertEquals(0, aluno.getXpTotal());
        assertEquals(1, aluno.getNivel());
    }

    @Test
    void estudarNoDiaSeguinteAumentaASequencia() {
        LocalDate ontem = LocalDate.of(2026, 11, 16);
        Aluno ana = novoAluno(450, 7, ontem);
        ana.registrarEstudo(ontem.plusDays(1));
        assertEquals(8, ana.getStreakDias());
        assertEquals(ontem.plusDays(1), ana.getUltimoAcesso());
    }

    @Test
    void estudarDuasVezesNoMesmoDiaNaoAumentaASequencia() {
        LocalDate hoje = LocalDate.of(2026, 11, 17);
        Aluno ana = novoAluno(450, 8, hoje);
        ana.registrarEstudo(hoje);
        assertEquals(8, ana.getStreakDias());
    }

    @Test
    void ficarDiasSemEstudarReiniciaASequencia() {
        Aluno matheus = novoAluno(120, 5, LocalDate.of(2026, 10, 28));
        matheus.registrarEstudo(LocalDate.of(2026, 11, 17));
        assertEquals(1, matheus.getStreakDias());
    }

    @Test
    void primeiroEstudoComecaASequencia() {
        Aluno novo = novoAluno(0, 0, null);
        novo.registrarEstudo(LocalDate.of(2026, 11, 17));
        assertEquals(1, novo.getStreakDias());
    }
}
