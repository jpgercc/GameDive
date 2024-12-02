package orga.example;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Scanner;
import java.net.HttpURLConnection;
import java.net.URL;

public class GameSearchApp {

    private static final String API_KEY = "5586cb616813455a88ee39f7f2a12e24";  // Substitua pela sua chave de API

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("O que você deseja pesquisar?");
        System.out.println("1. Por nome");
        System.out.println("2. Por categoria");
        System.out.println("3. Por ano");

        String choice = scanner.nextLine();

        try {
            switch (choice) {
                case "1":
                    System.out.print("Digite o nome do jogo: ");
                    String gameName = scanner.nextLine();
                    searchGame("search", gameName);
                    break;

                case "2":
                    System.out.print("Digite a categoria do jogo: ");
                    String category = scanner.nextLine();
                    searchGame("genres", category);
                    break;

                case "3":
                    System.out.print("Digite o ano: ");
                    String year = scanner.nextLine();
                    searchGame("dates", year + "-01-01," + year + "-12-31");
                    break;

                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void searchGame(String queryParam, String queryValue) {
        try {
            String urlString = "https://api.rawg.io/api/games?key=" + API_KEY + "&" + queryParam + "=" + queryValue + "&page_size=5&ordering=-rating";
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

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

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
                String description = game.optString("description_raw", "Descrição não disponível.");
                double rating = game.optDouble("rating", 0.0);
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