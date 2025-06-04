public class PriceTest {
    public static void main(String[] args) throws Exception {
        StockPriceProvider provider = new StockPriceProvider();
        double price = provider.getPrice("AAPL");
        System.out.println("Aktueller Preis von AAPL: $" + price);
    }
}
