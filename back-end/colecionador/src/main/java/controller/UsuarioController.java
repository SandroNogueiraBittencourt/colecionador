package controller;

import java.io.InputStream;

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

import model.bo.UsuarioBO;
import model.vo.UsuarioVO;

@Path("/usuario")
public class UsuarioController {
	
    @POST
    @Path("/cadastrar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cadastrarUsuarioController(InputStream usuarioInputStream) {
        try {
            UsuarioBO usuarioBO = new UsuarioBO();
            UsuarioVO usuarioCadastrado = usuarioBO.cadastrarUsuarioBO(usuarioInputStream);

            if (usuarioCadastrado == null || usuarioCadastrado.getIdUsuario() == 0) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Erro ao cadastrar o usuário.").build();
            }

            return Response.status(Response.Status.CREATED).entity(usuarioCadastrado).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao cadastrar usuário: " + e.getMessage()).build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response logarUsuarioController(InputStream usuarioInputStream) {
        try {
            UsuarioBO usuarioBO = new UsuarioBO();
            UsuarioVO usuarioLogado = usuarioBO.logarUsuarioBO(usuarioInputStream);

            if (usuarioLogado == null || usuarioLogado.getIdUsuario() == 0) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Credenciais inválidas.").build();
            }

            return Response.ok(usuarioLogado).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao tentar logar: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/listar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultarTodosUsuariosController() {
        try {
            UsuarioBO usuarioBO = new UsuarioBO();
            return usuarioBO.consultarTodosUsuariosBO();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao listar usuários: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/pesquisar/{idUsuario}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultarUsuarioController(@PathParam("idUsuario") int idUsuario) {
        try {
            UsuarioBO usuarioBO = new UsuarioBO();
            return usuarioBO.consultarUsuarioBO(idUsuario);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao consultar usuário: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/atualizar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response atualizarUsuarioController(InputStream usuarioInputStream) {
        try {
            UsuarioBO usuarioBO = new UsuarioBO();
            boolean atualizado = usuarioBO.atualizarUsuarioBO(usuarioInputStream);

            if (atualizado) {
                return Response.ok("Usuário atualizado com sucesso.").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuário não encontrado para atualização.").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao atualizar usuário: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/excluir")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response excluirUsuarioController(UsuarioVO usuarioVO) {
        try {
            UsuarioBO usuarioBO = new UsuarioBO();
            boolean excluido = usuarioBO.excluirUsuarioBO(usuarioVO);

            if (excluido) {
                return Response.ok("Usuário excluído com sucesso.").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuário não encontrado para exclusão.").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao excluir usuário: " + e.getMessage()).build();
        }
    }
}
