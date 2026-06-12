import java.util.Random;

public class RandomStrategy implements TradingStrategy{
    private Random random = new Random();

    @Override
    public boolean shouldBuy(Stock stock) {
        return random.nextDouble() < 0.5; // prob. to buy 50%
    }

    @Override
    public boolean shouldSell(Stock stock) {
        return random.nextDouble() < 0.5; // prob. to sell 50%
    }

    @Override
    public String getName() {
        return "Random";
    }
}
