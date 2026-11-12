package com.trabalho.dao;

import com.trabalho.ConexaoBanco;
import com.trabalho.model.HistoricoProj;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** Acesso JDBC à tabela associativa historico_proj. */
public class HistoricoProjDAO {
    public boolean inserir(HistoricoProj historico) throws SQLException {
        String sql = "INSERT INTO historico_proj (id_projeto, cpf_colaborador, funcao, data_inicio, data_fim) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherParametros(stmt, historico);
            stmt.executeUpdate();
            return true;
        }
    }

    public List<HistoricoProj> listarTodos() throws SQLException {
        String sql = "SELECT id_projeto, cpf_colaborador, funcao, data_inicio, data_fim FROM historico_proj ORDER BY id_projeto, cpf_colaborador";
        List<HistoricoProj> lista = new ArrayList<>();
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<HistoricoProj> listarPorProjeto(int idProjeto) throws SQLException {
        return listarPor("id_projeto", idProjeto);
    }

    public List<HistoricoProj> listarPorParticipante(String cpf) throws SQLException {
        String sql = "SELECT id_projeto, cpf_colaborador, funcao, data_inicio, data_fim FROM historico_proj WHERE cpf_colaborador = ? ORDER BY data_inicio";
        List<HistoricoProj> lista = new ArrayList<>();
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public boolean atualizar(HistoricoProj historico) throws SQLException {
        String sql = "UPDATE historico_proj SET funcao = ?, data_inicio = ?, data_fim = ? WHERE id_projeto = ? AND cpf_colaborador = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, historico.getFuncao());
            stmt.setDate(2, Date.valueOf(historico.getDataInicio()));
            definirDataFim(stmt, 3, historico.getDataFim());
            stmt.setInt(4, historico.getIdProjeto());
            stmt.setString(5, historico.getCpfColaborador());
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean deletar(int idProjeto, String cpfColaborador) throws SQLException {
        String sql = "DELETE FROM historico_proj WHERE id_projeto = ? AND cpf_colaborador = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProjeto);
            stmt.setString(2, cpfColaborador);
            return stmt.executeUpdate() == 1;
        }
    }

    private List<HistoricoProj> listarPor(String coluna, int idProjeto) throws SQLException {
        // O nome da coluna é fixo e vem apenas dos métodos internos, nunca de entrada do usuário.
        String sql = "SELECT id_projeto, cpf_colaborador, funcao, data_inicio, data_fim FROM historico_proj WHERE " + coluna + " = ? ORDER BY data_inicio";
        List<HistoricoProj> lista = new ArrayList<>();
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProjeto);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private static HistoricoProj mapear(ResultSet rs) throws SQLException {
        Date dataFim = rs.getDate("data_fim");
        return new HistoricoProj(rs.getInt("id_projeto"), rs.getString("cpf_colaborador"),
                rs.getString("funcao"), rs.getDate("data_inicio").toLocalDate(),
                dataFim == null ? null : dataFim.toLocalDate());
    }

    private static void preencherParametros(PreparedStatement stmt, HistoricoProj historico) throws SQLException {
        stmt.setInt(1, historico.getIdProjeto());
        stmt.setString(2, historico.getCpfColaborador());
        stmt.setString(3, historico.getFuncao());
        stmt.setDate(4, Date.valueOf(historico.getDataInicio()));
        definirDataFim(stmt, 5, historico.getDataFim());
    }

    private static void definirDataFim(PreparedStatement stmt, int indice, java.time.LocalDate dataFim) throws SQLException {
        if (dataFim == null) stmt.setNull(indice, Types.DATE);
        else stmt.setDate(indice, Date.valueOf(dataFim));
    }
}
