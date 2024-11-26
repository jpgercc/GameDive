import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Classe para armazenar informações do jogo
class Game {
    String nome;
    int ano;
    String genero;
    int nota;

    // Construtor
    public Game(String nome, int ano, String genero, int nota) {
        this.nome = nome;
        this.ano = ano;
        this.genero = genero;
        this.nota = nota;
    }

    @Override
    public String toString() {
        return "Nome: " + nome + ", Ano: " + ano + ", Gênero: " + genero + ", Nota: " + nota;
    }
}

public class SearchGames {
    
    public static void main(String[] args) {
        List<Game> games = loadGamesFromFile("games.json");
        if (games.isEmpty()) {
            System.out.println("Nenhum jogo carregado. Verifique se o arquivo 'games.json' existe.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Escolha uma opção de busca:");
        System.out.println("1 - Pesquisar por nome do jogo");
        System.out.println("2 - Pesquisar por gênero");

        int escolha = scanner.nextInt();
        scanner.nextLine(); // Consumir a nova linha após o int

        switch (escolha) {
            case 1:
                System.out.print("Digite o nome do jogo: ");
                String nomeJogo = scanner.nextLine().trim().toUpperCase();
                searchByName(games, nomeJogo);
                break;
            case 2:
                System.out.print("Digite o gênero do jogo: ");
                String genero = scanner.nextLine().trim().toUpperCase();
                searchByGenre(games, genero);
                break;
            default:
                System.out.println("Opção inválida!");
        }

        scanner.close();
    }

    // Método para buscar jogo pelo nome
    private static void searchByName(List<Game> games, String nome) {
        boolean found = false;
        for (Game game : games) {
            if (game.nome.toUpperCase().contains(nome)) {
                System.out.println(game);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Jogo não encontrado.");
        }
    }

    // Método para buscar jogos por gênero
    private static void searchByGenre(List<Game> games, String genero) {
        boolean found = false;
        for (Game game : games) {
            if (game.genero.toUpperCase().equals(genero)) {
                System.out.println(game);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Nenhum jogo encontrado para o gênero '" + genero + "'.");
        }
    }

    // Método para carregar jogos do arquivo JSON manualmente (sem usar bibliotecas)
    private static List<Game> loadGamesFromFile(String filePath) {
        List<Game> games = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonContent.append(line.trim());
            }

            // Ajuste para remover caracteres JSON desnecessários e separar objetos
            String cleanedJson = jsonContent.toString().replace("[", "").replace("]", "").replace("},{", "}#{");
            String[] jsonObjects = cleanedJson.split("#");

            for (String jsonObject : jsonObjects) {
                jsonObject = jsonObject.replace("{", "").replace("}", "");
                String[] fields = jsonObject.split(",");
                String nome = "", genero = "";
                int ano = 0, nota = 0;

                for (String field : fields) {
                    String[] keyValue = field.split(":");
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");

                    switch (key) {
                        case "nome":
                            nome = value;
                            break;
                        case "ano":
                            ano = Integer.parseInt(value);
                            break;
                        case "genero":
                            genero = value;
                            break;
                        case "nota":
                            nota = Integer.parseInt(value);
                            break;
                    }
                }
                games.add(new Game(nome, ano, genero, nota));
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar dados: " + e.getMessage());
        }

        return games;
    }
}
