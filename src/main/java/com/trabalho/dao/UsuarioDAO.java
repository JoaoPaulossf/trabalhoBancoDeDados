package com.trabalho.dao;

import com.trabalho.ConexaoBanco;
import com.trabalho.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acesso JDBC à tabela usuario. */
public class UsuarioDAO {
    public boolean inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (cpf, nome, email_institucional, nivel_acesso, senha) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherParametros(stmt, usuario);
            stmt.executeUpdate();
            return true;
        }
    }

    public Optional<Usuario> buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT cpf, nome, email_institucional, nivel_acesso, senha FROM usuario WHERE cpf = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public Optional<Usuario> buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT cpf, nome, email_institucional, nivel_acesso, senha FROM usuario WHERE email_institucional = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT cpf, nome, email_institucional, nivel_acesso, senha FROM usuario ORDER BY nome";
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapear(rs));
            }
        }
        return usuarios;
    }

    public boolean atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET nome = ?, email_institucional = ?, nivel_acesso = ?, senha = ? WHERE cpf = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmailInstitucional());
            stmt.setString(3, usuario.getNivelAcesso());
            stmt.setString(4, usuario.getSenha());
            stmt.setString(5, usuario.getCpf());
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean deletar(String cpf) throws SQLException {
        String sql = "DELETE FROM usuario WHERE cpf = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            return stmt.executeUpdate() == 1;
        }
    }

    private static Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getString("cpf"),
                rs.getString("nome"),
                rs.getString("email_institucional"),
                rs.getString("nivel_acesso"),
                rs.getString("senha")
        );
    }

    private static void preencherParametros(PreparedStatement stmt, Usuario usuario) throws SQLException {
        stmt.setString(1, usuario.getCpf());
        stmt.setString(2, usuario.getNome());
        stmt.setString(3, usuario.getEmailInstitucional());
        stmt.setString(4, usuario.getNivelAcesso());
        stmt.setString(5, usuario.getSenha());
    }
}
