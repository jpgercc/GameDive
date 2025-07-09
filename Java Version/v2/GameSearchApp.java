package orga.example;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GameSearchApp {

    private static final String API_KEY = "SUA_CHAVE";  // Substitua pela sua chave de API

    public static void main(String[] args) {
        System.out.println("O que você deseja pesquisar?");
        System.out.println("1. Por nome");
        System.out.println("2. Por categoria");
        System.out.println("3. Por ano");

        // Obtendo a escolha do usuário
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String choice = reader.readLine();

            switch (choice) {
                case "1":
                    // Pesquisa por nome
                    System.out.print("Digite o nome do jogo: ");
                    String gameName = reader.readLine();
                    searchByName(gameName);
                    break;

                case "2":
                    // Pesquisa por categoria
                    System.out.print("Digite a categoria do jogo: ");
                    String category = reader.readLine();
                    searchByCategory(category);
                    break;

                case "3":
                    // Pesquisa por ano
                    System.out.print("Digite o ano: ");
                    String year = reader.readLine();
                    searchByYear(year);
                    break;

                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void searchByName(String name) {
        try {
            String urlString = "https://api.rawg.io/api/games?key=" + API_KEY + "&search=" + name + "&page_size=5&ordering=-rating";
            String response = makeApiRequest(urlString);
            if (response != null) {
                parseAndDisplayGames(response);
            } else {
                System.out.println("Erro ao recuperar dados da API.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void searchByCategory(String category) {
        try {
            String urlString = "https://api.rawg.io/api/games?key=" + API_KEY + "&genres=" + category + "&page_size=5&ordering=-rating";
            String response = makeApiRequest(urlString);
            if (response != null) {
                parseAndDisplayGames(response);
            } else {
                System.out.println("Erro ao recuperar dados da API.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void searchByYear(String year) {
        try {
            String urlString = "https://api.rawg.io/api/games?key=" + API_KEY + "&dates=" + year + "-01-01," + year + "-12-31&page_size=5&ordering=-rating";
            String response = makeApiRequest(urlString);
            if (response != null) {
                parseAndDisplayGames(response);
            } else {
                System.out.println("Erro ao recuperar dados da API.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String makeApiRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void parseAndDisplayGames(String response) {
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray games = jsonResponse.getJSONArray("results");

        if (games.length() == 0) {
            System.out.println("Nenhum jogo encontrado.");
        } else {
            for (int i = 0; i < games.length(); i++) {
                JSONObject game = games.getJSONObject(i);
                String name = game.getString("name");

                // Usando optString para evitar exceções
                String description = game.optString("description_raw", "Descrição não disponível.");
                double rating = game.optDouble("rating", 0.0);

                // Usando optJSONArray para verificar se a chave "platforms" está presente
                String platforms = game.optJSONArray("platforms") != null ? game.getJSONArray("platforms").toString() : "Plataformas não disponíveis.";

                System.out.println("\nJogo #" + (i + 1));
                System.out.println("Nome: " + name);
                System.out.println("Nota: " + rating);
                System.out.println("Plataformas: " + platforms);
                System.out.println("Descrição: " + description);
            }
        }
    }
}
