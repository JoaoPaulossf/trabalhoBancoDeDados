package com.trabalho.dao;

import com.trabalho.ConexaoBanco;
import com.trabalho.model.Autoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acesso JDBC à tabela associativa autoria. */
public class AutoriaDAO {
    public boolean inserir(Autoria autoria) throws SQLException {
        String sql = "INSERT INTO autoria (id_producao, cpf_particip) VALUES (?, ?)";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, autoria.getIdProducao());
            stmt.setString(2, autoria.getCpfParticip());
            return stmt.executeUpdate() == 1;
        }
    }

    public List<Autoria> listarTodos() throws SQLException {
        String sql = "SELECT id_producao, cpf_particip FROM autoria ORDER BY id_producao, cpf_particip";
        List<Autoria> lista = new ArrayList<>();
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Autoria> listarPorProducao(int idProducao) throws SQLException {
        String sql = "SELECT id_producao, cpf_particip FROM autoria WHERE id_producao = ? ORDER BY cpf_particip";
        List<Autoria> lista = new ArrayList<>();
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProducao);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Autoria> listarPorParticipante(String cpfParticipante) throws SQLException {
        String sql = "SELECT id_producao, cpf_particip FROM autoria WHERE cpf_particip = ? ORDER BY id_producao";
        List<Autoria> lista = new ArrayList<>();
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpfParticipante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public boolean deletar(int idProducao, String cpfParticipante) throws SQLException {
        String sql = "DELETE FROM autoria WHERE id_producao = ? AND cpf_particip = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProducao);
            stmt.setString(2, cpfParticipante);
            return stmt.executeUpdate() == 1;
        }
    }

    public int deletarPorProducao(int idProducao) throws SQLException {
        String sql = "DELETE FROM autoria WHERE id_producao = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProducao);
            return stmt.executeUpdate();
        }
    }

    public void substituirPorProducao(int idProducao, List<String> cpfs) throws SQLException {
        String sqlDelete = "DELETE FROM autoria WHERE id_producao = ?";
        String sqlInsert = "INSERT INTO autoria (id_producao, cpf_particip) VALUES (?, ?)";
        try (Connection conn = ConexaoBanco.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement(sqlDelete);
                 PreparedStatement insertStmt = conn.prepareStatement(sqlInsert)) {
                deleteStmt.setInt(1, idProducao);
                deleteStmt.executeUpdate();
                if (cpfs != null) {
                    for (String cpf : cpfs) {
                        insertStmt.setInt(1, idProducao);
                        insertStmt.setString(2, cpf);
                        insertStmt.addBatch();
                    }
                    insertStmt.executeBatch();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private static Autoria mapear(ResultSet rs) throws SQLException {
        return new Autoria(rs.getInt("id_producao"), rs.getString("cpf_particip"));
    }
}
