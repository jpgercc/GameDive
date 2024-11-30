import webview
import requests
import urllib.parse

def buscar_jogo(nome, ano):
    api_key = '56dfcd31d514483799768426b382aa82'
    url = "https://api.rawg.io/api/games?"
    parametros = []

    if nome:
        nome_codificado = urllib.parse.quote(nome)
        parametros.append(f"search={nome_codificado}")
        parametros.append("search_precise=true")  # Busca precisa
        parametros.append("search_exact=true")    # Busca exata

    if ano:
        parametros.append(f"dates={ano}-01-01,{ano}-12-31")

    parametros.append("page_size=5")
    parametros.append("ordering=-rating")
    url += "&".join(parametros) + f"&key={api_key}"

    try:
        response = requests.get(url)

        if response.status_code == 200:
            jogos = response.json().get('results', [])
            if jogos:
                return jogos  # Retorna todos os jogos encontrados, não apenas o primeiro
            else:
                return {"error": "Nenhum jogo encontrado."}
        else:
            return {"error": f"Erro ao conectar com o servidor. Código: {response.status_code}"}

    except requests.exceptions.RequestException as e:
        return {"error": "Erro ao conectar com o servidor. Tente novamente mais tarde."}

class ApiBridge:
    def buscar_jogo(self, nome, ano):
        return buscar_jogo(nome, ano)

if __name__ == '__main__':
    api_bridge = ApiBridge()
    webview.create_window('Busca de Jogos', 'index.html', js_api=api_bridge)
    webview.start()
