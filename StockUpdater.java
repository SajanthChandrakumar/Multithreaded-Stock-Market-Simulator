
public class StockUpdater implements Runnable {
    private Market market;
    private PriceProvider priceProvider; // Live price provider

    public StockUpdater(Market market) {  // Constructor expects a Market instance
        this.market = market;
        this.priceProvider = new StockPriceProvider();
    }

    @Override
public void run() {
    while (true) {
        try {
            Thread.sleep(2000); // alle 2 Sekunden
            
            for (Stock stock : market.getStocks()) {
                String symbol = stock.getSymbol();

                try {
                    double newPrice = priceProvider.getPrice(symbol);
                    stock.updatePrice(newPrice);
                    System.out.println("[Live] Updated " + symbol + " price to $" + newPrice);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (InterruptedException e) {
            break;
        }
    }
}

    private synchronized void notifyTraders(Stock stock) { // Notifies waiting traders
        System.out.println("[StockUpdater] Price of " + stock.getSymbol() + " dropped! Notifying traders...");
        notifyAll();
    }

}
