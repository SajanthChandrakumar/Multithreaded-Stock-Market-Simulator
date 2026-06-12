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

        // 2 & 3. Strategien festlegen und Threadpool für Trader starten
        TradingStrategy randomStrategy = new RandomStrategy();
        TradingStrategy meanStrategy = new MeanReversionStrategy();

        ExecutorService traderPool = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 1; i <= 10; i++) {
            Trader t1 = new StrategyTrader("RandomTrader" + i, 1000, market, randomStrategy);
            Trader t2 = new StrategyTrader("SmartTrader" + i, 1000, market, meanStrategy);
            allTraders.add(t1);
            allTraders.add(t2);
            traderPool.execute(t1);
            traderPool.execute(t2);
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

