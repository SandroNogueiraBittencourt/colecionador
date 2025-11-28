(() => {
    if (window.isEdicaoScriptLoaded) {
        console.warn("O script edicao.js já foi carregado.");
        return;
    }
    window.isEdicaoScriptLoaded = true;

    console.log("Script edicao.js carregado.");

    const edicaoForm = document.querySelector('form');
    const deletarUsuarioButton = document.getElementById("deletarUsuario");

    function carregarDadosUsuarioLogado() {
        try {
            const usuarioLogadoStr = sessionStorage.getItem("usuarioLogado");
            console.log("Verificando dados do usuário logado no sessionStorage:", usuarioLogadoStr);

            if (!usuarioLogadoStr) {
                alert("Sessão expirada. Faça login novamente.");
                window.location.href = "/modules/login.html";
                return;
            }

            const usuarioLogado = JSON.parse(usuarioLogadoStr);

            if (!usuarioLogado || !usuarioLogado.idUsuario) {
                alert("Sessão expirada. Faça login novamente.");
                window.location.href = "/modules/login.html";
                return;
            }

            console.log("Tentando definir valores nos campos:");
            console.log(`Nome: ${usuarioLogado.nome}`);
            console.log(`Email: ${usuarioLogado.email}`);
            console.log(`Login: ${usuarioLogado.login}`);
            console.log(`Senha: ${usuarioLogado.senha}`);

            document.getElementById("name").value = usuarioLogado.nome || "";
            document.getElementById("email").value = usuarioLogado.email || "";
            document.getElementById("login").value = usuarioLogado.login || "";
            document.getElementById("senha").value = usuarioLogado.senha || "";

            console.log("Dados carregados com sucesso no formulário:", usuarioLogado);
        } catch (error) {
            console.error("Erro ao carregar os dados do usuário logado:", error);
            alert("Erro ao carregar os dados do usuário. Faça login novamente.");
            window.location.href = "/modules/login.html";
        }
    }

    async function atualizarUsuario(event) {
        event.preventDefault(); // Impede o comportamento padrão de envio do formulário

        // Captura os valores dos campos do formulário
        const nome = document.getElementById("name").value.trim();
        const email = document.getElementById("email").value.trim();
        const login = document.getElementById("login").value.trim();
        const senha = document.getElementById("senha").value.trim();

        // Validações de campo
        if (!nome || nome.length < 3) {
            alert("O nome deve ter pelo menos 3 caracteres.");
            document.getElementById("name").focus();
            return;
        }
        if (!email || email.length < 3) {
            alert("O email deve ter pelo menos 3 caracteres.");
            document.getElementById("email").focus();
            return;
        }
        if (!login || login.length < 3) {
            alert("O login deve ter pelo menos 3 caracteres.");
            document.getElementById("login").focus();
            return;
        }
        if (!senha || senha.length < 3) {
            alert("A senha deve ter pelo menos 3 caracteres.");
            document.getElementById("senha").focus();
            return;
        }

        // Recupera o usuário logado do sessionStorage
        const usuarioLogado = JSON.parse(sessionStorage.getItem("usuarioLogado"));

        if (!usuarioLogado || !usuarioLogado.idUsuario) {
            alert("Erro: Usuário não está logado. Faça login novamente.");
            window.location.href = "/modules/login.html";
            return;
        }

        // Cria um objeto com os dados do usuário
        const usuarioVO = {
            idUsuario: usuarioLogado.idUsuario,
            nome,
            email,
            login,
            senha,
        };

        try {
            // Faz uma requisição HTTP PUT para atualizar os dados do usuário
            const response = await fetch("http://localhost:8080/Colecionador/rest/usuario/atualizar", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(usuarioVO),
            });

            if (response.ok) {
                alert("Usuário atualizado com sucesso!");
                sessionStorage.setItem("usuarioLogado", JSON.stringify(usuarioVO)); // Atualiza o sessionStorage
                console.log("Dados atualizados:", usuarioVO);
            } else {
                const errorMessage = await response.text();
                console.error("Erro do servidor:", errorMessage);
                alert(`Erro ao atualizar usuário: ${errorMessage}`);
            }
        } catch (error) {
            console.error("Erro ao conectar ao servidor:", error);
            alert("Erro ao salvar as alterações. Tente novamente mais tarde.");
        }
    }

    async function excluirUsuario() {
        const usuarioLogado = JSON.parse(sessionStorage.getItem("usuarioLogado"));

        if (!usuarioLogado || !usuarioLogado.idUsuario) {
            alert("Erro: Usuário não está logado. Faça login novamente.");
            window.location.href = "/modules/login.html";
            return;
        }

        const confirmacao = confirm("Tem certeza de que deseja excluir este usuário? Esta ação não pode ser desfeita.");
        if (!confirmacao) {
            return;
        }

        try {
            const response = await fetch("http://localhost:8080/Colecionador/rest/usuario/excluir", {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ idUsuario: usuarioLogado.idUsuario }),
            });

            if (response.ok) {
                alert("Usuário excluído com sucesso!");
                sessionStorage.removeItem("usuarioLogado");
                window.location.href = "/modules/login.html";
            } else {
                const errorMessage = await response.text();
                console.error("Erro do servidor ao excluir usuário:", errorMessage);
                alert(`Erro ao excluir usuário: ${errorMessage}`);
            }
        } catch (error) {
            console.error("Erro ao tentar excluir o usuário:", error);
            alert("Erro ao tentar excluir o usuário. Tente novamente mais tarde.");
        }
    }

    document.addEventListener("DOMContentLoaded", () => {
        carregarDadosUsuarioLogado();
    });

    edicaoForm.addEventListener("submit", atualizarUsuario);
    deletarUsuarioButton.addEventListener("click", excluirUsuario);
})();
