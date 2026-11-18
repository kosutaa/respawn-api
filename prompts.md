# Registro de uso de IA (prompts.md)

O briefing da Global Solution 2026.2 permite usar IA como copiloto, desde que os prompts sejam registrados e a equipe saiba explicar cada linha. Este arquivo registra como a IA foi usada nas partes de **Domain Driven Design - Java** e **Data Science** do Respawn.

- **Ferramenta:** Claude (Anthropic), via Claude Code no VS Code / app desktop
- **Período:** 15 a 19/11/2026
- **Responsável pelas conversas:** Guilherme Ruiz Costa (RM 563236)

## Prompts utilizados

Os prompts estão transcritos em ordem. Os de ajuste fino, como responder "sim", aprovar uma etapa ou mandar um print de erro, foram agrupados.

| # | Etapa | Prompt (resumo fiel) | O que a IA produziu | O que a equipe fez |
|---|---|---|---|---|
| 1 | Ideação | "Vou ter a Global Solution agora na faculdade, tenho a documentação e um tema/ideia aqui, consegue me gerar ideias de aplicações?" (com o PDF do briefing e o starter kit "Viva Games") | 6 ideias de CitizenTech ligadas a educação e games | Escolhemos a ideia de reforço escolar gamificado |
| 2 | Ideação | "Não entendi bem a ideia 1" / "Me explique a segunda ideia" | Explicação de cada ideia com persona, fluxo e encaixe nas 7 disciplinas | Decidimos pela ideia 1 |
| 3 | Nome | "Vamos fazer a ideia 1, com isso me dê sugestões de nomes" | Lista de nomes por estilo | Escolhemos **Respawn** |
| 4 | Planejamento | "Crie um plano de desenvolvimento da GS e me deixe analisá-lo" e depois "No meu caso eu tenho só o trabalho de Java e de Data Science" | Plano com escopo do MVP, modelo de domínio, checklist por critério de nota e cronograma | Revisamos e seguimos o plano |
| 5 | Banco | "Meu amigo me mandou o trabalho de Database, confere pra mim" e depois "Pode corrigir você mesmo e utilize ele corrigido" | Conferência com os critérios; correção do XP das missões no DML e dos DROPs do DDL; sequences para a API | Rodamos os scripts no Oracle da FIAP e conferimos as contagens |
| 6 | Ambiente | Ajuda para instalar a extensão Oracle SQL Developer no VS Code e criar a conexão | Passo a passo da conexão e dos scripts | Conexão e execução feitas pela equipe (a senha nunca foi passada para a IA digitar) |
| 7 | API Java | "Pode usar o VS Code mesmo com a extensão" (criação do projeto Spring Boot) | Projeto Spring Boot em camadas model, dao, bo, controller e exception, com testes JUnit | Revisamos as classes e as regras de negócio e testamos as rotas com o Oracle |
| 8 | Deploy | "Sim, vamos ao deploy" e "qual nome devo colocar no repo?" | Dockerfile, render.yaml, README e passo a passo do Render | Criamos o repositório e o serviço no Render e configuramos as variáveis |
| 9 | Documentação | "Faça o PDF e antes de adicionar os esboços me mande aqui para eu verificar" | Diagramas de classes (PlantUML), 3 esboços de tela e PDF da documentação | Aprovamos os esboços e revisamos o PDF |
| 10 | Data Science | "Sim, Data Science" e "Pode baixar" (microdados do ENEM 2023) | Script de recorte da base, notebook com regressão linear e logística, recomendações e limitações | Revisamos as decisões de limpeza, as métricas e as recomendações |
| 11 | Revisão | "Quero que confira o que foi pedido em cada trabalho, verifique se bate com os requisitos e veja se não pode aprimorar nada" | Conferência contra o briefing; inclusão da Ficha da CitizenTech, deste arquivo, das metas dos ODS e de melhorias estatísticas | Revisão final antes da entrega |
| 12 | Aplicação web | "O vídeo de Agile pede a aplicação em funcionamento, podemos fazer essa aplicação?" e "Sim, dentro da API" | Front-end com as 3 telas aprovadas, servido pelo Spring Boot em `/app`, e o script `db/preparar_demo.sql` | Testamos o fluxo completo (missão, respostas, painel) com o Oracle e gravamos o vídeo com a aplicação |

## Como garantimos o entendimento

- Cada camada da API (model → dao → bo → controller → exception) foi lida e discutida, com foco nas regras de gamificação (`RespostaAlunoBO`) e no tratamento de erros (`GlobalExceptionHandler`).
- Todas as rotas foram executadas contra o Oracle da FIAP antes do deploy (cenários de sucesso e de erro descritos no capítulo 8 da documentação).
- No notebook, cada decisão (remover notas zero, não usar as outras provas para evitar vazamento de dados, limiar de 0,40) está justificada no próprio texto.
- Credenciais do banco nunca foram compartilhadas com a IA para digitação nem colocadas no código: ficam no `.env` (fora do Git) e nas variáveis de ambiente do Render.
