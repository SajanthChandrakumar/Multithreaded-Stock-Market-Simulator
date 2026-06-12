public class MeanReversionStrategy implements TradingStrategy {
    private final double MEAN_PRICE = 100.0;
    
    @Override
    public boolean shouldBuy(Stock stock) {
        // Buy if the stock is significantly below the mean
        return stock.getPrice() < MEAN_PRICE * 0.95; 
    }

    @Override
    public boolean shouldSell(Stock stock) {
        // Sell if the stock is significantly above the mean
        return stock.getPrice() > MEAN_PRICE * 1.05;
    }

    @Override
    public String getName() {
        return "Mean Reversion";
    }
}
