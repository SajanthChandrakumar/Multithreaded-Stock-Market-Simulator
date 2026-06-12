import java.util.List;
import java.util.Random;

public class StrategyTrader extends Trader { // Trader, der mithilfe einer Strategie entscheidet, ob er handeln soll.
    private TradingStrategy strategy;

    public StrategyTrader(String name, double balance, Market market, TradingStrategy strategy) {
        super(name, balance, market);
        this.strategy = strategy;
    }

    public String getStrategyName() {
        return strategy.getName();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            List<Stock> stocks = market.getStocks();

            for (Stock stock : stocks) {
                String symbol = stock.getSymbol();
                // Kaufentscheidung basierend auf Strategie
                if (strategy.shouldBuy(stock)) {
                    buyStock(symbol, 1);
                    System.out.println(getName() + " BUY " + symbol);
                }

                // Verkaufsentscheidung basierend auf Strategie
                if (strategy.shouldSell(stock)) {
                    sellStock(symbol, 1);
                    System.out.println(getName() + " SELL " + symbol);
                }
            }
            try {
                Thread.sleep(random.nextInt(2000)); // zufällige Pause zwischen Entscheidungen
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
