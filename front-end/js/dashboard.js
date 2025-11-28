// Seleciona os elementos HTML que serão atualizados dinamicamente
const totalMoedas = document.getElementById('total-moedas');
const valorTotal = document.getElementById('valor-total');
const containerMoedasRecentes = document.getElementById('moedas-recentes');

// Recupera o usuário logado do sessionStorage
const usuarioLogadoStr = sessionStorage.getItem('usuarioLogado');
const usuarioLogado = JSON.parse(usuarioLogadoStr);
const userId = usuarioLogado?.idUsuario || null;

// Verifica se o usuário está logado; caso contrário, redireciona para a página de login
if (!userId) {
    alert('Sessão expirada. Você será redirecionado para a página de login.');
    window.location.href = './login.html';
}

// Função para buscar os dados do servidor
async function buscarDadosDashboard() {
    try {
        console.log(`Buscando dados do dashboard para o usuário: ${userId}`);
        const response = await fetch(`http://localhost:8080/Colecionador/rest/moeda/listar/${userId}`);

        if (response.ok) {
            const moedas = await response.json();
            console.log('Dados recebidos do backend:', moedas);

            // Atualiza os valores do dashboard com base nos dados recebidos
            atualizarDashboard(moedas);
        } else {
            const errorMessage = await response.text();
            console.error('Erro ao buscar dados do backend:', errorMessage);
            alert('Erro ao buscar dados. Por favor, tente novamente mais tarde.');
        }
    } catch (error) {
        console.error('Erro ao buscar dados do backend:', error);
        alert('Erro ao se conectar ao servidor. Verifique sua conexão.');
    }
}

// Função para atualizar o dashboard
function atualizarDashboard(moedas) {
    // Atualiza o total de moedas
    totalMoedas.textContent = moedas.length;

    // Calcula o valor total das moedas
    const valorTotalColecao = moedas.reduce((acc, moeda) => acc + moeda.valor, 0);
    valorTotal.textContent = `R$ ${valorTotalColecao.toFixed(2)}`;

    // Preenche as moedas recentes
    preencherMoedasRecentes(moedas);
}

// Função para preencher as moedas recentes
function preencherMoedasRecentes(moedas) {
    containerMoedasRecentes.innerHTML = ''; // Limpa o container antes de renderizar

    // Ordena as moedas por ID (as mais recentes aparecem primeiro)
    moedas.sort((a, b) => b.idMoeda - a.idMoeda);

    // Pega as três últimas moedas ou menos
    const ultimasMoedas = moedas.slice(0, 3);

    // Cria os cards das moedas recentes
    ultimasMoedas.forEach((moeda) => {
        const card = document.createElement('div');
        card.classList.add('moeda-card');

        const img = document.createElement('img');
        img.src = moeda.imagem
            ? `data:image/jpeg;base64,${moeda.imagem}`
            : '../img/semmoeda.jpg'; // Mostra imagem padrão se não houver imagem
        img.alt = moeda.nome;

        const nome = document.createElement('h3');
        nome.textContent = moeda.nome;

        const pais = document.createElement('p');
        pais.textContent = `País: ${moeda.pais}`;

        const ano = document.createElement('p');
        ano.textContent = `Ano: ${moeda.ano}`;

        const valor = document.createElement('p');
        valor.textContent = `Valor: R$ ${moeda.valor.toFixed(2)}`;

        // Adiciona os elementos ao card
        card.appendChild(img);
        card.appendChild(nome);
        card.appendChild(pais);
        card.appendChild(ano);
        card.appendChild(valor);

        // Adiciona o card ao container
        containerMoedasRecentes.appendChild(card);
    });

    // Exibe uma mensagem se não houver moedas
    if (ultimasMoedas.length === 0) {
        containerMoedasRecentes.innerHTML = '<p>Nenhuma moeda cadastrada.</p>';
    }
}

// Chama a função para buscar os dados ao carregar o script
buscarDadosDashboard();
