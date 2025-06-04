public class TraderStats {
    public int buyCount = 0;
    public int sellCount = 0;
    public int totalTrades = 0;
    public double totalVolume = 0.0;
    public double netProfit = 0.0;


    public void recordBuy(double volume) {
        buyCount++;
        totalTrades++;
        totalVolume += volume;
        netProfit -= volume;
    }

    public void recordSell(double volume) {
        sellCount++;
        totalTrades++;
        totalVolume += volume;
        netProfit += volume;
    }
    public double getAverageTradeSize() {
        return totalTrades == 0 ? 0 : totalVolume / totalTrades;
    }
}
