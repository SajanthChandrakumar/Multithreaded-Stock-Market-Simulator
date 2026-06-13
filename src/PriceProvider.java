public interface PriceProvider {
    double getPrice(String symbol) throws Exception;
}