package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import model.vo.MoedaVO;

public class MoedaDAO {

    public boolean verificarCadastroMoedaBancoDAO(MoedaVO moedaVO) {
        String query = "SELECT idmoeda FROM moeda WHERE nome = ? AND pais = ? AND ano = ? AND idUsuario = ?";
        try (Connection conn = Banco.getConnection();
             PreparedStatement pstmt = Banco.getPreparedStatement(conn, query)) {
             
            pstmt.setString(1, moedaVO.getNome());
            pstmt.setString(2, moedaVO.getPais());
            pstmt.setInt(3, moedaVO.getAno());
            pstmt.setInt(4, moedaVO.getIdUsuario());
            try (ResultSet resultado = pstmt.executeQuery()) {
                return resultado.next();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar cadastro da moeda: " + e.getMessage());
            return false;
        }
    }

    public MoedaVO cadastrarMoedaDAO(MoedaVO moedaVO) {
        String query = "INSERT INTO moeda (nome, pais, ano, valor, detalhes, datacadastro, idusuario, imagem) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Banco.getConnection();
             PreparedStatement pstmt = Banco.getPreparedStatementWithPk(conn, query)) {
             
            pstmt.setString(1, moedaVO.getNome());
            pstmt.setString(2, moedaVO.getPais());
            pstmt.setInt(3, moedaVO.getAno());
            pstmt.setDouble(4, moedaVO.getValor());
            pstmt.setString(5, moedaVO.getDetalhes());
            pstmt.setObject(6, LocalDate.now());
            pstmt.setInt(7, moedaVO.getIdUsuario());
            pstmt.setBytes(8, moedaVO.getImagem());

            pstmt.execute();
            try (ResultSet resultado = pstmt.getGeneratedKeys()) {
                if (resultado.next()) {
                    moedaVO.setIdMoeda(resultado.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar moeda: " + e.getMessage());
        }
        return moedaVO;
    }

    public ArrayList<MoedaVO> consultarMoedaDAO(int idusuario) {
        String query = "SELECT idmoeda, nome, pais, ano, valor, detalhes, imagem FROM moeda WHERE idusuario = ?";
        ArrayList<MoedaVO> listaMoeda = new ArrayList<>();

        try (Connection conn = Banco.getConnection();
             PreparedStatement pstmt = Banco.getPreparedStatement(conn, query)) {
             
            pstmt.setInt(1, idusuario);
            try (ResultSet resultado = pstmt.executeQuery()) {
                while (resultado.next()) {
                    MoedaVO moeda = new MoedaVO();
                    moeda.setIdMoeda(resultado.getInt("idmoeda"));
                    moeda.setNome(resultado.getString("nome"));
                    moeda.setPais(resultado.getString("pais"));
                    moeda.setAno(resultado.getInt("ano"));
                    moeda.setValor(resultado.getDouble("valor"));
                    moeda.setDetalhes(resultado.getString("detalhes"));
                    moeda.setImagem(resultado.getBytes("imagem"));
                    listaMoeda.add(moeda);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar moedas: " + e.getMessage());
        }

        return listaMoeda;
    }

    public boolean verificarCadastroMoedaPorIDDAO(MoedaVO moedaVO) {
        String query = "SELECT idmoeda FROM moeda WHERE idmoeda = ?";
        try (Connection conn = Banco.getConnection();
             PreparedStatement pstmt = Banco.getPreparedStatement(conn, query)) {
             
            pstmt.setInt(1, moedaVO.getIdMoeda());
            try (ResultSet resultado = pstmt.executeQuery()) {
                return resultado.next();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar cadastro da moeda por ID: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarMoedaDAO(MoedaVO moedaVO) {
        String query = moedaVO.getImagem() != null && moedaVO.getImagem().length > 0
                ? "UPDATE moeda SET nome = ?, pais = ?, ano = ?, valor = ?, detalhes = ?, imagem = ? WHERE idmoeda = ?"
                : "UPDATE moeda SET nome = ?, pais = ?, ano = ?, valor = ?, detalhes = ? WHERE idmoeda = ?";
        
        try (Connection conn = Banco.getConnection();
             PreparedStatement pstmt = Banco.getPreparedStatement(conn, query)) {
             
            pstmt.setString(1, moedaVO.getNome());
            pstmt.setString(2, moedaVO.getPais());
            pstmt.setInt(3, moedaVO.getAno());
            pstmt.setDouble(4, moedaVO.getValor());
            pstmt.setString(5, moedaVO.getDetalhes());

            if (moedaVO.getImagem() != null && moedaVO.getImagem().length > 0) {
                pstmt.setBytes(6, moedaVO.getImagem());
                pstmt.setInt(7, moedaVO.getIdMoeda());
            } else {
                pstmt.setInt(6, moedaVO.getIdMoeda());
            }

            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar moeda: " + e.getMessage());
            return false;
        }
    }

    public boolean excluirMoedaDAO(MoedaVO moedaVO) {
        String query = "DELETE FROM moeda WHERE idmoeda = ?";
        try (Connection conn = Banco.getConnection();
             PreparedStatement pstmt = Banco.getPreparedStatement(conn, query)) {
             
            pstmt.setInt(1, moedaVO.getIdMoeda());
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir moeda: " + e.getMessage());
            return false;
        }
    }

    public byte[] consultarImagemMoedaDAO(int idMoeda) {
        String query = "SELECT imagem FROM moeda WHERE idmoeda = ?";
        try (Connection conn = Banco.getConnection();
             PreparedStatement pstmt = Banco.getPreparedStatement(conn, query)) {
             
            pstmt.setInt(1, idMoeda);
            try (ResultSet resultado = pstmt.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getBytes("imagem");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar imagem da moeda: " + e.getMessage());
        }
        return null;
    }
}
