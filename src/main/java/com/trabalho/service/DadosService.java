package com.trabalho.service;

import com.trabalho.dao.AutoriaDAO;
import com.trabalho.dao.CoordenadorDAO;
import com.trabalho.dao.FinanciamentoDAO;
import com.trabalho.dao.HistoricoProjDAO;
import com.trabalho.dao.ParticipanteDAO;
import com.trabalho.dao.ProducaoDAO;
import com.trabalho.dao.ProjetoDAO;
import com.trabalho.dao.UsuarioDAO;
import com.trabalho.model.Autoria;
import com.trabalho.model.Coordenador;
import com.trabalho.model.Financiamento;
import com.trabalho.model.HistoricoProj;
import com.trabalho.model.Participante;
import com.trabalho.model.Producao;
import com.trabalho.model.Projeto;
import com.trabalho.model.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Serviço da interface; todas as leituras e gravações são feitas pelos DAO's MySQL. */
public class DadosService {
    private static DadosService instance;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final CoordenadorDAO coordenadorDAO = new CoordenadorDAO();
    private final ParticipanteDAO participanteDAO = new ParticipanteDAO();
    private final ProjetoDAO projetoDAO = new ProjetoDAO();
    private final HistoricoProjDAO historicoDAO = new HistoricoProjDAO();
    private final FinanciamentoDAO financiamentoDAO = new FinanciamentoDAO();
    private final ProducaoDAO producaoDAO = new ProducaoDAO();
    private final AutoriaDAO autoriaDAO = new AutoriaDAO();

    // Os ObservableLists permanecem para alimentar as TableViews e ComboBoxes JavaFX.
    private final ObservableList<Usuario> usuarios = FXCollections.observableArrayList();
    private final ObservableList<Coordenador> coordenadores = FXCollections.observableArrayList();
    private final ObservableList<Participante> participantes = FXCollections.observableArrayList();
    private final ObservableList<Projeto> projetos = FXCollections.observableArrayList();
    private final ObservableList<HistoricoProj> historicoProjetos = FXCollections.observableArrayList();
    private final ObservableList<Financiamento> financiamentos = FXCollections.observableArrayList();
    private final ObservableList<Producao> producoes = FXCollections.observableArrayList();
    private final ObservableList<Autoria> autorias = FXCollections.observableArrayList();

    private Usuario usuarioLogado;
    private String erroInicializacao;

    private DadosService() {
        inicializarBanco();
    }

    public static synchronized DadosService getInstance() {
        if (instance == null) instance = new DadosService();
        return instance;
    }

    private void inicializarBanco() {
        try {
            recarregarDados();
        } catch (SQLException e) {
            erroInicializacao = e.getMessage();
            System.err.println("Não foi possível inicializar os dados do MySQL: " + e.getMessage());
        }
    }

    private void recarregarDados() throws SQLException {
        usuarios.setAll(usuarioDAO.listarTodos());
        coordenadores.setAll(coordenadorDAO.listarTodos());
        participantes.setAll(participanteDAO.listarTodos());
        projetos.setAll(projetoDAO.listarTodos());
        historicoProjetos.setAll(historicoDAO.listarTodos());
        financiamentos.setAll(financiamentoDAO.listarTodos());
        producoes.setAll(producaoDAO.listarTodos());
        autorias.setAll(autoriaDAO.listarTodos());
        erroInicializacao = null;
    }

    public String getErroInicializacao() {
        return erroInicializacao;
    }

    private static IllegalStateException erroBanco(SQLException e) {
        return new IllegalStateException("Falha ao acessar o banco de dados: " + e.getMessage(), e);
    }

    public Usuario autenticar(String email, String senha) {
        if (email == null || senha == null) return null;
        try {
            Usuario usuario = usuarioDAO.buscarPorEmail(email.trim()).orElse(null);
            if (usuario != null && usuario.autenticar(email, senha)) {
                usuarioLogado = usuario;
                return usuario;
            }
            return null;
        } catch (SQLException e) {
            throw erroBanco(e);
        }
    }

    public Usuario getUsuarioLogado() { return usuarioLogado; }
    public void setUsuarioLogado(Usuario usuarioLogado) { this.usuarioLogado = usuarioLogado; }
    public void logout() { usuarioLogado = null; }

    public ObservableList<Projeto> getProjetos() { return projetos; }
    public ObservableList<Participante> getParticipantes() { return participantes; }
    public ObservableList<HistoricoProj> getHistoricoProjetos() { return historicoProjetos; }
    public ObservableList<Financiamento> getFinanciamentos() { return financiamentos; }
    public ObservableList<Producao> getProducoes() { return producoes; }
    public ObservableList<Autoria> getAutorias() { return autorias; }
    public ObservableList<Coordenador> getCoordenadores() { return coordenadores; }

    public int gerarProximoIdProjeto() {
        return projetos.stream().mapToInt(Projeto::getIdProjeto).max().orElse(0) + 1;
    }

    public boolean salvarProjeto(Projeto projeto) {
        try {
            boolean existe = projeto.getIdProjeto() > 0 && projetoDAO.buscarPorId(projeto.getIdProjeto()).isPresent();
            boolean sucesso;
            if (existe) sucesso = projetoDAO.atualizar(projeto);
            else {
                projeto.setIdProjeto(0);
                sucesso = projetoDAO.inserir(projeto);
            }
            if (sucesso) substituirOuAdicionar(projetos, projeto, p -> p.getIdProjeto() == projeto.getIdProjeto());
            return sucesso;
        } catch (SQLException e) { throw erroBanco(e); }
    }

    public Projeto buscarProjetoPorId(int idProjeto) {
        return projetos.stream().filter(p -> p.getIdProjeto() == idProjeto).findFirst().orElse(null);
    }

    public boolean deletarProjeto(int idProjeto) {
        try {
            boolean sucesso = projetoDAO.deletar(idProjeto);
            if (sucesso) projetos.removeIf(p -> p.getIdProjeto() == idProjeto);
            return sucesso;
        } catch (SQLException e) { throw erroBanco(e); }
    }

    public boolean salvarParticipante(Participante participante) {
        try {
            boolean existe = participanteDAO.buscarPorCpf(limparCpf(participante.getCpfColaborador())).isPresent();
            boolean sucesso = existe ? participanteDAO.atualizar(participante) : participanteDAO.inserir(participante);
            if (sucesso) substituirOuAdicionar(participantes, participante,
                    p -> limparCpf(p.getCpfColaborador()).equals(limparCpf(participante.getCpfColaborador())));
            return sucesso;
        } catch (SQLException e) { throw erroBanco(e); }
    }

    public Participante buscarParticipantePorCpf(String cpf) {
        if (cpf == null) return null;
        String limpo = limparCpf(cpf);
        Participante cache = participantes.stream().filter(p -> limparCpf(p.getCpfColaborador()).equals(limpo)).findFirst().orElse(null);
        if (cache != null) return cache;
        try {
            return participanteDAO.buscarPorCpf(limpo).orElse(null);
        } catch (SQLException e) { throw erroBanco(e); }
    }

    public boolean salvarVinculo(HistoricoProj vinculo) {
        try {
            HistoricoProj existente = historicoProjetos.stream()
                    .filter(h -> h.getIdProjeto() == vinculo.getIdProjeto()
                            && limparCpf(h.getCpfColaborador()).equals(limparCpf(vinculo.getCpfColaborador())))
                    .findFirst().orElse(null);
            boolean sucesso = existente == null ? historicoDAO.inserir(vinculo) : historicoDAO.atualizar(vinculo);
            if (sucesso) substituirOuAdicionar(historicoProjetos, vinculo,
                    h -> h.getIdProjeto() == vinculo.getIdProjeto()
                            && limparCpf(h.getCpfColaborador()).equals(limparCpf(vinculo.getCpfColaborador())));
            return sucesso;
        } catch (SQLException e) { throw erroBanco(e); }
    }

    public boolean deletarVinculo(int idProjeto, String cpf) {
        try {
            boolean sucesso = historicoDAO.deletar(idProjeto, limparCpf(cpf));
            if (sucesso) historicoProjetos.removeIf(h -> h.getIdProjeto() == idProjeto && limparCpf(h.getCpfColaborador()).equals(limparCpf(cpf)));
            return sucesso;
        } catch (SQLException e) { throw erroBanco(e); }
    }

    public ObservableList<Financiamento> getFinanciamentosDisponiveis() { return financiamentos; }

    public Financiamento buscarFinanciamentoPorProjeto(int idProjeto) {
        return financiamentos.stream().filter(f -> f.getIdProjeto() == idProjeto).findFirst().orElse(null);
    }

    public boolean salvarFinanciamento(Financiamento financiamento) {
        try {
            boolean existe = financiamentoDAO.buscarPorProjeto(financiamento.getIdProjeto()).isPresent();
            boolean sucesso = existe ? financiamentoDAO.atualizar(financiamento) : financiamentoDAO.inserir(financiamento);
            if (sucesso) substituirOuAdicionar(financiamentos, financiamento, f -> f.getIdProjeto() == financiamento.getIdProjeto());
            return sucesso;
        } catch (SQLException e) { throw erroBanco(e); }
    }

    public boolean deletarFinanciamento(int idProjeto) {
        try {
            boolean sucesso = financiamentoDAO.deletar(idProjeto);
            if (sucesso) financiamentos.removeIf(f -> f.getIdProjeto() == idProjeto);
            return sucesso;
        } catch (SQLException e) { throw erroBanco(e); }
    }

    public int gerarProximoIdProducao() {
        return producoes.stream().mapToInt(Producao::getIdProducao).max().orElse(0) + 1;
    }

    public boolean salvarProducao(Producao producao) {
        try {
            boolean existe = producao.getIdProducao() > 0 && producaoDAO.buscarPorId(producao.getIdProducao()).isPresent();
            boolean sucesso;
            if (existe) sucesso = producaoDAO.atualizar(producao);
            else {
                producao.setIdProducao(0);
                sucesso = producaoDAO.inserir(producao);
            }
            if (sucesso) substituirOuAdicionar(producoes, producao, p -> p.getIdProducao() == producao.getIdProducao());
            return sucesso;
        } catch (SQLException e) { throw erroBanco(e); }
    }

    public boolean salvarAutoria(Autoria autoria) {
        try {
            boolean existe = autorias.stream().anyMatch(a -> a.getIdProducao() == autoria.getIdProducao()
                    && limparCpf(a.getCpfParticip()).equals(limparCpf(autoria.getCpfParticip())));
            if (existe) return true;
            boolean sucesso = autoriaDAO.inserir(autoria);
            if (sucesso) autorias.add(autoria);
            return sucesso;
        } catch (SQLException e) { throw erroBanco(e); }
    }

    public boolean removerAutoria(Autoria autoria) {
        try {
            boolean sucesso = autoriaDAO.deletar(autoria.getIdProducao(), limparCpf(autoria.getCpfParticip()));
            if (sucesso) autorias.removeIf(a -> a.getIdProducao() == autoria.getIdProducao()
                    && limparCpf(a.getCpfParticip()).equals(limparCpf(autoria.getCpfParticip())));
            return sucesso;
        } catch (SQLException e) { throw erroBanco(e); }
    }

    public void substituirAutoresDaProducao(int idProducao, List<String> cpfsAutores) {
        try {
            List<String> cpfsNormalizados = cpfsAutores == null ? null
                    : cpfsAutores.stream().map(DadosService::limparCpf).distinct().collect(Collectors.toList());
            autoriaDAO.substituirPorProducao(idProducao, cpfsNormalizados);
            autorias.removeIf(a -> a.getIdProducao() == idProducao);
            if (cpfsNormalizados != null) {
                for (String cpf : cpfsNormalizados) {
                    autorias.add(new Autoria(idProducao, cpf));
                }
            }
        } catch (SQLException e) { throw erroBanco(e); }
    }

    public boolean deletarProducao(int idProducao) {
        try {
            autoriaDAO.deletarPorProducao(idProducao);
            boolean sucesso = producaoDAO.deletar(idProducao);
            if (sucesso) {
                autorias.removeIf(a -> a.getIdProducao() == idProducao);
                producoes.removeIf(p -> p.getIdProducao() == idProducao);
            }
            return sucesso;
        } catch (SQLException e) { throw erroBanco(e); }
    }

    public List<Participante> listarAutoresDaProducao(int idProducao) {
        List<Participante> lista = new ArrayList<>();
        for (Autoria autoria : autorias) {
            if (autoria.getIdProducao() == idProducao) {
                Participante participante = buscarParticipantePorCpf(autoria.getCpfParticip());
                if (participante != null) lista.add(participante);
            }
        }
        return lista;
    }

    public List<Projeto> consultarProjetosPorDocente(String termoDocente) {
        if (termoDocente == null || termoDocente.trim().isEmpty()) return new ArrayList<>(projetos);
        String termo = termoDocente.toLowerCase().trim();
        return projetos.stream().filter(p -> (p.getIdCoordenador() != null && p.getIdCoordenador().toLowerCase().contains(termo))
                || (p.getNomeCoordenador() != null && p.getNomeCoordenador().toLowerCase().contains(termo)))
                .collect(Collectors.toList());
    }

    public List<HistoricoProj> consultarParticipantesPorProjeto(int idProjeto) {
        return historicoProjetos.stream().filter(h -> h.getIdProjeto() == idProjeto).collect(Collectors.toList());
    }

    public List<HistoricoProj> consultarHistoricoPorParticipante(String cpf) {
        String limpo = limparCpf(cpf);
        return historicoProjetos.stream().filter(h -> limparCpf(h.getCpfColaborador()).equals(limpo)).collect(Collectors.toList());
    }

    public List<Producao> consultarProducoesPorProjeto(int idProjeto) {
        return producoes.stream().filter(p -> p.getIdProjeto() == idProjeto).collect(Collectors.toList());
    }

    public Financiamento consultarFinanciamentoPorProjeto(int idProjeto) {
        return buscarFinanciamentoPorProjeto(idProjeto);
    }

    public String buscarNomeCoordenador(String cpfOuNome) {
        if (cpfOuNome == null) return "Não atribuído";
        return coordenadores.stream()
                .filter(c -> c.getCpf().equalsIgnoreCase(cpfOuNome) || c.getNome().equalsIgnoreCase(cpfOuNome))
                .map(Coordenador::getNome).findFirst().orElse(cpfOuNome);
    }

    private static <T> void substituirOuAdicionar(ObservableList<T> lista, T novo, java.util.function.Predicate<T> mesmoRegistro) {
        for (int i = 0; i < lista.size(); i++) {
            if (mesmoRegistro.test(lista.get(i))) {
                lista.set(i, novo);
                return;
            }
        }
        lista.add(novo);
    }

    public static boolean validarCPF(String cpf) {
        if (cpf == null) return false;
        String limpo = limparCpf(cpf);
        if (limpo.length() != 11 || limpo.matches("(\\d)\\1{10}")) return false;
        try {
            int soma1 = 0;
            for (int i = 0; i < 9; i++) soma1 += (limpo.charAt(i) - '0') * (10 - i);
            int digito1 = 11 - (soma1 % 11);
            if (digito1 >= 10) digito1 = 0;
            int soma2 = 0;
            for (int i = 0; i < 10; i++) soma2 += (limpo.charAt(i) - '0') * (11 - i);
            int digito2 = 11 - (soma2 % 11);
            if (digito2 >= 10) digito2 = 0;
            return limpo.charAt(9) - '0' == digito1 && limpo.charAt(10) - '0' == digito2;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static boolean validarEmailInstitucional(String email) {
        if (email == null) return false;
        String e = email.trim().toLowerCase();
        if (!e.contains("@") || !e.contains(".")) return false;
        return e.endsWith("ufes.br") || e.endsWith(".edu.br") || e.endsWith(".gov.br") || e.contains("@ufes.");
    }

    public static String limparCpf(String cpf) {
        return cpf == null ? "" : cpf.replaceAll("\\D", "");
    }

    public static String formatarCpf(String cpf) {
        String limpo = limparCpf(cpf);
        if (limpo.length() != 11) return cpf;
        return String.format("%s.%s.%s-%s", limpo.substring(0, 3), limpo.substring(3, 6),
                limpo.substring(6, 9), limpo.substring(9, 11));
    }
}
