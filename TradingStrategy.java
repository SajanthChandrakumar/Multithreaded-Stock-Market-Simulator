public interface TradingStrategy {
    boolean shouldBuy(Stock stock); // Should the Trader buy the Stock?
    boolean shouldSell(Stock stock); // Should the Trader sell the Stock?
    String getName(); // Name of the strategy
}
