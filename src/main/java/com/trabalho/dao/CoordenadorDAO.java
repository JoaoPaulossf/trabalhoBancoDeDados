package com.trabalho.dao;

import com.trabalho.ConexaoBanco;
import com.trabalho.model.Coordenador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acesso JDBC ao subtipo coordenador e aos dados herdados de usuario. */
public class CoordenadorDAO {
    public boolean inserir(Coordenador coordenador) throws SQLException {
        String sqlUsuario = "INSERT INTO usuario (cpf, nome, email_institucional, nivel_acesso, senha) VALUES (?, ?, ?, 'Coordenador de Projeto', ?)";
        String sqlCoordenador = "INSERT INTO coordenador (cpf) VALUES (?)";
        try (Connection conn = ConexaoBanco.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement usuarioStmt = conn.prepareStatement(sqlUsuario);
                 PreparedStatement coordenadorStmt = conn.prepareStatement(sqlCoordenador)) {
                usuarioStmt.setString(1, coordenador.getCpf());
                usuarioStmt.setString(2, coordenador.getNome());
                usuarioStmt.setString(3, coordenador.getEmailInstitucional());
                usuarioStmt.setString(4, coordenador.getSenha());
                usuarioStmt.executeUpdate();
                coordenadorStmt.setString(1, coordenador.getCpf());
                coordenadorStmt.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public Optional<Coordenador> buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT u.cpf, u.nome, u.email_institucional, u.senha FROM coordenador c JOIN usuario u ON u.cpf = c.cpf WHERE c.cpf = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public List<Coordenador> listarTodos() throws SQLException {
        String sql = "SELECT u.cpf, u.nome, u.email_institucional, u.senha FROM coordenador c JOIN usuario u ON u.cpf = c.cpf ORDER BY u.nome";
        List<Coordenador> lista = new ArrayList<>();
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public boolean atualizar(Coordenador coordenador) throws SQLException {
        String sql = "UPDATE usuario SET nome = ?, email_institucional = ?, senha = ? WHERE cpf = ? AND nivel_acesso = 'Coordenador de Projeto'";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, coordenador.getNome());
            stmt.setString(2, coordenador.getEmailInstitucional());
            stmt.setString(3, coordenador.getSenha());
            stmt.setString(4, coordenador.getCpf());
            stmt.executeUpdate();
            return true;
        }
    }

    /** Remove o subtipo e o usuário numa transação; projetos vinculados impedem a exclusão. */
    public boolean deletar(String cpf) throws SQLException {
        try (Connection conn = ConexaoBanco.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement coordenadorStmt = conn.prepareStatement("DELETE FROM coordenador WHERE cpf = ?");
                 PreparedStatement usuarioStmt = conn.prepareStatement("DELETE FROM usuario WHERE cpf = ? AND nivel_acesso = 'Coordenador de Projeto'")) {
                coordenadorStmt.setString(1, cpf);
                int removidos = coordenadorStmt.executeUpdate();
                if (removidos == 0) {
                    conn.rollback();
                    return false;
                }
                usuarioStmt.setString(1, cpf);
                usuarioStmt.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private static Coordenador mapear(ResultSet rs) throws SQLException {
        return new Coordenador(rs.getString("cpf"), rs.getString("nome"),
                rs.getString("email_institucional"), rs.getString("senha"));
    }
}
