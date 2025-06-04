import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class StockPriceProvider implements PriceProvider {

    private static final String BASE_URL = "https://api.twelvedata.com/price";
    private static final String API_KEY = "e18f892ba5de405eab06735dea250a2d";

    public double getPrice(String symbol) {
        try {
            // 1. URL bauen
            String URL =  BASE_URL + "?symbol=" + symbol + "&apikey=" + API_KEY;

            // 2. Client + Request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .build();

            // 3. Antwort erhalten
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            // 4. Prüfen ob eine Fehlermeldung drin steht
            if (!body.contains("\"price\"")) {
                System.out.println("[API WARNING] No price in response for " + symbol + ": " + body);
                return -1.0;
            }

            // 5. Preis extrahieren
            int start = body.indexOf(":\"") + 2;
            int end = body.indexOf("\"", start);
            String priceStr = body.substring(start, end);

            // 6. Umwandeln
            return Double.parseDouble(priceStr);

        } catch (Exception e) {
            System.out.println("[API ERROR] Failed to get price for " + symbol + ": " + e.getMessage());
            return -1.0;
        }
    }

}