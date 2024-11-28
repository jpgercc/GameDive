import requests
import tkinter as tk
from tkinter import messagebox
import urllib.parse

# Função para buscar jogos na API RAWG
def buscar_jogo():
    nome = entry_nome.get()
    ano = entry_ano.get()
    
    # Sua chave de API (substitua pelo valor real)
    api_key = 'CHAVEAPI'

    # Construir a URL da requisição com base nos filtros
    url = "https://api.rawg.io/api/games?"
    parametros = []

    # Codificar os parâmetros para a URL
    if nome:
        nome_codificado = urllib.parse.quote(nome)  # Codifica o nome do jogo
        parametros.append(f"search={nome_codificado}")
    if ano:
        parametros.append(f"dates={ano}-01-01,{ano}-12-31")

    # Limitar a quantidade de resultados para 1 (o mais popular ou bem avaliado)
    parametros.append("page_size=1")
    parametros.append("ordering=-rating")  # Ordena por avaliação decrescente

    # Se existirem filtros, adicioná-los à URL
    if parametros:
        url += "&".join(parametros)
    
    # Adicionar a chave de API aos cabeçalhos (se necessário)
    headers = {
        'Authorization': f'Bearer {api_key}'
    }

    # Verifique se você precisa adicionar a chave na URL, se necessário:
    url += f"&key={api_key}"  # Caso a chave de API precise ser adicionada diretamente na URL

    # Imprimir a URL para depuração
    print("URL gerada:", url)

    # Fazer a requisição à API RAWG com a chave de API
    response = requests.get(url, headers=headers)
    
    if response.status_code == 200:
        try:
            jogos = response.json()['results']
            
            if jogos:
                mostrar_top_jogo(jogos[0])  # Chama a função para mostrar o jogo mais popular ou bem avaliado
            else:
                messagebox.showinfo("Resultado", "Nenhum jogo encontrado com os filtros dados.")
        except KeyError:
            messagebox.showerror("Erro", "Erro ao processar os dados da resposta. Resposta inesperada da API.")
            print("Resposta da API:", response.json())  # Imprime a resposta completa para depuração
    else:
        messagebox.showerror("Erro", f"Erro ao buscar os dados. Código de erro: {response.status_code}")
        print("Erro na requisição. Código:", response.status_code)

# Função para mostrar o jogo mais popular ou bem avaliado em um pop-up
def mostrar_top_jogo(jogo):
    nome = jogo['name']
    ano_lancamento = jogo['released']
    plataformas = ', '.join([plataforma['platform']['name'] for plataforma in jogo['platforms']])
    avaliacao = jogo['rating']

    # Criando a janela pop-up para exibir o jogo
    top_jogo_window = tk.Toplevel(root)
    top_jogo_window.title("Jogo Mais Popular/Bem Avaliado")
    
    label_top_jogo = tk.Label(top_jogo_window, text=f"Jogo: {nome}", font=("Arial", 14))
    label_top_jogo.pack(padx=10, pady=5)

    label_ano_lancamento = tk.Label(top_jogo_window, text=f"Ano de Lançamento: {ano_lancamento}")
    label_ano_lancamento.pack(padx=10, pady=5)

    label_plataformas = tk.Label(top_jogo_window, text=f"Plataformas: {plataformas}")
    label_plataformas.pack(padx=10, pady=5)

    label_avaliacao = tk.Label(top_jogo_window, text=f"Avaliação: {avaliacao}")
    label_avaliacao.pack(padx=10, pady=5)

    # Botão para fechar a janela pop-up
    button_fechar = tk.Button(top_jogo_window, text="Fechar", command=top_jogo_window.destroy)
    button_fechar.pack(pady=10)

# Criando a interface gráfica
root = tk.Tk()
root.title("Busca de Jogos")

# Labels e entradas para nome e ano
label_nome = tk.Label(root, text="Nome do Jogo:")
label_nome.grid(row=0, column=0, padx=10, pady=5)
entry_nome = tk.Entry(root)
entry_nome.grid(row=0, column=1, padx=10, pady=5)

label_ano = tk.Label(root, text="Ano de Lançamento:")
label_ano.grid(row=2, column=0, padx=10, pady=5)
entry_ano = tk.Entry(root)
entry_ano.grid(row=2, column=1, padx=10, pady=5)

# Botão para buscar o jogo
button_buscar = tk.Button(root, text="Buscar Jogo", command=buscar_jogo)
button_buscar.grid(row=3, column=0, columnspan=2, pady=10)

# Iniciar a interface
root.mainloop()
