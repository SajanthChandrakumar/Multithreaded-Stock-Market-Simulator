import java.util.ArrayList;
import java.util.List;

public class Market {
    private List<Stock> stock = new ArrayList<>(); 

    public Market() {
        this.stock = new ArrayList<>();
    }

    public synchronized List<Stock> getStocks() {
        return stock;
    }

    public void addStock(Stock stocks) {
        stock.add(stocks);
    }

    public void displayMarket() {
        for (Stock sto : stock) {
            sto.displayStock();
        }
    }
}
