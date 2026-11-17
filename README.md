# Respawn API

API REST do **Respawn**, CitizenTech da Global Solution 2026.2 (FIAP, Engenharia de Software, 2º ano).
Disciplina: **Domain Driven Design - Java**.

> **Respawn** é uma plataforma de microlearning gamificado para reforço escolar.
> O aluno de escola pública informa quantos minutos tem (por exemplo, 15 minutos no ônibus),
> recebe uma "missão do dia" com exercícios curtos de matemática e português e ganha XP,
> níveis e sequência de dias. O professor acompanha num painel os tópicos em que a turma mais erra
> e os alunos que pararam de estudar.
> ODS 4 (Educação de qualidade) e ODS 10 (Redução das desigualdades).

## Links

| | |
|---|---|
| **Deploy (Render)** | https://respawn-api-gk7x.onrender.com |
| **Documentação interativa (Swagger)** | https://respawn-api-gk7x.onrender.com/swagger-ui.html |
| **Repositório** | https://github.com/kosutaa/respawn-api-gs2026 |

> O plano gratuito do Render "dorme" após um período sem uso: a primeira requisição pode levar até 1 minuto.

## Tecnologias

- Java 21
- Spring Boot 4.1 (Web MVC, JDBC, Validation)
- Oracle Database (servidor da FIAP) com driver `ojdbc17`
- springdoc-openapi (Swagger UI)
- Maven (Maven Wrapper incluso)
- Docker + Render (deploy)

## Arquitetura

```
br.com.fiap.respawn
├── model        Classes do domínio, iguais às tabelas do banco, com validações
├── dao          Acesso a dados (SQL com JdbcTemplate e sequences do Oracle)
├── bo           Regras de negócio e gamificação
├── controller   Endpoints REST (GET, POST, PUT, DELETE)
└── exception    Exceções e tratamento global de erros (JSON padronizado)
```

Fluxo de uma requisição: `Controller` → `BO` (regras) → `DAO` (SQL) → Oracle.

## Principais regras de negócio

- **Gamificação** (`RespostaAlunoBO`): uma resposta correta soma o XP do exercício à missão e ao aluno, e o nível é recalculado (1 nível a cada 100 XP, mínimo 1). Estudar atualiza a sequência de dias: o mesmo dia mantém, o dia seguinte soma 1 e um intervalo maior recomeça em 1.
- Uma missão não aceita o mesmo exercício duas vezes, nem respostas que passem do tempo disponível, nem respostas depois de concluída.
- XP, nível e sequência não podem ser alterados pelo PUT de aluno: só mudam pela gamificação.
- Excluir uma resposta correta devolve o XP ganho.
- O professor de uma turma precisa ser da mesma escola da turma.
- Um tópico não pode ser pré-requisito de si mesmo nem formar um ciclo de pré-requisitos.

## Endpoints

| Recurso | Rotas |
|---|---|
| Escolas | `GET/POST /escolas`, `GET/PUT/DELETE /escolas/{id}` |
| Professores | `GET/POST /professores`, `GET/PUT/DELETE /professores/{id}` |
| Turmas | `GET/POST /turmas`, `GET/PUT/DELETE /turmas/{id}` |
| Alunos | `GET/POST /alunos`, `GET/PUT/DELETE /alunos/{id}` |
| Tópicos | `GET/POST /topicos`, `GET/PUT/DELETE /topicos/{id}` |
| Exercícios | `GET/POST /exercicios`, `GET/PUT/DELETE /exercicios/{id}` |
| Missões | `GET/POST /missoes`, `GET/PUT/DELETE /missoes/{id}` |
| Respostas | `GET /respostas`, `GET/DELETE /respostas/{id}` |

**Rotas de negócio**

| Método | Rota | O que faz |
|---|---|---|
| POST | `/missoes/{id}/respostas` | Registra a resposta e aplica a gamificação |
| GET | `/missoes/{id}/respostas` | Respostas da missão |
| POST | `/missoes/{id}/concluir` | Conclui a missão |
| GET | `/alunos/{id}/missoes` | Histórico de missões do aluno |
| GET | `/turmas/{id}/alunos` | Alunos da turma, do maior para o menor XP |
| GET | `/turmas/{id}/erros-por-topico` | Painel do professor: tópicos com mais erros |
| GET | `/turmas/{id}/alunos-inativos?dias=7` | Busca ativa: alunos sem estudar há N dias |
| GET | `/topicos/{id}/exercicios` | Exercícios do tópico |
| GET | `/topicos/{id}/trilha` | Trilha de pré-requisitos até o tópico |

**Exemplo: registrar uma resposta**

```http
POST /missoes/1/respostas
Content-Type: application/json

{ "idExercicio": 3, "correta": true, "tempoGastoMin": 5 }
```

**Erros** sempre voltam neste formato:

```json
{
  "timestamp": "2026-11-17T20:36:24",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Essa resposta ultrapassa o tempo disponivel da missao (11 de 10 minutos)",
  "caminho": "/missoes/100/respostas",
  "detalhes": []
}
```

Status usados: `200`, `201`, `204`, `400` (dados inválidos ou regra de negócio), `404`, `405`, `409` (vínculos ou valor único repetido), `500` e `503` (banco indisponível).

## Como executar localmente

**Pré-requisitos:** JDK 21, acesso ao Oracle da FIAP e VS Code (com o Extension Pack for Java) ou IntelliJ.

1. **Banco de dados:** no Oracle da FIAP, execute nesta ordem:
   1. `db/GS2_Respawn_Script_DDL.sql` (tabelas, entregue em Database Design)
   2. `db/GS2_Respawn_Script_DML.sql` (dados de exemplo)
   3. `db/sequences.sql` (sequences usadas pela API para gerar os IDs)
2. **Credenciais:** copie `.env.example` para `.env` e preencha `DB_USERNAME` e `DB_PASSWORD`.
   O `.env` está no `.gitignore` e não vai para o repositório.
3. **Executar:**
   - **VS Code:** aba *Run and Debug*, configuração "Respawn API (local)", **F5**.
   - **Terminal (PowerShell):**
     ```powershell
     $env:DB_USERNAME="rmSEUNUMERO"; $env:DB_PASSWORD="SUA_SENHA"
     .\mvnw.cmd spring-boot:run
     ```
4. Acesse `http://localhost:8080/swagger-ui.html`.

**Testes:** `.\mvnw.cmd test` (regras de nível, XP e sequência de dias).

## Deploy no Render

O projeto tem `Dockerfile` e `render.yaml`. No Render: *New* → *Blueprint* (ou *Web Service* com runtime Docker), conectar este repositório e informar as variáveis `DB_USERNAME` e `DB_PASSWORD`.

## Equipe SysCore

| Nome | RM |
|---|---|
| Guilherme Moura Gama | 562162 |
| Guilherme Ruiz Costa | 563236 |
| Júlio César Augusto Vieira | 563366 |
