// Seleciona o formulário do DOM
const form = document.querySelector('form');

// Adiciona um ouvinte de evento ao formulário para capturar o envio
form.addEventListener('submit', async (event) => {
    console.log("Formulário enviado!"); // Log para verificar o envio
    event.preventDefault(); // Impede o comportamento padrão do formulário

    // Captura os valores dos campos de entrada do formulário
    const nome = document.getElementById("nome").value.trim();
    const email = document.getElementById("email").value.trim();
    const login = document.getElementById("login").value.trim();
    const senha = document.getElementById("senha").value.trim();

    // Validações de campo para garantir que os dados atendem aos critérios mínimos
    if (!nome || nome.length < 3) {
        alert("O campo Nome deve possuir no mínimo 3 caracteres.");
        document.getElementById("nome").focus();
        return;
    }
    if (!email || email.length < 3) {
        alert("O campo E-mail deve possuir no mínimo 3 caracteres.");
        document.getElementById("email").focus();
        return;
    }
    if (!login || login.length < 3) {
        alert("O campo Login deve possuir no mínimo 3 caracteres.");
        document.getElementById("login").focus();
        return;
    }
    if (!senha || senha.length < 3) {
        alert("O campo Senha deve possuir no mínimo 3 caracteres.");
        document.getElementById("senha").focus();
        return;
    }

    // Cria um objeto com os dados do usuário para envio ao servidor
    const usuario = { nome, email, login, senha };

    try {
        // Desativa temporariamente o botão de submit para evitar cliques duplos
        const submitButton = form.querySelector('button[type="submit"]');
        submitButton.disabled = true;

        // Envia uma solicitação HTTP para o backend para cadastrar o usuário
        const response = await fetch("http://localhost:8080/Colecionador/rest/usuario/cadastrar", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(usuario),
        });

        // Reativa o botão de submit
        submitButton.disabled = false;

        // Verifica se a resposta do servidor foi bem-sucedida
        if (response.ok) {
            const data = await response.json();
            alert("Cadastro efetuado com sucesso!");
            window.location.href = "login.html";
        } else {
            // Trata erros retornados pelo servidor
            const error = await response.json();
            alert(`Erro ao cadastrar: ${error.mensagem || "Verifique os dados e tente novamente."}`);
        }
    } catch (error) {
        console.error("Erro ao conectar ao servidor:", error);
        alert("Ocorreu um erro ao processar a solicitação. Tente novamente em breve.");
    }
});
