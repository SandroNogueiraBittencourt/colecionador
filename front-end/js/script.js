// Seleciona os elementos necessários do DOM
const form = document.querySelector('#formulario');
const limpar = document.querySelector('#limpar');
const exibir = document.querySelector('#exibir');
const principal = document.querySelector('.principal');
const inputFile = document.getElementById('arquivo');
const fileName = document.getElementById('nome-arquivo');

// Objetos para armazenar informações do usuário e da moeda
let usuario = {};
let moeda = {};

// Atualiza o nome do arquivo selecionado no input
inputFile.addEventListener('change', () => {
    fileName.textContent = inputFile.files.length > 0 ? inputFile.files[0].name : 'Nenhum arquivo selecionado';
});

// Alterna a exibição do formulário principal
exibir.addEventListener('click', () => {
    const isVisible = principal.style.display === 'flex';
    principal.style.display = isVisible ? 'none' : 'flex';
    exibir.textContent = isVisible ? 'Mostrar' : 'Ocultar';
});

// Limpa o formulário e reseta o estado do arquivo
limpar.addEventListener('click', () => {
    form.reset();
    inputFile.value = '';
    fileName.textContent = 'Nenhum arquivo selecionado';
});

// Eventualmente, adicione outras funções de manipulação conforme necessário
