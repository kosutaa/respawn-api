package br.com.fiap.respawn.bo;

import br.com.fiap.respawn.dao.EscolaDAO;
import br.com.fiap.respawn.exception.RecursoNaoEncontradoException;
import br.com.fiap.respawn.model.Escola;
import org.springframework.stereotype.Service;

import java.util.List;

/** Regras de negocio de escolas. */
@Service
public class EscolaBO {

    private final EscolaDAO escolaDAO;

    public EscolaBO(EscolaDAO escolaDAO) {
        this.escolaDAO = escolaDAO;
    }

    public List<Escola> listar() {
        return escolaDAO.listar();
    }

    public Escola buscar(Long id) {
        return escolaDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Escola", id));
    }

    public Escola cadastrar(Escola escola) {
        escola.setId(null);
        return escolaDAO.inserir(escola);
    }

    public Escola atualizar(Long id, Escola escola) {
        buscar(id);
        escola.setId(id);
        escolaDAO.atualizar(escola);
        return escola;
    }

    public void excluir(Long id) {
        buscar(id);
        escolaDAO.excluir(id);
    }
}
