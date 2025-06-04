import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StockMarketSimulator {
    public static void main(String[] args) {
        
        Market market = new Market();

        String[] symbols = {"AAPL", "GOOG", "AMZN", "MSFT"};
        for (String sym : symbols) {
            market.addStock(new Stock(sym, 100.0)); // 100 is a Dummy value
        }


        try(ExecutorService traderPool = Executors.newVirtualThreadPerTaskExecutor()) {// Create a thread pool with 3 threads the executeService
        for (int i = 1; i <= 1000; i++){
            Trader trader = new Trader("Trader" + i, 1000, market);
            traderPool.execute(trader);
        } 

        Thread StockThread = new Thread(new StockUpdater(market));
        StockThread.start(); // Start stock price updates

            Thread.sleep(10000); // Run the simulation for 10 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Simulation finished.");
    }
}
