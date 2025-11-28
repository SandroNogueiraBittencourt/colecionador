// Adiciona um ouvinte para o evento "submit" no formulário de login
document.getElementById("loginForm").addEventListener("submit", async (event) => {
    event.preventDefault();

    const login = document.getElementById("username").value.trim();
    const senha = document.getElementById("password").value.trim();

    if (!login || !senha) {
        alert("Por favor, preencha todos os campos.");
        return;
    }

    const dados = { login, senha };

    try {
        const response = await fetch("http://localhost:8080/Colecionador/rest/usuario/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dados),
        });

        if (response.ok) {
            const data = await response.json();
            console.log("Dados recebidos do backend no login:", data);

            if (data.idUsuario > 0 && data.nome && data.email && data.login && data.senha) {
                alert("Login efetuado com sucesso!");
                sessionStorage.setItem("usuarioLogado", JSON.stringify(data));
                window.location.href = "/index.html";
            } else {
                alert("Erro: Dados do usuário incompletos. Contate o suporte.");
            }
        } else {
            const error = await response.json();
            alert(`Erro no login: ${error.mensagem || "Credenciais inválidas."}`);
        }
    } catch (error) {
        console.error("Erro ao conectar ao servidor:", error);
        alert("Erro ao processar a solicitação. Tente novamente mais tarde.");
    }
});
