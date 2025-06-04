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
    private Market market;
    private Random random = new Random();


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
        
        while (cost > balance) { 
        System.out.println("Not enough money to buy " + quantity + " shares of " + stock.getSymbol());
         try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        cost = stock.getPrice() * quantity;
    }
        balance -= cost;
        portfolio.put(stock.getSymbol(), portfolio.getOrDefault(stock.getSymbol(), 0) + quantity);
        System.out.println( name + " bought " + quantity + " shares of " + stock.getSymbol() + " for $" + cost);
    }

    public void sellStock(Stock stock, int quantity) {
        if(!stock.tryLock()){
            return;
        }
        try{
            balanceLock.lock(); // lock the balance of the trader
            try {
                System.out.println("[Trader] " + name + " sold " + quantity + " shares of " + stock.getSymbol());
                balance += stock.getPrice() * quantity;
            } finally {
                balanceLock.unlock();
            }
        } finally {
            stock.unlock();
        }
    }

    public void displayPortfolio() {
        System.out.println("Portfolio for " + name);
        for (String stock : portfolio.keySet()) {
            System.out.println("Stock: " + stock + ", Quantity: " + portfolio.get(stock));
        }
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
