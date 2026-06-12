import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

public class ApiServer {

    private final Market market;
    private final List<Trader> traders;

    public ApiServer(Market market, List<Trader> traders) {
        this.market = market;
        this.traders = traders;
    }

    public void start(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/api/stocks", new StocksHandler());
            server.createContext("/api/traders", new TradersHandler());
            server.setExecutor(null); // creates a default executor
            server.start();
            System.out.println("API Server started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    class StocksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                addCorsHeaders(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            addCorsHeaders(exchange);
            StringBuilder json = new StringBuilder();
            json.append("[");
            List<Stock> stocks = market.getStocks();
            for (int i = 0; i < stocks.size(); i++) {
                Stock s = stocks.get(i);
                json.append(String.format("{\"symbol\": \"%s\", \"price\": %.2f}", s.getSymbol(), s.getPrice()));
                if (i < stocks.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");

            String response = json.toString();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    class TradersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                addCorsHeaders(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            addCorsHeaders(exchange);
            StringBuilder json = new StringBuilder();
            json.append("[");
            for (int i = 0; i < traders.size(); i++) {
                Trader t = traders.get(i);
                TraderStats s = t.getStats();
                json.append(String.format(
                        "{\"name\": \"%s\", \"balance\": %.2f, \"totalTrades\": %d, \"buyCount\": %d, \"sellCount\": %d, \"netProfit\": %.2f}",
                        t.getName(), t.getBalance(), s.totalTrades, s.buyCount, s.sellCount, s.netProfit
                ));
                if (i < traders.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");

            String response = json.toString();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
