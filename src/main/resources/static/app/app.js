'use strict';

// login simulado (personas da equipe)
const PERFIS = {
  aluna: { id: 1 },
  professora: { id: 1, turma: 1, tratamento: 'Profa.' },
};
const TEMPOS = [5, 10, 15, 20];
const MAX_EXERCICIOS = 3;
const LIMIAR_DOMINIO = 0.7;

// no banco os nomes estao sem acento
const TOPICOS = {
  1: { nome: 'Operações básicas e divisão', curto: 'Operações básicas' },
  2: { nome: 'Frações e proporções', curto: 'Frações' },
  3: { nome: 'Porcentagem aplicada', curto: 'Porcentagem' },
  4: { nome: 'Regra de três simples', curto: 'Regra de três' },
  5: { nome: 'Leitura e localização de informações', curto: 'Leitura' },
  6: { nome: 'Interpretação de texto e inferência', curto: 'Interpretação' },
};
const DISCIPLINAS = { MATEMATICA: 'Matemática', PORTUGUES: 'Português' };
const DIFICULDADES = { BASICO: 'Básico', INTERMEDIARIO: 'Intermediário', AVANCADO: 'Avançado' };

// o banco nao tem as alternativas, entao ficam aqui
const CONTEUDO = {
  1: {
    titulo: 'Divisão no dia a dia',
    enunciado: 'A escola recebeu 144 livros e vai dividir igualmente em 12 caixas. Quantos livros vão em cada caixa?',
    alternativas: ['10', '12', '14', '132'], certa: 1,
    explicacao: '144 ÷ 12 = 12, porque 12 × 12 = 144.',
  },
  2: {
    titulo: 'Soma de frações no ônibus',
    enunciado: 'No ônibus, 1/4 dos passageiros desce na primeira estação e mais 2/4 descem na seguinte. Que fração dos passageiros desceu?',
    alternativas: ['2/8', '3/4', '3/8', '1/2'], certa: 1,
    explicacao: 'Frações com o mesmo denominador: some só os numeradores (1 + 2 = 3).',
  },
  3: {
    titulo: 'Equivalência de frações',
    enunciado: 'Uma receita usa 1/2 xícara de açúcar. Qual fração abaixo é exatamente a mesma quantidade?',
    alternativas: ['1/4', '2/3', '2/4', '3/4'], certa: 2,
    explicacao: 'Multiplicando em cima e embaixo pelo mesmo número a fração não muda: 1×2 / 2×2 = 2/4.',
  },
  4: {
    titulo: 'Desconto de 25% na feira',
    enunciado: 'Uma compra de R$ 80 na feira tem desconto de 1/4. Quanto é esse desconto?',
    alternativas: ['R$ 25, ou 25%', 'R$ 20, ou 25%', 'R$ 20, ou 40%', 'R$ 4, ou 14%'], certa: 1,
    explicacao: '1/4 = 25/100 = 25%. E 1/4 de R$ 80 é 80 ÷ 4 = R$ 20.',
  },
  5: {
    titulo: 'Ideia central da notícia',
    apoio: '"A prefeitura ampliou o horário da biblioteca do bairro até as 21h. Segundo a direção, a mudança atende estudantes que trabalham durante o dia e só conseguem estudar à noite."',
    enunciado: 'Qual é a ideia central da notícia?',
    alternativas: ['A biblioteca ganhou livros novos.', 'O horário foi ampliado para atender quem só estuda à noite.', 'Os estudantes do bairro trabalham durante o dia.', 'A direção da biblioteca foi trocada.'], certa: 1,
    explicacao: 'A notícia gira em torno da mudança de horário e do motivo dela; os outros itens são detalhes ou não aparecem no texto.',
  },
  6: {
    titulo: 'Ironia e sentido figurado',
    apoio: 'Depois de esperar o ônibus por uma hora debaixo de chuva, Joana diz: "Que dia maravilhoso para passear!"',
    enunciado: 'O que Joana quis dizer com a fala dela?',
    alternativas: ['Ela está feliz com o passeio.', 'Ela está irritada e usa ironia.', 'Ela gosta de dias de chuva.', 'O ônibus chegou rápido.'], certa: 1,
    explicacao: 'Ironia é dizer o contrário do que se pensa: o contexto (uma hora na chuva) mostra que o dia foi ruim.',
  },
};


const $app = document.getElementById('app');
const $toast = document.getElementById('toast');
const estado = { tempo: null, dias: 7, aba: 'painel', abertas: new Set() };

const LOGO = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M4 12a8 8 0 1 0 2.6-5.9"/><path d="M4 3.5v4.6h4.6"/></svg>';

function esc(texto) {
  return String(texto ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

async function api(caminho, opcoes = {}) {
  const resposta = await fetch(caminho, {
    ...opcoes,
    headers: { 'Accept': 'application/json', 'Content-Type': 'application/json' },
  });
  if (!resposta.ok) {
    let mensagem = `A API respondeu com erro ${resposta.status}.`;
    try {
      const corpo = await resposta.json();
      mensagem = corpo.mensagem || corpo.erro || mensagem;
    } catch (_) { /* corpo sem JSON */ }
    throw new Error(mensagem);
  }
  return resposta.status === 204 ? null : resposta.json();
}

const post = (caminho, corpo) => api(caminho, { method: 'POST', body: corpo ? JSON.stringify(corpo) : undefined });

function hoje() {
  return new Date().toLocaleDateString('en-CA', { timeZone: 'America/Sao_Paulo' });
}

function dataCurta(iso) {
  if (!iso) return '-';
  const [, mes, dia] = iso.split('-');
  return `${dia}/${mes}`;
}

function saudacao() {
  const hora = Number(new Date().toLocaleString('en-US', { timeZone: 'America/Sao_Paulo', hour: 'numeric', hour12: false }));
  if (hora < 12) return 'Bom dia,';
  if (hora < 18) return 'Boa tarde,';
  return 'Boa noite,';
}

const nomeTopico = (id, original) => TOPICOS[id]?.nome || original || `Tópico ${id}`;
const curtoTopico = (id, original) => TOPICOS[id]?.curto || original || `Tópico ${id}`;
const tituloExercicio = ex => CONTEUDO[ex.id]?.titulo || ex.titulo;
const porcento = valor => `${valor.toLocaleString('pt-BR', { maximumFractionDigits: 1 })}%`;

function lerLocal(chave) {
  try { return JSON.parse(localStorage.getItem(chave)); } catch (_) { return null; }
}
function gravarLocal(chave, valor) {
  try { localStorage.setItem(chave, JSON.stringify(valor)); } catch (_) { /* navegador sem armazenamento */ }
}

let timerToast;
function avisar(texto) {
  $toast.textContent = texto;
  $toast.hidden = false;
  clearTimeout(timerToast);
  timerToast = setTimeout(() => { $toast.hidden = true; }, 3200);
}

function carregando(texto) {
  $app.innerHTML = `<div class="carregando"><div><div class="giro"></div>${esc(texto)}<br><small>No primeiro acesso do dia o servidor pode levar até 1 minuto para acordar.</small></div></div>`;
}

function falha(erro, tentar) {
  $app.innerHTML = `<div class="falha"><h2>Não foi possível carregar</h2><p>${esc(erro.message)}</p><button class="botao" id="tentar">Tentar de novo</button></div>`;
  document.getElementById('tentar').onclick = tentar;
}

function modoCelular(ligado) {
  document.body.classList.toggle('modo-celular', ligado);
}


function progressoNivel(aluno) {
  const nivel = aluno.nivel;
  const inicio = nivel <= 1 ? 0 : nivel * 100;
  const proximo = (nivel + 1) * 100;
  return { faltam: proximo - aluno.xpTotal, pct: Math.max(0, Math.min(100, ((aluno.xpTotal - inicio) / (proximo - inicio)) * 100)) };
}

// mesma ideia do grafo.py de Dynamic Programming
function diagnosticar(topicos, exercicios, respostas) {
  const topicoDoExercicio = new Map(exercicios.map(e => [e.id, e.idTopico]));
  const contagem = {};
  for (const r of respostas) {
    const t = topicoDoExercicio.get(r.idExercicio);
    if (t == null) continue;
    contagem[t] ??= { total: 0, certas: 0 };
    contagem[t].total += 1;
    if (r.correta) contagem[t].certas += 1;
  }
  const taxa = id => (contagem[id] ? contagem[id].certas / contagem[id].total : null);
  const fraco = id => taxa(id) !== null && taxa(id) < LIMIAR_DOMINIO;

  const filhos = {};
  for (const t of topicos) {
    if (t.idTopicoPrereq != null) (filhos[t.idTopicoPrereq] ??= []).push(t.id);
  }
  const memo = {};
  const dependentes = id => {
    if (memo[id]) return memo[id];
    const achados = new Set();
    for (const f of filhos[id] || []) {
      achados.add(f);
      dependentes(f).forEach(d => achados.add(d));
    }
    return (memo[id] = achados);
  };

  const porId = {};
  for (const t of topicos) {
    let situacao = 'dominado';
    let mult = 1;
    if (taxa(t.id) === null) situacao = 'nao_avaliado';
    else if (!fraco(t.id)) situacao = 'dominado';
    else if (t.idTopicoPrereq != null && fraco(t.idTopicoPrereq)) { situacao = 'travado'; mult = 2; }
    else {
      situacao = 'lacuna';
      mult = 4 + [...dependentes(t.id)].filter(fraco).length;
    }
    porId[t.id] = { situacao, mult, taxa: taxa(t.id) };
  }
  return { porId, fraco, filhos };
}

function caminhoAte(idTopico, topicos) {
  const porId = new Map(topicos.map(t => [t.id, t]));
  const caminho = [];
  let atual = porId.get(idTopico);
  while (atual) {
    caminho.unshift(atual);
    atual = atual.idTopicoPrereq != null ? porId.get(atual.idTopicoPrereq) : null;
  }
  return caminho;
}

function escolherTrilha(topicos, diag) {
  const lacunas = topicos.filter(t => diag.porId[t.id].situacao === 'lacuna')
    .sort((a, b) => diag.porId[b.id].mult - diag.porId[a.id].mult);
  let alvo;
  if (lacunas.length) {
    const raiz = lacunas[0];
    const travados = (diag.filhos[raiz.id] || []).filter(diag.fraco);
    alvo = travados.length ? travados[0] : (diag.filhos[raiz.id] || [raiz.id])[0];
  } else {
    const naoDominado = topicos.find(t => diag.porId[t.id].situacao !== 'dominado');
    alvo = naoDominado ? naoDominado.id : topicos[topicos.length - 1].id;
  }
  const caminho = caminhoAte(alvo, topicos);
  const indiceAtual = caminho.findIndex(t => diag.porId[t.id].situacao !== 'dominado');
  return { alvo: caminho[caminho.length - 1], caminho, indiceAtual: indiceAtual === -1 ? caminho.length - 1 : indiceAtual };
}

// mochila 0/1: dp[i][k][t] = melhor valor com i exercicios, k vagas e t minutos
function montarMissao(candidatos, tempo, maxEx = MAX_EXERCICIOS) {
  const n = candidatos.length;
  const dp = Array.from({ length: n + 1 }, () => Array.from({ length: maxEx + 1 }, () => new Array(tempo + 1).fill(0)));
  for (let i = 1; i <= n; i++) {
    const ex = candidatos[i - 1];
    for (let k = 0; k <= maxEx; k++) {
      for (let t = 0; t <= tempo; t++) {
        dp[i][k][t] = dp[i - 1][k][t];
        if (k > 0 && ex.tempo <= t) {
          const comEle = dp[i - 1][k - 1][t - ex.tempo] + ex.valor;
          if (comEle > dp[i][k][t]) dp[i][k][t] = comEle;
        }
      }
    }
  }
  const escolhidos = [];
  let k = maxEx;
  let t = tempo;
  for (let i = n; i >= 1; i--) {
    if (dp[i][k][t] !== dp[i - 1][k][t]) {
      const ex = candidatos[i - 1];
      escolhidos.push(ex);
      k -= 1;
      t -= ex.tempo;
    }
  }
  return escolhidos.reverse();
}

function planejarMissao(contexto, tempo) {
  const { topicos, exercicios, diag, trilha } = contexto;
  const disciplina = trilha.alvo.disciplina;
  const ordem = new Map(topicos.map((t, i) => [t.id, i]));
  const candidatos = exercicios
    .filter(e => topicos.find(t => t.id === e.idTopico)?.disciplina === disciplina)
    .map(e => ({ ...e, tempo: e.tempoEstimadoMin, valor: e.xpRecompensa * diag.porId[e.idTopico].mult }));
  return montarMissao(candidatos, tempo)
    .sort((a, b) => (ordem.get(a.idTopico) - ordem.get(b.idTopico)) || a.id - b.id);
}


function telaInicio() {
  modoCelular(false);
  document.title = 'Respawn';
  $app.innerHTML = `
  <main class="inicio">
    <div class="marca">${LOGO}Respawn</div>
    <p class="sub">Microlearning gamificado para reforço escolar de alunos da rede pública: missões curtas que cabem no tempo do aluno, e um painel para o professor ver quem está ficando para trás.</p>
    <p class="frase">"Todo aluno merece um respawn: uma chance de voltar ao jogo de onde parou."</p>
    <div class="perfis">
      <a class="perfil-card" href="#/aluna">
        <span class="tipo">Entrar como aluna</span>
        <h2>Ana Beatriz Silva</h2>
        <p>15 anos, turma 1A-EM · estuda 15 minutos no ônibus</p>
        <span class="ir">Ver a missão do dia ›</span>
      </a>
      <a class="perfil-card" href="#/professora">
        <span class="tipo">Entrar como professora</span>
        <h2>Profa. Helena Souza</h2>
        <p>Matemática · E.E. Prof. Carlos de Moraes, Itaquera</p>
        <span class="ir">Abrir o painel da turma ›</span>
      </a>
    </div>
    <div class="rodape">
      <span><b>Equipe SysCore</b> · Guilherme Moura Gama (RM 562162) · Guilherme Ruiz Costa (RM 563236) · Júlio César Augusto Vieira (RM 563366)</span>
      <span>Global Solution 2026.2 · FIAP · Engenharia de Software · ODS 4 e ODS 10</span>
      <span>Protótipo acadêmico com dados fictícios e login simulado. Os dados vêm da API REST em Java com banco Oracle · <a href="/swagger-ui.html">documentação da API</a></span>
    </div>
  </main>`;
}


async function carregarContextoAluna() {
  const id = PERFIS.aluna.id;
  const [aluno, topicos, exercicios, missoes] = await Promise.all([
    api(`/alunos/${id}`), api('/topicos'), api('/exercicios'), api(`/alunos/${id}/missoes`),
  ]);
  topicos.sort((a, b) => a.id - b.id);
  const listas = await Promise.all(missoes.map(m => api(`/missoes/${m.id}/respostas`)));
  const respostas = listas.flat();
  const diag = diagnosticar(topicos, exercicios, respostas);
  const trilha = escolherTrilha(topicos, diag);
  const deHoje = missoes.filter(m => m.data === hoje()).sort((a, b) => b.id - a.id);
  return { aluno, topicos, exercicios, missoes, respostas, diag, trilha, missaoHoje: deHoje[0] || null };
}

async function telaAluna() {
  modoCelular(true);
  document.title = 'Missão do dia · Respawn';
  carregando('Carregando sua missão...');
  let ctx;
  try {
    ctx = await carregarContextoAluna();
  } catch (erro) {
    return falha(erro, telaAluna);
  }
  const { aluno } = ctx;
  if (estado.tempo == null) {
    estado.tempo = TEMPOS.reduce((melhor, t) => (Math.abs(t - aluno.tempoDiarioMin) < Math.abs(melhor - aluno.tempoDiarioMin) ? t : melhor), 15);
  }
  const prog = progressoNivel(aluno);
  const primeiroNome = aluno.nome.split(' ')[0];

  $app.innerHTML = `
  <div class="celular">
    <header class="topo">
      <div class="linha1">
        <div class="perfil">
          <div class="avatar" aria-hidden="true">${esc(primeiroNome[0])}</div>
          <div><div style="font-size:13px;opacity:.9">${saudacao()}</div><div style="font-size:19px;font-weight:700">${esc(primeiroNome)}</div></div>
        </div>
        <a class="sair" href="#/">Trocar perfil</a>
      </div>
      <div class="stats">
        <div class="stat"><b>Nível ${aluno.nivel}</b>${aluno.xpTotal} XP</div>
        <div class="stat"><b>🔥 ${aluno.streakDias} ${aluno.streakDias === 1 ? 'dia' : 'dias'}</b>sequência</div>
        <div class="stat"><b>${aluno.tempoDiarioMin} min</b>meta diária</div>
      </div>
      <div style="font-size:12px;margin-top:10px;opacity:.92">Faltam ${prog.faltam} XP para o nível ${aluno.nivel + 1}</div>
      <div class="barra" style="background:rgba(255,255,255,.25)"><span style="width:${prog.pct}%;background:#fff"></span></div>
    </header>
    <main class="conteudo" id="corpo-aluna"></main>
    <nav class="nav" aria-label="Menu da aluna">
      <span class="ativo"><span class="ico" aria-hidden="true">🏠</span>Início</span>
      <button data-embreve><span class="ico" aria-hidden="true">🗺️</span>Trilha</button>
      <button data-embreve><span class="ico" aria-hidden="true">🏆</span>Conquistas</button>
      <button data-embreve><span class="ico" aria-hidden="true">👤</span>Perfil</button>
    </nav>
  </div>`;
  $app.querySelectorAll('[data-embreve]').forEach(b => { b.onclick = () => avisar('Essa área entra na próxima versão do Respawn.'); });
  desenharCorpoAluna(ctx);
}

function desenharCorpoAluna(ctx) {
  const { diag, trilha, missaoHoje, exercicios, topicos } = ctx;
  const corpo = document.getElementById('corpo-aluna');
  const emAndamento = missaoHoje && !missaoHoje.concluida;

  const nos = trilha.caminho.map((t, i) => {
    const classe = i < trilha.indiceAtual ? 'feito' : i === trilha.indiceAtual ? 'atual' : 'bloq';
    const marca = classe === 'feito' ? '✓' : String(i + 1);
    return `<div class="no ${classe}"><div class="bola">${marca}</div>${esc(curtoTopico(t.id, t.nome))}</div>`;
  }).join('<div class="seta" aria-hidden="true">›</div>');
  const cartaoTrilha = `
    <section class="card">
      <h2>Sua trilha: ${esc(curtoTopico(trilha.alvo.id, trilha.alvo.nome))}</h2>
      <div class="trilha">${nos}</div>
    </section>`;

  let html = '';
  if (emAndamento) {
    const plano = lerLocal(`respawn.plano.${missaoHoje.id}`) || [];
    const feitos = new Set(ctx.respostas.filter(r => r.idMissao === missaoHoje.id).map(r => r.idExercicio));
    const linhas = plano.map(id => exercicios.find(e => e.id === id)).filter(Boolean).map(e => linhaExercicio(e, diag, feitos.has(e.id))).join('');
    html = `
      <section class="card">
        <h2>Missão em andamento · ${missaoHoje.tempoDisponivelMin} min</h2>
        <div class="rotulo">Você começou uma missão hoje. Continue de onde parou.</div>
        <div style="margin-top:8px">${linhas || '<div class="vazio">Missão aberta em outro aparelho.</div>'}</div>
      </section>
      ${cartaoTrilha}
      <button class="botao" id="continuar">Continuar missão ▸</button>`;
  } else {
    const missao = planejarMissao(ctx, estado.tempo);
    const total = missao.reduce((s, e) => s + e.tempoEstimadoMin, 0);
    const chips = TEMPOS.map(t => `<button class="chip" data-tempo="${t}" aria-pressed="${t === estado.tempo}">${t} min</button>`).join('');
    const concluida = missaoHoje && missaoHoje.concluida
      ? `<section class="card" style="border-color:#B5DEC5;background:var(--verde-claro)"><h2 style="color:var(--verde);margin:0">✓ Missão de hoje concluída · +${missaoHoje.xpGanho} XP</h2><div class="rotulo">Quer treinar mais? Escolha o tempo e faça outra.</div></section>` : '';
    html = `
      ${concluida}
      <section class="card">
        <h2>Quanto tempo você tem agora?</h2>
        <div class="rotulo">A missão cabe no seu tempo, no ônibus ou no intervalo.</div>
        <div class="chips" role="group" aria-label="Tempo disponível">${chips}</div>
      </section>
      ${cartaoTrilha}
      <section class="card">
        <h2>${missaoHoje && missaoHoje.concluida ? 'Nova missão' : 'Missão de hoje'} · ${total} de ${estado.tempo} min</h2>
        ${missao.length ? missao.map(e => linhaExercicio(e, diag, false)).join('') : '<div class="vazio">Nenhum exercício cabe nesse tempo. Escolha mais minutos.</div>'}
      </section>
      <button class="botao" id="comecar" ${missao.length ? '' : 'disabled'}>Começar missão ▸</button>`;
    corpo.innerHTML = html;
    corpo.querySelectorAll('[data-tempo]').forEach(b => {
      b.onclick = () => { estado.tempo = Number(b.dataset.tempo); desenharCorpoAluna(ctx); };
    });
    document.getElementById('comecar').onclick = e => comecarMissao(e.currentTarget, ctx, missao);
    return;
  }
  corpo.innerHTML = html;
  document.getElementById('continuar').onclick = () => { location.hash = `#/missao/${missaoHoje.id}`; };
}

function linhaExercicio(ex, diag, feito) {
  const revisao = diag.porId[ex.idTopico]?.situacao === 'dominado' ? 'Revisão · ' : '';
  return `<div class="ex${feito ? ' feito' : ''}"><div>${feito ? '✓ ' : ''}${esc(tituloExercicio(ex))}<small>${revisao}${esc(curtoTopico(ex.idTopico))} · ${ex.tempoEstimadoMin} min</small></div><span class="xp">+${ex.xpRecompensa} XP</span></div>`;
}

async function comecarMissao(botao, ctx, missao) {
  botao.disabled = true;
  botao.textContent = 'Criando a missão...';
  try {
    const criada = await post('/missoes', { idAluno: ctx.aluno.id, tempoDisponivelMin: estado.tempo });
    gravarLocal(`respawn.plano.${criada.id}`, missao.map(e => e.id));
    gravarLocal(`respawn.inicio.${criada.id}`, { nivel: ctx.aluno.nivel, xp: ctx.aluno.xpTotal });
    location.hash = `#/missao/${criada.id}`;
  } catch (erro) {
    avisar(erro.message);
    botao.disabled = false;
    botao.textContent = 'Começar missão ▸';
  }
}


async function telaMissao(idMissao) {
  modoCelular(true);
  document.title = 'Exercício · Respawn';
  carregando('Abrindo a missão...');
  let missao, aluno, exercicios, respostas;
  try {
    missao = await api(`/missoes/${idMissao}`);
    [aluno, exercicios, respostas] = await Promise.all([
      api(`/alunos/${missao.idAluno}`), api('/exercicios'), api(`/missoes/${idMissao}/respostas`),
    ]);
  } catch (erro) {
    return falha(erro, () => telaMissao(idMissao));
  }
  if (missao.concluida) return telaResumo(missao, aluno, respostas);

  const respondidos = new Set(respostas.map(r => r.idExercicio));
  const plano = lerLocal(`respawn.plano.${idMissao}`) || [...respondidos];
  const pendentes = plano.filter(id => !respondidos.has(id));
  if (!pendentes.length) return concluirMissao(missao, aluno, respostas);

  const ex = exercicios.find(e => e.id === pendentes[0]);
  const gasto = respostas.reduce((s, r) => s + r.tempoGastoMin, 0);
  const numero = plano.indexOf(ex.id) + 1;
  const conteudo = CONTEUDO[ex.id];
  const passos = plano.map((id, i) => `<span class="${i < numero - 1 ? 'ok' : ''}"></span>`).join('');
  const alternativas = conteudo
    ? conteudo.alternativas.map((a, i) => `<button class="alt" data-i="${i}"><span class="letra">${'ABCD'[i]}</span>${esc(a)}</button>`).join('')
    : `<button class="alt" data-auto="1"><span class="letra">✓</span>Acertei</button><button class="alt" data-auto="0"><span class="letra">✗</span>Errei</button>`;

  $app.innerHTML = `
  <div class="celular">
    <header class="topo">
      <div class="linha1">
        <a class="sair" href="#/aluna">✕ Sair</a>
        <span>⏱ ${missao.tempoDisponivelMin - gasto} min restantes</span>
      </div>
      <h1>Exercício ${numero} de ${plano.length}</h1>
      <div class="passos" aria-hidden="true">${passos}</div>
    </header>
    <main class="conteudo">
      <span class="selo">${esc(curtoTopico(ex.idTopico))} · ${esc(DIFICULDADES[ex.dificuldade] || ex.dificuldade)}</span>
      ${conteudo?.apoio ? `<div class="texto-apoio">${esc(conteudo.apoio)}</div>` : ''}
      <p class="enunciado">${esc(conteudo ? conteudo.enunciado : ex.enunciado)}</p>
      <div class="alts">${alternativas}</div>
      <div id="retorno"></div>
    </main>
  </div>`;

  $app.querySelectorAll('.alt').forEach(botao => {
    botao.onclick = () => responder(botao, { missao, ex, conteudo, ultimo: pendentes.length === 1 });
  });
}

async function responder(botao, { missao, ex, conteudo, ultimo }) {
  const botoes = [...$app.querySelectorAll('.alt')];
  botoes.forEach(b => { b.disabled = true; });
  const correta = conteudo ? Number(botao.dataset.i) === conteudo.certa : botao.dataset.auto === '1';
  try {
    await post(`/missoes/${missao.id}/respostas`, { idExercicio: ex.id, correta, tempoGastoMin: ex.tempoEstimadoMin });
  } catch (erro) {
    botoes.forEach(b => { b.disabled = false; });
    return avisar(erro.message);
  }
  const marcar = (b, classe) => {
    b.classList.add(classe);
    b.querySelector('.letra').textContent = classe === 'certa' ? '✓' : '✗';
  };
  if (conteudo) {
    marcar(botoes[conteudo.certa], 'certa');
    if (!correta) marcar(botao, 'errada');
  } else {
    marcar(botao, correta ? 'certa' : 'errada');
  }
  let atual = missao;
  let aluno = null;
  try {
    [atual, aluno] = await Promise.all([api(`/missoes/${missao.id}`), api(`/alunos/${missao.idAluno}`)]);
  } catch (_) { /* o retorno mostra só o que já sabemos */ }

  const titulo = correta ? `Acertou! +${ex.xpRecompensa} XP` : `Quase! A resposta certa é ${conteudo ? 'ABCD'[conteudo.certa] : 'outra'}.`;
  const explicacao = conteudo ? conteudo.explicacao : '';
  document.getElementById('retorno').innerHTML = `
    <div class="retorno ${correta ? 'ok' : 'erro'}">
      <h3>${esc(titulo)}</h3>
      ${explicacao ? `<p>${esc(explicacao)}</p>` : ''}
      <div class="linha"><span>XP da missão</span><b>${atual.xpGanho} XP</b></div>
      ${aluno ? `<div class="linha"><span>Sequência</span><b>🔥 ${aluno.streakDias} ${aluno.streakDias === 1 ? 'dia' : 'dias'}</b></div>` : ''}
    </div>
    <button class="botao" id="proximo" style="margin-top:12px">${ultimo ? 'Concluir missão ▸' : 'Próximo exercício ▸'}</button>`;
  const proximo = document.getElementById('proximo');
  proximo.focus();
  proximo.onclick = () => telaMissao(missao.id);
}

async function concluirMissao(missao, aluno, respostas) {
  carregando('Concluindo a missão...');
  try {
    const concluida = await post(`/missoes/${missao.id}/concluir`);
    const atualizado = await api(`/alunos/${aluno.id}`);
    telaResumo(concluida, atualizado, respostas);
  } catch (erro) {
    falha(erro, () => telaMissao(missao.id));
  }
}

function telaResumo(missao, aluno, respostas) {
  const acertos = respostas.filter(r => r.correta).length;
  const inicio = lerLocal(`respawn.inicio.${missao.id}`);
  const subiu = inicio && aluno.nivel > inicio.nivel;
  $app.innerHTML = `
  <div class="celular">
    <header class="topo"><div class="linha1"><a class="sair" href="#/aluna">✕ Fechar</a></div><h1>Missão concluída!</h1></header>
    <main class="conteudo">
      <section class="card resumo">
        <div class="rotulo">Você ganhou</div>
        <div class="grande">+${missao.xpGanho} XP</div>
        ${subiu ? `<div class="subiu">Subiu para o nível ${aluno.nivel}!</div>` : ''}
        <div class="numeros">
          <div><b>${acertos} de ${respostas.length}</b>acertos</div>
          <div><b>Nível ${aluno.nivel}</b>${aluno.xpTotal} XP</div>
          <div><b>🔥 ${aluno.streakDias}</b>${aluno.streakDias === 1 ? 'dia seguido' : 'dias seguidos'}</div>
        </div>
      </section>
      <p class="rotulo" style="text-align:center">Amanhã tem missão nova. Volte para manter a sequência!</p>
      <a class="botao" href="#/aluna">Voltar ao início</a>
    </main>
  </div>`;
}


async function telaProfessora(silencioso = false) {
  modoCelular(false);
  document.title = 'Painel da turma · Respawn';
  if (!silencioso) carregando('Carregando o painel da turma...');
  let d;
  try {
    const idTurma = PERFIS.professora.turma;
    const [turma, alunos, erros, inativos, topicos, exercicios] = await Promise.all([
      api(`/turmas/${idTurma}`), api(`/turmas/${idTurma}/alunos`), api(`/turmas/${idTurma}/erros-por-topico`),
      api(`/turmas/${idTurma}/alunos-inativos?dias=${estado.dias}`), api('/topicos'), api('/exercicios'),
    ]);
    const [escola, professor] = await Promise.all([api(`/escolas/${turma.idEscola}`), api(`/professores/${turma.idProfessor}`)]);
    d = { turma, alunos, erros, inativos, topicos: topicos.sort((a, b) => a.id - b.id), exercicios, escola, professor };
  } catch (erro) {
    if (silencioso) return avisar(erro.message);
    return falha(erro, () => telaProfessora());
  }

  const menu = [
    ['painel', '📊', 'Painel da turma'], ['alunos', '👥', 'Alunos'], ['topicos', '🗺️', 'Tópicos e trilhas'], ['exercicios', '📝', 'Exercícios'],
  ].map(([id, ico, rotulo]) => `<button class="menu-item" data-aba="${id}" ${estado.aba === id ? 'aria-current="page"' : ''}><span aria-hidden="true">${ico}</span>${rotulo}</button>`).join('');
  const disciplina = DISCIPLINAS[d.professor.disciplina] || d.professor.disciplina;

  $app.innerHTML = `
  <div class="painel">
    <aside class="lateral">
      <div class="marca">${LOGO}Respawn</div>
      <nav aria-label="Menu da professora" style="display:contents">${menu}</nav>
      <div class="quem"><b>${esc(PERFIS.professora.tratamento)} ${esc(d.professor.nome)}</b>${esc(disciplina)}<br><a href="#/">Trocar perfil</a></div>
    </aside>
    <main class="principal">
      <div class="cabecalho">
        <div>
          <div class="escola">${esc(d.escola.nome)} · ${esc(d.escola.bairro)}</div>
          <h1>Turma ${esc(d.turma.nome)} · ${esc(disciplina)}</h1>
        </div>
        <div class="acoes">
          <label class="visualmente-oculto" for="dias">Período da busca ativa</label>
          <select class="seletor" id="dias">
            ${[7, 14, 30].map(n => `<option value="${n}" ${n === estado.dias ? 'selected' : ''}>Últimos ${n} dias</option>`).join('')}
          </select>
          <button class="btn-borda" id="atualizar">↻ Atualizar</button>
        </div>
      </div>
      <div id="aba"></div>
    </main>
  </div>`;

  $app.querySelectorAll('[data-aba]').forEach(b => {
    b.onclick = () => { estado.aba = b.dataset.aba; telaProfessora(true); };
  });
  document.getElementById('dias').onchange = e => { estado.dias = Number(e.target.value); telaProfessora(true); };
  document.getElementById('atualizar').onclick = async e => {
    e.currentTarget.disabled = true;
    await telaProfessora(true);
    avisar('Painel atualizado com os dados mais recentes.');
  };

  const aba = document.getElementById('aba');
  if (estado.aba === 'alunos') aba.innerHTML = abaAlunos(d);
  else if (estado.aba === 'topicos') aba.innerHTML = abaTopicos(d);
  else if (estado.aba === 'exercicios') aba.innerHTML = abaExercicios(d);
  else {
    aba.innerHTML = abaPainel(d);
    ligarConversas(d, disciplina);
  }
}

function corDoErro(pct) {
  if (pct >= 60) return 'var(--vermelho)';
  if (pct >= 30) return 'var(--laranja)';
  return 'var(--verde)';
}

function abaPainel(d) {
  const inativos = new Set(d.inativos.map(a => a.id));
  const ativos = d.alunos.filter(a => !inativos.has(a.id)).sort((a, b) => b.streakDias - a.streakDias);
  const erros = [...d.erros].sort((a, b) => (b.percentualErro - a.percentualErro) || (b.totalErros - a.totalErros));
  const pior = erros.find(e => e.totalErros > 0);
  const maiorSeq = d.alunos.reduce((m, a) => Math.max(m, a.streakDias), 0);

  let dica = '';
  if (pior) {
    const topico = d.topicos.find(t => t.id === pior.idTopico);
    const pre = topico && topico.idTopicoPrereq != null ? d.erros.find(e => e.idTopico === topico.idTopicoPrereq && e.totalErros > 0) : null;
    dica = pre
      ? `💡 Sugestão: revisar <b>${esc(curtoTopico(pre.idTopico).toLowerCase())}</b> antes de ${esc(curtoTopico(pior.idTopico).toLowerCase())}: é o pré-requisito na trilha.`
      : `💡 Sugestão: reforçar <b>${esc(curtoTopico(pior.idTopico).toLowerCase())}</b> com a turma nesta semana.`;
  }

  const linhasErro = erros.map(e => {
    const pct = e.percentualErro ?? 0;
    const cor = corDoErro(pct);
    return `<tr><td><b>${esc(nomeTopico(e.idTopico, e.topico))}</b></td><td class="num">${e.totalRespostas}</td><td class="num">${e.totalErros}</td>
      <td><div class="erro-barra"><div class="barra"><span style="width:${Math.max(pct, 2)}%;background:${cor}"></span></div><b style="color:${cor}">${porcento(pct)}</b></div></td></tr>`;
  }).join('');

  const linhaInativo = a => `
    <div class="aluno-linha" style="flex-wrap:wrap">
      <div><b>${esc(a.nome)}</b><small>Último acesso: ${dataCurta(a.ultimoAcesso)} · sequência ${a.streakDias} · nível ${a.nivel}</small></div>
      <button class="btn-conversar" data-conversar="${a.id}" aria-expanded="${estado.abertas.has(a.id)}">Conversar</button>
      <div class="mensagem" id="msg-${a.id}" style="width:100%" ${estado.abertas.has(a.id) ? '' : 'hidden'}></div>
    </div>`;
  const linhaAtivo = a => `
    <div class="aluno-linha">
      <div><b>${esc(a.nome)}</b><small>Último acesso: ${dataCurta(a.ultimoAcesso)} · 🔥 ${a.streakDias} ${a.streakDias === 1 ? 'dia' : 'dias'} · nível ${a.nivel}</small></div>
      <span class="etiqueta">Em dia</span>
    </div>`;

  return `
    <div class="kpis">
      <div class="kpi"><div class="rotulo">Alunos na turma</div><div class="valor">${d.alunos.length}</div></div>
      <div class="kpi"><div class="rotulo">Estudaram nos últimos ${estado.dias} dias</div><div class="valor">${ativos.length}</div></div>
      <div class="kpi"><div class="rotulo">Tópico com mais erros</div><div class="valor texto">${pior ? esc(curtoTopico(pior.idTopico)) : 'Sem erros'}</div></div>
      <div class="kpi"><div class="rotulo">Maior sequência</div><div class="valor">🔥 ${maiorSeq} ${maiorSeq === 1 ? 'dia' : 'dias'}</div></div>
    </div>
    <div class="colunas">
      <section class="card">
        <h2>Onde a turma mais erra</h2>
        ${erros.length ? `<div style="overflow-x:auto"><table class="tabela"><thead><tr><th>Tópico</th><th class="num">Respostas</th><th class="num">Erros</th><th>% de erro</th></tr></thead><tbody>${linhasErro}</tbody></table></div>` : '<div class="vazio">A turma ainda não respondeu exercícios.</div>'}
        ${dica ? `<p class="dica">${dica}</p>` : ''}
      </section>
      <section class="card">
        <h2>Busca ativa · sem estudar há ${estado.dias}+ dias</h2>
        ${d.inativos.length ? d.inativos.map(linhaInativo).join('') : '<div class="vazio">Ninguém parado. A turma toda estudou no período.</div>'}
        <div class="secao-titulo">Alunos ativos</div>
        ${ativos.length ? ativos.map(linhaAtivo).join('') : '<div class="vazio">Nenhum aluno ativo no período.</div>'}
      </section>
    </div>`;
}

function ligarConversas(d, disciplina) {
  const professor = `${PERFIS.professora.tratamento} ${d.professor.nome}`;
  const preencher = (id) => {
    const aluno = d.inativos.find(a => a.id === id);
    const caixa = document.getElementById(`msg-${id}`);
    const texto = `Oi, ${aluno.nome.split(' ')[0]}! Sentimos sua falta no Respawn. Que tal uma missão de 5 minutos hoje? Sua trilha de ${disciplina} continua de onde você parou. Conte comigo! ${professor}`;
    caixa.innerHTML = `<label for="txt-${id}">Mensagem sugerida para enviar ao aluno ou à família:</label>
      <textarea id="txt-${id}">${esc(texto)}</textarea>
      <button class="btn-borda" data-copiar="${id}">Copiar mensagem</button>`;
    caixa.querySelector('[data-copiar]').onclick = async () => {
      const valor = document.getElementById(`txt-${id}`).value;
      try { await navigator.clipboard.writeText(valor); avisar('Mensagem copiada. Cole no canal da escola.'); } catch (_) { avisar('Selecione o texto e copie manualmente.'); }
    };
  };
  $app.querySelectorAll('[data-conversar]').forEach(botao => {
    const id = Number(botao.dataset.conversar);
    if (estado.abertas.has(id)) preencher(id);
    botao.onclick = () => {
      const caixa = document.getElementById(`msg-${id}`);
      const abrir = caixa.hidden;
      caixa.hidden = !abrir;
      botao.setAttribute('aria-expanded', String(abrir));
      if (abrir) { estado.abertas.add(id); preencher(id); } else { estado.abertas.delete(id); }
    };
  });
}

function abaAlunos(d) {
  const inativos = new Set(d.inativos.map(a => a.id));
  const linhas = [...d.alunos].sort((a, b) => a.nome.localeCompare(b.nome)).map(a => `
    <tr><td><b>${esc(a.nome)}</b></td><td class="num">${a.idade}</td><td class="num">${a.tempoDiarioMin} min</td><td class="num">${a.xpTotal}</td>
    <td class="num">${a.nivel}</td><td class="num">${a.streakDias}</td><td>${dataCurta(a.ultimoAcesso)}</td>
    <td>${inativos.has(a.id) ? '<span class="etiqueta" style="background:var(--vermelho-claro);color:var(--vermelho)">Busca ativa</span>' : '<span class="etiqueta">Em dia</span>'}</td></tr>`).join('');
  return `<section class="card" style="margin-top:18px"><h2>Alunos da turma ${esc(d.turma.nome)}</h2>
    <div style="overflow-x:auto"><table class="tabela"><thead><tr><th>Aluno</th><th class="num">Idade</th><th class="num">Meta diária</th><th class="num">XP</th><th class="num">Nível</th><th class="num">Sequência</th><th>Último acesso</th><th>Situação</th></tr></thead>
    <tbody>${linhas}</tbody></table></div></section>`;
}

function abaTopicos(d) {
  const erroDe = id => d.erros.find(e => e.idTopico === id);
  const cadeias = [];
  const folhas = d.topicos.filter(t => !d.topicos.some(o => o.idTopicoPrereq === t.id));
  for (const folha of folhas) cadeias.push(caminhoAte(folha.id, d.topicos));
  const porDisciplina = {};
  cadeias.forEach(c => (porDisciplina[c[0].disciplina] ??= []).push(c));
  const blocos = Object.entries(porDisciplina).map(([disc, lista]) => `
    <div class="secao-titulo">${esc(DISCIPLINAS[disc] || disc)}</div>
    ${lista.map(c => `<div class="cadeia">${c.map(t => {
      const e = erroDe(t.id);
      return `<span class="topico">${esc(nomeTopico(t.id, t.nome))}<small>${e ? `${porcento(e.percentualErro ?? 0)} de erro na turma` : 'sem respostas ainda'}</small></span>`;
    }).join('<span aria-hidden="true">›</span>')}</div>`).join('')}`).join('');
  return `<section class="card" style="margin-top:18px"><h2>Trilhas de pré-requisitos</h2>
    <p class="rotulo">Cada seta indica um pré-requisito: o aluno precisa dominar o tópico da esquerda antes do da direita.</p>
    <div class="grafo">${blocos}</div></section>`;
}

function abaExercicios(d) {
  const linhas = [...d.exercicios].sort((a, b) => a.idTopico - b.idTopico || a.id - b.id).map(e => `
    <tr><td><b>${esc(tituloExercicio(e))}</b></td><td>${esc(nomeTopico(e.idTopico))}</td><td>${esc(DIFICULDADES[e.dificuldade] || e.dificuldade)}</td>
    <td class="num">${e.tempoEstimadoMin} min</td><td class="num">${e.xpRecompensa} XP</td></tr>`).join('');
  return `<section class="card" style="margin-top:18px"><h2>Catálogo de exercícios</h2>
    <div style="overflow-x:auto"><table class="tabela"><thead><tr><th>Exercício</th><th>Tópico</th><th>Dificuldade</th><th class="num">Tempo</th><th class="num">Recompensa</th></tr></thead>
    <tbody>${linhas}</tbody></table></div></section>`;
}


function rotear() {
  const rota = location.hash.replace(/^#\/?/, '');
  window.scrollTo(0, 0);
  const missao = rota.match(/^missao\/(\d+)$/);
  if (missao) return telaMissao(Number(missao[1]));
  if (rota === 'aluna') return telaAluna();
  if (rota === 'professora') {
    estado.aba = 'painel';
    return telaProfessora();
  }
  return telaInicio();
}

window.addEventListener('hashchange', rotear);
window.addEventListener('focus', () => {
  if (location.hash === '#/professora') telaProfessora(true);
});
rotear();
