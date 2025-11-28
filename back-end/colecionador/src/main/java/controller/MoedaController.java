package controller;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import model.bo.MoedaBO;
import model.vo.MoedaVO;

@Path("/moeda")
public class MoedaController {

	@POST
	@Path("/cadastrar")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response cadastrarMoedaController(@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileMetaData,
			@FormDataParam("moedaVO") InputStream moedaInputStream) {
		MoedaBO moedaBO = new MoedaBO();
		MoedaVO moedaCadastrada = null;
		try {
			moedaCadastrada = moedaBO.cadastrarMoedaBO(moedaInputStream, fileInputStream, fileMetaData);
			if (moedaCadastrada != null) {
				return Response.ok(moedaCadastrada).build();
			} else {
				return Response.status(Response.Status.BAD_REQUEST).entity("Moeda já cadastrada.").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Erro ao cadastrar a moeda: " + e.getMessage()).build();
		}
	}

	@GET
	@Path("/listar/{idUsuario}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response consultarTodasMoedasController(@PathParam("idUsuario") int idUsuario) {
		try {
			MoedaBO moedaBO = new MoedaBO();
			return moedaBO.consultarTodasMoedasBO(idUsuario);
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Erro interno ao listar moedas: " + e.getMessage()).build();
		}
	}

	@PUT
	@Path("/atualizar")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response atualizarMoedaController(@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileMetaData,
			@FormDataParam("moedaVO") InputStream moedaInputStream) {
		try {
			MoedaBO moedaBO = new MoedaBO();
			boolean atualizado = moedaBO.atualizarMoedaBO(moedaInputStream, fileInputStream);

			if (atualizado) {
				return Response.ok("Moeda atualizada com sucesso.").build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Moeda não encontrada para atualização.")
						.build();
			}
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Erro interno ao atualizar moeda: " + e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/excluir")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response excluirMoedaController(MoedaVO moedaVO) {
		try {
			MoedaBO moedaBO = new MoedaBO();
			boolean excluido = moedaBO.excluirMoedaBO(moedaVO);

			if (excluido) {
				return Response.ok("Moeda excluída com sucesso.").build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Moeda não encontrada para exclusão.").build();
			}
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Erro interno ao excluir moeda: " + e.getMessage()).build();
		}
	}

	@GET
	@Path("/imagemMoeda/{idMoeda}")
	@Produces("image/jpeg")
	public Response consultarImagemMoedaController(@PathParam("idMoeda") int idMoeda) {
		try {
			MoedaBO moedaBO = new MoedaBO();
			return moedaBO.consultarImagemMoedaBO(idMoeda);
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Erro interno ao consultar a imagem da moeda: " + e.getMessage()).build();
		}
	}
}
