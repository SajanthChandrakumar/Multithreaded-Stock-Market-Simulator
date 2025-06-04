import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.List;

public class StockUpdater implements Runnable {
    private final Market market;
    private final PriceProvider priceProvider;

    public StockUpdater(Market market) {
        // Im Konstruktor NICHT erneut new StockUpdater(...) aufrufen!  wegen self loop rekursion
        this.market = market;
        this.priceProvider = new StockPriceProvider();
    }

    @Override
    public void run() {
        List<Stock> stocks = market.getStocks();
        int index = 0;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                // 1 Aktie alle 8 Sekunden updaten, um das Free-API-Limit nicht zu sprengen
                Thread.sleep(8000);

                if (!stocks.isEmpty()) {
                    Stock stock = stocks.get(index % stocks.size());
                    String symbol = stock.getSymbol();

                    double newPrice = priceProvider.getPrice(symbol);
                    if (newPrice > 0) {
                        stock.setPrice(newPrice);
                        System.out.println("[Live] Updated " + symbol + " price to $" + newPrice);
                    } else {
                        System.out.println("[Warning] Skipped update for " + symbol);
                    }
                    index++;
                }

            } catch (InterruptedException e) {
                // Wenn interrupt() gerufen wurde, beenden wir die Schleife und damit den Thread
                break;
            } catch (Exception e) {
                // Sonstige Fehler nur protokollieren und weitermachen
                System.out.println("[API ERROR] Could not update price: " + e.getMessage());
            }
        }
    }
}
