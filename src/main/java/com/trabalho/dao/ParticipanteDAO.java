package com.trabalho.dao;

import com.trabalho.ConexaoBanco;
import com.trabalho.model.Participante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acesso JDBC à tabela participante. */
public class ParticipanteDAO {
    public boolean inserir(Participante participante) throws SQLException {
        String sql = "INSERT INTO participante (cpf_colaborador, nome, email, tipo) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherParametros(stmt, participante);
            stmt.executeUpdate();
            return true;
        }
    }

    public Optional<Participante> buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT cpf_colaborador, nome, email, tipo FROM participante WHERE cpf_colaborador = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public List<Participante> listarTodos() throws SQLException {
        String sql = "SELECT cpf_colaborador, nome, email, tipo FROM participante ORDER BY nome";
        List<Participante> lista = new ArrayList<>();
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public boolean atualizar(Participante participante) throws SQLException {
        String sql = "UPDATE participante SET nome = ?, email = ?, tipo = ? WHERE cpf_colaborador = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, participante.getNome());
            stmt.setString(2, participante.getEmail());
            stmt.setString(3, participante.getTipo());
            stmt.setString(4, participante.getCpfColaborador());
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean deletar(String cpf) throws SQLException {
        String sql = "DELETE FROM participante WHERE cpf_colaborador = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            return stmt.executeUpdate() == 1;
        }
    }

    private static Participante mapear(ResultSet rs) throws SQLException {
        return new Participante(rs.getString("cpf_colaborador"), rs.getString("nome"),
                rs.getString("email"), rs.getString("tipo"));
    }

    private static void preencherParametros(PreparedStatement stmt, Participante participante) throws SQLException {
        stmt.setString(1, participante.getCpfColaborador());
        stmt.setString(2, participante.getNome());
        stmt.setString(3, participante.getEmail());
        stmt.setString(4, participante.getTipo());
    }
}
