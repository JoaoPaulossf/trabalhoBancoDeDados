package com.trabalho.dao;

import com.trabalho.ConexaoBanco;
import com.trabalho.model.Projeto;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acesso JDBC à tabela projeto. */
public class ProjetoDAO {
    public boolean inserir(Projeto projeto) throws SQLException {
        String sql = "INSERT INTO projeto (cpf_coordenador, titulo, descricao, data_inicio, data_termino, situacao) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preencherParametros(stmt, projeto);
            if (stmt.executeUpdate() != 1) return false;
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) projeto.setIdProjeto(keys.getInt(1));
            }
            return true;
        }
    }

    public Optional<Projeto> buscarPorId(int idProjeto) throws SQLException {
        String sql = "SELECT id_projeto, cpf_coordenador, titulo, descricao, data_inicio, data_termino, situacao FROM projeto WHERE id_projeto = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProjeto);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public List<Projeto> listarTodos() throws SQLException {
        String sql = "SELECT id_projeto, cpf_coordenador, titulo, descricao, data_inicio, data_termino, situacao FROM projeto ORDER BY id_projeto";
        List<Projeto> lista = new ArrayList<>();
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Projeto> listarPorCoordenador(String cpfCoordenador) throws SQLException {
        String sql = "SELECT id_projeto, cpf_coordenador, titulo, descricao, data_inicio, data_termino, situacao FROM projeto WHERE cpf_coordenador = ? ORDER BY id_projeto";
        List<Projeto> lista = new ArrayList<>();
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpfCoordenador);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public boolean atualizar(Projeto projeto) throws SQLException {
        String sql = "UPDATE projeto SET cpf_coordenador = ?, titulo = ?, descricao = ?, data_inicio = ?, data_termino = ?, situacao = ? WHERE id_projeto = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherParametros(stmt, projeto);
            stmt.setInt(7, projeto.getIdProjeto());
            stmt.executeUpdate();
            return true;
        }
    }

    public boolean deletar(int idProjeto) throws SQLException {
        String sql = "DELETE FROM projeto WHERE id_projeto = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProjeto);
            return stmt.executeUpdate() == 1;
        }
    }

    private static Projeto mapear(ResultSet rs) throws SQLException {
        Date dataTermino = rs.getDate("data_termino");
        return new Projeto(rs.getInt("id_projeto"), rs.getString("cpf_coordenador"),
                rs.getString("titulo"), rs.getString("descricao"),
                rs.getDate("data_inicio").toLocalDate(),
                dataTermino == null ? null : dataTermino.toLocalDate(), rs.getString("situacao"));
    }

    private static void preencherParametros(PreparedStatement stmt, Projeto projeto) throws SQLException {
        stmt.setString(1, projeto.getIdCoordenador());
        stmt.setString(2, projeto.getTitulo());
        stmt.setString(3, projeto.getDescricao());
        stmt.setDate(4, Date.valueOf(projeto.getDataInicio()));
        if (projeto.getDataTermino() == null) stmt.setNull(5, Types.DATE);
        else stmt.setDate(5, Date.valueOf(projeto.getDataTermino()));
        stmt.setString(6, projeto.getSituacao());
    }
}
