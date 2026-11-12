package com.trabalho.dao;

import com.trabalho.ConexaoBanco;
import com.trabalho.model.Financiamento;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acesso JDBC à tabela financiamento. */
public class FinanciamentoDAO {
    public boolean inserir(Financiamento financiamento) throws SQLException {
        String sql = "INSERT INTO financiamento (id_projeto, agencia, tipo_fomento, valor_fomento, data_inicio, data_fim, situacao) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherParametros(stmt, financiamento);
            stmt.executeUpdate();
            return true;
        }
    }

    public Optional<Financiamento> buscarPorProjeto(int idProjeto) throws SQLException {
        String sql = "SELECT id_projeto, agencia, tipo_fomento, valor_fomento, data_inicio, data_fim, situacao FROM financiamento WHERE id_projeto = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProjeto);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public List<Financiamento> listarTodos() throws SQLException {
        String sql = "SELECT id_projeto, agencia, tipo_fomento, valor_fomento, data_inicio, data_fim, situacao FROM financiamento ORDER BY id_projeto";
        List<Financiamento> lista = new ArrayList<>();
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public boolean atualizar(Financiamento financiamento) throws SQLException {
        String sql = "UPDATE financiamento SET agencia = ?, tipo_fomento = ?, valor_fomento = ?, data_inicio = ?, data_fim = ?, situacao = ? WHERE id_projeto = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, financiamento.getAgencia());
            stmt.setString(2, financiamento.getTipoFomento());
            stmt.setBigDecimal(3, converterValor(financiamento.getValorFomento()));
            stmt.setDate(4, Date.valueOf(financiamento.getDataInicio()));
            stmt.setDate(5, Date.valueOf(financiamento.getDataFim()));
            stmt.setString(6, financiamento.getSituacao());
            stmt.setInt(7, financiamento.getIdProjeto());
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean deletar(int idProjeto) throws SQLException {
        String sql = "DELETE FROM financiamento WHERE id_projeto = ?";
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProjeto);
            return stmt.executeUpdate() == 1;
        }
    }

    private static Financiamento mapear(ResultSet rs) throws SQLException {
        return new Financiamento(rs.getInt("id_projeto"), rs.getString("agencia"),
                rs.getString("tipo_fomento"), rs.getBigDecimal("valor_fomento").toPlainString(),
                rs.getDate("data_inicio").toLocalDate(), rs.getDate("data_fim").toLocalDate(),
                rs.getString("situacao"));
    }

    private static void preencherParametros(PreparedStatement stmt, Financiamento financiamento) throws SQLException {
        stmt.setInt(1, financiamento.getIdProjeto());
        stmt.setString(2, financiamento.getAgencia());
        stmt.setString(3, financiamento.getTipoFomento());
        stmt.setBigDecimal(4, converterValor(financiamento.getValorFomento()));
        stmt.setDate(5, Date.valueOf(financiamento.getDataInicio()));
        stmt.setDate(6, Date.valueOf(financiamento.getDataFim()));
        stmt.setString(7, financiamento.getSituacao());
    }

    /** Aceita valores como 150000.00 ou 150.000,00 digitados na interface. */
    private static BigDecimal converterValor(String valor) {
        String normalizado = valor.trim().replace("R$", "").replace(" ", "");
        if (normalizado.contains(",")) {
            normalizado = normalizado.replace(".", "").replace(',', '.');
        }
        return new BigDecimal(normalizado);
    }
}
