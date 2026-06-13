# Multithreaded Stock Market Simulator

This is a multithreaded stock market simulation built in Java. It uses Java's concurrency features (like Virtual Threads and ReentrantLocks) to simulate multiple traders concurrently buying and selling stocks in a live market.

## Features

- **Concurrent Traders:** Uses Java's `ExecutorService` (Virtual Threads) to run multiple traders simultaneously.
- **Thread-safe Transactions:** `ReentrantLock` is used in `Stock` and `Trader` classes to prevent race conditions during transactions.
- **Live Market Updates:** A dedicated `StockUpdater` thread randomly fluctuates stock prices.
- **Premium Web UI:** A beautiful, real-time dashboard built with pure HTML/CSS/JS that polls the Java backend to display live stock prices and trader leaderboards.

## Trading Strategies

The simulator uses the Strategy Design Pattern to determine how a trader behaves in the market.

1. **Random Strategy (`RandomStrategy.java`)**
   - This strategy represents an unpredictable or uninformed trader.
   - It has a 50% probability to buy a stock when evaluating it.
   - It has a 50% probability to sell a stock from the trader's portfolio.
   - It relies entirely on Java's `Random` class to make trading decisions, disregarding the current stock price or market trends.

*(More strategies like Mean Reversion or Momentum can be easily added by implementing the `TradingStrategy` interface).*

## How to Run

1. **Compile the Java code:**
   ```bash
   javac -d bin src/*.java
   ```

2. **Run the Simulator (Backend API):**
   ```bash
   java -cp bin StockMarketSimulator
   ```
   *This starts the simulation and an HTTP API server on port 8080.*

3. **Open the Web UI:**
   Open the `ui/index.html` file in your preferred web browser to see the live dashboard!
