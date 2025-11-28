package model.bo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.core.Response;
import model.dao.UsuarioDAO;
import model.vo.UsuarioVO;

public class UsuarioBO {

    private byte[] converterByteParaArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] dados = new byte[1024];
        int read;
        while ((read = inputStream.read(dados)) != -1) {
            buffer.write(dados, 0, read);
        }
        return buffer.toByteArray();
    }

    public UsuarioVO cadastrarUsuarioBO(InputStream usuarioInputStream) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioVO usuarioVO = null;

        try {
            // Converte o InputStream em uma string JSON
            String usuarioJSON = new String(this.converterByteParaArray(usuarioInputStream), StandardCharsets.UTF_8);
            // Converte o JSON para o objeto UsuarioVO
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            usuarioVO = objectMapper.readValue(usuarioJSON, UsuarioVO.class);

            // Verifica se o usuário já está cadastrado
            if (usuarioDAO.verificarCadastroUsuarioBancoDAO(usuarioVO)) {
                throw new IllegalArgumentException("Usuário já cadastrado no banco de dados.");
            }	

            // Cadastra o usuário
            usuarioVO = usuarioDAO.cadastrarUsuarioDAO(usuarioVO);
        } catch (IOException e) {
            System.err.println("Erro ao processar o InputStream: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de validação: " + e.getMessage());
        }

        return usuarioVO;
    }

    public Response consultarTodosUsuariosBO() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        ArrayList<UsuarioVO> listaUsuariosVO = usuarioDAO.consultarTodosUsuariosDAO();

        if (listaUsuariosVO.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).entity("Nenhum usuário encontrado.").build();
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            String usuariosJSON = objectMapper.writeValueAsString(listaUsuariosVO);

            return Response.ok(usuariosJSON).build();
        } catch (Exception e) {
            System.err.println("Erro ao processar a resposta JSON: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao processar a resposta.").build();
        }
    }

    public Response consultarUsuarioBO(int idUsuario) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioVO usuarioVO = usuarioDAO.consultarUsuarioDAO(idUsuario);

        if (usuarioVO == null) {
            return Response.status(Response.Status.NO_CONTENT).entity("Usuário não encontrado.").build();
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            String usuarioJSON = objectMapper.writeValueAsString(usuarioVO);

            return Response.ok(usuarioJSON).build();
        } catch (Exception e) {
            System.err.println("Erro ao processar a resposta JSON: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao processar a resposta.").build();
        }
    }
 
    public boolean atualizarUsuarioBO(InputStream usuarioInputStream) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioVO usuarioVO;

        try {
            // Converte o InputStream em uma string JSON
            String usuarioJSON = new String(this.converterByteParaArray(usuarioInputStream), StandardCharsets.UTF_8);
            // Converte o JSON para o objeto UsuarioVO
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            usuarioVO = objectMapper.readValue(usuarioJSON, UsuarioVO.class);

            // Verifica se o usuário existe no banco de dados
            if (!usuarioDAO.verificarCadastroUsuarioPorIDDAO(usuarioVO)) {
                throw new IllegalArgumentException("Usuário não encontrado na base de dados.");
            }

            return usuarioDAO.atualizarUsuarioDAO(usuarioVO);
        } catch (IOException e) {
            System.err.println("Erro ao processar o InputStream: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de validação: " + e.getMessage());
        }

        return false;
    }

    public boolean excluirUsuarioBO(UsuarioVO usuarioVO) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        if (!usuarioDAO.verificarCadastroUsuarioPorIDDAO(usuarioVO)) {
            System.err.println("Erro: Usuário não encontrado na base de dados.");
            return false;
        }

        return usuarioDAO.excluirUsuarioDAO(usuarioVO);
    }

    public UsuarioVO logarUsuarioBO(InputStream usuarioInputStream) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioVO usuarioVO = null;

        try {
            // Converte o InputStream em uma string JSON
            String usuarioJSON = new String(this.converterByteParaArray(usuarioInputStream), StandardCharsets.UTF_8);
            // Converte o JSON para o objeto UsuarioVO
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            usuarioVO = objectMapper.readValue(usuarioJSON, UsuarioVO.class);

            // Tenta logar o usuário
            return usuarioDAO.logarUsuarioDAO(usuarioVO);
        } catch (IOException e) {
            System.err.println("Erro ao processar o InputStream: " + e.getMessage());
        }

        return usuarioVO;
    }
}
