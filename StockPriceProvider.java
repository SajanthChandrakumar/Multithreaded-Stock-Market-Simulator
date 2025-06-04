import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class StockPriceProvider implements PriceProvider {

    private static final String BASE_URL = "https://api.twelvedata.com/price";
    private static final String API_KEY = "e18f892ba5de405eab06735dea250a2d";

    @Override
    public double getPrice (String symbol) throws Exception {
        // Schritt 1: URL zusammenbauen
        String URL = BASE_URL + "?symbol=" + symbol + "&apikey=" + API_KEY;
       // Schritt 2: HTTP-Client erzeugen
        HttpClient client = HttpClient.newHttpClient();
        // Schritt 3: Anfrage bauen
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .build();
        // Schritt 4: Anfrage senden und Antwort erhalten
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Schritt 5: JSON-Antwort extrahieren → z. B. {"price":"153.21"}
        String body = response.body();

        // Schritt 6: Preis-Text rausschneiden
        int start = body.indexOf(":\"") + 2;
        int end = body.indexOf("\"", start);
        String priceStr = body.substring(start, end);

        // Schritt 7: Preis-Text in double umwandeln
        return Double.parseDouble(priceStr);
    }
}