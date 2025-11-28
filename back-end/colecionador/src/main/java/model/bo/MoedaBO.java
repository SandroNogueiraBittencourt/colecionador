package model.bo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.core.Response;
import model.dao.MoedaDAO;
import model.vo.MoedaVO;

public class MoedaBO {

    private byte[] converterByteParaArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] dados = new byte[1024];
        int read;
        while ((read = inputStream.read(dados)) != -1) {
            buffer.write(dados, 0, read);
        }
        return buffer.toByteArray();
    }

    public MoedaVO cadastrarMoedaBO(InputStream moedaInputStream, InputStream fileInputStream,
            FormDataContentDisposition fileMetaData) {
        MoedaDAO moedaDAO = new MoedaDAO();
        MoedaVO moedaVO = null;
        try {
            byte[] arquivo = this.converterByteParaArray(fileInputStream); // Converte a imagem para byte[]
            String moedaJSON = new String(this.converterByteParaArray(moedaInputStream), StandardCharsets.UTF_8);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            moedaVO = objectMapper.readValue(moedaJSON, MoedaVO.class);
            moedaVO.setImagem(arquivo);

            if (moedaDAO.verificarCadastroMoedaBancoDAO(moedaVO)) {
                System.out.println("Moeda já cadastrada no banco de dados!");
            } else {
                moedaVO = moedaDAO.cadastrarMoedaDAO(moedaVO);
            }
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar moeda: " + e.getMessage()); // Log detalhado
            e.printStackTrace(); // Para rastrear o erro no console
        }
        return moedaVO;
    }

    public Response consultarTodasMoedasBO(int idUsuario) {
        MoedaDAO moedaDAO = new MoedaDAO();
        ArrayList<MoedaVO> listaMoedas = moedaDAO.consultarMoedaDAO(idUsuario);

        if (listaMoedas.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).entity("Nenhuma moeda encontrada.").build();
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            String moedasJSON = objectMapper.writeValueAsString(listaMoedas);

            return Response.ok(moedasJSON).build();
        } catch (Exception e) {
            System.err.println("Erro ao processar a resposta JSON: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao processar a resposta.").build();
        }
    }

    public boolean atualizarMoedaBO(InputStream moedaInputStream, InputStream fileInputStream) {
        MoedaDAO moedaDAO = new MoedaDAO();
        MoedaVO moedaVO;

        try {
            // Lê os dados da moeda como JSON
            String moedaJSON = new String(this.converterByteParaArray(moedaInputStream), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            moedaVO = objectMapper.readValue(moedaJSON, MoedaVO.class);

            // Lê o arquivo da imagem (se presente)
            byte[] imagem = fileInputStream != null ? this.converterByteParaArray(fileInputStream) : null;
            moedaVO.setImagem(imagem);

            // Verifica se a moeda existe no banco de dados
            if (!moedaDAO.verificarCadastroMoedaPorIDDAO(moedaVO)) {
                throw new IllegalArgumentException("Moeda não encontrada na base de dados.");
            }

            return moedaDAO.atualizarMoedaDAO(moedaVO);
        } catch (IOException e) {
            System.err.println("Erro ao processar o InputStream: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de validação: " + e.getMessage());
        }

        return false;
    }

    public boolean excluirMoedaBO(MoedaVO moedaVO) {
        MoedaDAO moedaDAO = new MoedaDAO();

        if (!moedaDAO.verificarCadastroMoedaPorIDDAO(moedaVO)) {
            System.err.println("Erro: Moeda não encontrada na base de dados.");
            return false;
        }

        return moedaDAO.excluirMoedaDAO(moedaVO);
    }

    public Response consultarImagemMoedaBO(int idMoeda) {
        MoedaDAO moedaDAO = new MoedaDAO();

        try {
            byte[] imagem = moedaDAO.consultarImagemMoedaDAO(idMoeda);

            if (imagem == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Imagem não encontrada para o ID especificado.").build();
            }

            return Response.ok(imagem).type("image/jpeg").build();
        } catch (Exception e) {
            System.err.println("Erro ao consultar imagem da moeda: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao processar a imagem.").build();
        }
    }
}
