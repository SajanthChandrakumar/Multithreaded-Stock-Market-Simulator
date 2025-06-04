import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Trader implements Runnable {
    private String name;
    private double balance;
    private HashMap<String, Integer> portfolio;
    private final Lock balanceLock = new ReentrantLock(); //Lock for trader's balance
    Market market;
    Random random = new Random();
    protected TraderStats stats = new TraderStats();


    public Trader(String name, double balance, Market market) {
        this.name = name;
        this.balance = balance;
        this.portfolio = new HashMap<>();
        this.market = market;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public synchronized void buyStock(Stock stock, int quantity) { 
    double cost = stock.getPrice() * quantity;

    // Add maximum attempts to prevent infinite waiting
    int maxAttempts = 3;
    int attempts = 0;

    while (cost > balance && attempts < maxAttempts) {
        System.out.println("Not enough money to buy " + quantity + " shares of " + stock.getSymbol());
        try {
            wait(5000); // Wait with timeout
            attempts++;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve interrupt status
            return; // Exit the method if interrupted
        }
        cost = stock.getPrice() * quantity; // Recalculate cost after waiting
    }

    if (cost > balance) {
        System.out.println("Transaction cancelled: Insufficient funds after " + maxAttempts + " attempts");
        return;
    }

    balance -= cost;
    portfolio.put(stock.getSymbol(), portfolio.getOrDefault(stock.getSymbol(), 0) + quantity);
    stats.recordBuy(cost);

    System.out.println(name + " bought " + quantity + " shares of " + stock.getSymbol() + " for $" + cost);
    notifyAll(); // Notify other waiting threads
}

    public synchronized void buyStock(String symbol, int quantity) {
        Stock stock = market.getStockBySymbol(symbol);
        if (stock == null) {
            System.out.println("Stock " + symbol + " not found!");
            return;
        }
        buyStock(stock, quantity); // nutze die bestehende Methode
    }

    public void sellStock(Stock stock, int quantity) {
        if(!stock.tryLock()){
            return;
        }
        try{
            balanceLock.lock(); // lock the balance of the trader
            try {

                double revenue = stock.getPrice() * quantity;
                //Statistik updaten
                stats.recordSell(revenue);

                System.out.println("[Trader] " + name + " sold " + quantity + " shares of " + stock.getSymbol());
                balance += stock.getPrice() * quantity;
            } finally {
                balanceLock.unlock();
            }
        } finally {
            stock.unlock();
        }
    }
    public void sellStock(String symbol, int quantity) {
        Stock stock = market.getStockBySymbol(symbol);
        if (stock == null) {
            System.out.println("Stock " + symbol + " not found!");
            return;
        }
        sellStock(stock, quantity); // ruft die Version mit Statistik-Update auf
    }


    public TraderStats getStats() {
        return stats;
    }

    @Override
public void run() {
    while (!Thread.currentThread().isInterrupted()) {
        try {
            Thread.sleep(random.nextInt(3000) + 2000); // Wait 2-5 seconds
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted");
        }

        List<Stock> stocks = market.getStocks();
        if (stocks.isEmpty()) continue;

        Stock stock = stocks.get(random.nextInt(stocks.size())); // Pick a random stock
        boolean buy = random.nextBoolean(); // Randomly decide to buy or sell

        synchronized (stock) { // Ensure only one trader modifies stock at a time
        if (buy) {
            int quantity = random.nextInt(5) + 1; // Buy 1-5 shares
            System.out.println( name + " wants to buy " + quantity + " shares of " + stock.getSymbol());
            buyStock(stock, quantity);
        } else {
            int quantity = portfolio.getOrDefault(stock.getSymbol(), 0);
            if (quantity > 0) {
                int sellAmount = random.nextInt(quantity) + 1; // Sell up to owned amount
                System.out.println( name + " wants to sell " + sellAmount + " shares of " + stock.getSymbol());
                sellStock(stock, sellAmount);
            }
        }
    }
}
}
}