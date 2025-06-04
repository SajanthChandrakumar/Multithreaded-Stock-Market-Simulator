import java.util.List;

public class StrategyTrader extends Trader {
    private TradingStrategy strategy;

    public StrategyTrader(String name, double balance, Market market, TradingStrategy strategy) {
        super(name, balance, market);
        this.strategy = strategy;
    }
    @Override
    public void run(){
        while(!Thread.currentThread().isInterrupted()){
            List<Stock> stocks = market.getStocks();

            for (Stock stock : stocks) {
                String symbol = stock.getSymbol();



            }
    }
}
