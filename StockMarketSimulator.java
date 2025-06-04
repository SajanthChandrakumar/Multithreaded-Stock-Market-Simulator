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

        // 5. Haupt‐Thread wartet 10 Sekunden, damit Trader und Updater arbeiten können
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 6. Nach 10 Sekunden stoppen wir alles sauber ab
        stockThread.interrupt();   // Stoppt den StockUpdater (der auf isInterrupted() hört)
        traderPool.shutdownNow();  // Unterbricht sämtliche Trader‐Threads

        // 7. Warten, bis StockUpdater wirklich fertig ist (optional, für Sauberkeit)
        try {
            stockThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 8. Statistik ausgeben
        System.out.println("\nSimulation finished.");
        System.out.println("\n=== TRADER STATISTIKEN ===");
        for (Trader trader : allTraders) {
            TraderStats s = trader.getStats();
            System.out.printf(
                    "%s | Trades: %d | Buys: %d | Sells: %d | Profit: %.2f | AvgTradeSize: %.2f | FinalBalance: %.2f%n",
                    trader.getName(),
                    s.totalTrades,
                    s.buyCount,
                    s.sellCount,
                    s.netProfit,
                    s.getAverageTradeSize(),
                    trader.getBalance()
            );
        }
    }
}

