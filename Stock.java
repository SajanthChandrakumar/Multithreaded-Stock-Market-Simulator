import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class Stock {
    private String name;
    private double price;
    private ReentrantLock lock = new ReentrantLock();
    private Queue<Double> history = new LinkedList<>();

    public Stock(String name, double price) {
        this.name = name;
        this.price = price;
        this.history.add(price);
    }

    public String getSymbol() {
        return name;
    }

    public synchronized double getPrice() {
        return price;
    }
    
    public synchronized Queue<Double> getHistory() {
        return history;
    }

    public synchronized void updatePrice(double percentage) {
        lock.lock(); // Prevents other threads from modifying the price at the same time
        try {
            this.price += this.price * percentage / 100;
            history.add(this.price);
            if (history.size() > 15) {
                history.poll();
            }
        } finally {
            lock.unlock(); // Always unlock after updating
        }
    }

    public boolean tryLock() {
        return lock.tryLock(); //  Allows traders to check if stock is available
    }

    public void unlock() {
        lock.unlock(); // Unlock after buying/selling
    }



    public void setPrice(double price) {
        if (price > 0) {
            this.price = price;
        }
    }

    public void displayStock() {
        System.out.println("Stock Name: " + name + ", Stock Price: " + price);
    }
}
