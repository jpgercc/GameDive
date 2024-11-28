import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GameSearchApp extends Application {

    private TextField nameField;
    private TextField yearField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Setup the main window
        primaryStage.setTitle("Game Search");

        // Create form fields
        nameField = new TextField();
        yearField = new TextField();

        // Create search button
        Button searchButton = new Button("Search Game");
        searchButton.setOnAction(e -> searchGame());

        // Create layout and add controls
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);

        grid.add(new Label("Game Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Release Year:"), 0, 1);
        grid.add(yearField, 1, 1);
        grid.add(searchButton, 0, 2, 2, 1);

        // Create scene
        Scene scene = new Scene(grid, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void searchGame() {
        String name = nameField.getText();
        String year = yearField.getText();

        String apiKey = "APIKEY";
        String urlString = "https://api.rawg.io/api/games?";

        // Add search parameters
        if (!name.isEmpty()) {
            urlString += "search=" + name.replace(" ", "%20");
        }
        if (!year.isEmpty()) {
            if (!urlString.endsWith("?")) {
                urlString += "&";
            }
            urlString += "dates=" + year + "-01-01," + year + "-12-31";
        }

        // Add other parameters
        urlString += "&page_size=1&ordering=-rating&key=" + apiKey;

        try {
            // Make the HTTP request
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray games = jsonResponse.getJSONArray("results");

                if (games.length() > 0) {
                    JSONObject topGame = games.getJSONObject(0);
                    showTopGame(topGame);
                } else {
                    showAlert("No results", "No game found with the given filters.");
                }
            } else {
                showAlert("Error", "Error fetching data. HTTP code: " + responseCode);
            }

        } catch (Exception e) {
            showAlert("Error", "Error connecting to the API: " + e.getMessage());
        }
    }

    private void showTopGame(JSONObject game) {
        String name = game.getString("name");
        String releaseDate = game.getString("released");
        JSONArray platforms = game.getJSONArray("platforms");
        StringBuilder platformNames = new StringBuilder();

        for (int i = 0; i < platforms.length(); i++) {
            platformNames.append(platforms.getJSONObject(i).getJSONObject("platform").getString("name"));
            if (i < platforms.length() - 1) platformNames.append(", ");
        }

        double rating = game.getDouble("rating");

        // Create a new window to show the top game
        Stage topGameStage = new Stage();
        topGameStage.setTitle("Top Game");

        // Create labels
        Label nameLabel = new Label("Game: " + name);
        Label releaseLabel = new Label("Release Year: " + releaseDate);
        Label platformLabel = new Label("Platforms: " + platformNames);
        Label ratingLabel = new Label("Rating: " + rating);

        // Create close button
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> topGameStage.close());

        // Set layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);
        grid.add(nameLabel, 0, 0);
        grid.add(releaseLabel, 0, 1);
        grid.add(platformLabel, 0, 2);
        grid.add(ratingLabel, 0, 3);
        grid.add(closeButton, 0, 4);

        Scene scene = new Scene(grid, 300, 200);
        topGameStage.setScene(scene);
        topGameStage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
