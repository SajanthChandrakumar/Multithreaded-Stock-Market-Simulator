import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StockMarketSimulator {
    public static void main(String[] args) {
        Market market = new Market();
        List<Trader> allTraders = new ArrayList<>();

        // 1. Aktien anlegen (mit Platzhalter-Startpreis)
        String[] symbols = {"AAPL", "GOOG", "AMZN", "MSFT"};
        for (String sym : symbols) {
            market.addStock(new Stock(sym, 100.0));
        }

        // 2. Strategie festlegen (z.B. Random oder MeanReversion)
        TradingStrategy strategy = new RandomStrategy();

        // 3. Threadpool für Trader starten
        ExecutorService traderPool = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 1; i <= 20; i++) {
            Trader t = new StrategyTrader("StrategicTrader" + i, 1000, market, strategy);
            allTraders.add(t);
            traderPool.execute(t);
        }

        // 4. Einmalig StockUpdater‐Thread starten (nicht in Schleife, nur hier)
        Thread stockThread = new Thread(new StockUpdater(market));
        stockThread.start();

        // 5. Start the API Server
        ApiServer apiServer = new ApiServer(market, allTraders);
        apiServer.start(8080);

        System.out.println("\nSimulation is running continuously.");
        System.out.println("API is available at http://localhost:8080/api/stocks and http://localhost:8080/api/traders");
        System.out.println("Press Ctrl+C to stop.");

        // Wait indefinitely so the server and threads keep running
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

