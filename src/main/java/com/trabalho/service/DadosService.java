package com.trabalho.service;

import com.trabalho.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servico central de dados em memoria para a interface grafica.
 * Fornece persistencia em memoria, regras de negocio e controle de sessao,
 * respeitando a restricao de nao utilizar scripts SQL nem conectores de banco.
 */
public class DadosService {

    private static DadosService instance;

    // Colecoes em memoria
    private final ObservableList<Usuario> usuarios = FXCollections.observableArrayList();
    private final ObservableList<Coordenador> coordenadores = FXCollections.observableArrayList();
    private final ObservableList<Participante> participantes = FXCollections.observableArrayList();
    private final ObservableList<Projeto> projetos = FXCollections.observableArrayList();
    private final ObservableList<HistoricoProj> historicoProjetos = FXCollections.observableArrayList();
    private final ObservableList<Financiamento> financiamentos = FXCollections.observableArrayList();
    private final ObservableList<Producao> producoes = FXCollections.observableArrayList();
    private final ObservableList<Autoria> autorias = FXCollections.observableArrayList();

    // Controle de sessao (RNF-02)
    private Usuario usuarioLogado;

    // Geradores de IDs exclusivos (RN-01)
    private int proximoIdProjeto = 1;
    private int proximoIdProducao = 1;

    private DadosService() {
        carregarDadosIniciais();
    }

    public static synchronized DadosService getInstance() {
        if (instance == null) {
            instance = new DadosService();
        }
        return instance;
    }

    // ==========================================
    // AUTENTICACAO E SESSAO (UC01 / RNF-02)
    // ==========================================

    public Usuario autenticar(String email, String senha) {
        if (email == null || senha == null) return null;
        for (Usuario u : usuarios) {
            if (u.autenticar(email, senha)) {
                this.usuarioLogado = u;
                return u;
            }
        }
        return null;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void logout() {
        this.usuarioLogado = null;
    }

    // ==========================================
    // PROJETOS (UC02 / UC07 / RN-01, RN-02, RN-03)
    // ==========================================

    public ObservableList<Projeto> getProjetos() {
        return projetos;
    }

    public int gerarProximoIdProjeto() {
        return proximoIdProjeto;
    }

    public boolean salvarProjeto(Projeto p) {
        if (p.getIdProjeto() <= 0) {
            p.setIdProjeto(proximoIdProjeto++);
        } else {
            // Se ja existir, atualiza
            for (int i = 0; i < projetos.size(); i++) {
                if (projetos.get(i).getIdProjeto() == p.getIdProjeto()) {
                    projetos.set(i, p);
                    return true;
                }
            }
            if (p.getIdProjeto() >= proximoIdProjeto) {
                proximoIdProjeto = p.getIdProjeto() + 1;
            }
        }
        projetos.add(p);
        return true;
    }

    public Projeto buscarProjetoPorId(int idProjeto) {
        for (Projeto p : projetos) {
            if (p.getIdProjeto() == idProjeto) {
                return p;
            }
        }
        return null;
    }

    // ==========================================
    // PARTICIPANTES E VINCULOS (UC03 / UC08 / RN-04, RN-05, RN-09)
    // ==========================================

    public ObservableList<Participante> getParticipantes() {
        return participantes;
    }

    public ObservableList<HistoricoProj> getHistoricoProjetos() {
        return historicoProjetos;
    }

    public boolean salvarParticipante(Participante p) {
        for (int i = 0; i < participantes.size(); i++) {
            if (participantes.get(i).getCpfColaborador().equals(p.getCpfColaborador())) {
                participantes.set(i, p);
                return true;
            }
        }
        participantes.add(p);
        return true;
    }

    public Participante buscarParticipantePorCpf(String cpf) {
        if (cpf == null) return null;
        String cpfLimpo = limparCpf(cpf);
        for (Participante p : participantes) {
            if (limparCpf(p.getCpfColaborador()).equals(cpfLimpo)) {
                return p;
            }
        }
        return null;
    }

    public boolean salvarVinculo(HistoricoProj vinculo) {
        for (int i = 0; i < historicoProjetos.size(); i++) {
            HistoricoProj h = historicoProjetos.get(i);
            if (h.getIdProjeto() == vinculo.getIdProjeto() &&
                limparCpf(h.getCpfColaborador()).equals(limparCpf(vinculo.getCpfColaborador()))) {
                historicoProjetos.set(i, vinculo);
                return true;
            }
        }
        historicoProjetos.add(vinculo);
        return true;
    }

    // ==========================================
    // FINANCIAMENTO (UC04 / RN-06, RN-07, RN-09)
    // ==========================================

    public ObservableList<Financiamento> getFinanciamentos() {
        return financiamentos;
    }

    public Financiamento buscarFinanciamentoPorProjeto(int idProjeto) {
        for (Financiamento f : financiamentos) {
            if (f.getIdProjeto() == idProjeto) {
                return f;
            }
        }
        return null;
    }

    public boolean salvarFinanciamento(Financiamento f) {
        for (int i = 0; i < financiamentos.size(); i++) {
            if (financiamentos.get(i).getIdProjeto() == f.getIdProjeto()) {
                financiamentos.set(i, f);
                return true;
            }
        }
        financiamentos.add(f);
        return true;
    }

    // ==========================================
    // PRODUCOES E AUTORIA (UC05 / RN-08)
    // ==========================================

    public ObservableList<Producao> getProducoes() {
        return producoes;
    }

    public ObservableList<Autoria> getAutorias() {
        return autorias;
    }

    public int gerarProximoIdProducao() {
        return proximoIdProducao;
    }

    public boolean salvarProducao(Producao prod) {
        if (prod.getIdProducao() <= 0) {
            prod.setIdProducao(proximoIdProducao++);
        } else {
            for (int i = 0; i < producoes.size(); i++) {
                if (producoes.get(i).getIdProducao() == prod.getIdProducao()) {
                    producoes.set(i, prod);
                    return true;
                }
            }
            if (prod.getIdProducao() >= proximoIdProducao) {
                proximoIdProducao = prod.getIdProducao() + 1;
            }
        }
        producoes.add(prod);
        return true;
    }

    public boolean salvarAutoria(Autoria autoria) {
        for (Autoria a : autorias) {
            if (a.getIdProducao() == autoria.getIdProducao() &&
                limparCpf(a.getCpfParticip()).equals(limparCpf(autoria.getCpfParticip()))) {
                return true;
            }
        }
        autorias.add(autoria);
        return true;
    }

    public boolean removerAutoria(Autoria autoria) {
        return autorias.removeIf(a -> a.getIdProducao() == autoria.getIdProducao() &&
                limparCpf(a.getCpfParticip()).equals(limparCpf(autoria.getCpfParticip())));
    }

    public void substituirAutoresDaProducao(int idProducao, List<String> cpfsAutores) {
        autorias.removeIf(a -> a.getIdProducao() == idProducao);
        if (cpfsAutores != null) {
            for (String cpf : cpfsAutores) {
                salvarAutoria(new Autoria(idProducao, cpf));
            }
        }
    }

    public List<Participante> listarAutoresDaProducao(int idProducao) {
        List<Participante> lista = new ArrayList<>();
        for (Autoria a : autorias) {
            if (a.getIdProducao() == idProducao) {
                Participante p = buscarParticipantePorCpf(a.getCpfParticip());
                if (p != null) {
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    // ==========================================
    // CONSULTAS E SAIDAS (UC06 / RF-05)
    // ==========================================

    public List<Projeto> consultarProjetosPorDocente(String termoDocente) {
        if (termoDocente == null || termoDocente.trim().isEmpty()) {
            return new ArrayList<>(projetos);
        }
        String termo = termoDocente.toLowerCase().trim();
        return projetos.stream().filter(p -> {
            boolean bateCpf = p.getIdCoordenador() != null && p.getIdCoordenador().toLowerCase().contains(termo);
            boolean bateNome = p.getNomeCoordenador() != null && p.getNomeCoordenador().toLowerCase().contains(termo);
            return bateCpf || bateNome;
        }).collect(Collectors.toList());
    }

    public List<HistoricoProj> consultarParticipantesPorProjeto(int idProjeto) {
        return historicoProjetos.stream()
                .filter(h -> h.getIdProjeto() == idProjeto)
                .collect(Collectors.toList());
    }

    public List<HistoricoProj> consultarHistoricoPorParticipante(String cpf) {
        String cpfLimpo = limparCpf(cpf);
        return historicoProjetos.stream()
                .filter(h -> limparCpf(h.getCpfColaborador()).equals(cpfLimpo))
                .collect(Collectors.toList());
    }

    public List<Producao> consultarProducoesPorProjeto(int idProjeto) {
        return producoes.stream()
                .filter(p -> p.getIdProjeto() == idProjeto)
                .collect(Collectors.toList());
    }

    public Financiamento consultarFinanciamentoPorProjeto(int idProjeto) {
        return buscarFinanciamentoPorProjeto(idProjeto);
    }

    // ==========================================
    // COORDENADORES E DOCENTES (RN-02)
    // ==========================================

    public ObservableList<Coordenador> getCoordenadores() {
        return coordenadores;
    }

    public String buscarNomeCoordenador(String cpf) {
        if (cpf == null) return "Não atribuído";
        for (Coordenador c : coordenadores) {
            if (c.getCpf().equalsIgnoreCase(cpf) || c.getNome().equalsIgnoreCase(cpf)) {
                return c.getNome();
            }
        }
        // Se for cadastrado em Usuario
        for (Usuario u : usuarios) {
            if (u.getCpf().equalsIgnoreCase(cpf) || u.getNome().equalsIgnoreCase(cpf)) {
                return u.getNome();
            }
        }
        return cpf;
    }

    // ==========================================
    // REGRAS DE NEGOCIO E VALIDACAO
    // ==========================================

    /**
     * RN-05: Valida se o CPF e valido matematicamente pelo algoritmo de digitos verificadores.
     */
    public static boolean validarCPF(String cpf) {
        if (cpf == null) return false;
        String limpo = limparCpf(cpf);
        if (limpo.length() != 11) return false;

        // Rejeita sequencias homogeneas
        if (limpo.matches("(\\d)\\1{10}")) return false;

        try {
            int soma1 = 0;
            for (int i = 0; i < 9; i++) {
                soma1 += (limpo.charAt(i) - '0') * (10 - i);
            }
            int digito1 = 11 - (soma1 % 11);
            if (digito1 >= 10) digito1 = 0;

            int soma2 = 0;
            for (int i = 0; i < 10; i++) {
                soma2 += (limpo.charAt(i) - '0') * (11 - i);
            }
            int digito2 = 11 - (soma2 % 11);
            if (digito2 >= 10) digito2 = 0;

            return (limpo.charAt(9) - '0' == digito1) && (limpo.charAt(10) - '0' == digito2);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * RN-05: Valida se o e-mail informado e institucional.
     */
    public static boolean validarEmailInstitucional(String email) {
        if (email == null) return false;
        String e = email.trim().toLowerCase();
        if (!e.contains("@") || !e.contains(".")) return false;
        // Aceita dominios institucionais (ufes.br, edu.br, etc.)
        return e.endsWith("ufes.br") || e.endsWith(".edu.br") || e.endsWith(".gov.br") || e.contains("@ufes.");
    }

    public static String limparCpf(String cpf) {
        if (cpf == null) return "";
        return cpf.replaceAll("\\D", "");
    }

    public static String formatarCpf(String cpf) {
        String limpo = limparCpf(cpf);
        if (limpo.length() != 11) return cpf;
        return String.format("%s.%s.%s-%s",
                limpo.substring(0, 3),
                limpo.substring(3, 6),
                limpo.substring(6, 9),
                limpo.substring(9, 11));
    }

    // ==========================================
    // CARGA DE DADOS DE DEMONSTRACAO
    // ==========================================

    private void carregarDadosIniciais() {
        // Coordenadores (Docentes principais)
        Coordenador c1 = new Coordenador("52998224725", "Dr. Carlos Eduardo", "carlos.eduardo@ufes.br", "123456");
        Coordenador c2 = new Coordenador("12345678909", "Dra. Mariana Ferreira", "mariana.ferreira@ufes.br", "123456");
        Coordenador c3 = new Coordenador("98765432100", "Dr. Roberto Almeida", "roberto.almeida@ufes.br", "123456");

        coordenadores.addAll(c1, c2, c3);
        usuarios.addAll(c1, c2, c3);

        // Gestor Administrativo (Perfil AT02)
        Usuario gestor = new Usuario("01234567890", "Prof. Marcos Oliveira", "gestor@ufes.br", "Gestor Administrativo", "123456");
        usuarios.add(gestor);

        // Participantes (Docentes, Discentes e Tecnicos)
        Participante p1 = new Participante("52998224725", "Dr. Carlos Eduardo", "carlos.eduardo@ufes.br", "Docente");
        Participante p2 = new Participante("12345678909", "Dra. Mariana Ferreira", "mariana.ferreira@ufes.br", "Docente");
        Participante p3 = new Participante("98765432100", "Dr. Roberto Almeida", "roberto.almeida@ufes.br", "Docente");
        Participante p4 = new Participante("23049582092", "Ana Beatriz Souza", "ana.souza@aluno.ufes.br", "Discente");
        Participante p5 = new Participante("85940381045", "Lucas Mendes Ribeiro", "lucas.mendes@aluno.ufes.br", "Discente");
        Participante p6 = new Participante("47291840291", "Juliana Paes Castro", "juliana.castro@ufes.br", "Técnico-Administrativo");

        participantes.addAll(p1, p2, p3, p4, p5, p6);

        // Projetos (RF-01 / RN-01, RN-02, RN-03)
        Projeto proj1 = new Projeto(1, "52998224725",
                "Inteligência Artificial Aplicada à Saúde",
                "Desenvolvimento de modelos preditivos em aprendizado profundo para diagnósticos clínicos",
                LocalDate.of(2023, 2, 1),
                LocalDate.of(2025, 2, 1),
                "Em andamento");

        Projeto proj2 = new Projeto(2, "12345678909",
                "Otimização de Bancos de Dados Distribuídos",
                "Estudo de particionamento e desempenho em consultas distribuidas de larga escala",
                LocalDate.of(2022, 8, 15),
                LocalDate.of(2024, 8, 15),
                "Concluído");

        Projeto proj3 = new Projeto(3, "98765432100",
                "Segurança da Informação e Criptografia Quântica",
                "Investigação de algoritmos pos-quanticos para redes de telecomunicacoes",
                LocalDate.of(2024, 1, 10),
                null, // RN-03: Em andamento sem data de termino
                "Em andamento");

        projetos.addAll(proj1, proj2, proj3);
        proximoIdProjeto = 4;

        // Vinculos de Participantes (Historico_Proj - UC03, UC08, RN-04)
        historicoProjetos.addAll(
                new HistoricoProj(1, "52998224725", "Coordenador", LocalDate.of(2023, 2, 1), null),
                new HistoricoProj(1, "23049582092", "Bolsista de IC", LocalDate.of(2023, 3, 1), LocalDate.of(2024, 2, 28)),
                new HistoricoProj(1, "85940381045", "Pesquisador Colaborador", LocalDate.of(2024, 3, 1), null),
                new HistoricoProj(2, "12345678909", "Coordenador", LocalDate.of(2022, 8, 15), LocalDate.of(2024, 8, 15)),
                new HistoricoProj(2, "47291840291", "Apoio Técnico", LocalDate.of(2022, 9, 1), LocalDate.of(2024, 8, 15)),
                new HistoricoProj(3, "98765432100", "Coordenador", LocalDate.of(2024, 1, 10), null),
                new HistoricoProj(3, "85940381045", "Bolsista de Mestrado", LocalDate.of(2024, 2, 1), null)
        );

        // Financiamentos (UC04 / RN-06, RN-07)
        financiamentos.addAll(
                new Financiamento(1, "CNPq", "Edital Universal", "150.000,00",
                        LocalDate.of(2023, 2, 1), LocalDate.of(2025, 2, 1), "Ativo"),
                new Financiamento(2, "FAPES", "Bolsa de Pesquisa e Extensão", "48.000,00",
                        LocalDate.of(2022, 8, 15), LocalDate.of(2024, 8, 15), "Inativo")
        );

        // Producoes Cientificas (UC05 / RN-08)
        Producao prod1 = new Producao(1, 1, "Deep Learning for Clinical Predictions",
                "Artigo em Periódico", "2023", "IEEE Transactions on Medical Imaging");

        Producao prod2 = new Producao(2, 2, "Benchmarking Distributed Query Optimizers",
                "Artigo em Congresso", "2024", "SBBD - Simpósio Brasileiro de Banco de Dados");

        Producao prod3 = new Producao(3, 1, "Relatório Técnico de Validação dos Modelos Preditivos",
                "Relatório Técnico", "2024", "Repositório Institucional UFES");

        producoes.addAll(prod1, prod2, prod3);
        proximoIdProducao = 4;

        // Autorias (Secao 9.8)
        autorias.addAll(
                new Autoria(1, "52998224725"),
                new Autoria(1, "23049582092"),
                new Autoria(2, "12345678909"),
                new Autoria(2, "47291840291"),
                new Autoria(3, "52998224725"),
                new Autoria(3, "85940381045")
        );
    }
}
