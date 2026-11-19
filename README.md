# Respawn API

API REST do **Respawn**, CitizenTech da Global Solution 2026.2 (FIAP, Engenharia de Software, 2º ano).
Disciplina: **Domain Driven Design - Java**.

## Ficha da CitizenTech

| Campo | Conteúdo |
|---|---|
| **Nome da CitizenTech** | Respawn |
| **Cidade e bairro** | São Paulo (SP), Itaquera, Zona Leste |
| **Cidadão beneficiado** | Estudante de escola pública (9º ano ao ensino médio) com lacunas em Matemática e Português e pouco tempo livre para estudar; e o professor dessas turmas |
| **Problema, em uma frase** | Alunos da rede pública que ficaram para trás no básico não têm reforço acessível, não sabem por onde recomeçar e desistem de videoaulas longas, enquanto o professor não consegue ver a tempo quem está ficando para trás. |
| **Como funciona** | O aluno informa quantos minutos tem (ex.: 15 minutos no ônibus) e recebe uma "missão do dia" com exercícios curtos, seguindo uma trilha de pré-requisitos. Cada acerto rende XP, nível e sequência de dias. O professor acompanha num painel os tópicos em que a turma mais erra e os alunos que pararam de estudar (busca ativa). |
| **O que cada parte faz** | **API Java (este repositório):** domínio, regras de gamificação e painel, com Oracle e deploy no Render. **Database Design:** modelo relacional das 8 tabelas. **Data Science:** análise do ENEM 2023 (escolas públicas da cidade de SP) com regressão linear e logística, que fundamenta as prioridades do produto. **Dynamic Programming:** algoritmo da trilha e da missão do dia. **Redes:** rede do laboratório da escola. **Agile:** personas, backlog e protótipos. **AR/VR:** vídeo da trilha em 3D. |
| **Link do repositório** | https://github.com/kosutaa/respawn-api |
| **Indicador de impacto** | % de alunos que mantêm 7 dias seguidos de estudo; aumento de acertos por tópico entre o diagnóstico e 30 dias depois; % de alunos da busca ativa que voltam a estudar |
| **ODS conectados** | **ODS 4**, metas **4.1** (aprendizagem efetiva na educação básica) e **4.6** (competências de leitura e matemática), e **ODS 10**, meta **10.2** (inclusão de todos, independentemente da condição econômica, raça ou sexo) |

> Uso de IA: os prompts utilizados estão registrados em [prompts.md](prompts.md).

## Links

| | |
|---|---|
| **Aplicação web (front-end)** | https://respawn-api-gk7x.onrender.com/app |
| **Deploy da API (Render)** | https://respawn-api-gk7x.onrender.com |
| **Documentação interativa (Swagger)** | https://respawn-api-gk7x.onrender.com/swagger-ui.html |
| **Repositório** | https://github.com/kosutaa/respawn-api |

> O plano gratuito do Render "dorme" após um período sem uso: a primeira requisição pode levar de 1 a 2 minutos. Depois disso, as respostas são imediatas.

## Aplicação web (front-end)

A pasta `src/main/resources/static/app` tem o front-end do Respawn (HTML, CSS e JavaScript, sem framework), servido pelo próprio Spring Boot no mesmo endereço da API. Ele implementa as 3 telas dos protótipos e grava de verdade no Oracle pelas rotas REST:

| Tela | O que faz | Rotas usadas |
|---|---|---|
| Missão do dia (aluna) | Mostra XP, nível e sequência; a aluna escolhe 5, 10, 15 ou 20 min e recebe a missão montada pelo grafo de pré-requisitos + mochila 0/1 (mesma lógica do trabalho de Dynamic Programming) | `GET /alunos/{id}`, `GET /alunos/{id}/missoes`, `GET /topicos`, `GET /exercicios`, `POST /missoes` |
| Exercício | Enunciado e alternativas; cada resposta aplica a gamificação da API (XP, nível e sequência) | `POST /missoes/{id}/respostas`, `POST /missoes/{id}/concluir` |
| Painel da professora | Tópicos com mais erros, busca ativa de quem parou, alunos, trilhas e catálogo | `GET /turmas/{id}/erros-por-topico`, `GET /turmas/{id}/alunos-inativos`, `GET /turmas/{id}/alunos` |

O login é simulado (personas Ana Beatriz Silva e Profa. Helena Souza). O banco guarda título, enunciado, tempo e XP dos exercícios; as alternativas ficam no front-end neste protótipo. Para voltar a Ana ao estado inicial antes de uma demonstração, execute `db/preparar_demo.sql`.

## Tecnologias

- Java 21
- Spring Boot 4.1 (Web MVC, JDBC, Validation)
- Oracle Database (servidor da FIAP) com driver `ojdbc17`
- springdoc-openapi (Swagger UI)
- Maven (Maven Wrapper incluso)
- HTML, CSS e JavaScript puro (front-end em `static/app`)
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
4. Acesse a aplicação em `http://localhost:8080/app` e a documentação em `http://localhost:8080/swagger-ui.html`.

**Testes:** `.\mvnw.cmd test` (regras de nível, XP e sequência de dias).

## Deploy no Render

O projeto tem `Dockerfile` e `render.yaml`. No Render: *New* → *Blueprint* (ou *Web Service* com runtime Docker), conectar este repositório e informar as variáveis `DB_USERNAME` e `DB_PASSWORD`.

## Equipe SysCore

| Nome | RM |
|---|---|
| Guilherme Moura Gama | 562162 |
| Guilherme Ruiz Costa | 563236 |
| Júlio César Augusto Vieira | 563366 |
