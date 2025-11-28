package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import model.vo.UsuarioVO;

public class UsuarioDAO {

	public boolean verificarCadastroUsuarioBancoDAO(UsuarioVO usuarioVO) {
		String query = "SELECT idUsuario FROM usuario WHERE email = ?";
		try (Connection conn = Banco.getConnection();
				PreparedStatement pstmt = Banco.getPreparedStatement(conn, query)) {

			pstmt.setString(1, usuarioVO.getEmail());
			try (ResultSet resultado = pstmt.executeQuery()) {
				return resultado.next();
			}
		} catch (SQLException e) {
			System.err.println("Erro ao verificar cadastro do usuário: " + e.getMessage());
			return false;
		}
	}

	public UsuarioVO cadastrarUsuarioDAO(UsuarioVO usuarioVO) {
		String query = "INSERT INTO usuario (nome, email, senha, dataCadastro, login) VALUES (?, ?, ?, ?, ?)";
		try (Connection conn = Banco.getConnection();
				PreparedStatement pstmt = Banco.getPreparedStatementWithPk(conn, query)) {

			pstmt.setString(1, usuarioVO.getNome());
			pstmt.setString(2, usuarioVO.getEmail());
			pstmt.setString(3, usuarioVO.getSenha());
			pstmt.setObject(4, LocalDate.now());
			pstmt.setString(5, usuarioVO.getLogin());

			pstmt.execute();
			try (ResultSet resultado = pstmt.getGeneratedKeys()) {
				if (resultado.next()) {
					usuarioVO.setIdUsuario(resultado.getInt(1));
				}
			}
		} catch (SQLException e) {
			System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
		}
		return usuarioVO;
	}

	public ArrayList<UsuarioVO> consultarTodosUsuariosDAO() {
		String query = "SELECT idUsuario, nome, email, senha, dataCadastro, login FROM usuario";
		ArrayList<UsuarioVO> listaUsuarios = new ArrayList<>();

		try (Connection conn = Banco.getConnection();
				Statement stmt = Banco.getStatement(conn);
				ResultSet resultado = stmt.executeQuery(query)) {

			while (resultado.next()) {
				UsuarioVO usuario = new UsuarioVO();
				usuario.setIdUsuario(resultado.getInt("idUsuario"));
				usuario.setNome(resultado.getString("nome"));
				usuario.setEmail(resultado.getString("email"));
				usuario.setSenha(resultado.getString("senha"));
				usuario.setDataCadastro(LocalDate.parse(resultado.getString("dataCadastro"),
						DateTimeFormatter.ofPattern("yyyy-MM-dd")));
				usuario.setLogin(resultado.getString("login"));
				listaUsuarios.add(usuario);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao consultar todos os usuários: " + e.getMessage());
		}

		return listaUsuarios;
	}

	public UsuarioVO consultarUsuarioDAO(int idUsuario) {
		String query = "SELECT idUsuario, nome, email, senha, dataCadastro, login FROM usuario WHERE idUsuario = ?";
		UsuarioVO usuario = null;

		try (Connection conn = Banco.getConnection();
				PreparedStatement pstmt = Banco.getPreparedStatement(conn, query)) {

			pstmt.setInt(1, idUsuario);
			try (ResultSet resultado = pstmt.executeQuery()) {
				if (resultado.next()) {
					usuario = new UsuarioVO();
					usuario.setIdUsuario(resultado.getInt("idUsuario"));
					usuario.setNome(resultado.getString("nome"));
					usuario.setEmail(resultado.getString("email"));
					usuario.setSenha(resultado.getString("senha"));
					usuario.setDataCadastro(LocalDate.parse(resultado.getString("dataCadastro"),
							DateTimeFormatter.ofPattern("yyyy-MM-dd")));
					usuario.setLogin(resultado.getString("login"));
				}
			}
		} catch (SQLException e) {
			System.err.println("Erro ao consultar usuário por ID: " + e.getMessage());
		}

		return usuario;
	}

	public boolean verificarCadastroUsuarioPorIDDAO(UsuarioVO usuarioVO) {
		String query = "SELECT idUsuario FROM usuario WHERE idUsuario = ?";
		try (Connection conn = Banco.getConnection();
				PreparedStatement pstmt = Banco.getPreparedStatement(conn, query)) {

			pstmt.setInt(1, usuarioVO.getIdUsuario());
			try (ResultSet resultado = pstmt.executeQuery()) {
				return resultado.next();
			}
		} catch (SQLException e) {
			System.err.println("Erro ao verificar cadastro do usuário por ID: " + e.getMessage());
			return false;
		}
	}

	/*
	 * public boolean atualizarUsuarioDAO(UsuarioVO usuarioVO) { String query =
	 * "UPDATE usuario SET nome = ?, email = ?, senha = ?, dataCadastro = ?, login = ? WHERE idUsuario = ?"
	 * ; try (Connection conn = Banco.getConnection(); PreparedStatement pstmt =
	 * Banco.getPreparedStatement(conn, query)) {
	 * 
	 * pstmt.setString(1, usuarioVO.getNome()); pstmt.setString(2,
	 * usuarioVO.getEmail()); pstmt.setString(3, usuarioVO.getSenha());
	 * pstmt.setObject(4, usuarioVO.getDataCadastro()); pstmt.setString(5,
	 * usuarioVO.getLogin()); pstmt.setInt(6, usuarioVO.getIdUsuario());
	 * 
	 * return pstmt.executeUpdate() == 1; } catch (SQLException e) {
	 * System.err.println("Erro ao atualizar usuário: " + e.getMessage()); return
	 * false; } }
	 */

	public boolean atualizarUsuarioDAO(UsuarioVO usuarioVO) {
		boolean retorno = false;
		Connection conn = Banco.getConnection();
		PreparedStatement pstmt = null;

		String query = "UPDATE usuario SET nome = ?, email = ?, senha = ?, login = ? WHERE idUsuario = ?";

		try {
			pstmt = Banco.getPreparedStatement(conn, query);
			pstmt.setString(1, usuarioVO.getNome());
			pstmt.setString(2, usuarioVO.getEmail());
			pstmt.setString(3, usuarioVO.getSenha());
			pstmt.setString(4, usuarioVO.getLogin());
			pstmt.setInt(5, usuarioVO.getIdUsuario());

			if (pstmt.executeUpdate() == 1) {
				retorno = true;
			}
		} catch (SQLException erro) {
			System.out.println("Erro ao executar o método atualizarUsuarioDAO: " + erro.getMessage());
		} finally {
			Banco.closePreparedStatement(pstmt);
			Banco.closeConnection(conn);
		}
		return retorno;
	}

	public boolean excluirUsuarioDAO(UsuarioVO usuarioVO) {
		String query = "UPDATE usuario SET dataExpiracao = ? WHERE idUsuario = ?";
		try (Connection conn = Banco.getConnection();
				PreparedStatement pstmt = Banco.getPreparedStatement(conn, query)) {

			pstmt.setObject(1, LocalDate.now());
			pstmt.setInt(2, usuarioVO.getIdUsuario());

			return pstmt.executeUpdate() == 1;
		} catch (SQLException e) {
			System.err.println("Erro ao excluir usuário: " + e.getMessage());
			return false;
		}
	}

	/*
	 * public UsuarioVO logarUsuarioDAO(UsuarioVO usuarioVO) { String query =
	 * "SELECT idUsuario FROM usuario WHERE login = ? AND senha = ? AND dataExpiracao IS NULL"
	 * ; UsuarioVO usuarioLogado = null;
	 * 
	 * try (Connection conn = Banco.getConnection(); PreparedStatement pstmt =
	 * Banco.getPreparedStatement(conn, query)) {
	 * 
	 * pstmt.setString(1, usuarioVO.getLogin()); pstmt.setString(2,
	 * usuarioVO.getSenha()); try (ResultSet resultado = pstmt.executeQuery()) { if
	 * (resultado.next()) { usuarioLogado = new UsuarioVO();
	 * usuarioLogado.setIdUsuario(resultado.getInt("idUsuario")); } } } catch
	 * (SQLException e) { System.err.println("Erro ao logar usuário: " +
	 * e.getMessage()); }
	 * 
	 * return usuarioLogado; }
	 */

	public UsuarioVO logarUsuarioDAO(UsuarioVO usuarioVO) {
		String query = "SELECT idUsuario, nome, email, login, senha FROM usuario WHERE login = ? AND senha = ? AND dataExpiracao IS NULL";
		Connection conn = Banco.getConnection();
		PreparedStatement pstmt = null;
		ResultSet resultado = null;

		try {
			pstmt = Banco.getPreparedStatement(conn, query);
			pstmt.setString(1, usuarioVO.getLogin());
			pstmt.setString(2, usuarioVO.getSenha());
			resultado = pstmt.executeQuery();

			if (resultado.next()) {
				usuarioVO.setIdUsuario(resultado.getInt("idUsuario"));
				usuarioVO.setNome(resultado.getString("nome"));
				usuarioVO.setEmail(resultado.getString("email"));
				usuarioVO.setLogin(resultado.getString("login"));
				usuarioVO.setSenha(resultado.getString("senha"));
			} else {
				usuarioVO = null;
			}
		} catch (SQLException erro) {
			System.out.println("Erro ao executar a query do método logarUsuarioDAO.");
			System.out.println("Erro: " + erro.getMessage());
		} finally {
			Banco.closeResultSet(resultado);
			Banco.closeStatement(pstmt);
			Banco.closeConnection(conn);
		}
		return usuarioVO;
	}
	
	public boolean verificarEmailOuLoginExistente(String email, String login) {
	    Connection conn = Banco.getConnection();
	    PreparedStatement pstmt = null;
	    ResultSet resultado = null;
	    boolean existe = false;
	    String query = "SELECT idUsuario FROM usuario WHERE email = ? OR login = ?";

	    try {
	        pstmt = Banco.getPreparedStatement(conn, query);
	        pstmt.setString(1, email);
	        pstmt.setString(2, login);
	        resultado = pstmt.executeQuery();
	        if (resultado.next()) {
	            existe = true; // Email ou login já estão cadastrados
	        }
	    } catch (SQLException e) {
	        System.out.println("Erro ao verificar email ou login existentes: " + e.getMessage());
	    } finally {
	        Banco.closeResultSet(resultado);
	        Banco.closeStatement(pstmt);
	        Banco.closeConnection(conn);
	    }

	    return existe;
	}
}
