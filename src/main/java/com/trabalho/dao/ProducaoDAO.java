package com.trabalho.dao;

import com.trabalho.ConexaoBanco;
import com.trabalho.model.Producao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acesso JDBC à tabela producao. */
public class ProducaoDAO {
    public boolean inserir(Producao producao) throws SQLException {
        String sql = "INSERT INTO producao (id_projeto, titulo, tipo, data_publicacao, meio_divulgacao) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preencherParametros(stmt, producao);
            if (stmt.executeUpdate() != 1) return false;
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) producao.setIdProducao(keys.getInt(1));
            }
            return true;
        }
    }

    public Optional<Producao> buscarPorId(int idProducao) throws SQLException {
        String sql = "SELECT id_producao, id_projeto, titulo, tipo, data_publicacao, meio_divulgacao FROM producao WHERE id_producao = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProducao);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public List<Producao> listarTodos() throws SQLException {
        String sql = "SELECT id_producao, id_projeto, titulo, tipo, data_publicacao, meio_divulgacao FROM producao ORDER BY id_producao";
        List<Producao> lista = new ArrayList<>();
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Producao> listarPorProjeto(int idProjeto) throws SQLException {
        String sql = "SELECT id_producao, id_projeto, titulo, tipo, data_publicacao, meio_divulgacao FROM producao WHERE id_projeto = ? ORDER BY id_producao";
        List<Producao> lista = new ArrayList<>();
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProjeto);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public boolean atualizar(Producao producao) throws SQLException {
        String sql = "UPDATE producao SET id_projeto = ?, titulo = ?, tipo = ?, data_publicacao = ?, meio_divulgacao = ? WHERE id_producao = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherParametros(stmt, producao);
            stmt.setInt(6, producao.getIdProducao());
            stmt.executeUpdate();
            return true;
        }
    }

    public boolean deletar(int idProducao) throws SQLException {
        String sql = "DELETE FROM producao WHERE id_producao = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProducao);
            return stmt.executeUpdate() == 1;
        }
    }

    private static Producao mapear(ResultSet rs) throws SQLException {
        return new Producao(rs.getInt("id_producao"), rs.getInt("id_projeto"),
                rs.getString("titulo"), rs.getString("tipo"),
                rs.getString("data_publicacao"), rs.getString("meio_divulgacao"));
    }

    private static void preencherParametros(PreparedStatement stmt, Producao producao) throws SQLException {
        stmt.setInt(1, producao.getIdProjeto());
        stmt.setString(2, producao.getTitulo());
        stmt.setString(3, producao.getTipo());
        stmt.setString(4, producao.getDataPublicacao());
        stmt.setString(5, producao.getMeioDivulgacao());
    }
}
