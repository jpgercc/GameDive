import webview
import requests
import urllib.parse

API_KEY = 'SUA_CHAVE'


def buscar_jogo(nome, ano):
    url = "https://api.rawg.io/api/games?"
    parametros = []

    if nome:
        nome_codificado = urllib.parse.quote(nome)
        parametros.append(f"search={nome_codificado}")
        parametros.append("search_precise=true")
        parametros.append("search_exact=true")

    if ano:
        parametros.append(f"dates={ano}-01-01,{ano}-12-31")

    parametros.append("page_size=5")
    parametros.append("ordering=-rating")
    url += "&".join(parametros) + f"&key={API_KEY}"

    try:
        response = requests.get(url)

        if response.status_code == 200:
            jogos = response.json().get('results', [])
            if jogos:
                return jogos
            else:
                return {"error": "Nenhum jogo encontrado."}
        else:
            return {"error": f"Erro ao conectar com o servidor. Código: {response.status_code}"}

    except requests.exceptions.RequestException as e:
        return {"error": "Erro ao conectar com o servidor. Tente novamente mais tarde."}


def buscar_top_5_por_categoria(categoria):
    # Caso a categoria seja 'rpg', ajustamos o valor para o slug correto
    if categoria.lower() == 'rpg':
        categoria = 'role-playing-games-rpg'

    url = f"https://api.rawg.io/api/games?"
    parametros = [
        f"genres={categoria}",  # Categoria pode ser RPG ou qualquer outro valor válido
        "page_size=5",
        "ordering=-rating"
    ]
    url += "&".join(parametros) + f"&key={API_KEY}"

    print(f"URL gerada: {url}")  # Para depuração

    try:
        response = requests.get(url)

        if response.status_code == 200:
            jogos = response.json().get('results', [])
            if jogos:
                return jogos
            else:
                return {"error": "Nenhum jogo encontrado para essa categoria."}
        else:
            return {"error": f"Erro ao conectar com o servidor. Código: {response.status_code}"}

    except requests.exceptions.RequestException as e:
        return {"error": "Erro ao conectar com o servidor. Tente novamente mais tarde."}
class ApiBridge:
    def buscar_jogo(self, nome, ano):
        return buscar_jogo(nome, ano)

    def buscar_top_5_por_categoria(self, categoria):
        return buscar_top_5_por_categoria(categoria)


if __name__ == '__main__':
    api_bridge = ApiBridge()
    webview.create_window('Busca de Jogos', 'index.html', js_api=api_bridge)
    webview.start()
