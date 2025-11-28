// Seleciona elementos do DOM
const form = document.querySelector('#formulario');
const limpar = document.querySelector('#limpar');
const exibir = document.querySelector('#exibir');
const principal = document.querySelector('.principal');
const inputFile = document.getElementById('arquivo');
const fileName = document.getElementById('nome-arquivo');
const tabelaCorpo = document.getElementById('tbody');

// Recupera o usuário logado da sessão
const usuarioLogadoStr = sessionStorage.getItem("usuarioLogado");
const usuarioLogado = JSON.parse(usuarioLogadoStr);
const userId = usuarioLogado?.idUsuario || null;

// Verifica se o usuário está logado
if (!userId) {
    alert("Sessão expirada. Você será redirecionado para a página de login.");
    window.location.href = "login.html";
}

// Atualiza o nome do arquivo selecionado no input de arquivo
inputFile.addEventListener('change', () => {
    fileName.textContent = inputFile.files.length > 0 ? inputFile.files[0].name : 'Nenhum arquivo selecionado';
});

// Alterna a exibição do formulário de cadastro/edição
exibir.addEventListener("click", () => {
    const isDisplayed = principal.style.display === 'flex';
    principal.style.display = isDisplayed ? 'none' : 'flex';
    exibir.textContent = isDisplayed ? 'Mostrar' : 'Ocultar';
});

// Limpa o formulário e reinicia o estado do input de arquivo
limpar.addEventListener('click', () => {
    form.reset();
    inputFile.value = '';
    fileName.textContent = 'Nenhum arquivo selecionado';
});

// Função para buscar moedas do servidor
async function buscarMoedas() {
    try {
        const response = await fetch(`http://localhost:8080/Colecionador/rest/moeda/listar/${userId}`);

        if (response.ok) {
            const moedas = await response.json();

            if (moedas.length === 0) {
                tabelaCorpo.innerHTML = '<tr><td colspan="8">Nenhuma moeda encontrada.</td></tr>';
                return;
            }

            preencherTabela(moedas);
        } else {
            const errorMessage = await response.text();
            alert("Erro ao buscar moedas: " + errorMessage);
        }
    } catch (error) {
        console.error("Erro ao buscar moedas:", error);
        alert("Erro ao buscar moedas. Verifique sua conexão com o servidor.");
    }
}

// Preenche a tabela com as moedas recebidas do servidor
function preencherTabela(moedas) {
    tabelaCorpo.innerHTML = '';

    moedas.forEach(moeda => {
        const linha = document.createElement('tr');

        // Adiciona a coluna com a imagem da moeda
        const imgCelula = document.createElement('td');
        if (moeda.imagem) {
            const img = document.createElement('img');
            img.src = `data:image/jpeg;base64,${moeda.imagem}`;
            img.style.width = '50px';
            img.style.height = '50px';
            imgCelula.appendChild(img);
        } else {
            imgCelula.textContent = 'Sem imagem';
        }
        linha.appendChild(imgCelula);

        // Adiciona as colunas com os dados da moeda
        linha.innerHTML += `
            <td>${moeda.idMoeda}</td>
            <td>${moeda.nome}</td>
            <td>${moeda.pais}</td>
            <td>${moeda.ano}</td>
            <td>R$ ${moeda.valor.toFixed(2)}</td>
            <td>${moeda.detalhes}</td>
        `;

        // Adiciona botões de ação (editar/excluir)
        const acoesCelula = document.createElement('td');
        const botaoEditar = document.createElement('button');
        botaoEditar.textContent = 'Editar';
        botaoEditar.className = 'acao-button';
        botaoEditar.onclick = () => editarMoeda(moeda);

        const botaoExcluir = document.createElement('button');
        botaoExcluir.textContent = 'Excluir';
        botaoExcluir.className = 'acao-button';
        botaoExcluir.onclick = () => excluirMoeda(moeda);

        acoesCelula.appendChild(botaoEditar);
        acoesCelula.appendChild(botaoExcluir);
        linha.appendChild(acoesCelula);

        tabelaCorpo.appendChild(linha);
    });
}

// Submete os dados do formulário para cadastrar ou atualizar uma moeda
form.addEventListener('submit', async (event) => {
    event.preventDefault();

    const action = form.dataset.action || 'cadastrar';
    const file = inputFile.files[0];

    const moedaVO = {
        idMoeda: form.dataset.moedaId || null,
        nome: document.getElementById('nome').value.trim(),
        pais: document.getElementById('pais').value.trim(),
        ano: parseInt(document.getElementById('ano').value),
        valor: parseFloat(document.getElementById('valor').value),
        detalhes: document.getElementById('detalhes').value.trim(),
        idUsuario: userId,
    };

    const formData = new FormData();
    formData.append('moedaVO', new Blob([JSON.stringify(moedaVO)], { type: 'application/json' }));
    if (file) {
        formData.append('file', file);
    }

    try {
        const endpoint = action === 'cadastrar' ? 'cadastrar' : 'atualizar';
        const response = await fetch(`http://localhost:8080/Colecionador/rest/moeda/${endpoint}`, {
            method: action === 'cadastrar' ? 'POST' : 'PUT',
            body: formData,
        });

        if (response.ok) {
            alert(`Moeda ${action === 'cadastrar' ? 'cadastrada' : 'atualizada'} com sucesso.`);
            form.reset();
            principal.style.display = 'none';
            exibir.textContent = 'Mostrar';
            buscarMoedas();
        } else {
            const errorMessage = await response.text();
            alert("Erro ao salvar a moeda. Detalhes: " + errorMessage);
        }
    } catch (error) {
        alert("Erro ao processar a solicitação. Tente novamente.");
    }
});

// Exclui uma moeda após confirmação do usuário
async function excluirMoeda(moeda) {
    if (confirm('Confirma a exclusão desta moeda? Esta ação não pode ser desfeita.')) {
        try {
            const response = await fetch('http://localhost:8080/Colecionador/rest/moeda/excluir', {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ idMoeda: moeda.idMoeda }),
            });

            if (response.ok) {
                alert('A moeda foi removida com sucesso.');
                buscarMoedas();
            } else {
                const errorMessage = await response.text();
                alert("Falha ao excluir a moeda. Detalhes: " + errorMessage);
            }
        } catch (error) {
            alert("Erro ao excluir a moeda. Verifique sua conexão e tente novamente.");
        }
    }
}

// Preenche o formulário para edição de uma moeda
function editarMoeda(moeda) {
    form.dataset.action = 'atualizar';
    form.dataset.moedaId = moeda.idMoeda;
    document.getElementById('nome').value = moeda.nome;
    document.getElementById('pais').value = moeda.pais;
    document.getElementById('ano').value = moeda.ano;
    document.getElementById('valor').value = moeda.valor;
    document.getElementById('detalhes').value = moeda.detalhes;
    principal.style.display = 'flex';
    exibir.textContent = 'Ocultar';
}

// Inicializa a tabela de moedas ao carregar
buscarMoedas();
